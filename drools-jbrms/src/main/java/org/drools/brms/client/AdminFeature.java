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

import com.google.gwt.user.client.ui.TabPanel;

/**
 * This feature contains the administrative functions of the BRMS.
 */
public class AdminFeature extends JBRMSFeature {

  private TabPanel tab;

  public AdminFeature() {
      tab = new TabPanel();
      tab.setWidth( "100%" );
      tab.setHeight( "100%" );

      tab.add( new CategoryManager(), "<img src='images/category_small.gif'/>Manage categories", true );
      tab.add( new StateManager(),  "<img src='images/status_small.gif'/>Manage states", true );
      tab.add( new ArchivedAssetManager(), "<img src='images/backup_small.gif'/>Manage Archived Assets", true );
      tab.add( new BackupManager(), "<img src='images/backup_small.gif'/>Import Export", true );

      tab.selectTab( 0 );

      initWidget( tab );

  }

  public static ComponentInfo init() {
    return new ComponentInfo("Admin",
      "Administer the repository") {
      public JBRMSFeature createInstance() {
        return new AdminFeature();
      }

    };
  }


  public void onShow() {
  }
}