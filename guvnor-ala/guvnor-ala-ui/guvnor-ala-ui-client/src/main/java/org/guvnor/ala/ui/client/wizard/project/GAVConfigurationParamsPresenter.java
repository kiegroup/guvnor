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
package org.guvnor.ala.ui.client.wizard.project;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.util.AbstractHasContentChangeHandlers;
import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.client.wizard.pipeline.params.PipelineParamsForm;
import org.guvnor.ala.ui.client.wizard.project.artifact.ArtifactSelectorPresenter;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.GAVConfigurationParamsPresenter_LoadGAVErrorMessage;
import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.guvnor.ala.ui.client.util.UIUtil.trimOrGetEmpty;

@ApplicationScoped
public class GAVConfigurationParamsPresenter
        extends AbstractHasContentChangeHandlers
        implements PipelineParamsForm {

    public interface View
            extends UberElement<GAVConfigurationParamsPresenter> {

        String getWizardTitle();

        String getGroupId();

        void setGroupId(final String groupId);

        String getArtifactId();

        void setArtifactId(final String artifactId);

        String getVersion();

        void setVersion(final String version);

        void setArtifactSelectorPresenter(final IsElement artifactSelector);

        void clear();

        void setGroupIdStatus(final FormStatus status);

        void setArtifactIdStatus(final FormStatus status);

        void setVersionStatus(final FormStatus status);
    }

    public static final String GROUP_ID = "group-id";

    public static final String ARTIFACT_ID = "artifact-id";

    public static final String VERSION = "version";

    private final View view;

    private final ArtifactSelectorPresenter artifactSelector;

    private PopupHelper popupHelper;

    private TranslationService translationService;

    private final Caller<M2RepoService> m2RepoService;

    private final Event<GAVConfigurationChangeEvent> gavConfigurationChangeEvent;

    @Inject
    public GAVConfigurationParamsPresenter(final View view,
                                           final ArtifactSelectorPresenter artifactSelector,
                                           final TranslationService translationService,
                                           final PopupHelper popupHelper,
                                           final Caller<M2RepoService> m2RepoService,
                                           final Event<GAVConfigurationChangeEvent> gavConfigurationChangeEvent) {
        this.view = view;
        this.artifactSelector = artifactSelector;
        this.popupHelper = popupHelper;
        this.translationService = translationService;
        this.m2RepoService = m2RepoService;
        this.gavConfigurationChangeEvent = gavConfigurationChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        artifactSelector.setArtifactSelectHandler(this::onArtifactSelected);
        view.setArtifactSelectorPresenter(artifactSelector.getView());
    }

    @Override
    public IsElement getView() {
        return view;
    }

    @Override
    public Map<String, String> buildParams() {
        Map<String, String> params = new HashMap<>();
        params.put(GROUP_ID,
                   getGroupId());
        params.put(ARTIFACT_ID,
                   getArtifactId());
        params.put(VERSION,
                   getVersion());
        return params;
    }

    @Override
    public void initialise() {
        artifactSelector.clear();
    }

    @Override
    public void prepareView() {
        artifactSelector.refresh();
    }

    @Override
    public void clear() {
        view.clear();
        fireGAVChangeEvent();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        boolean complete = isValid();
        callback.callback(complete);
    }

    @Override
    public String getWizardTitle() {
        return view.getWizardTitle();
    }

    private String getGroupId() {
        return trimOrGetEmpty(view.getGroupId());
    }

    private String getArtifactId() {
        return trimOrGetEmpty(view.getArtifactId());
    }

    private String getVersion() {
        return trimOrGetEmpty(view.getVersion());
    }

    protected void onArtifactSelected(final String path) {
        m2RepoService.call(getLoadGAVSuccessCallback(),
                           getLoadGAVErrorCallback()).loadGAVFromJar(path);
    }

    protected void onGroupIdChange() {
        if (!getGroupId().isEmpty()) {
            view.setGroupIdStatus(FormStatus.VALID);
        } else {
            view.setGroupIdStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onArtifactIdChange() {
        if (!getArtifactId().isEmpty()) {
            view.setArtifactIdStatus(FormStatus.VALID);
        } else {
            view.setArtifactIdStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onVersionChange() {
        if (!getVersion().isEmpty()) {
            view.setVersionStatus(FormStatus.VALID);
        } else {
            view.setVersionStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    private boolean isValid() {
        return !getGroupId().isEmpty() &&
                !getArtifactId().isEmpty() &&
                !getVersion().isEmpty();
    }

    private RemoteCallback<GAV> getLoadGAVSuccessCallback() {
        return (gav) -> {
            view.setGroupId(gav.getGroupId());
            view.setArtifactId(gav.getArtifactId());
            view.setVersion(gav.getVersion());
            onContentChange();
        };
    }

    private ErrorCallback<Message> getLoadGAVErrorCallback() {
        return (message, throwable) -> {
            view.setGroupId(EMPTY_STRING);
            view.setArtifactId(EMPTY_STRING);
            view.setVersion(EMPTY_STRING);
            popupHelper.showErrorPopup(translationService.format(GAVConfigurationParamsPresenter_LoadGAVErrorMessage,
                                                                 throwable.getMessage()));
            onContentChange();
            return false;
        };
    }

    protected void onContentChange() {
        fireGAVChangeEvent();
        fireChangeHandlers();
    }

    private void fireGAVChangeEvent() {
        if (isValid()) {
            gavConfigurationChangeEvent.fire(new GAVConfigurationChangeEvent(new GAV(getGroupId(),
                                                                                     getArtifactId(),
                                                                                     getVersion())));
        } else {
            gavConfigurationChangeEvent.fire(new GAVConfigurationChangeEvent());
        }
    }
}