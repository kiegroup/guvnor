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

package org.guvnor.ala.ui.client.wizard.source;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.model.InternalGitSource;
import org.guvnor.ala.ui.model.Source;
import org.guvnor.ala.ui.service.SourceService;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class SourceConfigurationPagePresenter
        implements WizardPage {

    public interface View
            extends UberElement<SourceConfigurationPagePresenter> {

        String getRuntimeName();

        String getOU();

        String getRepository();

        String getBranch();

        String getProject();

        void setRuntimeStatus(final FormStatus status);

        void setOUStatus(final FormStatus formStatus);

        void setRepositoryStatus(final FormStatus formStatus);

        void setBranchStatus(final FormStatus formStatus);

        void setProjectStatus(final FormStatus formStatus);

        void clear();

        void disable();

        void enable();

        String getTitle();

        void clearRepositories();

        void addRepository(String repo);

        void clearBranches();

        void addBranch(String branch);

        void clearOrganizationUnits();

        void addOrganizationUnit(String ou);

        void clearProjects();

        void addProject(String projectName);
    }

    private final View view;
    private final Caller<SourceService> sourceService;
    private final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    @Inject
    public SourceConfigurationPagePresenter(final View view,
                                            final Caller<SourceService> sourceService,
                                            final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent) {
        this.view = view;
        this.sourceService = sourceService;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    @Override
    public String getTitle() {
        return view.getTitle();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback(isValid());
    }

    @Override
    public void initialise() {
    }

    @Override
    public void prepareView() {
    }

    public Source buildSource() {
        return new InternalGitSource(getOU(),
                                     getRepository(),
                                     getBranch(),
                                     getProject());
    }

    public void clear() {
        view.clear();
    }

    public void setup() {
        loadOUs();
    }

    public String getRuntime() {
        return view.getRuntimeName();
    }

    private String getBranch() {
        return view.getBranch();
    }

    private String getRepository() {
        return view.getRepository();
    }

    private String getOU() {
        return view.getOU();
    }

    private String getProject() {
        return view.getProject();
    }

    private boolean isValid() {
        return !getRuntime().trim().isEmpty() &&
                !getOU().isEmpty() &&
                !getRepository().isEmpty() &&
                !getBranch().isEmpty() &&
                !getProject().isEmpty();
    }

    public void disable() {
        view.disable();
    }

    protected void onRuntimeNameChange() {
        if (!view.getRuntimeName().trim().isEmpty()) {
            view.setRuntimeStatus(FormStatus.VALID);
        } else {
            view.setRuntimeStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onOrganizationalUnitChange() {
        if (!view.getOU().isEmpty()) {
            view.setOUStatus(FormStatus.VALID);
            view.clearRepositories();
            view.clearBranches();
            view.clearProjects();
            loadRepositories(getOU());
        } else {
            view.setOUStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onRepositoryChange() {
        if (!view.getRepository().isEmpty()) {
            view.setRepositoryStatus(FormStatus.VALID);
            view.clearBranches();
            view.clearProjects();
            loadBranches(getRepository());
        } else {
            view.setRepositoryStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onBranchChange() {
        if (!view.getBranch().isEmpty()) {
            view.setBranchStatus(FormStatus.VALID);
            view.clearProjects();
            loadProjects(getRepository(),
                         getBranch());
        } else {
            view.setBranchStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onProjectChange() {
        if (!view.getProject().isEmpty()) {
            view.setProjectStatus(FormStatus.VALID);
        } else {
            view.setProjectStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    private void loadOUs() {
        sourceService.call((Collection<String> ous) -> {
                               view.clearOrganizationUnits();
                               ous.forEach(view::addOrganizationUnit);
                               view.clearRepositories();
                               view.clearBranches();
                               view.clearProjects();
                           },
                           new DefaultErrorCallback()
        ).getOrganizationUnits();
    }

    private void loadRepositories(final String ou) {
        sourceService.call((Collection<String> repos) -> {
                               view.clearRepositories();
                               repos.forEach(view::addRepository);
                               view.clearBranches();
                               view.clearProjects();
                           },
                           new DefaultErrorCallback()
        ).getRepositories(ou);
    }

    private void loadBranches(final String repository) {
        sourceService.call((Collection<String> branches) -> {
                               view.clearBranches();
                               branches.forEach(view::addBranch);
                               view.clearProjects();
                           },
                           new DefaultErrorCallback()
        ).getBranches(repository);
    }

    private void loadProjects(String repository,
                              String branch) {
        sourceService.call((Collection<Project> projects) -> {
                               view.clearProjects();
                               projects.forEach(project -> view.addProject(project.getProjectName()));
                           },
                           new DefaultErrorCallback()
        ).getProjects(repository,
                      branch);
    }

    private void onContentChange() {
        wizardPageStatusChangeEvent.fire(new WizardPageStatusChangeEvent(SourceConfigurationPagePresenter.this));
    }
}
