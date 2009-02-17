package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.widgets.tree.TreePanel;
import org.drools.guvnor.client.messages.Constants;

/**
 * @author Anton Arhipov
 */
public class QAPanel extends GenericPanel {
    private static Constants constants = ((Constants) GWT.create(Constants.class));

    public QAPanel(ExplorerViewCenterPanel tabbedPanel) {

        super(constants.QA1(), tabbedPanel);
        setIconCls("nav-qa"); //NON-NLS

        final VerticalPanel qaPanel = new VerticalPanel();
        qaPanel.setWidth("100%");
        TreePanel qaTree = genericExplorerWidget(ExplorerNodeConfig.getQAStructure(centertabbedPanel));
        qaTree.setRootVisible(false);
        qaPanel.add(qaTree);
        add(qaPanel);
    }

}
