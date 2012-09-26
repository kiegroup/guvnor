package org.drools.guvnor.client.explorer.navigation.admin;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.explorer.navigation.admin.widget.ArchivedAssetManager;
import org.drools.guvnor.client.explorer.navigation.admin.widget.BackupManager;
import org.drools.guvnor.client.explorer.navigation.admin.widget.CategoryManager;
import org.drools.guvnor.client.explorer.navigation.admin.widget.EventLogPresenter;
import org.drools.guvnor.client.explorer.navigation.admin.widget.EventLogViewImpl;
import org.drools.guvnor.client.explorer.navigation.admin.widget.PermissionViewer;
import org.drools.guvnor.client.explorer.navigation.admin.widget.RepoConfigManager;
import org.drools.guvnor.client.explorer.navigation.admin.widget.StateManager;
import org.drools.guvnor.client.explorer.navigation.admin.widget.WorkspaceManager;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.util.Activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.PopupPanel;

public class ManagerActivity extends Activity {

    private ConstantsCore constants = GWT.create( ConstantsCore.class );

    private final int           id;
    private final ClientFactory clientFactory;

    public ManagerActivity(int id,
                           ClientFactory clientFactory) {
        this.id = id;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptItem tabbedPanel,
                      EventBus eventBus) {
        openAdministrationSelection( tabbedPanel,
                                     id,
                                     eventBus );
    }

    public void openAdministrationSelection(final AcceptItem tabbedPanel,
                                            final int id,
                                            final EventBus eventBus) {

        switch ( id ) {
            case 0 :
                tabbedPanel.add( constants.CategoryManager(),
                                 new CategoryManager() );
                break;
            case 1 :
//                tabbedPanel.add( constants.ArchivedManager(),
//                                 new ArchivedAssetManager( clientFactory,
//                                                           eventBus ) );
                break;

            case 2 :
                tabbedPanel.add( constants.StateManager(),
                                 new StateManager() );
                break;
            case 3 :
                tabbedPanel.add( constants.ImportExport(),
                                 new BackupManager() );
                break;

            case 4 :
                EventLogPresenter.EventLogView eventLogView = new EventLogViewImpl();
                RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);
//                new EventLogPresenter( repositoryService,
//                                       eventLogView );
                tabbedPanel.add( constants.EventLog(),
                                 eventLogView );
                break;
            case 5 :
                tabbedPanel.add( constants.UserPermissionMappings(),
                                 new PermissionViewer() );
                break;
            case 6 :
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

                //When the popup is closed signal closure of place
                aboutPop.addCloseHandler( new CloseHandler<PopupPanel>() {

                    public void onClose(CloseEvent<PopupPanel> event) {
                        ManagerPlace place = new ManagerPlace( id );
                        eventBus.fireEvent( new ClosePlaceEvent( place ) );
                    }

                } );
                break;
/*
            case 7 :
                tabbedPanel.add( constants.RulesVerificationManager(),
                                 new RuleVerifierManager() );
                break;*/
            case 8 :
                tabbedPanel.add( constants.RepositoryConfig(),
                                 new RepoConfigManager() );
                break;
            case 9 :
                tabbedPanel.add( constants.Workspaces(),
                                 new WorkspaceManager() );
                break;
        }
    }
}
