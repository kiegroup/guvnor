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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewAssetWizardContext;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.events.ConditionsDefinedEvent;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventBus;

/**
 * A page for the guided Decision Table Wizard to define which columns will be
 * expanded when the Decision Table is generated
 */
public class ColumnExpansionPage extends AbstractGuidedDecisionTableWizardPage
    implements
    ColumnExpansionPageView.Presenter,
    ConditionsDefinedEvent.Handler {

    private ColumnExpansionPageView view;

    private List<ConditionCol52>    columnsToExpand;

    public ColumnExpansionPage(NewAssetWizardContext context,
                               GuidedDecisionTable52 dtable,
                               EventBus eventBus,
                               Validator validator) {
        super( context,
               dtable,
               eventBus,
               validator );
        this.view = new ColumnExpansionPageViewImpl( validator );

        //Wire-up the events
        eventBus.addHandler( ConditionsDefinedEvent.TYPE,
                             this );

    }

    public String getTitle() {
        return constants.DecisionTableWizardColumnExpansion();
    }

    public void initialise() {
        if ( sce == null ) {
            return;
        }
        view.setPresenter( this );
        content.setWidget( view );
    }

    public void prepareView() {
        //Setup the available columns, that could have changed each time this page is visited
        List<ConditionCol52> availableColumns = findAvailableColumnsToExpand();
        view.setAvailableColumns( availableColumns );
        columnsToExpand = availableColumns;
    }

    private List<ConditionCol52> findAvailableColumnsToExpand() {
        List<ConditionCol52> availableColumns = new ArrayList<ConditionCol52>();
        for ( Pattern52 p : dtable.getConditionPatterns() ) {
            for ( ConditionCol52 c : p.getConditions() ) {
                switch ( dtable.getTableFormat() ) {
                    case EXTENDED_ENTRY :
                        String[] values = dtable.getValueList( c,
                                                               sce );
                        if ( values != null && values.length > 1 ) {
                            availableColumns.add( c );
                        }
                        break;
                    case LIMITED_ENTRY :
                        availableColumns.add( c );
                }
            }
        }
        return availableColumns;
    }

    public boolean isComplete() {
        //Expansion can involve zero or more columns, so the page is always complete
        return true;
    }

    public void onConditionsDefined(ConditionsDefinedEvent event) {
        view.setAreConditionsDefined( event.getAreConditionsDefined() );
    }

    public void setColumnsToExpand(List<ConditionCol52> columns) {
        this.columnsToExpand = columns;
    }

    public List<ConditionCol52> getColumnsToExpand() {
        //If the page has not been viewed the default setting is to use all columns
        if ( this.columnsToExpand == null ) {
            return findAvailableColumnsToExpand();
        }

        //Otherwise return those chosen in the UI
        return this.columnsToExpand;
    }

}
