package org.kie.projecteditor.client.forms;


import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.projecteditor.client.resources.constants.ProjectEditorConstants;
import org.kie.projecteditor.shared.model.KProjectModel;
import org.kie.projecteditor.shared.model.KnowledgeBaseConfiguration;
import org.kie.projecteditor.shared.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "projectEditorScreen")
public class ProjectEditorScreen
        implements ProjectEditorScreenView.Presenter {

    private final Caller<ProjectEditorService> projectEditorServiceCaller;
    private final ProjectEditorScreenView view;
    private final AddNewKBasePopup addNewKBasePopup;

    private KProjectModel model;

    private String selectedItemName = null;

    @Inject
    public ProjectEditorScreen(Caller<ProjectEditorService> projectEditorServiceCaller,
                               AddNewKBasePopup addNewKBasePopup,
                               ProjectEditorScreenView view) {
        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.addNewKBasePopup = addNewKBasePopup;
        this.view = view;

        view.setPresenter(this);
    }

    @OnStart
    public void init(PlaceRequest placeRequest) {
        projectEditorServiceCaller.call(new RemoteCallback<KProjectModel>() {
            @Override
            public void callback(KProjectModel model) {
                ProjectEditorScreen.this.model = model;

                for (KnowledgeBaseConfiguration configuration : model) {
                    view.addKnowledgeBaseConfiguration(configuration.getFullName());
                }
            }
        }).load((Path) placeRequest.getParameter("path", null));
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
        showKBaseForm(name);
    }

    @Override
    public void onAddNewKBase() {
        addNewKBasePopup.show(new AddKBaseCommand() {
            @Override
            public void add(KnowledgeBaseConfiguration knowledgeBaseConfiguration) {
                model.add(knowledgeBaseConfiguration);
                view.addKnowledgeBaseConfiguration(knowledgeBaseConfiguration.getFullName());
                view.selectKBase(knowledgeBaseConfiguration.getFullName());
                showKBaseForm(knowledgeBaseConfiguration.getFullName());
            }
        });
    }

    @Override
    public void onRemoveKBase() {
        if (selectedItemName == null) {
            view.showPleaseSelectAKBaseInfo();
        } else {
            model.remove(selectedItemName);
            view.removeKnowledgeBaseConfiguration(selectedItemName);
            selectedItemName = null;
        }
    }

    private void showKBaseForm(String name) {
        selectedItemName = name;
        view.showForm(model.get(name));
    }
}
