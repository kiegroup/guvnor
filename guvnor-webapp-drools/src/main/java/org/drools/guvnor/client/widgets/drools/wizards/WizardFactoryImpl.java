/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.widgets.drools.wizards;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewAssetWizardContext;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewGuidedDecisionTableAssetWizardContext;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.NewGuidedDecisionTableWizard;
import org.drools.guvnor.client.widgets.wizards.Wizard;
import org.drools.guvnor.client.widgets.wizards.WizardActivityView;
import org.drools.guvnor.client.widgets.wizards.WizardContext;
import org.drools.guvnor.client.widgets.wizards.WizardFactory;
import org.drools.guvnor.client.widgets.wizards.WizardActivityView.Presenter;

import com.google.gwt.event.shared.EventBus;

/**
 * A Factory implementation to create different Wizards
 */
public class WizardFactoryImpl
    implements
    WizardFactory {

    private final ClientFactory clientFactory;
    private final EventBus      eventBus;

    public WizardFactoryImpl(ClientFactory clientFactory,
                             EventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
    }

    public Wizard getWizard(WizardContext context,
                            WizardActivityView.Presenter presenter) {
        if ( context instanceof NewAssetWizardContext ) {
            NewGuidedDecisionTableAssetWizardContext newAssetContext = (NewGuidedDecisionTableAssetWizardContext) context;
            if ( newAssetContext.getFormat().equals( AssetFormats.DECISION_TABLE_GUIDED ) ) {
                return new NewGuidedDecisionTableWizard( clientFactory,
                                                         eventBus,
                                                         newAssetContext,
                                                         presenter );
            }
        }
        return null;
    }

}
