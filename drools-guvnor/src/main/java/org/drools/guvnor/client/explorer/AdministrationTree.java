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

import org.drools.guvnor.client.admin.ArchivedAssetManager;
import org.drools.guvnor.client.admin.BackupManager;
import org.drools.guvnor.client.admin.CategoryManager;
import org.drools.guvnor.client.admin.LogViewer;
import org.drools.guvnor.client.admin.PermissionViewer;
import org.drools.guvnor.client.admin.RepoConfigManager;
import org.drools.guvnor.client.admin.RuleVerifierManager;
import org.drools.guvnor.client.admin.StateManager;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.images.Images;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class AdministrationTree extends AbstractTree {
    private static Constants constants = GWT.create( Constants.class );
    private static Images    images    = (Images) GWT.create( Images.class );

    public AdministrationTree(ExplorerViewCenterPanel tabbedPanel) {
        super( tabbedPanel );
        this.name = constants.Administration();
        this.image = images.rules();

        //Add Selection listener
        mainTree.addSelectionHandler( this );
    }

    @Override
    Tree getTree() {
        return ExplorerNodeConfig.getAdminStructure( itemWidgets );
    }

    // Show the associated widget in the deck panel
    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();
        String widgetID = itemWidgets.get( item );

        int id = Integer.parseInt( widgetID );
        switch ( id ) {
            case 0 :
                if ( !centertabbedPanel.showIfOpen( "catman" ) ) centertabbedPanel.addTab( constants.CategoryManager(),
                                                                                           new CategoryManager(),
                                                                                           "catman" );
                break;
            case 1 :
                if ( !centertabbedPanel.showIfOpen( "archman" ) ) centertabbedPanel.addTab( constants.ArchivedManager(),
                                                                                            new ArchivedAssetManager( centertabbedPanel ),
                                                                                            "archman" );
                break;

            case 2 :
                if ( !centertabbedPanel.showIfOpen( "stateman" ) ) centertabbedPanel.addTab( constants.StateManager(),
                                                                                             new StateManager(),
                                                                                             "stateman" );
                break;
            case 3 :
                if ( !centertabbedPanel.showIfOpen( "bakman" ) ) centertabbedPanel.addTab( constants.ImportExport(),
                                                                                           new BackupManager(),
                                                                                           "bakman" );
                break;

            case 4 :
                if ( !centertabbedPanel.showIfOpen( "errorLog" ) ) centertabbedPanel.addTab( constants.EventLog(),
                                                                                             new LogViewer(),
                                                                                             "errorLog" );
                break;
            case 5 :
                if ( !centertabbedPanel.showIfOpen( "securityPermissions" ) ) centertabbedPanel.addTab( constants.UserPermissionMappings(),
                                                                                                        new PermissionViewer(),
                                                                                                        "securityPermissions" );
                break;
            case 6 :
                Frame aboutInfoFrame = new Frame( "../AboutInfo.html" ); //NON-NLS

                FormStylePopup aboutPop = new FormStylePopup();
                aboutPop.setWidth( 600 + "px" );
                aboutPop.setTitle( constants.About() );
                String hhurl = GWT.getModuleBaseURL() + "webdav";
                aboutPop.addAttribute( constants.WebDAVURL() + ":",
                                       new SmallLabel( "<b>" + hhurl + "</b>" ) );
                aboutPop.addAttribute( constants.Version() + ":",
                                       aboutInfoFrame );
                aboutPop.show();
                break;

            case 7 :
                if ( !centertabbedPanel.showIfOpen( "ruleVerifierManager" ) ) {
                    centertabbedPanel.addTab( constants.RulesVerificationManager(),
                                              new RuleVerifierManager(),
                                              "ruleVerifierManager" );
                }
                break;
            case 8 :
                if ( !centertabbedPanel.showIfOpen( "repoconfig" ) ) //NON-NLS
                centertabbedPanel.addTab( constants.RepositoryConfig(),
                                          new RepoConfigManager(),
                                          "repoconfig" );
                break;
        }
    }
}
