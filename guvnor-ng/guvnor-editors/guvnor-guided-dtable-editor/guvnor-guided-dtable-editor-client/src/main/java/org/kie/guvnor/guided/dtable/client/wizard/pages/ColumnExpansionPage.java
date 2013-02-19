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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import org.kie.guvnor.guided.dtable.client.resources.i18n.Constants;
import org.kie.guvnor.guided.dtable.client.widget.Validator;
import org.kie.guvnor.guided.dtable.client.wizard.pages.events.ConditionsDefinedEvent;
import org.kie.guvnor.guided.dtable.client.wizard.util.NewAssetWizardContext;
import org.kie.guvnor.guided.dtable.model.CompositeColumn;
import org.kie.guvnor.guided.dtable.model.ConditionCol52;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.dtable.model.Pattern52;

/**
 * A page for the guided Decision Table Wizard to define which columns will be
 * expanded when the Decision Table is generated
 */
public class ColumnExpansionPage extends AbstractGuidedDecisionTableWizardPage
        implements
        ColumnExpansionPageView.Presenter,
        ConditionsDefinedEvent.Handler {

    private ColumnExpansionPageView view;

    private List<ConditionCol52> columnsToExpand;

    public ColumnExpansionPage( final NewAssetWizardContext context,
                                final GuidedDecisionTable52 dtable,
                                final EventBus eventBus,
                                final Validator validator ) {
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
        return Constants.INSTANCE.DecisionTableWizardColumnExpansion();
    }

    public void initialise() {
        if ( oracle == null ) {
            return;
        }
        view.setPresenter( this );
        content.setWidget( view );
    }

    public void prepareView() {
        //Setup the available columns, that could have changed each time this page is visited
        final List<ConditionCol52> availableColumns = findAvailableColumnsToExpand();
        view.setAvailableColumns( availableColumns );
        columnsToExpand = availableColumns;
    }

    private List<ConditionCol52> findAvailableColumnsToExpand() {
        final List<ConditionCol52> availableColumns = new ArrayList<ConditionCol52>();
        for ( CompositeColumn<?> cc : model.getPatterns() ) {
            if ( cc instanceof Pattern52 ) {
                final Pattern52 p = (Pattern52) cc;
                for ( ConditionCol52 c : p.getChildColumns() ) {
                    switch ( model.getTableFormat() ) {
                        case EXTENDED_ENTRY:
                            if ( modelUtils.hasValueList( c ) ) {
                                final String[] values = modelUtils.getValueList( c );
                                if ( values != null && values.length > 1 ) {
                                    availableColumns.add( c );
                                }

                            } else if ( oracle.hasEnums( p.getFactType(),
                                                         c.getFactField() ) ) {
                                availableColumns.add( c );
                            }
                            break;
                        case LIMITED_ENTRY:
                            availableColumns.add( c );
                    }
                }
            }
        }
        return availableColumns;
    }

    public boolean isComplete() {
        //Expansion can involve zero or more columns, so the page is always complete
        return true;
    }

    public void onConditionsDefined( final ConditionsDefinedEvent event ) {
        if ( event.getSource() != context ) {
            return;
        }
        view.setAreConditionsDefined( event.getAreConditionsDefined() );
    }

    public void setColumnsToExpand( final List<ConditionCol52> columns ) {
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
