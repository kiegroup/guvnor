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

    private static final String REPOCONFIG            = "repoconfig";
    private static final String RULE_VERIFIER_MANAGER = "ruleVerifierManager";
    private static final String SECURITY_PERMISSIONS  = "securityPermissions";
    private static final String ERROR_LOG             = "errorLog";
    private static final String BAKMAN                = "bakman";
    private static final String STATEMAN              = "stateman";
    private static final String ARCHMAN               = "archman";
    private static final String CATMAN                = "catman";

    private static Constants    constants             = GWT.create( Constants.class );
    private static Images       images                = (Images) GWT.create( Images.class );

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
                if ( !centertabbedPanel.showIfOpen( CATMAN ) ) {
                    centertabbedPanel.addTab( constants.CategoryManager(),
                                              new CategoryManager(),
                                              CATMAN );
                }
                break;
            case 1 :
                if ( !centertabbedPanel.showIfOpen( ARCHMAN ) ) {
                    centertabbedPanel.addTab( constants.ArchivedManager(),
                                              new ArchivedAssetManager( centertabbedPanel ),
                                              ARCHMAN );
                }
                break;

            case 2 :
                if ( !centertabbedPanel.showIfOpen( STATEMAN ) ) {
                    centertabbedPanel.addTab( constants.StateManager(),
                                              new StateManager(),
                                              STATEMAN );
                }
                break;
            case 3 :
                if ( !centertabbedPanel.showIfOpen( BAKMAN ) ) {
                    centertabbedPanel.addTab( constants.ImportExport(),
                                              new BackupManager(),
                                              BAKMAN );
                }
                break;

            case 4 :
                if ( !centertabbedPanel.showIfOpen( ERROR_LOG ) ) {
                    centertabbedPanel.addTab( constants.EventLog(),
                                              new LogViewer(),
                                              ERROR_LOG );
                }
                break;
            case 5 :
                if ( !centertabbedPanel.showIfOpen( SECURITY_PERMISSIONS ) ) {
                    centertabbedPanel.addTab( constants.UserPermissionMappings(),
                                              new PermissionViewer(),
                                              SECURITY_PERMISSIONS );
                }
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
                if ( !centertabbedPanel.showIfOpen( RULE_VERIFIER_MANAGER ) ) {
                    centertabbedPanel.addTab( constants.RulesVerificationManager(),
                                              new RuleVerifierManager(),
                                              RULE_VERIFIER_MANAGER );
                }
                break;
            case 8 :
                if ( !centertabbedPanel.showIfOpen( REPOCONFIG ) ) //NON-NLS
                centertabbedPanel.addTab( constants.RepositoryConfig(),
                                          new RepoConfigManager(),
                                          REPOCONFIG );
                break;
        }
    }
}
