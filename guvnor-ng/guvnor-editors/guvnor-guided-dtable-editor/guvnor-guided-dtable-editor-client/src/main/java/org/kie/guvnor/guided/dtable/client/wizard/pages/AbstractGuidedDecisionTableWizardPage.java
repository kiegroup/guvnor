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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.guvnor.commons.ui.client.wizards.WizardPage;
import org.kie.guvnor.commons.ui.client.wizards.WizardPageStatusChangeEvent;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.guided.dtable.client.widget.Validator;
import org.kie.guvnor.guided.dtable.client.widget.table.DTCellValueUtilities;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewAssetWizardContext;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.dtable.model.util.GuidedDecisionTableUtils;

/**
 * Base page for the guided Decision Table Wizard
 */
public abstract class AbstractGuidedDecisionTableWizardPage
        implements
        WizardPage {

    protected static final String NEW_FACT_PREFIX = "f";

    protected final SimplePanel content = new SimplePanel();

    protected final NewAssetWizardContext context;
    protected final GuidedDecisionTable52 model;
    protected final EventBus eventBus;
    protected final Validator validator;

    protected GuidedDecisionTableUtils modelUtils;
    protected DTCellValueUtilities cellUtils;
    protected DataModelOracle oracle;

    public AbstractGuidedDecisionTableWizardPage( final NewAssetWizardContext context,
                                                  final GuidedDecisionTable52 model,
                                                  final EventBus eventBus,
                                                  final Validator validator ) {
        this.context = context;
        this.model = model;
        this.eventBus = eventBus;
        this.validator = validator;
    }

    public Widget asWidget() {
        return content;
    }

    public void setDataModelOracle( final DataModelOracle oracle ) {
        this.oracle = oracle;
        this.cellUtils = new DTCellValueUtilities( this.model,
                                                   this.oracle );
        this.modelUtils = new GuidedDecisionTableUtils( this.oracle,
                                                        this.model );
    }

    public Validator getValidator() {
        return this.validator;
    }

    /**
     * Broadcast a change in state on a page
     */
    public void stateChanged() {
        WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( this );
        eventBus.fireEventFromSource( event,
                                      context );
    }

    /**
     * When the Widget is finished a GuidedDecisionTable52 instance is passed to
     * each page for enrichment. Some pages are able to work on this instance
     * directly (i.e. the model is suitable for direct use in the page, such as
     * FactPatternsPage) however others maintain their own representation of the
     * model that must be copied into the GuidedDecisionTable52.
     * @param model
     */
    public void makeResult( final GuidedDecisionTable52 model ) {
        //Default implementation does nothing
    }

    /**
     * Check whether empty values are permitted
     * @return True if empty values are permitted
     */
    protected boolean allowEmptyValues() {
        return this.model.getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;
    }

}
