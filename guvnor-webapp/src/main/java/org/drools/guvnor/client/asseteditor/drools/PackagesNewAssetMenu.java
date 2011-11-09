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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;

public class PackagesNewAssetMenu implements IsWidget, PackagesNewAssetMenuView.Presenter {

    private PackagesNewAssetMenuView view;
    private ClientFactory clientFactory;
    private final EventBus eventBus;

    public PackagesNewAssetMenu( ClientFactory clientFactory, EventBus eventBus ) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.view = new PackagesNewAssetMenuViewImpl();
        view.setPresenter( this );
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    public void onNewModule() {
        view.openNewPackageWizard( clientFactory, eventBus );
    }

    public void onNewSpringContext() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.SPRING_CONTEXT, clientFactory, eventBus );
    }

    public void onNewWorkingSet() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.WORKING_SET, clientFactory, eventBus);
    }

    public void onNewRule() {
        view.openNewAssetWizardWithCategories( null, clientFactory, eventBus );
    }

    public void onNewRuleTemplate() {
        view.openNewAssetWizardWithCategories( AssetFormats.RULE_TEMPLATE, clientFactory, eventBus );
    }

    public void onNewPojoModel() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.MODEL, clientFactory, eventBus );
    }

    public void onNewDeclarativeModel() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.DRL_MODEL, clientFactory, eventBus );
    }

    public void onNewBPELPackage() {
        view.openNewAssetWizardWithCategories( AssetFormats.BPEL_PACKAGE, clientFactory, eventBus );
    }

    public void onNewFunction() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.FUNCTION, clientFactory, eventBus );
    }

    public void onNewDSL() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.DSL, clientFactory, eventBus );
    }

    public void onNewRuleFlow() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.RULE_FLOW_RF, clientFactory, eventBus );
    }

    public void onNewBPMN2Process() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.BPMN2_PROCESS, clientFactory, eventBus );
    }

    public void onNewWorkitemDefinition() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.WORKITEM_DEFINITION, clientFactory, eventBus );
    }

    public void onNewEnumeration() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.ENUMERATION, clientFactory, eventBus );
    }

    public void onNewTestScenario() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.TEST_SCENARIO, clientFactory, eventBus );
    }

    public void onNewFile() {
        view.openNewAssetWizardWithoutCategories( "", clientFactory, eventBus );
    }

    public void onRebuildAllPackages() {
        view.confirmRebuild();
    }

    public void onRebuildConfirmed() {
        view.showLoadingPopUpRebuildingPackageBinaries();
        clientFactory.getPackageService().rebuildPackages( new GenericCallback() {
            public void onSuccess( Object result ) {
                view.closeLoadingPopUp();
            }
        } );
    }
}
