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
package org.drools.guvnor.client.decisiontable.widget;

import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Simple container for controls to manipulate a Decision Table
 */
public class DecisionTableControlsWidget extends Composite {

    private Panel panel = new HorizontalPanel();

    public DecisionTableControlsWidget(final VerticalDecisionTableWidget dtable) {

        // Add row button
        Button btnAddRow = new Button( "Add Row",
                                       new ClickHandler() {

                                           public void onClick(ClickEvent event) {
                                               dtable.appendRow();
                                           }
                                       } );
        panel.add( btnAddRow );

        final TextBox txtRows = new TextBox();
        panel.add(txtRows);
        
        final TextBox txtCols = new TextBox();
        panel.add(txtCols);
        
        Button btnMakeGrid = new Button( "Make grid",
                                         new ClickHandler() {

                                             public void onClick(ClickEvent event) {
                                                 int rows = Integer.valueOf( txtRows.getText());
                                                 int cols = Integer.valueOf( txtCols.getText());
                                                 
                                                 GuidedDecisionTable model = makeModel( rows,
                                                                                        cols );
                                                 dtable.setModel( model );
                                             }
                                         } );
        panel.add( btnMakeGrid );

        initWidget( panel );

    }

    private GuidedDecisionTable makeModel(int rows,
                                          int cols) {
        GuidedDecisionTable model = new GuidedDecisionTable();
        for ( int iCol = 0; iCol < cols; iCol++ ) {
            AttributeCol ac = new AttributeCol();
            ac.setAttribute( "col" + iCol );
            model.getAttributeCols().add( ac );
        }
        cols += model.INTERNAL_ELEMENTS;
        String[][] data = new String[rows][cols];
        for ( int iRow = 0; iRow < rows; iRow++ ) {
            String[] row = new String[cols];
            for ( int iCol = 0; iCol < cols; iCol++ ) {
                row[iCol] = "(" + iRow + "," + iCol + ")";
            }
            data[iRow] = row;
        }
        model.setData( data );
        return model;
    }

}
