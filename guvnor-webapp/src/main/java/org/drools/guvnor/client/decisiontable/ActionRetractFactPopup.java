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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
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

public class ActionRetractFactPopup extends FormStylePopup {

    private static Constants       constants = GWT.create( Constants.class );

    private ActionRetractFactCol52 editingCol;
    private GuidedDecisionTable52  model;

    //TODO Limited Entry support, show pattern drop-down
    public ActionRetractFactPopup(final GuidedDecisionTable52 model,
                                  final GenericColumnCommand refreshGrid,
                                  final ActionRetractFactCol52 col,
                                  final boolean isNew) {
        this.editingCol = cloneActionRetractColumn( col );
        this.model = model;

        setTitle( constants.ColumnConfigurationRetractAFact() );
        setModal( false );

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
            DTCellValue52 dcv = cloneLimitedEntryValue( ((LimitedEntryCol) col).getValue() );
            ((LimitedEntryCol) clone).setValue( dcv );
        } else {
            clone = new ActionRetractFactCol52();
        }
        clone.setHeader( col.getHeader() );
        clone.setHideColumn( col.isHideColumn() );
        return clone;
    }
    
    private DTCellValue52 cloneLimitedEntryValue(DTCellValue52 dcv) {
        if ( dcv == null ) {
            return null;
        }
        DTCellValue52 clone = new DTCellValue52();
        switch ( dcv.getDataType() ) {
            case BOOLEAN :
                clone.setBooleanValue( dcv.getBooleanValue() );
                break;
            case DATE :
                clone.setDateValue( dcv.getDateValue() );
                break;
            case NUMERIC :
                clone.setNumericValue( dcv.getNumericValue() );
                break;
            case STRING :
                clone.setStringValue( dcv.getStringValue() );
        }
        return clone;
    }

    private void makeLimitedValueWidget() {
        //TODO Support Limited Entry
        //TODO This will be a list of bound facts from which to select
    }

    private ListBox loadBoundFacts() {
        Set<String> facts = new HashSet<String>();
        for ( Pattern52 p : model.getConditionPatterns() ) {
            if ( !p.isNegated() && !"".equals( p.getBoundName() ) ) {
                facts.add( p.getBoundName() );
            }
        }

        ListBox box = new ListBox();
        for ( Iterator<String> iterator = facts.iterator(); iterator.hasNext(); ) {
            String b = iterator.next();
            box.addItem( b );
        }

        return box;
    }

}
