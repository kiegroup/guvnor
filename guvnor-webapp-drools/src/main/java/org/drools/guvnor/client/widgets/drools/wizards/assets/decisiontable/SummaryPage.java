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
package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewGuidedDecisionTableAssetWizardContext;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;

/**
 * A summary page for the guided Decision Table Wizard
 */
public class SummaryPage extends AbstractGuidedDecisionTableWizardPage
    implements
    SummaryPageView.Presenter {

    private static Constants constants = GWT.create( Constants.class );

    private SummaryPageView  view      = new SummaryPageViewImpl();

    public SummaryPage(NewGuidedDecisionTableAssetWizardContext context,
                       GuidedDecisionTable52 dtable,
                       EventBus eventBus,
                       Validator validator) {
        super( context,
               dtable,
               eventBus,
               validator );
    }

    public String getTitle() {
        return constants.DecisionTableWizardSummary();
    }

    public String getAssetName() {
        return view.getAssetName();
    }

    public boolean isComplete() {
        String assetName = view.getAssetName();
        boolean isValid = (assetName != null && !assetName.equals( "" ));
        view.setHasInvalidAssetName( !isValid );
        return isValid;
    }

    public void initialise() {
        view.setPresenter( this );
        view.setAssetName( context.getAssetName() );
        view.setAssetDescription( context.getDescription() );
        view.setPackageName( context.getPackageName() );
        view.setTableFormat( ((NewGuidedDecisionTableAssetWizardContext) context).getTableFormat() );
        content.setWidget( view );
    }

    public void prepareView() {
        //Nothing required
    }

}
