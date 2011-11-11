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
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewAssetWizardContext;
import org.drools.guvnor.client.widgets.wizards.WizardPage;
import org.drools.guvnor.client.widgets.wizards.WizardPageStatusChangeEvent;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base page for the guided Decision Table Wizard
 */
public abstract class AbstractGuidedDecisionTableWizardPage
    implements
    WizardPage {

    protected static Constants           constants       = GWT.create( Constants.class );
    protected static Images              images          = GWT.create( Images.class );

    protected static final String        NEW_FACT_PREFIX = "f";

    protected SimplePanel                content         = new SimplePanel();

    protected GuidedDecisionTable52      dtable;
    protected EventBus                   eventBus;
    protected NewAssetWizardContext      context;
    protected SuggestionCompletionEngine sce;

    private Validator                    validator;

    public AbstractGuidedDecisionTableWizardPage(NewAssetWizardContext context,
                                                 GuidedDecisionTable52 dtable,
                                                 EventBus eventBus,
                                                 Validator validator) {
        this.context = context;
        this.dtable = dtable;
        this.eventBus = eventBus;
        this.validator = validator;
    }

    public Widget asWidget() {
        return content;
    }

    public void setSuggestionCompletionEngine(SuggestionCompletionEngine sce) {
        this.sce = sce;
    }

    public Validator getValidator() {
        return this.validator;
    }

    /**
     * Broadcast a change in state on a page
     */
    public void stateChanged() {
        WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( this );
        eventBus.fireEvent( event );
    }

    /**
     * When the Widget is finished a GuidedDecisionTable52 instance is passed to
     * each page for enrichment. Some pages are able to work on this instance
     * directly (i.e. the model is suitable for direct use in the page, such as
     * FactPatternsPage) however others maintain their own representation of the
     * model that must be copied into the GuidedDecisionTable52.
     * 
     * @param dtable
     */
    public void makeResult(GuidedDecisionTable52 dtable) {
        //Default implementation does nothing
    }

}
