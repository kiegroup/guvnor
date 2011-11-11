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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;

import org.drools.guvnor.client.asseteditor.RuleViewerWrapper;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.util.Activity;

public class AssetEditorActivity extends Activity {

    private Constants constants = GWT.create( Constants.class );

    private final AssetEditorPlace place;
    private final ClientFactory clientFactory;
    private EventBus eventBus;

    public AssetEditorActivity(AssetEditorPlace place,
                               ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        this.place = place;
    }

    @Override
    public void start(AcceptItem tabbedPanel, EventBus eventBus) {
        this.eventBus = eventBus;
        final boolean[] loading = {true};

        Timer t = new Timer() {
            public void run() {
                if ( loading[0] ) {
                    LoadingPopup.showMessage( constants.LoadingAsset() );
                }
            }
        };
        t.schedule( 200 );

        loadRuleAsset(
                tabbedPanel,
                place.getUuid(),
                loading );
    }

    private void loadRuleAsset(final AcceptItem tabbedPanel,
                               final String uuid,
                               final boolean[] loading) {
        clientFactory.getAssetService().loadRuleAsset( uuid,
                createGenericCallback(
                        tabbedPanel,
                        loading ) );
    }

    private GenericCallback<RuleAsset> createGenericCallback(final AcceptItem tabbedPanel,
                                                             final boolean[] loading) {
        return new GenericCallback<RuleAsset>() {
            public void onSuccess(final RuleAsset ruleAsset) {
            	eventBus.fireEvent(new RefreshModuleDataModelEvent(ruleAsset.metaData.packageName,
                        createCommandForSuggestCompletionCache( loading,
                                ruleAsset )));
            }

            private Command createCommandForSuggestCompletionCache(final boolean[] loading,
                                                                   final RuleAsset ruleAsset) {
                return new Command() {
                    public void execute() {
                        loading[0] = false;

                        tabbedPanel.add(
                                ruleAsset.getName(),
                                new RuleViewerWrapper(
                                        clientFactory,
                                        eventBus,
                                        ruleAsset ) );

                        LoadingPopup.close();
                    }
                };
            }
        };
    }
}
