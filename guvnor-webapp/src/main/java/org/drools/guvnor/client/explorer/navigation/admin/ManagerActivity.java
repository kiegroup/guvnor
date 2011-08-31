package org.drools.guvnor.client.explorer.navigation.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Frame;
import org.drools.guvnor.client.admin.ArchivedAssetManager;
import org.drools.guvnor.client.admin.BackupManager;
import org.drools.guvnor.client.admin.CategoryManager;
import org.drools.guvnor.client.admin.EventLogPresenter;
import org.drools.guvnor.client.admin.EventLogViewImpl;
import org.drools.guvnor.client.admin.PermissionViewer;
import org.drools.guvnor.client.admin.RepoConfigManager;
import org.drools.guvnor.client.admin.RuleVerifierManager;
import org.drools.guvnor.client.admin.StateManager;
import org.drools.guvnor.client.admin.WorkspaceManager;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.util.Activity;

public class ManagerActivity extends Activity {

    private Constants constants = GWT.create(Constants.class);

    private final int id;
    private final ClientFactory clientFactory;

    public ManagerActivity(int id, ClientFactory clientFactory) {
        this.id = id;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptItem tabbedPanel, EventBus eventBus) {
        openAdministrationSelection(tabbedPanel, id);
    }

    public void openAdministrationSelection(AcceptItem tabbedPanel, int id) {

        switch (id) {
            case 0:
                tabbedPanel.add(constants.CategoryManager(),
                        new CategoryManager());
                break;
            case 1:
                tabbedPanel.add(constants.ArchivedManager(),
                        new ArchivedAssetManager(clientFactory));
                break;

            case 2:
                tabbedPanel.add(constants.StateManager(),
                        new StateManager());
                break;
            case 3:
                tabbedPanel.add(constants.ImportExport(),
                        new BackupManager());
                break;

            case 4:
                EventLogPresenter.EventLogView eventLogView = new EventLogViewImpl();
                RepositoryServiceAsync repositoryService = RepositoryServiceFactory.getService();
                new EventLogPresenter(repositoryService,
                        eventLogView);
                tabbedPanel.add(constants.EventLog(),
                        eventLogView);
                break;
            case 5:
                tabbedPanel.add(constants.UserPermissionMappings(),
                        new PermissionViewer());
                break;
            case 6:
                // TODO: Not a manager -Rikkola-
                Frame aboutInfoFrame = new Frame("../AboutInfo.html"); // NON-NLS

                FormStylePopup aboutPop = new FormStylePopup();
                aboutPop.setWidth(600 + "px");
                aboutPop.setTitle(constants.About());
                String hhurl = GWT.getModuleBaseURL()
                        + "webdav";
                aboutPop.addAttribute(constants.WebDAVURL()
                        + ":",
                        new SmallLabel("<b>"
                                + hhurl
                                + "</b>"));
                aboutPop.addAttribute(constants.Version()
                        + ":",
                        aboutInfoFrame);
                aboutPop.show();
                break;

            case 7:
                tabbedPanel.add(constants.RulesVerificationManager(),
                        new RuleVerifierManager());
                break;
            case 8:
                tabbedPanel.add(constants.RepositoryConfig(),
                        new RepoConfigManager());
                break;
            case 9:
                tabbedPanel.add(constants.Workspaces(),
                        new WorkspaceManager());
                break;
        }
    }
}
