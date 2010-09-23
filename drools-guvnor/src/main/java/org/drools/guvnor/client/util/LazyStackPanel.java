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
package org.drools.guvnor.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The GWT StackPanel is not working as we want. So doing a custom one.
 * 
 * @author rikkola
 *
 */
public class LazyStackPanel extends FlexTable
    implements
    HasSelectionHandlers<LazyStackPanelRow> {

    private int rowIndex = 0;

    public LazyStackPanel() {
        addSelectionHandler( new SelectionHandler<LazyStackPanelRow>() {
            public void onSelection(SelectionEvent<LazyStackPanelRow> event) {
                LazyStackPanelRow row = event.getSelectedItem();
                if ( row.isExpanded() ) {
                    row.compress();
                } else {
                    row.expand();
                }
            }
        } );
    }

    public void add(String headerText,
                    LoadContentCommand contentLoad) {

        LazyStackPanelHeader header = new LazyStackPanelHeader( headerText );

        final LazyStackPanelRow row = new LazyStackPanelRow( header,
                                                             contentLoad );

        header.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                selectRow( row );
            }
        } );

        addHeaderRow( row );

        addContentRow( row.getContentPanel() );

    }

    private void addHeaderRow(final LazyStackPanelRow row) {
        setWidget( rowIndex,
                   0,
                   row );
        getFlexCellFormatter().setStyleName( rowIndex,
                                             0,
                                             "guvnor-LazyStackPanel-row-header" );
        rowIndex++;
    }

    private void addContentRow(final SimplePanel panel) {
        setWidget( rowIndex++,
                   0,
                   panel );
    }

    private void selectRow(LazyStackPanelRow row) {
        SelectionEvent.fire( this,
                             row );
    }

    public HandlerRegistration addSelectionHandler(SelectionHandler<LazyStackPanelRow> handler) {
        return addHandler( handler,
                           SelectionEvent.getType() );
    }
}
