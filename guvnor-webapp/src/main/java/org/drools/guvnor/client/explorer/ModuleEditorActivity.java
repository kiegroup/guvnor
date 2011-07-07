package org.drools.guvnor.client.explorer;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.packages.PackageEditorWrapper;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

public class ModuleEditorActivity extends AbstractActivity {

    private final ClientFactory clientFactory;
    private ModuleEditorActivityView view;
    private String uuid;

    public ModuleEditorActivity( String uuid, ClientFactory clientFactory ) {
        this.view = clientFactory.getModuleEditorActivityView();

        this.uuid = uuid;

        this.clientFactory = clientFactory;
    }

    public void start( final AcceptsOneWidget panel, final EventBus eventBus ) {

        view.showLoadingPackageInformationMessage();

        RepositoryServiceFactory.getPackageService().loadPackageConfig( uuid,
                new GenericCallback<PackageConfigData>() {
                    public void onSuccess( PackageConfigData packageConfigData ) {
                        RulePackageSelector.currentlySelectedPackage = packageConfigData.getUuid();
                        panel.setWidget( new PackageEditorWrapper( packageConfigData, clientFactory ) );

                        LoadingPopup.close();

                        panel.setWidget( new Label( "MODULE" ) );
                    }
                } );
    }
}
