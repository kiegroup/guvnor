package org.drools.guvnor.client.explorer;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;

/**
 * @author Michael Neale
 */
public class ProcessServerPanel extends GenericPanel {

    protected ProcessServerPanel(String title, final ExplorerViewCenterPanel centertabbedPanel) {
        super(title, centertabbedPanel);

        TreeNode root = new TreeNode("");


        Map<String, String> pages = new HashMap<String, String>();
        pages.put("Processes", 
    		"http://localhost:8080/gwt-console/org.jboss.bpm.console.Application/" +
    		"Application.html#showEditor=org.jboss.bpm.console.client.process.ProcessEditor");
        pages.put("Tasks", 
    		"http://localhost:8080/gwt-console/org.jboss.bpm.console.Application/" +
    		"Application.html#showEditor=org.jboss.bpm.console.client.task.TaskEditor");
        pages.put("Reports", 
    		"http://localhost:8080/gwt-console/org.jboss.bpm.console.Application/" +
    		"Application.html#showEditor=org.jboss.bpm.console.client.report.ReportEditor");
        for (Map.Entry<String, String> e : pages.entrySet()) {
            final TreeNode tn = new TreeNode(e.getKey());
            tn.setId(e.getKey());
            tn.setAttribute("url", e.getValue());
            root.appendChild(tn);
            tn.addListener(new TreeNodeListenerAdapter() {
                @Override
                public void onClick(Node node, EventObject eventObject) {
                  if (!centertabbedPanel.showIfOpen(tn.getId())) {
                      centertabbedPanel.addTab(tn.getText(), true, openEmbedded(tn.getAttribute("url")), tn.getId());
                  }
                }
            });

        }



        TreePanel tp = new TreePanel();
        tp.setRootNode(root);
        add(tp);

    }

    private Widget openEmbedded(String url) {
        return new Frame(url);
    }
}
