/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.LoggedInUserInfo;
import org.drools.guvnor.client.security.Capabilities;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.dom.client.Style.Unit;

import com.gwtext.client.widgets.QuickTips;


/**
 * This is the main part of the app that lays everything out. 
 */
public class ExplorerLayoutManager {
    /**
     * These are used to decide what to display or not.
     */
    protected static Capabilities capabilities;

    private DockPanel titlePanel;
    private StackLayoutPanel navigationStackLayoutPanel;
    private ExplorerViewCenterPanel centertabbedPanel;
    private DockLayoutPanel mainPanel;


    public ExplorerLayoutManager(LoggedInUserInfo uif, Capabilities caps) {
        QuickTips.init();

        Preferences.INSTANCE.loadPrefs(caps);

        String tok = History.getToken();


        //we use this to decide what to display.
        BookmarkInfo bi = handleHistoryToken(tok);
        ExplorerLayoutManager.capabilities = caps;
                
        
        if (bi.showChrome) {
        	setupTitlePanel(uif);
        }
        setupExplorerViewCenterPanel();
        setupNavigationPanels();      
        setupMainPanel(bi);

        
        //Open default widgets
        if (bi.loadAsset) {
            centertabbedPanel.openAsset(bi.assetId);
        }
        centertabbedPanel.openFind();
    }
    
    /**
     * Create the title bar at the top of the application.
     * 
     * @param LoggedInUserInfo uif
     */
    private void setupTitlePanel(LoggedInUserInfo uif) {  
        titlePanel = new DockPanel();
        titlePanel.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
        titlePanel.add(new HTML("<div class='header'><img src='header_logo.gif' /></div>"), DockPanel.WEST);
        titlePanel.add(uif, DockPanel.EAST);
        titlePanel.setStyleName("header");
        titlePanel.setWidth("100%");
    }   
    
    /**
     * Create the navigation panel for the west area.
     * 
     */
    private void setupNavigationPanels() {  
        navigationStackLayoutPanel = new StackLayoutPanel(Unit.EM);

        //Browse
        DockLayoutPanel browseDockLayoutPanel = new DockLayoutPanel(Unit.EM);
        BrowseTree categoriesTreeItem = new BrowseTree(centertabbedPanel);
        ScrollPanel categoriesTreeItemPanel = new ScrollPanel(categoriesTreeItem.getTree());
        
        if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_CREATE_NEW_ASSET)) {
        	browseDockLayoutPanel.addNorth(RulesNewMenu.getMenu(categoriesTreeItem),2);
        }
        browseDockLayoutPanel.add(categoriesTreeItemPanel);
        
        navigationStackLayoutPanel.add(browseDockLayoutPanel, categoriesTreeItem.getHeaderHTML(), 2);
       
      
        //Knowledge Bases (Packages)
        if (shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {
        	DockLayoutPanel packageDockLayoutPanel = new DockLayoutPanel(Unit.EM);
            final PackagesTree packagesTreeItem = new PackagesTree(centertabbedPanel);
            ScrollPanel packagesTreeItemPanel = new ScrollPanel(packagesTreeItem.getTree());
            
            if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_CREATE_NEW_ASSET)) {
            	packageDockLayoutPanel.addNorth(PackagesNewMenu.getMenu(packagesTreeItem),2);
            }
            packageDockLayoutPanel.add(packagesTreeItemPanel);
            
            navigationStackLayoutPanel.add(packageDockLayoutPanel, packagesTreeItem.getHeaderHTML(), 2);
  
            //lazy loaded to easy startup wait time.
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                	packagesTreeItem.loadPackageList();
                }
            });         
        }

        //QA
        if (shouldShow(Capabilities.SHOW_QA)) {
        	DockLayoutPanel qaDockLayoutPanel = new DockLayoutPanel(Unit.EM);
        	QATree qaTreeItem = new QATree(centertabbedPanel);
            ScrollPanel qaTreeItemPanel = new ScrollPanel(qaTreeItem.getTree());

            qaDockLayoutPanel.add(qaTreeItemPanel);
            
            navigationStackLayoutPanel.add(qaDockLayoutPanel, qaTreeItem.getHeaderHTML(), 2);               	
        }

        //Deployment(Package snapshots)
        if (shouldShow(Capabilities.SHOW_DEPLOYMENT, Capabilities.SHOW_DEPLOYMENT_NEW)) {
        	DockLayoutPanel deploymentDockLayoutPanel = new DockLayoutPanel(Unit.EM);
        	DeploymentTree deploymentTreeItem = new DeploymentTree(centertabbedPanel);
            ScrollPanel deploymentTreeItemPanel = new ScrollPanel(deploymentTreeItem.getTree());
            
            if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_CREATE_NEW_ASSET)) {
            	deploymentDockLayoutPanel.addNorth(DeploymentNewMenu.getMenu(deploymentTreeItem),2);
            }
            
            deploymentDockLayoutPanel.add(deploymentTreeItemPanel);
            
            navigationStackLayoutPanel.add(deploymentDockLayoutPanel, deploymentTreeItem.getHeaderHTML(), 2);   
        }

        //Admin
        if (shouldShow(Capabilities.SHOW_ADMIN)) {
        	DockLayoutPanel adminDockLayoutPanel = new DockLayoutPanel(Unit.EM);
        	AdministrationTree deploymentTreeItem = new AdministrationTree(centertabbedPanel);
            ScrollPanel adminTreeItemPanel = new ScrollPanel(deploymentTreeItem.getTree());

            adminDockLayoutPanel.add(adminTreeItemPanel);
            
            navigationStackLayoutPanel.add(adminDockLayoutPanel, deploymentTreeItem.getHeaderHTML(), 2);   
        }

        //accordion.add(new ProcessServerPanel("Process Server", centertabbedPanel));

    }

    /**
     * Create the explorer view tabbed panel 
     * 
     */
    private void setupExplorerViewCenterPanel() {  
    	centertabbedPanel = new ExplorerViewCenterPanel();
    }   
   
    /**
     * Create the main panel.
     * 
     */
    private void setupMainPanel(BookmarkInfo bi) {        
        mainPanel = new DockLayoutPanel(Unit.EM);        
        
        if (bi.showChrome) {
            mainPanel.addNorth(titlePanel, 4);
        }        
        SplitLayoutPanel centerPanel = new SplitLayoutPanel();        
        centerPanel.addWest(navigationStackLayoutPanel, 192);
        centerPanel.add(centertabbedPanel.getPanel());        
        mainPanel.add(centerPanel);
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

    /**
     * Parse the bookmark/history token (the bit after the "#" in the URL)
     * to work out what we will display.
     */
    static BookmarkInfo handleHistoryToken(String tok) {
        if (tok == null) return new BookmarkInfo();
       BookmarkInfo bi = new BookmarkInfo();
        if (tok.startsWith("asset=")) { //NON-NLS
        	String uuid = null;
        	//URLDecoder is not supported in GWT. We decode  ampersand (&) here by ourself. 
        	if(tok.indexOf("%26nochrome") >= 0) {
        		uuid = tok.substring(6).split("%26nochrome")[0]; //NON-NLS
        	} else {
                uuid = tok.substring(6).split("&nochrome")[0]; //NON-NLS
        	}
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
