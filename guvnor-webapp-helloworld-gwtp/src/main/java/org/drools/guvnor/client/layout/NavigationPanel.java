package org.drools.guvnor.client.layout;

import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.explorer.BrowseTree;
import org.drools.guvnor.client.explorer.KnowledgeTree;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;

import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;

/**
 * Navigation panel for the west area.
 */
public class NavigationPanel extends Composite {
    private Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);

    private StackLayoutPanel layout = new StackLayoutPanel(Unit.EM);

    public NavigationPanel() {
        initWidget(layout);
        addBrowsePanel();
        addModulesPanel();
    }

    private void addBrowsePanel() {
        DockLayoutPanel browseDockLayoutPanel = new DockLayoutPanel(Unit.EM);

        BrowseTree tree = new BrowseTree();
        tree.setStyleName("lhs-Tree");
        ScrollPanel treePanel = new ScrollPanel(tree);
        browseDockLayoutPanel.add(treePanel);

        layout.add(browseDockLayoutPanel,
                Util.getHeaderHTML(images.ruleAsset(), constants.Browse()), 2);
    }

    private void addModulesPanel() {
        DockLayoutPanel browseDockLayoutPanel = new DockLayoutPanel(Unit.EM);

        KnowledgeTree tree = new KnowledgeTree();
        tree.setStyleName("lhs-Tree");
        ScrollPanel treePanel = new ScrollPanel(tree);
        browseDockLayoutPanel.add(treePanel);

        layout.add(browseDockLayoutPanel,
                Util.getHeaderHTML(images.packages(), constants.KnowledgeBases()), 2);
    }

}
