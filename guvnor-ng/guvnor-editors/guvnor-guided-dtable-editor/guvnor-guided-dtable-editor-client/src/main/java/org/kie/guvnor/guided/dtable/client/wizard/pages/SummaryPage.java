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
package org.kie.guvnor.guided.dtable.client.wizard.pages;

import com.google.gwt.event.shared.EventBus;
import org.kie.guvnor.guided.dtable.client.resources.i18n.Constants;
import org.kie.guvnor.guided.dtable.client.widget.Validator;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewAssetWizardContext;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewGuidedDecisionTableAssetWizardContext;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;

/**
 * A summary page for the guided Decision Table Wizard
 */
public class SummaryPage extends AbstractGuidedDecisionTableWizardPage
    implements
    SummaryPageView.Presenter {

    private final SummaryPageView  view      = new SummaryPageViewImpl();

    public SummaryPage(final NewAssetWizardContext context,
                       final GuidedDecisionTable52 dtable,
                       final EventBus eventBus,
                       final Validator validator) {
        super( context,
               dtable,
               eventBus,
               validator );
    }

    public String getTitle() {
        return Constants.INSTANCE.DecisionTableWizardSummary();
    }

    public boolean isComplete() {
        String assetName = view.getBaseFileName();
        boolean isValid = (assetName != null && !assetName.equals( "" ));
        view.setHasInvalidAssetName( !isValid );
        return isValid;
    }

    public void initialise() {
        view.setPresenter( this );
        view.setBaseFileName( context.getBaseFileName() );
        view.setContextPath( context.getContextPath() );
        view.setTableFormat( ((NewGuidedDecisionTableAssetWizardContext) context).getTableFormat() );
        content.setWidget( view );
    }

    public void prepareView() {
        //Nothing required
    }

}
