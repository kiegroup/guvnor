package org.drools.guvnor.client.explorer.navigation.deployment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.InlineLabel;
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
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.widgets.events.ChangeTitleWidgetEvent;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "snapshotScreen")
public class SnapshotActivity {

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private final SimplePanel simplePanel = new SimplePanel();

    private final ClientFactory clientFactory;
    private final GuvnorEventBus eventBus;

    private String moduleName;
    private String snapshotName;
    private PlaceRequest place;

    @Inject
    public SnapshotActivity(ClientFactory clientFactory, GuvnorEventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
    }

    @OnStart
    public void init(final PlaceRequest place) {
        this.place = place;
        moduleName = place.getParameterString("moduleName", null);
        snapshotName = place.getParameterString("snapshotName", null);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "";
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

        moduleService.loadModule(snapshotInfo.getPath(),
                new GenericCallback<Module>() {
                    public void onSuccess(Module module) {
                        simplePanel.add(
                                new SnapshotView(
                                        clientFactory,
                                        eventBus,
                                        snapshotInfo,
                                        module));
                        LoadingPopup.close();

                        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(place, new InlineLabel(Constants.INSTANCE.SnapshotLabel(snapshotInfo.getName()))));
                    }
                });

    }

}
