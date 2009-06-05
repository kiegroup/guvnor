package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.LoggedInUserInfo;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.security.Capabilities;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.core.client.GWT;
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


/**
 * This is the main part of the app that lays everything out. 
 */
public class ExplorerLayoutManager {

    /**
     * These are used to decide what to display or not.
     */
    protected static Capabilities capabilities;

    private ExplorerViewCenterPanel centertabbedPanel;

    private Panel northPanel;
    private Panel accordion;
    private Panel mainPanel;


    public ExplorerLayoutManager(LoggedInUserInfo uif, Capabilities caps) {
        Field.setMsgTarget("side");
        QuickTips.init();

        Preferences.INSTANCE.loadPrefs(caps);

        String tok = History.getToken();

        centertabbedPanel = new ExplorerViewCenterPanel();

        /**
         * we use this to decide what to display.
         */
        BookmarkInfo bi = handleHistoryToken(tok);


        ExplorerLayoutManager.capabilities = caps;
        
        if (bi.showChrome) {
 
        //north
            northPanel = new Panel();
            DockPanel dock = new DockPanel();
            dock.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
            dock.add(new HTML("<div class='header'><img src='header_logo.gif' /></div>"), DockPanel.WEST);
            dock.add(uif, DockPanel.EAST);
            dock.setStyleName("header");
            dock.setWidth("100%");



            northPanel.add(dock);
            northPanel.setHeight(50);

            // add a navigation for the west area
            accordion = new Panel();
            accordion.setLayout(new AccordionLayout(true));

            createNavigationPanels();

            centertabbedPanel.openFind();


        }

        setUpMain(bi);

        if (bi.loadAsset) {
            centertabbedPanel.openAsset(bi.assetId);
        }

    }

    private void createNavigationPanels() {


        accordion.add(new CategoriesPanel(centertabbedPanel));



        if (shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {
            final PackagesPanel pp = new PackagesPanel(centertabbedPanel);
            accordion.add(pp);
            /*
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    pp.loadPackageList();
                }
            });
            */

        }


        if (shouldShow(Capabilities.SHOW_QA)) {
            accordion.add(new QAPanel(centertabbedPanel));
        }

        if (shouldShow(Capabilities.SHOW_DEPLOYMENT, Capabilities.SHOW_DEPLOYMENT_NEW)) {
            accordion.add(new DeploymentPanel(centertabbedPanel));
        }

        if (shouldShow(Capabilities.SHOW_ADMIN)) {
            accordion.add(new AdministrationPanel(centertabbedPanel));
        }

        //accordion.add(new ProcessServerPanel("Process Server", centertabbedPanel));

    }

    private void setUpMain(BookmarkInfo bi) {

        mainPanel = new Panel();
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

        if (bi.showChrome) {
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
            westPanel.setTitle(((Constants) GWT.create(Constants.class)).Navigate());
            westPanel.setLayout(new FitLayout());
            westPanel.setWidth(210);
            westPanel.setCollapsible(true);//MN createWestPanel();
            westPanel.add(accordion);
            mainPanel.add(westPanel, westLayoutData);
        }

        centerPanelWrappper.add(centertabbedPanel.getPanel());

        mainPanel.add(centerPanelWrappper, centerLayoutData);
        if (bi.showChrome) {
            mainPanel.add(northPanel, northLayoutData);
        }

    }


    public Panel getBaseLayout() {
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


    /**
     * Parse the bookmark/history token (the bit after the "#" in the URL)
     * to work out what we will display.
     */
    static BookmarkInfo handleHistoryToken(String tok) {
        if (tok == null) return new BookmarkInfo();
       BookmarkInfo bi = new BookmarkInfo();
        if (tok.startsWith("asset=")) { //NON-NLS
            String uuid = tok.substring(6).split("&nochrome")[0]; //NON-NLS
            bi.loadAsset = true;
            bi.assetId = uuid;
        }

        if (tok.contains("nochrome") || tok.contains("nochrome==true")) {
            bi.showChrome = false;
        }

       return bi;
    }

    public static class BookmarkInfo {
        String assetId;
        boolean showChrome = true;
        boolean loadAsset = false;
    }


}
