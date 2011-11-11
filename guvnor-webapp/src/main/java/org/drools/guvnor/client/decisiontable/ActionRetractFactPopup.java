/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.decisiontable;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A popup to define the parameters of an Action to retract a Fact
 */
public class ActionRetractFactPopup extends FormStylePopup {

    private static Constants       constants = GWT.create( Constants.class );

    private ActionRetractFactCol52 editingCol;
    private GuidedDecisionTable52  model;

    public ActionRetractFactPopup(final GuidedDecisionTable52 model,
                                  final GenericColumnCommand refreshGrid,
                                  final ActionRetractFactCol52 col,
                                  final boolean isNew) {
        this.editingCol = cloneActionRetractColumn( col );
        this.model = model;

        setTitle( constants.ColumnConfigurationRetractAFact() );
        setModal( false );

        //Show available pattern bindings, if Limited Entry
        if ( model.getTableFormat() == TableFormat.LIMITED_ENTRY ) {
            final LimitedEntryActionRetractFactCol52 ler = (LimitedEntryActionRetractFactCol52) editingCol;
            final ListBox patterns = loadBoundFacts( ler.getValue().getStringValue() );
            patterns.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    int index = patterns.getSelectedIndex();
                    if ( index > -1 ) {
                        ler.getValue().setStringValue( patterns.getValue( index ) );
                    }
                }

            } );
            addAttribute( constants.FactToRetractColon(),
                          patterns );
        }

        //Column header
        final TextBox header = new TextBox();
        header.setText( col.getHeader() );
        header.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                editingCol.setHeader( header.getText() );
            }
        } );
        addAttribute( constants.ColumnHeaderDescription(),
                      header );

        //Hide column tick-box
        addAttribute( constants.HideThisColumn(),
                      DTCellValueWidgetFactory.getHideColumnIndicator( editingCol ) );

        Button apply = new Button( constants.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( null == editingCol.getHeader()
                        || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( constants.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }
                if ( isNew ) {
                    if ( !unique( editingCol.getHeader() ) ) {
                        Window.alert( constants.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                        return;
                    }

                } else {
                    if ( !col.getHeader().equals( editingCol.getHeader() ) ) {
                        if ( !unique( editingCol.getHeader() ) ) {
                            Window.alert( constants.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                            return;
                        }
                    }
                }

                // Pass new\modified column back for handling
                refreshGrid.execute( editingCol );
                hide();
            }
        } );
        addAttribute( "",
                      apply );

    }

    private boolean unique(String header) {
        for ( ActionCol52 o : model.getActionCols() ) {
            if ( o.getHeader().equals( header ) ) return false;
        }
        return true;
    }

    private ActionRetractFactCol52 cloneActionRetractColumn(ActionRetractFactCol52 col) {
        ActionRetractFactCol52 clone = null;
        if ( col instanceof LimitedEntryCol ) {
            clone = new LimitedEntryActionRetractFactCol52();
            DTCellValue52 dcv = new DTCellValue52( ((LimitedEntryCol) col).getValue().getStringValue() );
            ((LimitedEntryCol) clone).setValue( dcv );
        } else {
            clone = new ActionRetractFactCol52();
        }
        clone.setHeader( col.getHeader() );
        clone.setHideColumn( col.isHideColumn() );
        return clone;
    }

    private ListBox loadBoundFacts(String binding) {
        ListBox listBox = new ListBox();
        listBox.addItem( constants.Choose() );
        for ( int index = 0; index < model.getConditionPatterns().size(); index++ ) {
            Pattern52 p = model.getConditionPatterns().get( index );
            String boundName = p.getBoundName();
            if ( !"".equals( boundName ) ) {
                listBox.addItem( boundName );
                if ( boundName.equals( binding ) ) {
                    listBox.setSelectedIndex( index + 1 );
                }
            }
        }
        listBox.setEnabled( listBox.getItemCount() > 1 );
        if ( listBox.getItemCount() == 1 ) {
            listBox.clear();
            listBox.addItem( constants.NoPatternBindingsAvailable() );
        }
        return listBox;
    }

}
