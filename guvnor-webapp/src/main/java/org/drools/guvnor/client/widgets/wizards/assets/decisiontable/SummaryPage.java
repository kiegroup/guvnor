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
package org.drools.guvnor.client.widgets.wizards.assets.decisiontable;

import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.widgets.wizards.assets.NewAssetWizardContext;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A summary page for the guided Decision Table Wizard
 */
public class SummaryPage
        extends AbstractGuidedDecisionTableWizardPage {

    private static Constants constants = GWT.create( Constants.class );

    private SimplePanel      content   = new SimplePanel();

    public SummaryPage(NewAssetWizardContext context,
                       GuidedDecisionTable52 dtable,
                       EventBus eventBus) {
        super( context,
               dtable,
               eventBus );
        this.content.add( new SummaryPageWidget( context ) );
    }

    public String getTitle() {
        return constants.DecisionTableWizardSummary();
    }

    public Widget asWidget() {
        return content;
    }

    public boolean isComplete() {
        return true;
    }

    public void initialise() {
        //Nothing required
    }

    public void prepareView() {
        //Nothing required
    }

    private class SummaryPageWidget extends SimplePanel {

        private SummaryPageWidget(NewAssetWizardContext context) {
            FormStyleLayout layout = new FormStyleLayout();
            layout.addAttribute( constants.NameColon(),
                                 new Label( context.getAssetName() ) );
            layout.addAttribute( constants.CreateInPackage(),
                                 new Label( context.getPackageName() ) );
            layout.addAttribute( constants.InitialDescription(),
                                 new Label( getDescription( context.getDescription() ) ) );
            setWidget( layout );
        }

        private String getDescription(String description) {
            if ( description == null || description.length() == 0 ) {
                description = "<None>";
            }
            return description;
        }

    }

}
