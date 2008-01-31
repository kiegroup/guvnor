package org.drools.brms.client;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.brms.client.admin.ArchivedAssetManager;
import org.drools.brms.client.admin.BackupManager;
import org.drools.brms.client.admin.CategoryManager;
import org.drools.brms.client.admin.StateManager;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.TabPanelItem;

//import com.google.gwt.user.client.ui.TabPanel;

/**
 * This feature contains the administrative functions of the BRMS.
 */
public class AdminFeature extends JBRMSFeature {

  //  private TabPanel tab;

    public AdminFeature() {
        final TabPanel tp = new TabPanel( "tab-1" );
        tp.setWidth( "100%" );
        tp.setHeight( "100%" );

        TabPanelItem tpCategory = tp.addTab( "tpi1", "<img src='images/category_small.gif'/>Manage categories", false );
        TabPanelItem tpStateManager = tp.addTab( "tpi2", "<img src='images/status_small.gif'/>Manage states", false );
        TabPanelItem tpArchivedAssetManager = tp.addTab( "tpi3", "<img src='images/backup_small.gif'/>Manage Archived Assets", false );
        TabPanelItem tpBackupManager = tp.addTab( "tpi4", "<img src='images/backup_small.gif'/>Import Export", false );

        VerticalPanel vp1 = new VerticalPanel();
        VerticalPanel vp2 = new VerticalPanel();
        VerticalPanel vp3 = new VerticalPanel();
        VerticalPanel vp4 = new VerticalPanel();

        vp1.add( new CategoryManager() );
        vp1.setSpacing( 15 );
        vp2.add( new StateManager() );
        vp2.setSpacing( 15 );
        vp3.add( new ArchivedAssetManager() );
        vp3.setSpacing( 15 );
        vp4.add( new BackupManager() );
        vp4.setSpacing( 15 );
        
        tpCategory.setContent( vp1 );
        tpStateManager.setContent( vp2 );
        tpArchivedAssetManager.setContent( vp3 );
        tpBackupManager.setContent( vp4 );
        
        tp.activate( 0 );

        initWidget( tp );
        


    //  initWidget( tab );

    }

    public static ComponentInfo init() {
        return new ComponentInfo( "Admin",
                                  "Administer the repository" ) {
            public JBRMSFeature createInstance() {
                return new AdminFeature();
            }

        };
    }

    public void onShow() {
    }
}