package org.kie.projecteditor.client.forms;


import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.projecteditor.client.resources.constants.ProjectEditorConstants;
import org.kie.projecteditor.shared.model.KProjectModel;
import org.kie.projecteditor.shared.model.KnowledgeBaseConfiguration;
import org.kie.projecteditor.shared.service.ProjectEditorService;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "projectModel")
public class ProjectEditorScreen
        implements ProjectEditorScreenView.Presenter {

    @Inject
    private Caller<ProjectEditorService> projectEditorServiceCaller;

    private final ProjectEditorScreenView view;

    private KProjectModel model;

    @Inject
    public ProjectEditorScreen(ProjectEditorScreenView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @OnStart
    public void init() {
        projectEditorServiceCaller.call(new RemoteCallback<KProjectModel>() {
            @Override
            public void callback(KProjectModel model) {
                ProjectEditorScreen.this.model = model;

                for (KnowledgeBaseConfiguration configuration : model) {
                    view.addKnowledgeBaseConfiguration(configuration.getFullName());
                }
            }
        }).load();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorConstants.INSTANCE.ProjectModel();
    }

    @Override
    public void onKBaseSelection(String name) {
        view.showForm(model.get(name));
    }
}
