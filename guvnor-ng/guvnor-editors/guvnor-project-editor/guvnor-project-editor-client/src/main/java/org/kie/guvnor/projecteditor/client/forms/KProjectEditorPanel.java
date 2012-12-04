package org.kie.guvnor.projecteditor.client.forms;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.projecteditor.client.widgets.ListFormComboPanel;
import org.kie.guvnor.projecteditor.client.widgets.NamePopup;
import org.kie.guvnor.projecteditor.model.KBaseModel;
import org.kie.guvnor.projecteditor.model.KProjectModel;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;

import javax.inject.Inject;

public class KProjectEditorPanel
        extends ListFormComboPanel<KBaseModel> {

    private final Caller<ProjectEditorService> projectEditorServiceCaller;

    private KProjectModel model;
    private Path path;

    private final KProjectEditorPanelView view;

    @Inject
    public KProjectEditorPanel(Caller<ProjectEditorService> projectEditorServiceCaller,
                               KBaseForm form,
                               NamePopup namePopup,
                               KProjectEditorPanelView view) {
        super(view, form, namePopup);

        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.view = view;
    }

    public void init(Path path) {
        this.path = path;

        projectEditorServiceCaller.call(new RemoteCallback<KProjectModel>() {
            @Override
            public void callback(KProjectModel model) {

                KProjectEditorPanel.this.model = model;

                setItems(model.getKBases());
            }
        }).loadKProject(path);
    }

    @Override
    protected KBaseModel createNew(String name) {
        KBaseModel model = new KBaseModel();
        model.setName(name);
        return model;
    }

    public void save() {
        projectEditorServiceCaller.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                view.showSaveSuccessful();
            }
        }).saveKProject(path, model);
    }
}
