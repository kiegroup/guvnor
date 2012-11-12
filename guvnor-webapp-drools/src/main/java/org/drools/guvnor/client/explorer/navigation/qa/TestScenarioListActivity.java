package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;
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
@WorkbenchScreen(identifier = "testScenarioList")
public class TestScenarioListActivity {


    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private final SimplePanel simplePanel = new SimplePanel();

    private final ClientFactory clientFactory;
    private String moduleUuid;
    private PlaceRequest place;

    @Inject
    public TestScenarioListActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @OnStart
    public void init(final PlaceRequest place) {
        this.place = place;
        moduleUuid = place.getParameterString("moduleUuid", null);
    }


    @WorkbenchPartTitle
    public String getTitle() {
        return createTitle(" ");
    }

    private String createTitle(String packageName) {
        return Constants.INSTANCE.ScenariosForPackage(packageName);
    }

    @WorkbenchPartView
    public Widget asWidget() {

        Path path = new PathImpl();
        path.setUUID(moduleUuid);
        clientFactory.getModuleService().loadModule(
                path,
                new GenericCallback<Module>() {
                    public void onSuccess(Module packageConfigData) {

                        simplePanel.add(
                                new ScenarioPackageScreen(
                                        packageConfigData.getUuid(),
                                        packageConfigData.getName(),
                                        clientFactory));

                        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(place, new InlineLabel(createTitle(packageConfigData.getName()))));
                    }
                });

        return simplePanel;
    }
}
