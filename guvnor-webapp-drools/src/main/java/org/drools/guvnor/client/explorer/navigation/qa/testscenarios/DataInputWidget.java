/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class DataInputWidget extends DirtyableFlexTable {

    private final Scenario                   scenario;
    private final SuggestionCompletionEngine suggestionCompletionEngine;
    protected final String                   type;
    private final ScenarioWidget             parent;
    private final ExecutionTrace             executionTrace;
    private final FixtureList                definitionList;
    private final String                     headerText;

    public DataInputWidget(String factType,
                           FixtureList definitionList,
                           Scenario sc,
                           ScenarioWidget parent,
                           ExecutionTrace executionTrace,
                           String headerText) {

        scenario = sc;
        this.suggestionCompletionEngine = parent.suggestionCompletionEngine;
        this.type = factType;

        this.parent = parent;
        this.executionTrace = executionTrace;
        this.definitionList = definitionList;
        this.headerText = headerText;

        setStyles();

        render();

    }

    private void setStyles() {
        getCellFormatter().setStyleName( 0,
                                         0,
                                         "modeller-fact-TypeHeader" ); //NON-NLS
        getCellFormatter().setAlignment( 0,
                                         0,
                                         HasHorizontalAlignment.ALIGN_CENTER,
                                         HasVerticalAlignment.ALIGN_MIDDLE );
        setStyleName( "modeller-fact-pattern-Widget" ); //NON-NLS
    }

    protected ClickHandler addFieldClickHandler() {
        return new ClickHandler() {

            public void onClick(ClickEvent event) {
                //build up a list of what we have got, don't want to add it twice
                HashSet<String> existingFields = new HashSet<String>();
                if ( definitionList.size() > 0 ) {
                    FactData factData = (FactData) definitionList.get( 0 );
                    for ( FieldData fieldData : factData.getFieldData() ) {
                        existingFields.add( fieldData.getName() );
                    }

                }
                String[] fields = (String[]) suggestionCompletionEngine.getModelFields( type );
                final FormStylePopup pop = new FormStylePopup(); //NON-NLS
                pop.setTitle( Constants.INSTANCE.ChooseDotDotDot() );
                final ListBox fieldsListBox = new ListBox();
                for ( int i = 0; i < fields.length; i++ ) {
                    String field = fields[i];
                    if ( !existingFields.contains( field ) ) fieldsListBox.addItem( field );
                }

                Button ok = new Button( Constants.INSTANCE.OK() );
                ok.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        String field = fieldsListBox.getItemText( fieldsListBox.getSelectedIndex() );
                        for ( Fixture fixture : definitionList ) {
                            if ( fixture instanceof FactData ) {
                                FactData factData = (FactData) fixture;
                                factData.getFieldData().add( new FieldData( field,
                                                                            "" ) );
                            }
                        }
                        render();
                        pop.hide();
                    }
                } );
                HorizontalPanel h = new HorizontalPanel();
                h.add( fieldsListBox );
                h.add( ok );
                pop.addAttribute( Constants.INSTANCE.ChooseAFieldToAdd(),
                                  h );

                pop.show();
            }
        };
    }

    private void render() {

        clear();

        setWidget( 0,
                   0,
                   new ClickableLabel( headerText,
                                       addFieldClickHandler() ) );

        if ( definitionList.size() == 0 ) {
            parent.renderEditor();
        }

        //This will work out what row is for what field, adding labels and remove icons
        RowIndexByFieldName rowIndexByFieldName = new RowIndexByFieldName();
        int col = 0;
        int totalCols = definitionList.size();
        for ( Fixture fixture : definitionList ) {
            if ( fixture instanceof FactData ) {
                final FactData factData = (FactData) fixture;

                // Set Header
                setWidget( 0,
                           ++col,
                           new SmallLabel( "[" + factData.getName() + "]" ) );

                Map<String, Integer> presentFields = new HashMap<String, Integer>();

                // Sets row name and delete button.
                for ( final FieldData fieldData : factData.getFieldData() ) {
                    // Avoid duplicate field rows, only one for each name.
                    if ( rowIndexByFieldName.doesNotContain( fieldData.getName() ) ) {
                        newRow( rowIndexByFieldName,
                                totalCols,
                                factData.getName(),
                                fieldData.getName() );
                    }

                    // Sets row data
                    int fieldRowIndex = rowIndexByFieldName.getRowIndex( fieldData.getName() );
                    setWidget( fieldRowIndex,
                               col,
                               editableCell( fieldData,
                                             factData,
                                             factData.getType(),
                                             this.executionTrace ) );
                    presentFields.remove( fieldData.getName() );
                }

                // 
                for ( Map.Entry<String, Integer> entry : presentFields.entrySet() ) {
                    int fieldRow = ((Integer) entry.getValue()).intValue();
                    FieldData fieldData = new FieldData( (String) entry.getKey(),
                                                         "" );
                    factData.getFieldData().add( fieldData );
                    setWidget( fieldRow,
                               col,
                               editableCell( fieldData,
                                             factData,
                                             factData.getType(),
                                             this.executionTrace ) );
                }

                // Set Delete 
                setWidget( rowIndexByFieldName.amountOrRows() + 1,
                           col,
                           new DeleteFactColumnButton( factData ) );
            }
        }

        int totalRows = rowIndexByFieldName.amountOrRows();

        getFlexCellFormatter().setHorizontalAlignment( totalRows + 1,
                                                       0,
                                                       HasHorizontalAlignment.ALIGN_RIGHT );

        if ( totalRows == 0 ) {
            Button b = new Button( Constants.INSTANCE.AddAField() );
            b.addClickHandler( addFieldClickHandler() );

            setWidget( 1,
                       1,
                       b );
        }
    }

    private void newRow(RowIndexByFieldName rowIndexByFieldName,
                        int totalCols,
                        final String factName,
                        final String fieldName) {
        rowIndexByFieldName.addRow( fieldName );

        int rowIndex = rowIndexByFieldName.getRowIndex( fieldName );

        setWidget( rowIndex,
                   0,
                   new SmallLabel( fieldName + ":" ) );
        setWidget( rowIndex,
                   totalCols + 1,
                   new DeleteFieldRowButton( factName,
                                             fieldName ) );
        getCellFormatter().setHorizontalAlignment( rowIndex,
                                                   0,
                                                   HasHorizontalAlignment.ALIGN_RIGHT );
    }

    /**
     * This will provide a cell editor. It will filter non numerics, show choices etc as appropriate.
     * @param fd
     * @param factType
     * @return
     */
    private Widget editableCell(final FieldData fd,
                                FactData factData,
                                String factType,
                                ExecutionTrace executionTrace) {
        return new FieldDataConstraintEditor( factType,
                                              new ValueChanged() {
                                                  public void valueChanged(String newValue) {
                                                      fd.setValue( newValue );
                                                  }
                                              },
                                              fd,
                                              factData,
                                              suggestionCompletionEngine,
                                              scenario,
                                              executionTrace );
    }

    class DeleteFactColumnButton extends ImageButton {

        public DeleteFactColumnButton(final FactData factData) {
            super( Images.INSTANCE.deleteItemSmall(),
                    Constants.INSTANCE.RemoveTheColumnForScenario( factData.getName() ) );

            addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if ( scenario.isFactDataReferenced( factData ) ) {
                        Window.alert( Constants.INSTANCE.CanTRemoveThisColumnAsTheName0IsBeingUsed( factData.getName() ) );
                    } else if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToRemoveColumn0( factData.getName() ) ) ) {
                        scenario.removeFixture( factData );
                        definitionList.remove( factData );

                        render();
                    }
                }
            } );
        }

    }

    class DeleteFieldRowButton extends ImageButton {
        public DeleteFieldRowButton(final String factName,
                                    final String fieldName) {
            super( Images.INSTANCE.deleteItemSmall(),
                   Constants.INSTANCE.RemoveThisRow() );

            addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToRemoveRow0( factName ) ) ) {
                        ScenarioHelper.removeFields( definitionList,
                                                     fieldName );

                        render();
                    }
                }
            } );
        }
    }

    class RowIndexByFieldName {
        private Map<String, Integer> rows = new HashMap<String, Integer>();

        public void addRow(String fieldName) {
            rows.put( fieldName,
                      rows.size() + 1 );
        }

        public boolean doesNotContain(String fieldName) {
            return !rows.containsKey( fieldName );
        }

        public Integer getRowIndex(String fieldName) {
            return rows.get( fieldName );
        }

        public int amountOrRows() {
            return rows.size();
        }
    }
}
