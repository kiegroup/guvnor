package org.drools.guvnor.client.explorer.navigation.deployment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.GuvnorEventBus;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SnapshotView;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "snapshotScreen")
public class SnapshotActivity {

    private final SimplePanel simplePanel = new SimplePanel();

    private final ClientFactory clientFactory;
    private final GuvnorEventBus eventBus;

    private String moduleName;
    private String snapshotName;

    @Inject
    public SnapshotActivity(ClientFactory clientFactory, GuvnorEventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
    }

    @OnStart
    public void init(final PlaceRequest place) {
        moduleName = place.getParameters().get("moduleName");
        snapshotName = place.getParameters().get("snapshotName");
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.SnapshotLabel("snapshotInfo.getName()"); // TODO : -Rikkola-
    }

    @WorkbenchPartView
    public Widget asWidget() {
        clientFactory.getModuleService().loadSnapshotInfo(
                moduleName,
                snapshotName,
                new GenericCallback<SnapshotInfo>() {
                    public void onSuccess(SnapshotInfo snapshotInfo) {
                        showTab(snapshotInfo);
                    }
                });

        return simplePanel;
    }

    private void showTab(final SnapshotInfo snapshotInfo) {

        LoadingPopup.showMessage(Constants.INSTANCE.LoadingSnapshot());

        ModuleServiceAsync moduleService = GWT.create(ModuleService.class);
        moduleService.loadModule(snapshotInfo.getUuid(),
                new GenericCallback<Module>() {
                    public void onSuccess(Module module) {
                        simplePanel.add(
                                new SnapshotView(
                                        clientFactory,
                                        eventBus,
                                        snapshotInfo,
                                        module));
                        LoadingPopup.close();
                    }
                });

    }

}
