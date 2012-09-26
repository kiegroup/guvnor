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


package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.GuvnorEventBus;
import org.drools.guvnor.client.asseteditor.RuleViewerWrapper;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.Asset;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "assetEditor")
public class AssetEditorActivity {


    private final ClientFactory clientFactory;
    private final EventBus eventBus;
    private final PlaceManager placeManager;
    private final SimplePanel simplePanel = new SimplePanel();

    @Inject
    private Event<RefreshModuleDataModelEvent> refreshModuleDataModelEvents;

    @Inject
    public AssetEditorActivity(PlaceManager placeManager, ClientFactory clientFactory, GuvnorEventBus eventBus) {
        this.clientFactory = clientFactory;
        this.placeManager = placeManager;
        this.eventBus = eventBus;
    }

    @OnStart
    public void init() {
        loadRuleAsset(placeManager.getCurrentPlaceRequest().getParameters().get("uuid"));
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return simplePanel;

    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "ruleAsset.getName()";
    }

    private boolean[] loadingTimer() {
        final boolean[] loading = {true};

        Timer timer = new Timer() {
            public void run() {
                if (loading[0]) {
                    LoadingPopup.showMessage(ConstantsCore.INSTANCE.LoadingAsset());
                }
            }
        };
        timer.schedule(200);
        return loading;
    }


    private void loadRuleAsset(String uuid) {
        clientFactory.getAssetService().loadRuleAsset(uuid,
                createGenericCallback(loadingTimer()));
    }

    private GenericCallback<Asset> createGenericCallback(final boolean[] loading) {
        return new GenericCallback<Asset>() {
            public void onSuccess(final Asset ruleAsset) {
                refreshModuleDataModelEvents.fire(
                        new RefreshModuleDataModelEvent(
                                ruleAsset.metaData.moduleName,
                                createOnRefreshModuleDataModelCompletion(ruleAsset, loading)));
            }

            private Command createOnRefreshModuleDataModelCompletion(final Asset ruleAsset, final boolean[] loading) {
                return new Command() {
                    public void execute() {
                        loading[0] = false;

                        simplePanel.add(
                                new RuleViewerWrapper(
                                        clientFactory,
                                        eventBus,
                                        ruleAsset));

                        LoadingPopup.close();
                    }
                };
            }
        };
    }
}
