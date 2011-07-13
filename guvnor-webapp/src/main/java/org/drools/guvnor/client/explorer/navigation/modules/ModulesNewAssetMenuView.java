package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.ClientFactory;

public interface ModulesNewAssetMenuView extends IsWidget {

    interface Presenter {

        void onNewModule();

        void onNewSpringContext();

        void onNewWorkingSet();

        void onNewRule();

        void onNewRuleTemplate();

        void onNewPojoModel();

        void onNewDeclarativeModel();

        void onNewBPELPackage();

        void onNewFunction();

        void onNewDSL();

        void onNewRuleFlow();

        void onNewBPMN2Process();

        void onNewWorkitemDefinition();

        void onNewEnumeration();

        void onNewTestScenario();

        void onNewFile();

        void onRebuildAllPackages();

        void onRebuildConfirmed();

    }

    void setPresenter( Presenter presenter );

    void openNewPackageWizard(ClientFactory clientFactory);

    void openNewAssetWizardWithoutCategories( String format );

    void openNewAssetWizardWithCategories( String format );

    void confirmRebuild();

    void showLoadingPopUpRebuildingPackageBinaries();

    void closeLoadingPopUp();
}
