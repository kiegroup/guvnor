package org.kie.guvnor.editors.projecteditor.client.forms;


import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.editors.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.guvnor.editors.projecteditor.client.widgets.ListFormComboPanel;
import org.kie.guvnor.editors.projecteditor.client.widgets.ListFormComboPanelView;
import org.kie.guvnor.editors.projecteditor.client.widgets.NamePopup;
import org.kie.guvnor.editors.projecteditor.shared.model.KBaseModel;
import org.kie.guvnor.editors.projecteditor.shared.model.KProjectModel;
import org.kie.guvnor.editors.projecteditor.shared.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@WorkbenchEditor(identifier = "projectEditorScreen", fileTypes = "xml")
public class ProjectEditorScreen
        extends ListFormComboPanel<KBaseModel> {

    private final Caller<ProjectEditorService> projectEditorServiceCaller;

    private KProjectModel model;
    private Path path;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    public ProjectEditorScreen(Caller<ProjectEditorService> projectEditorServiceCaller,
                               KBaseForm form,
                               NamePopup namePopup,
                               ListFormComboPanelView view) {
        super(view, form, namePopup);

        this.projectEditorServiceCaller = projectEditorServiceCaller;
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

        toolBar.addItem(
                new DefaultMenuItemCommand(
                        ProjectEditorConstants.INSTANCE.Save(),
                        new Command() {
                            @Override
                            public void execute() {
                                projectEditorServiceCaller.call(new RemoteCallback<Void>() {
                                    @Override
                                    public void callback(Void v) {
                                        notification.fire(new NotificationEvent( ProjectEditorConstants.INSTANCE.SaveSuccessful()));
                                    }
                                }).save(path, model);
                            }
                        }));

        return toolBar;
    }
}
