package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.navigation.ModulesNewAssetMenuView.Presenter;
import org.drools.guvnor.client.rpc.PackageServiceAsync;

public class ModulesNewAssetMenu implements IsWidget, Presenter {

    private ModulesNewAssetMenuView view;
    private PackageServiceAsync packageService;


    public ModulesNewAssetMenu( ModulesNewAssetMenuView view,
                                PackageServiceAsync packageService ) {
        this.view = view;
        this.packageService = packageService;
        view.setPresenter( this );
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    public void onNewModule() {
        view.openNewPackageWizard();
    }

    public void onNewSpringContext() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.SPRING_CONTEXT );
    }

    public void onNewWorkingSet() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.WORKING_SET );
    }

    public void onNewRule() {
        view.openNewAssetWizardWithCategories( null );
    }

    public void onNewRuleTemplate() {
        view.openNewAssetWizardWithCategories( AssetFormats.RULE_TEMPLATE );
    }

    public void onNewPojoModel() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.MODEL );
    }

    public void onNewDeclarativeModel() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.DRL_MODEL );
    }

    public void onNewBPELPackage() {
        view.openNewAssetWizardWithCategories( AssetFormats.BPEL_PACKAGE );
    }

    public void onNewFunction() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.FUNCTION );
    }

    public void onNewDSL() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.DSL );
    }

    public void onNewRuleFlow() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.RULE_FLOW_RF );
    }

    public void onNewBPMN2Process() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.BPMN2_PROCESS );
    }

    public void onNewWorkitemDefinition() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.WORKITEM_DEFINITION );
    }

    public void onNewEnumeration() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.ENUMERATION );
    }

    public void onNewTestScenario() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.TEST_SCENARIO );
    }

    public void onNewFile() {
        view.openNewAssetWizardWithoutCategories( "*" );
    }

    public void onRebuildAllPackages() {
        view.confirmRebuild();
    }

    public void onRebuildConfirmed() {
        view.showLoadingPopUpRebuildingPackageBinaries();
        packageService.rebuildPackages( new GenericCallback() {
            public void onSuccess( Object result ) {
                view.closeLoadingPopUp();
            }
        } );
    }
}
