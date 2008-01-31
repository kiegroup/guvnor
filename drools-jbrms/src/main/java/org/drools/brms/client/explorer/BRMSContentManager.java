package org.drools.brms.client.explorer;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.UpdateManager;
import com.gwtext.client.widgets.layout.ContentPanel;
import com.gwtext.client.widgets.layout.event.ContentPanelListenerAdapter;
import com.gwtext.client.widgets.tree.TreeNode;

public abstract class BRMSContentManager {

	private boolean init = false;
	protected ContentPanel[] panels;

	public ContentPanel[] getPanels() {
		if (!init) {
			init = true;
			setup();
		}
		return panels;
	}

	public abstract void setup();

	protected VerticalPanel createPanel() {
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(15);
		return vp;
	}

	protected ContentPanel getUrlContentPanel(String label, String sourceUrl) {
		ContentPanel panel = new ContentPanel(Ext.generateId(), label);

		final UpdateManager updateManager = panel.getUpdateManager();
		updateManager.setDefaultUrl(sourceUrl);
		updateManager.setLoadScripts(true);
		updateManager.setDisableCaching(false);

		panel.addContentPanelListener(new ContentPanelListenerAdapter() {
			public void onActivate(final ContentPanel cp) {
				Timer t = new Timer() {
					public void run() {
						if (cp.getEl().isVisible()) {
							updateManager.refresh();
							cp.purgeListeners();
						}
					}
				};
				t.schedule(1000);
			}
		});
		return panel;
	}

	public static String getScreenName(TreeNode node, String name) {
		TreeNode parentNode = (TreeNode) node.getParentNode();
		return (parentNode == null || parentNode.getParentNode() == null) ? name
				: getScreenName(parentNode, parentNode.getText() + ">" + name);
	}

}