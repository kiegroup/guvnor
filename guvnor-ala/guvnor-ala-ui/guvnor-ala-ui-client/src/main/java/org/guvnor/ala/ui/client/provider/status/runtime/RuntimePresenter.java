/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.provider.status.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.ala.ui.client.widget.pipeline.PipelinePresenter;
import org.guvnor.ala.ui.client.widget.pipeline.stage.StagePresenter;
import org.guvnor.ala.ui.client.widget.pipeline.stage.State;
import org.guvnor.ala.ui.client.widget.pipeline.transition.TransitionPresenter;
import org.guvnor.ala.ui.events.PipelineStatusChangeEvent;
import org.guvnor.ala.ui.events.StageStatusChangeEvent;
import org.guvnor.ala.ui.model.Pipeline;
import org.guvnor.ala.ui.model.PipelineExecutionTrace;
import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.PipelineStatus;
import org.guvnor.ala.ui.model.Runtime;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.Stage;
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

import static org.guvnor.ala.ui.client.provider.status.runtime.RuntimePresenterHelper.buildStyle;

@Dependent
public class RuntimePresenter {

    public interface View
            extends UberElement<RuntimePresenter> {

        void setup(final String name,
                   final String date,
                   final String pipeline);

        void setEndpoint(String endpoint);

        void disableStart();

        void enableStart();

        void disableStop();

        void enableStop();

        void setStatus(final Collection<String> strings);

        void addExpandedContent(final IsElement element);
    }

    private static final String SYSTEM_PIPELINE_NAME = "<system>";

    private final View view;
    private final PipelinePresenter pipelinePresenter;
    private final ManagedInstance<StagePresenter> stagePresenterInstance;
    private final ManagedInstance<TransitionPresenter> transitionPresenterInstance;
    private final Caller<RuntimeService> runtimeService;

    private final List<Stage> currentStages = new ArrayList<>();
    private final Map<Stage, StagePresenter> stagePresenters = new HashMap<>();
    private final List<TransitionPresenter> currentTransitions = new ArrayList<>();

    private RuntimeListItem item;

    @Inject
    public RuntimePresenter(final View view,
                            final PipelinePresenter pipelinePresenter,
                            final ManagedInstance<StagePresenter> stagePresenterInstance,
                            final ManagedInstance<TransitionPresenter> transitionPresenterInstance,
                            final Caller<RuntimeService> runtimeService) {
        this.view = view;
        this.pipelinePresenter = pipelinePresenter;
        this.stagePresenterInstance = stagePresenterInstance;
        this.transitionPresenterInstance = transitionPresenterInstance;
        this.runtimeService = runtimeService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final RuntimeListItem runtimeListItem) {
        this.item = runtimeListItem;
        clearPipeline();
        if (item.isRuntime()) {
            setupRuntime(runtimeListItem);
        } else {
            setupPipelineTrace(runtimeListItem);
        }
        view.addExpandedContent(pipelinePresenter.getView());
    }

    private void setupRuntime(RuntimeListItem item) {
        String itemLabel = item.getItemLabel();
        String pipelineName = SYSTEM_PIPELINE_NAME;
        String createdDate = item.getRuntime().createDate();
        String endpoint = "";

        Runtime runtime = item.getRuntime();
        PipelineExecutionTrace trace = runtime.getPipelineTrace();

        if (trace != null) {
            pipelineName = trace.getPipeline().getKey().getId();
            setupPipeline(trace);
        }
        view.setup(itemLabel,
                   createdDate,
                   pipelineName);
        if (runtime.getEndpoint() != null) {
            endpoint = runtime.getEndpoint();
        }
        view.setEndpoint(endpoint);
        //TODO, when a runtime exists we should ideally set the runtime status instead.
        if (trace != null) {
            processPipelineStatus(trace.getPipelineStatus());
        }
    }

    private void setupPipelineTrace(RuntimeListItem item) {
        PipelineExecutionTrace trace = item.getPipelineTrace();
        String itemLabel = item.getItemLabel();
        String pipelineName = trace.getPipeline().getKey().getId();
        String createdDate = "";

        view.setup(itemLabel,
                   createdDate,
                   pipelineName);
        setupPipeline(trace);
        processPipelineStatus(trace.getPipelineStatus());
    }

    private void setupPipeline(final PipelineExecutionTrace trace) {
        clearPipeline();
        boolean showStep = true;
        Pipeline pipeline = trace.getPipeline();
        for (int i = 0; showStep && i < pipeline.getStages().size(); i++) {
            Stage stage = pipeline.getStages().get(i);
            PipelineStatus stageStatus = trace.getStageStatus(stage.getName());
            showStep = showStage(stageStatus);
            if (showStep) {
                if (i > 0) {
                    TransitionPresenter transitionPresenter = newTransitionPresenter();
                    currentTransitions.add(transitionPresenter);
                    pipelinePresenter.addStage(transitionPresenter.getView());
                }
                final StagePresenter stagePresenter = newStagePresenter();
                stagePresenter.setup(stage);
                stagePresenter.setState(calculateState(stageStatus));
                pipelinePresenter.addStage(stagePresenter.getView());
                currentStages.add(stage);
                stagePresenters.put(stage,
                                    stagePresenter);
            }
        }
    }

    private boolean showStage(final PipelineStatus stageStatus) {
        return stageStatus == PipelineStatus.RUNNING ||
                stageStatus == PipelineStatus.FINISHED ||
                stageStatus == PipelineStatus.ERROR;
    }

    private State calculateState(final PipelineStatus stageStatus) {
        if (stageStatus == PipelineStatus.RUNNING) {
            return State.EXECUTING;
        } else if (stageStatus == PipelineStatus.ERROR) {
            return State.ERROR;
        } else {
            return State.DONE;
        }
    }

    private void processRuntimeStatus(final Runtime runtime) {
        //TODO set the proper runtime status.
        if (runtime.getStatus() != null) {
            switch (runtime.getStatus()) {
                case STARTED:
                case LOADING:
                case WARN:
                    view.setEndpoint(runtime.getEndpoint());
                    view.enableStop();
                    view.disableStart();
                    break;
                case STOPPED:
                case ERROR:
                    view.disableStop();
                    view.enableStart();
                    break;
            }
            view.setStatus(buildStyle(runtime.getStatus()));
        }
    }

    private void processPipelineStatus(final PipelineStatus status) {
        //TODO check if we need additinal processing like enabling the start, stop, buttons.
        view.setStatus(buildStyle(status));
    }

    public void onStageStatusChange(@Observes StageStatusChangeEvent event) {
        if (isFromCurrentPipeline(event.getPipelineExecutionTraceKey())) {
            PipelineExecutionTrace trace = item.getPipelineTrace();
            Stage currentStage = currentStages.stream().
                    filter(step -> event.getStage().equals(step.getName()))
                    .findFirst()
                    .orElse(null);

            if (currentStage != null) {
                StagePresenter stagePresenter = stagePresenters.get(currentStage);
                stagePresenter.setState(calculateState(event.getStatus()));
            } else {
                Stage stage = new Stage(item.getPipelineTrace().getPipeline().getKey(),
                                        event.getStage());
                PipelineStatus stageStatus = event.getStatus();
                StagePresenter stagePresenter = newStagePresenter();
                stagePresenter.setup(stage);
                stagePresenter.setState(calculateState(stageStatus));
                if (!currentStages.isEmpty()) {
                    TransitionPresenter transitionPresenter = newTransitionPresenter();
                    currentTransitions.add(transitionPresenter);
                    pipelinePresenter.addStage(transitionPresenter.getView());
                }
                pipelinePresenter.addStage(stagePresenter.getView());

                currentStages.add(stage);
                stagePresenters.put(stage,
                                    stagePresenter);
            }
            trace.setStageStatus(event.getStage(),
                                 event.getStatus());
        }
    }

    public void onPipelineStatusChange(@Observes final PipelineStatusChangeEvent event) {
        if (isFromCurrentPipeline(event.getPipelineExecutionTraceKey())) {
            processPipelineStatus(event.getStatus());
            if (PipelineStatus.FINISHED.equals(event.getStatus()) &&
                    !PipelineStatus.FINISHED.equals(item.getPipelineTrace().getPipelineStatus())) {
                refresh(event.getPipelineExecutionTraceKey());
            }
        }
    }

    private void refresh(PipelineExecutionTraceKey pipelineExecutionTraceKey) {
        runtimeService.call(getLoadItemSuccessCallback(),
                            new DefaultErrorCallback()).getRuntimeItem(pipelineExecutionTraceKey);
    }

    private RemoteCallback<RuntimeListItem> getLoadItemSuccessCallback() {
        return runtimeListItem -> {
            if (runtimeListItem != null) {
                setup(runtimeListItem);
            }
        };
    }

    public void start() {
        //runtimeService.start(...)
        Window.alert("Not yet implemented");
    }

    public void stop() {
        //runtimeService.stop(...)
        Window.alert("Not yet implemented");
    }

    public void rebuild() {
        //runtimeService.rebuild(...)
        Window.alert("Not yet implemented");
    }

    public void delete() {
        //runtimeService.delete(...)
        Window.alert("Not yet implemented");
    }

    public View getView() {
        return view;
    }

    private boolean isFromCurrentPipeline(PipelineExecutionTraceKey pipelineExecutionTraceKey) {
        return item != null &&
                !item.isRuntime() &&
                item.getPipelineTrace().getKey().equals(pipelineExecutionTraceKey);
    }

    private void clearPipeline() {
        pipelinePresenter.clearStages();
        currentStages.clear();
        stagePresenters.values().forEach(stagePresenterInstance::destroy);
        currentTransitions.forEach(transitionPresenterInstance::destroy);
        currentTransitions.clear();
    }

    protected StagePresenter newStagePresenter() {
        return stagePresenterInstance.get();
    }

    protected TransitionPresenter newTransitionPresenter() {
        return transitionPresenterInstance.get();
    }
}