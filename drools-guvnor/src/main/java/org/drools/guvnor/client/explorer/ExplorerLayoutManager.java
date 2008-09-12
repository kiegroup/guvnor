package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.QuickTips;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import org.drools.guvnor.client.LoggedInUserInfo;
import org.drools.guvnor.client.security.Capabilities;

public class ExplorerLayoutManager {

    protected static Capabilities capabilities;

    private ExplorerViewCenterPanel centertabbedPanel;

    private Panel northPanel;
    private Panel accordion;

    public ExplorerLayoutManager(LoggedInUserInfo uif, Capabilities caps) {
        Field.setMsgTarget("side");
        QuickTips.init();

        centertabbedPanel = new ExplorerViewCenterPanel();

        //north
        northPanel = new Panel();
        DockPanel dock = new DockPanel();
        dock.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
        dock.add(new HTML("<div class='header'><img src='header_logo.gif' /></div>"), DockPanel.WEST);
        dock.add(uif, DockPanel.EAST);
        dock.setStyleName("header");
        dock.setWidth("100%");

        ExplorerLayoutManager.capabilities = caps;

        northPanel.add(dock);
        northPanel.setHeight(50);

        // add a navigation for the west area
        accordion = new Panel();
        accordion.setLayout(new AccordionLayout(true));

        createNavigationPanels();

        centertabbedPanel.openFind();

    }

    private void createNavigationPanels() {
        accordion.add(new CategoriesPanel(centertabbedPanel));

        Panel tpPackageExplorer = new PackagesPanel(centertabbedPanel);
        if (shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {
            accordion.add(tpPackageExplorer);
        }

        Panel tpQA = new QAPanel(centertabbedPanel);
        if (shouldShow(Capabilities.SHOW_QA)) {
            accordion.add(tpQA);
        }

        Panel tpDeployment = new DeploymentPanel(centertabbedPanel);
        if (shouldShow(Capabilities.SHOW_DEPLOYMENT, Capabilities.SHOW_DEPLOYMENT_NEW)) {
            accordion.add(tpDeployment);
        }

        Panel tpAdmin = new AdministrationPanel(centertabbedPanel);
        if (shouldShow(Capabilities.SHOW_ADMIN)) {
            accordion.add(tpAdmin);
        }


    }

    public Panel getBaseLayout() {
        Panel mainPanel = new Panel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMargins(0, 0, 0, 0);

        BorderLayoutData northLayoutData = new BorderLayoutData(RegionPosition.NORTH);
        northLayoutData.setMargins(0, 0, 0, 0);

        BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
        centerLayoutData.setMargins(new Margins(5, 0, 5, 5));

        Panel centerPanelWrappper = new Panel();
        centerPanelWrappper.setLayout(new FitLayout());
        centerPanelWrappper.setBorder(false);
        centerPanelWrappper.setBodyBorder(false);

        //setup the west regions layout properties
        BorderLayoutData westLayoutData = new BorderLayoutData(RegionPosition.WEST);
        westLayoutData.setMargins(new Margins(5, 5, 0, 5));
        westLayoutData.setCMargins(new Margins(5, 5, 5, 5));
        westLayoutData.setMinSize(155);
        westLayoutData.setMaxSize(350);
        westLayoutData.setSplit(true);

        //create the west panel and add it to the main panel applying the west region layout properties
        Panel westPanel = new Panel();
        westPanel.setId("side-nav");
        westPanel.setTitle("Navigate Guvnor");
        westPanel.setLayout(new FitLayout());
        westPanel.setWidth(210);
        westPanel.setCollapsible(true);//MN createWestPanel();
        westPanel.add(accordion);
        mainPanel.add(westPanel, westLayoutData);

        centerPanelWrappper.add(centertabbedPanel.getPanel());

        mainPanel.add(centerPanelWrappper, centerLayoutData);
        mainPanel.add(northPanel, northLayoutData);

        return mainPanel;
    }

    public static boolean shouldShow(Integer... capability) {
        for (Integer cap : capability) {
           if (capabilities.list.contains(cap)) {
                return true;
            }
        }
        return false;
    }

    public static TreePanel genericExplorerWidget(final TreeNode childNode) {
        // create and configure the main tree
        final TreePanel menuTree = new TreePanel();
        menuTree.setAnimate(true);
        menuTree.setEnableDD(true);
        menuTree.setContainerScroll(true);
        menuTree.setRootVisible(true);
        menuTree.setBodyBorder(false);
        menuTree.setBorder(false);
        menuTree.setRootNode(childNode);
        return menuTree;
    }


}
