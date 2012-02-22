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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.NewPackageWizard;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.Util;

public class PackagesNewAssetMenuViewImpl implements PackagesNewAssetMenuView {

    private MenuBar createNewMenu = new MenuBar( true );
    private Presenter presenter;

    private MenuBar getMenu() {

        addNewPackageMenuItem();
        addNewChangeSetMenuItem();
        addNewSpringContextMenuItem();
        addNewWorkingSetMenuItem();
        addNewRuleMenuItem();
        addNewRuleTemplateMenuItem();
        addNewPojoModelMenuItem();
        addNewDeclarativeModelMenuItem();
        addNewFunctionMenuItem();
        addNewDSLMenuItem();
        addNewRuleFlowMenuItem();
        addNewBPMN2ProcessMenuItem();
        addNewWorkItemDefinitionMenuItem();
        addNewFormDefinitionMenuItem();
        addNewEnumerationMenuItem();
        addNewTestScenarioMenuItem();
        addNewFileMenuItem();
        rebuildAllPackagesMenuItem();

        MenuBar rootMenuBar = new MenuBar( true );
        rootMenuBar.setAutoOpen( false );
        rootMenuBar.setAnimationEnabled( false );

        rootMenuBar.addItem( new MenuItem( Constants.INSTANCE.CreateNew(), createNewMenu ) );

        return rootMenuBar;
    }

    private void addNewFileMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.newFile(), Constants.INSTANCE.CreateAFile() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewFile();
                    }
                } );
    }

    private void addNewTestScenarioMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.testManager(), Constants.INSTANCE.NewTestScenario() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewTestScenario();
                    }
                } );
    }

    private void addNewEnumerationMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.newEnumeration(), Constants.INSTANCE.NewEnumeration() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewEnumeration();
                    }
                } );
    }

    private void addNewWorkItemDefinitionMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.newEnumeration(), Constants.INSTANCE.NewWorkitemDefinition() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewWorkitemDefinition();
                    }
                } );
    }

    private void addNewBPMN2ProcessMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.ruleflowSmall(), Constants.INSTANCE.NewBPMN2Process() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewBPMN2Process();
                    }
                } );
    }

    private void addNewFormDefinitionMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.formDefIcon(), Constants.INSTANCE.FormDefinition() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewFormDefinition();
                    }
                });
    }

    private void addNewRuleFlowMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.ruleflowSmall(), Constants.INSTANCE.NewRuleFlow() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewRuleFlow();
                    }
                } );
    }

    private void addNewDSLMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.dsl(), Constants.INSTANCE.NewDSL() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewDSL();
                    }
                } );
    }

    private void addNewFunctionMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.functionAssets(), Constants.INSTANCE.NewFunction() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewFunction();
                    }
                } );
    }

    private void addNewDeclarativeModelMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.modelAsset(), Constants.INSTANCE.NewDeclarativeModel() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewDeclarativeModel();
                    }
                } );
    }

    private void addNewPojoModelMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.modelAsset(), Constants.INSTANCE.UploadPOJOModelJar() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewPojoModel();
                    }
                } );
    }

    private void addNewRuleTemplateMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.newTemplate(), Constants.INSTANCE.NewRuleTemplate() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewRuleTemplate();
                    }
                } );
    }

    private void addNewRuleMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.ruleAsset(), Constants.INSTANCE.NewRule() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewRule();
                    }
                } );
    }

    private void addNewWorkingSetMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.newPackage(), Constants.INSTANCE.NewWorkingSet() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewWorkingSet();
                    }
                } );
    }

    private void addNewSpringContextMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.newEnumeration(), Constants.INSTANCE.NewSpringContext() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewSpringContext();
                    }
                } );
    }

    private void addNewPackageMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.newPackage(), Constants.INSTANCE.NewPackage1() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewModule();
                    }
                } );
    }
    
    private void addNewChangeSetMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.newEnumeration(), Constants.INSTANCE.NewChangeSet() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onNewChangeSet();
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
        if ( Window.confirm( Constants.INSTANCE.RebuildConfirmWarning() ) ) {
            presenter.onRebuildConfirmed();
        }
    }

    public void showLoadingPopUpRebuildingPackageBinaries() {
        LoadingPopup.showMessage( Constants.INSTANCE.RebuildingPackageBinaries() );
    }

    public void closeLoadingPopUp() {
        LoadingPopup.close();
    }

    private void rebuildAllPackagesMenuItem() {
        createNewMenu.addItem( Util.getHeader( Images.INSTANCE.refresh(), Constants.INSTANCE.RebuildAllPackageBinariesQ() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onRebuildAllPackages();
                    }
                } );
    }
}
