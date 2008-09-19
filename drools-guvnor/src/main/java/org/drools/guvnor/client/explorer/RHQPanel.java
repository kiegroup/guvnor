package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;

/** This is experimental code to help flush out some requirements */
public class RHQPanel extends GenericPanel {

	protected RHQPanel(String title, final ExplorerViewCenterPanel centertabbedPanel) {
		super(title, centertabbedPanel);
		setIconCls("nav-categories");

		TreeNode tn = new TreeNode("JON");
		tn.appendChild(addNode("Dashboard", "images/package_build.gif", centertabbedPanel, getBrowse()));

		TreeNode br = addNode("Browse resources","images/note.gif", centertabbedPanel, new Image("rhq/browse_resources.png"));
		tn.appendChild(br);
		br.appendChild(addNode("Platforms", null, centertabbedPanel, new Image("rhq/monitor.png")));
		br.appendChild(addNode("Servers", null, centertabbedPanel, getBrowse()));
		br.appendChild(addNode("Services", null, centertabbedPanel, getBrowse()));
		br.appendChild(addNode("Compatible groups", null, centertabbedPanel, getBrowse()));
		br.appendChild(addNode("Mixed groups", null, centertabbedPanel, getBrowse()));
		br.appendChild(addNode("Group definitions", null, centertabbedPanel, getBrowse()));

		tn.appendChild(addNode("Help", "images/topic.gif", centertabbedPanel, getBrowse()));


		add(genericExplorerWidget(tn));
		tn.expand();
	}

	private TreeNode addNode(final String title, String image, final ExplorerViewCenterPanel centertabbedPanel, final Widget content) {
		TreeNode browse = new TreeNode(title);
		if (image != null) browse.setIcon(image);
		browse.addListener(new TreeNodeListenerAdapter() {
			@Override

			public void onClick(Node node, EventObject e) {
				if (!centertabbedPanel.showIfOpen(title)) {
					centertabbedPanel.addTab(title, true, content, title);
				}
			}
		});
		return browse;
	}

	private Widget getBrowse() {
		//Frame fm = new Frame("http://jon04.qa.atl.jboss.com:27080/Login.do");
		Frame fm = new Frame("rhq/JON.html");
		fm.setWidth("100%");
		fm.setHeight("100%");
		return fm;
	}

}
