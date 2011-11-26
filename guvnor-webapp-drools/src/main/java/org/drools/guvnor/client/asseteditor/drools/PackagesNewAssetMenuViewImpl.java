/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.NewPackageWizard;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.Util;

public class PackagesNewAssetMenuViewImpl implements PackagesNewAssetMenuView {

    private static Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private MenuBar createNewMenu = new MenuBar( true );
    private Presenter presenter;

    private MenuBar getMenu() {

        addNewPackageMenuItem();
        addNewSpringContextMenuItem();
        addNewWorkingSetMenuItem();
        addNewRuleMenuItem();
        addNewRuleTemplateMenuItem();
        addNewPojoModelMenuItem();
        addNewDeclarativeModelMenuItem();
        addNewBPELEditorMenuItem();
        addNewFunctionMenuItem();
        addNewDSLMenuItem();
        addNewRuleFlowMenuItem();
        addNewBPMN2ProcessMenuItem();
        addNewWorkItemDefinitionMenuItem();
        addNewEnumerationMenuItem();
        addNewTestScenarioMenuItem();
        addNewFileMenuItem();
        rebuildAllPackagesMenuItem();

        MenuBar rootMenuBar = new MenuBar( true );
        rootMenuBar.setAutoOpen( false );
        rootMenuBar.setAnimationEnabled( false );

        rootMenuBar.addItem( new MenuItem( constants.CreateNew(), createNewMenu ) );

        return rootMenuBar;
    }

    private void addNewFileMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.newFile(), constants.CreateAFile() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewFile();
                    }
                } );
    }

    private void addNewTestScenarioMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.testManager(), constants.NewTestScenario() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewTestScenario();
                    }
                } );
    }

    private void addNewEnumerationMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.newEnumeration(), constants.NewEnumeration() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewEnumeration();
                    }
                } );
    }

    private void addNewWorkItemDefinitionMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.newEnumeration(), constants.NewWorkitemDefinition() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewWorkitemDefinition();
                    }
                } );
    }

    private void addNewBPMN2ProcessMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.ruleflowSmall(), constants.NewBPMN2Process() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewBPMN2Process();
                    }
                } );
    }

    private void addNewRuleFlowMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.ruleflowSmall(), constants.NewRuleFlow() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewRuleFlow();
                    }
                } );
    }

    private void addNewDSLMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.dsl(), constants.NewDSL() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewDSL();
                    }
                } );
    }

    private void addNewFunctionMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.functionAssets(), constants.NewFunction() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewFunction();
                    }
                } );
    }

    private void addNewBPELEditorMenuItem() {
        if ( ApplicationPreferences.showFlewBPELEditor() ) {
            createNewMenu.addItem( Util.getHeader( images.modelAsset(), constants.NewBPELPackage() ).asString(),
                    true,
                    new Command() {
                        public void execute() {
                            presenter.onNewBPELPackage();
                        }
                    } );
        }
    }

    private void addNewDeclarativeModelMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.modelAsset(), constants.NewDeclarativeModel() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewDeclarativeModel();
                    }
                } );
    }

    private void addNewPojoModelMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.modelAsset(), constants.UploadPOJOModelJar() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewPojoModel();
                    }
                } );
    }

    private void addNewRuleTemplateMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.newTemplate(), constants.NewRuleTemplate() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewRuleTemplate();
                    }
                } );
    }

    private void addNewRuleMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.ruleAsset(), constants.NewRule() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewRule();
                    }
                } );
    }

    private void addNewWorkingSetMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.newPackage(), constants.NewWorkingSet() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewWorkingSet();
                    }
                } );
    }

    private void addNewSpringContextMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.newEnumeration(), constants.NewSpringContext() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewSpringContext();
                    }
                } );
    }

    private void addNewPackageMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.newPackage(), constants.NewPackage1() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewModule();
                    }
                } );
    }

    public Widget asWidget() {
        return getMenu();
    }

    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    public void openNewPackageWizard( ClientFactory clientFactory, EventBus eventBus) {
        NewPackageWizard wiz = new NewPackageWizard( clientFactory, eventBus );
        wiz.show();
    }

    public void openNewAssetWizardWithoutCategories( String format, ClientFactory clientFactory, EventBus eventBus  ) {
        openWizard( format, false, clientFactory, eventBus);
    }

    public void openNewAssetWizardWithCategories( String format, ClientFactory clientFactory, EventBus eventBus ) {
        openWizard( format, true, clientFactory, eventBus );
    }

    private void openWizard( String format, boolean showCategories, ClientFactory clientFactory, EventBus eventBus  ) {
        NewAssetWizard pop = new NewAssetWizard( showCategories, format, clientFactory, eventBus );

        pop.show();
    }

    public void confirmRebuild() {
        if ( Window.confirm( constants.RebuildConfirmWarning() ) ) {
            presenter.onRebuildConfirmed();
        }
    }

    public void showLoadingPopUpRebuildingPackageBinaries() {
        LoadingPopup.showMessage( constants.RebuildingPackageBinaries() );
    }

    public void closeLoadingPopUp() {
        LoadingPopup.close();
    }

    private void rebuildAllPackagesMenuItem() {
        createNewMenu.addItem( Util.getHeader( images.refresh(), constants.RebuildAllPackageBinariesQ() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onRebuildAllPackages();
                    }
                } );
    }
}
