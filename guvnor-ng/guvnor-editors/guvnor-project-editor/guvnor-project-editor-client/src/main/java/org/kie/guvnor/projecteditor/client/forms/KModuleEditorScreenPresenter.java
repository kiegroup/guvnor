package org.kie.guvnor.projecteditor.client.forms;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilder;
import org.kie.guvnor.projecteditor.client.KModuleResourceType;
import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.shared.mvp.PlaceRequest;

@WorkbenchEditor(identifier = "kmoduleScreen", supportedTypes = { KModuleResourceType.class })
public class KModuleEditorScreenPresenter {

    private       boolean             isReadOnly;
    private       Path                path;
    private final KModuleEditorPanel  kModuleEditorPanel;
    private       MenuBar             menuBar;
    private final ResourceMenuBuilder menuBuilder;

    @Inject
    public KModuleEditorScreenPresenter( KModuleEditorPanel kModuleEditorPanel,
                                         ResourceMenuBuilder menuBuilder ) {
        this.kModuleEditorPanel = kModuleEditorPanel;
        this.menuBuilder = menuBuilder;
    }

    @OnStart
    public void init( final Path path,
                      final PlaceRequest request ) {
        this.path = path;
        this.isReadOnly = request.getParameter( "readOnly", null ) == null ? false : true;

        kModuleEditorPanel.init( path, isReadOnly );

        fillMenuBar();
    }

    private void fillMenuBar() {
        if ( isReadOnly ) {
            menuBar = menuBuilder.addFileMenu().addRestoreVersion( path ).build();
        }
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        return menuBar;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorConstants.INSTANCE.KModuleDotXml();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return kModuleEditorPanel.asWidget();
    }

}



