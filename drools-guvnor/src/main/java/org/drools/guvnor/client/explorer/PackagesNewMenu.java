/*
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

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.NewPackageWizard;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.util.Util;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;


public class PackagesNewMenu {
    private static Constants constants = ((Constants) GWT.create(Constants.class));
    private static Images images = (Images) GWT.create(Images.class);

    public static MenuBar getMenu(final AbstractTree manager) {    
    	
    	MenuBar createNewMenu = new MenuBar(true);

    	createNewMenu.addItem(Util.getHeader(images.newPackage(), constants.NewPackage1()),
        		true,
        		new Command() {
            public void execute() {
                NewPackageWizard wiz = new NewPackageWizard(new Command() {
                    public void execute() {
                    	manager.refreshTree();
                    }
                });
                wiz.show();            }
        });

    	createNewMenu.addItem(Util.getHeader(images.newPackage(), constants.NewWorkingSet()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.WORKING_SET, constants.NewWorkingSet(), false);
            }
        });         
    	
    	createNewMenu.addItem(Util.getHeader(images.ruleAsset(), constants.NewRule()),
        		true,
        		new Command() {
            public void execute() {
            	manager.launchWizard(null, constants.NewRule(), true);
            }
        });      	

        createNewMenu.addItem(Util.getHeader(images.newTemplate(), constants.NewRuleTemplate()),
                true,
                new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.RULE_TEMPLATE, constants.NewRuleTemplate(), true);
           }
        }); 
        
    	createNewMenu.addItem(Util.getHeader(images.modelAsset(), constants.UploadPOJOModelJar()),
        		true,
        		new Command() {
            public void execute() {
            	manager.launchWizard(AssetFormats.MODEL, constants.NewModelArchiveJar(), false);
            }
        }); 
    	
    	createNewMenu.addItem(Util.getHeader(images.modelAsset(), constants.NewDeclarativeModel()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.DRL_MODEL, constants.NewDeclarativeModelUsingGuidedEditor(), false);
            }
        }); 
    	
        if (Preferences.getBooleanPref("flex-bpel-editor")) {
        	createNewMenu.addItem(Util.getHeader(images.modelAsset(), constants.NewBPELPackage()),
            		true,
            		new Command() {
                        public void execute() {
    				        manager.launchWizard(AssetFormats.BPEL_PACKAGE, constants
    						    .CreateANewBPELPackage(), false);
                }
            }); 
        }
    	
    	createNewMenu.addItem(Util.getHeader(images.functionAssets(), constants.NewFunction()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.FUNCTION, constants.CreateANewFunction(), false);
            }
        }); 
    	
    	createNewMenu.addItem(Util.getHeader(images.dsl(), constants.NewDSL()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.DSL, constants.CreateANewDSLConfiguration(), false);
            }
        }); 
    	
    	createNewMenu.addItem(Util.getHeader(images.ruleflowSmall(), constants.NewRuleFlow()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.RULE_FLOW_RF, constants.CreateANewRuleFlow(), false);
            }
        });  
    	
    	createNewMenu.addItem(Util.getHeader(images.newEnumeration(), constants.NewEnumeration()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.ENUMERATION, constants.CreateANewEnumerationDropDownMapping(), false);
            }
        });  
    	
    	createNewMenu.addItem(Util.getHeader(images.testManager(), constants.NewTestScenario()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.TEST_SCENARIO, constants.CreateATestScenario(), false);
           }
        }); 
    	
    	createNewMenu.addItem(Util.getHeader(images.newFile(), constants.CreateAFile()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard("*", constants.CreateAFile(), false);
           }
        }); 
    	
    	createNewMenu.addItem(Util.getHeader(images.refresh(), constants.RebuildAllPackageBinariesQ()),
        		true,
        		new Command() {
            public void execute() {
                if (Window.confirm(constants.RebuildConfirmWarning())) {
                    LoadingPopup.showMessage(constants.RebuildingPackageBinaries());
                    RepositoryServiceFactory.getService().rebuildPackages(new GenericCallback<Void>() {
                        public void onSuccess(Void data) {
                            LoadingPopup.close();
                        }
                    });
                }
            }
        }); 
    	

        MenuBar rootMenuBar = new MenuBar(true);
        rootMenuBar.setAutoOpen(true);
        rootMenuBar.setAnimationEnabled(true);
        
        rootMenuBar.addItem(new MenuItem(constants.CreateNew(), createNewMenu));

        return rootMenuBar;
     }


}
