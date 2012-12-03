package org.kie.guvnor.projecteditor.client.forms;


import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.projecteditor.client.MessageService;
import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.guvnor.projecteditor.client.widgets.ListFormComboPanel;
import org.kie.guvnor.projecteditor.client.widgets.NamePopup;
import org.kie.guvnor.projecteditor.model.KBaseModel;
import org.kie.guvnor.projecteditor.model.KProjectModel;
import org.kie.guvnor.projecteditor.model.builder.Messages;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchEditor(identifier = "projectEditorScreen", fileTypes = "xml")
public class ProjectEditorScreen
        extends ListFormComboPanel<KBaseModel> {

    private final Caller<ProjectEditorService> projectEditorServiceCaller;

    private KProjectModel model;
    private Path path;

    private final MessageService messageService;
    private final ProjectEditorScreenView view;

    @Inject
    public ProjectEditorScreen(Caller<ProjectEditorService> projectEditorServiceCaller,
                               MessageService messageService,
                               KBaseForm form,
                               NamePopup namePopup,
                               ProjectEditorScreenView view) {
        super(view, form, namePopup);

        this.messageService = messageService;
        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.view = view;
    }

    @OnStart
    public void init(Path path) {
        this.path = path;

        projectEditorServiceCaller.call(new RemoteCallback<KProjectModel>() {
            @Override
            public void callback(KProjectModel model) {

                ProjectEditorScreen.this.model = model;

                setItems(model.getKBases());
            }
        }).load(path);
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return super.asWidget();
    }

    @Override
    protected KBaseModel createNew(String name) {
        KBaseModel model = new KBaseModel();
        model.setName(name);
        return model;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorConstants.INSTANCE.ProjectModel();
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        DefaultMenuBar toolBar = new DefaultMenuBar();

        toolBar.addItem(newSaveMenuItem());
        toolBar.addItem(newBuildMenuItem());

        return toolBar;
    }

    private MenuItem newSaveMenuItem() {
        return new DefaultMenuItemCommand(view.getSaveMenuItemText(),
                new Command() {
                    @Override
                    public void execute() {
                        projectEditorServiceCaller.call(new RemoteCallback<Void>() {
                            @Override
                            public void callback(Void v) {
                                view.showSaveSuccessful();
                            }
                        }).save(path, model);
                    }
                });
    }

    private MenuItem newBuildMenuItem() {
        return new DefaultMenuItemCommand(view.getBuildMenuItemText(),
                new Command() {
                    @Override
                    public void execute() {
                        // TODO: Check if the latest changes are saved before building. -Rikkola-
                        projectEditorServiceCaller.call(new RemoteCallback<Messages>() {
                            @Override
                            public void callback(Messages messages) {
                                if (messages.isEmpty()) {
                                    view.showBuildSuccessful();
                                }

                                messageService.addMessages(messages);
                            }
                        }).build(path);
                    }
                });
    }
}
