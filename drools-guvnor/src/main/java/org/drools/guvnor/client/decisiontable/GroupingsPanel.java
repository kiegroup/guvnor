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
package org.drools.guvnor.client.decisiontable;

import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * 
 * @author rikkola
 *
 */
public class GroupingsPanel extends HorizontalPanel {

    private Constants                 constants      = ((Constants) GWT.create( Constants.class ));

    private final ListBox             columnsListBox = new ListBox();
    private final GuidedDecisionTable guidedDecisionTable;
    private final Command             refreshCommand;

    public GroupingsPanel(final GuidedDecisionTable guidedDecisionTable,
                          final Command refreshCommand) {
        this.guidedDecisionTable = guidedDecisionTable;
        this.refreshCommand = refreshCommand;

        initColumnsListBox();

        add( new SmallLabel( constants.GroupByColumn() ) );
        add( columnsListBox );

        add( getOkButton() );
    }

    private void initColumnsListBox() {
        columnsListBox.addItem( constants.Description(),
                                "desc" ); //NON-NLS

        addListItems();

        if ( guidedDecisionTable.getGroupField() == null ) {
            columnsListBox.setSelectedIndex( columnsListBox.getItemCount() - 1 );
        }
    }

    private Button getOkButton() {
        Button ok = new Button( constants.Apply() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                guidedDecisionTable.setGroupField( columnsListBox.getValue( columnsListBox.getSelectedIndex() ) );
                refreshCommand.execute();
            }
        } );
        return ok;
    }

    private void addListItems() {
        addMetaDataColumns();
        addAttributeColumns();
        addConditionColumns();
        addActionColumns();
        addNone();
    }

    private void addNone() {
        columnsListBox.addItem( constants.none(),
                                "" );
    }

    private void addActionColumns() {
        for ( ActionCol c : guidedDecisionTable.getActionCols() ) {
            columnsListBox.addItem( c.getHeader(),
                                    c.getHeader() );
            if ( c.getHeader().equals( guidedDecisionTable.getGroupField() ) ) {
                columnsListBox.setSelectedIndex( columnsListBox.getItemCount() - 1 );
            }
        }
    }

    private void addConditionColumns() {
        for ( ConditionCol c : guidedDecisionTable.getConditionCols() ) {
            columnsListBox.addItem( c.getHeader(),
                                    c.getHeader() );
            if ( c.getHeader().equals( guidedDecisionTable.getGroupField() ) ) {
                columnsListBox.setSelectedIndex( columnsListBox.getItemCount() - 1 );
            }
        }
    }

    private void addAttributeColumns() {
        for ( AttributeCol c : guidedDecisionTable.getAttributeCols() ) {
            columnsListBox.addItem( c.attr,
                                    c.attr );
            if ( c.attr.equals( guidedDecisionTable.getGroupField() ) ) {
                columnsListBox.setSelectedIndex( columnsListBox.getItemCount() - 1 );
            }
        }
    }

    private void addMetaDataColumns() {
        for ( MetadataCol c : guidedDecisionTable.getMetadataCols() ) {
            columnsListBox.addItem( c.attr,
                                    c.attr );
            if ( c.attr.equals( guidedDecisionTable.getGroupField() ) ) {
                columnsListBox.setSelectedIndex( columnsListBox.getItemCount() - 1 );
            }
        }
    }

    public void refresh() {
        columnsListBox.clear();
        addListItems();
    }
}
