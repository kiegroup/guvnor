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

import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.widgets.wizards.WizardPage;
import org.drools.guvnor.client.widgets.wizards.assets.NewAssetWizardContext;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base page for the guided Decision Table Wizard
 */
public abstract class AbstractGuidedDecisionTableWizardPage
    implements
    WizardPage {

    protected static Constants           constants = GWT.create( Constants.class );
    protected static Images              images    = GWT.create( Images.class );

    protected SimplePanel                content   = new SimplePanel();

    protected GuidedDecisionTable52      dtable;
    protected SuggestionCompletionEngine sce;
    protected EventBus                   eventBus;
    protected NewAssetWizardContext      context;

    public AbstractGuidedDecisionTableWizardPage(NewAssetWizardContext context,
                                                 GuidedDecisionTable52 dtable,
                                                 EventBus eventBus) {
        this.context = context;
        this.dtable = dtable;
        this.eventBus = eventBus;
    }

    public Widget getContent() {
        if ( this.sce == null ) {
            SuggestionCompletionCache.getInstance().loadPackage( context.getPackageName(),
                                                                 new Command() {

                                                                     public void execute() {
                                                                         LoadingPopup.close();
                                                                         sce = SuggestionCompletionCache.getInstance().getEngineFromCache( context.getPackageName() );
                                                                         populateContent();
                                                                     }

                                                                 } );
        }
        return content;
    }

    /**
     * Populate content Widget once SuggestionCompletionEngine has been loaded
     */
    public abstract void populateContent();

}
