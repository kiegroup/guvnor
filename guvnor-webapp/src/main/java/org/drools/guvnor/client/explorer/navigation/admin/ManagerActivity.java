package org.drools.guvnor.client.explorer.navigation.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Frame;
import org.drools.guvnor.client.admin.*;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.explorer.AcceptTabItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.util.Activity;

public class ManagerActivity extends Activity {

    private Constants constants = GWT.create( Constants.class );

    private final int id;
    private final ClientFactory clientFactory;

    public ManagerActivity(int id, ClientFactory clientFactory) {
        this.id = id;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptTabItem tabbedPanel, EventBus eventBus) {
        openAdministrationSelection( tabbedPanel, id );
    }

    public void openAdministrationSelection(AcceptTabItem tabbedPanel, int id) {

        switch (id) {
            case 0:
                tabbedPanel.addTab( constants.CategoryManager(),
                        new CategoryManager() );
                break;
            case 1:
                tabbedPanel.addTab( constants.ArchivedManager(),
                        new ArchivedAssetManager( clientFactory ) );
                break;

            case 2:
                tabbedPanel.addTab( constants.StateManager(),
                        new StateManager() );
                break;
            case 3:
                tabbedPanel.addTab( constants.ImportExport(),
                        new BackupManager() );
                break;

            case 4:
                EventLogPresenter.EventLogView eventLogView = new EventLogViewImpl();
                RepositoryServiceAsync repositoryService = RepositoryServiceFactory.getService();
                new EventLogPresenter( repositoryService,
                        eventLogView );
                tabbedPanel.addTab( constants.EventLog(),
                        eventLogView );
                break;
            case 5:
                tabbedPanel.addTab( constants.UserPermissionMappings(),
                        new PermissionViewer() );
                break;
            case 6:
                // TODO: Not a manager -Rikkola-
                Frame aboutInfoFrame = new Frame( "../AboutInfo.html" ); // NON-NLS

                FormStylePopup aboutPop = new FormStylePopup();
                aboutPop.setWidth( 600 + "px" );
                aboutPop.setTitle( constants.About() );
                String hhurl = GWT.getModuleBaseURL()
                        + "webdav";
                aboutPop.addAttribute( constants.WebDAVURL()
                        + ":",
                        new SmallLabel( "<b>"
                                + hhurl
                                + "</b>" ) );
                aboutPop.addAttribute( constants.Version()
                        + ":",
                        aboutInfoFrame );
                aboutPop.show();
                break;

            case 7:
                tabbedPanel.addTab( constants.RulesVerificationManager(),
                        new RuleVerifierManager() );
                break;
            case 8:
                tabbedPanel.addTab( constants.RepositoryConfig(),
                        new RepoConfigManager() );
                break;
            case 9:
                tabbedPanel.addTab( constants.Workspaces(),
                        new WorkspaceManager() );
                break;
            case 10:
                openPerspectivesManager( tabbedPanel );
                break;
        }
    }


    private void openPerspectivesManager(AcceptTabItem tabbedPanel) {
        PerspectivesManagerView perspectivesManagerView = new PerspectivesManagerViewImpl();
        new PerspectivesManager( GWT.<ConfigurationServiceAsync>create( ConfigurationService.class ),
                perspectivesManagerView );
        tabbedPanel.addTab( constants.PerspectivesConfiguration(),
                perspectivesManagerView );
    }
}
