package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.widgets.tree.TreePanel;

/**
 * @author Anton Arhipov
 */
public class QAPanel extends GenericPanel {

    public QAPanel(ExplorerViewCenterPanel tabbedPanel) {
        super("QA", tabbedPanel);
        setIconCls("nav-qa");

        final VerticalPanel qaPanel = new VerticalPanel();
        qaPanel.setWidth("100%");
        TreePanel qaTree = genericExplorerWidget(ExplorerNodeConfig.getQAStructure(centertabbedPanel));
        qaTree.setRootVisible(false);
        qaPanel.add(qaTree);
        add(qaPanel);
    }

}
