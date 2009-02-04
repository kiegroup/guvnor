package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.admin.ArchivedAssetManager;
import org.drools.guvnor.client.admin.BackupManager;
import org.drools.guvnor.client.admin.CategoryManager;
import org.drools.guvnor.client.admin.LogViewer;
import org.drools.guvnor.client.admin.PermissionViewer;
import org.drools.guvnor.client.admin.StateManager;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author Anton Arhipov
 */
public class AdministrationPanel extends GenericPanel {
    private static Constants constants = GWT.create(Constants.class);

    public AdministrationPanel(ExplorerViewCenterPanel tabbedPanel) {
		super(constants.Administration(), tabbedPanel);
		setIconCls("nav-admin"); //NON-NLS

		TreePanel adminTree = basicTreeStructure(ExplorerNodeConfig
				.getAdminStructure(), new TreePanelListenerAdapter() {
			public void onClick(TreeNode self, EventObject e) {

				int id = Integer.parseInt(self.getAttribute("id"));
				switch (id) {
				case 0:
					if (!centertabbedPanel.showIfOpen("catman")) //NON-NLS
						centertabbedPanel.addTab(constants.CategoryManager(), true,
								new CategoryManager(), "catman"); //NON-NLS
					break;
				case 1:
					if (!centertabbedPanel.showIfOpen("archman"))  //NON-NLS
						centertabbedPanel.addTab(constants.ArchivedManager(), true,
								new ArchivedAssetManager(centertabbedPanel),
								"archman");      //NON-NLS
					break;

				case 2:
					if (!centertabbedPanel.showIfOpen("stateman")) //NON-NLS
						centertabbedPanel.addTab(constants.StateManager(), true,
								new StateManager(), "stateman");
					break;
				case 3:
					if (!centertabbedPanel.showIfOpen("bakman"))
						centertabbedPanel.addTab(constants.ImportExport(), true,
								new BackupManager(), "bakman");
					break;

				case 4:
					if (!centertabbedPanel.showIfOpen("errorLog"))
						centertabbedPanel.addTab(constants.EventLog(), true,
								new LogViewer(), "errorLog");
					break;
				case 5:
					if (!centertabbedPanel.showIfOpen("securityPermissions"))
						centertabbedPanel.addTab(constants.UserPermissionMappings(),
								true, new PermissionViewer(),
								"securityPermissions");
					break;
				case 6:
					Frame aboutFrame = new Frame("version.txt");  //NON-NLS

					FormStylePopup aboutPop = new FormStylePopup();
                    aboutPop.setWidth(600);
					aboutPop.setTitle(constants.About());
					String hhurl = GWT.getModuleBaseURL() + "webdav";
					aboutPop.addAttribute(constants.WebDAVURL(), new SmallLabel("<b>"
							+ hhurl + "</b>"));
					aboutPop.addAttribute(constants.Version() + ":", aboutFrame);
					aboutPop.show();
					break;

				}
			}
		});
		adminTree.setRootVisible(false);

		VerticalPanel adminPanel = new VerticalPanel();
		adminPanel.add(adminTree);
		adminPanel.setWidth("100%");
		add(adminPanel);
	}

}
