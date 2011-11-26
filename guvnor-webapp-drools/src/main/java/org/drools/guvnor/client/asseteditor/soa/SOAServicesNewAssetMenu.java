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

package org.drools.guvnor.client.asseteditor.soa;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ClientFactory;

public class SOAServicesNewAssetMenu implements IsWidget, SOAServicesNewAssetMenuView.Presenter {

    private SOAServicesNewAssetMenuView view;
    private ClientFactory clientFactory;
    private final EventBus eventBus;

    public SOAServicesNewAssetMenu( ClientFactory clientFactory, EventBus eventBus ) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.view = new SOAServicesNewAssetMenuViewImpl();
        view.setPresenter( this );
    }

    public Widget asWidget() {
        return view.asWidget();
    }
    public void onNewService() {
        view.openNewServiceWizard( clientFactory, eventBus );
    }

    public void onNewAsset(String format) {
        view.openNewAssetWizardWithoutCategories(format, clientFactory, eventBus );
    }

    public void onNewBPELPackage() {
        view.openNewAssetWizardWithCategories( AssetFormats.BPEL_PACKAGE, clientFactory, eventBus );
    }

    public void onNewRuleFlow() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.RULE_FLOW_RF, clientFactory, eventBus );
    }

    public void onNewBPMN2Process() {
        view.openNewAssetWizardWithoutCategories( AssetFormats.BPMN2_PROCESS, clientFactory, eventBus );
    }

    public void onNewFile() {
        view.openNewAssetWizardWithoutCategories( "*", clientFactory, eventBus );
    }

}
