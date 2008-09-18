package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;

public class RHQPanel extends GenericPanel {

	protected RHQPanel(String title, final ExplorerViewCenterPanel centertabbedPanel) {
		super(title, centertabbedPanel);


		TreeNode tn = new TreeNode("JON");
		TreeNode dashBoard = new TreeNode("Dashboard");
		dashBoard.addListener(new TreeNodeListenerAdapter() {
			@Override
			public void onClick(Node node, EventObject e) {
				if (!centertabbedPanel.showIfOpen("dashboard")) {
					centertabbedPanel.addTab("Dashboard", true, getDashboard(), "dashboard");
				}
			}
		});

		tn.appendChild(dashBoard);
		add(genericExplorerWidget(tn));
		tn.expand();
	}

	private Widget getDashboard() {
		//Frame fm = new Frame("http://jon04.qa.atl.jboss.com:27080/Login.do");
		Frame fm = new Frame("http://www.smh.com.au");
		fm.setWidth("100%");
		fm.setHeight("100%");
		return fm;
	}

}
