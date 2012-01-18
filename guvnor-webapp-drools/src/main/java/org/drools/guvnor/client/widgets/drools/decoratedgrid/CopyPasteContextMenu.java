/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.CopyRowEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;

/**
 * A context menu for the copying\pasting rows
 */
public class CopyPasteContextMenu extends AbstractContextMenu {

    private int             rowIndex;
    private int             sourceRowIndex = -1;
    private int             targetRowIndex = -1;

    private ContextMenuItem itemCopy;
    private ContextMenuItem itemPaste;
    
    private Constants                  constants = GWT.create( Constants.class );

    public CopyPasteContextMenu(final EventBus eventBus) {
        super( eventBus );

        itemCopy = new ContextMenuItem( constants.Copy(),
                                        true,
                                        new ClickHandler() {

                                            public void onClick(ClickEvent event) {
                                                itemPaste.setEnabled( true );
                                                sourceRowIndex = rowIndex;
                                                hide();
                                            }

                                        } );
        addContextMenuItem( itemCopy );

        itemPaste = new ContextMenuItem( constants.Paste(),
                                         false,
                                         new ClickHandler() {

                                             public void onClick(ClickEvent event) {
                                                 targetRowIndex = rowIndex;
                                                 CopyRowEvent cpre = new CopyRowEvent( sourceRowIndex,
                                                                                       targetRowIndex );
                                                 eventBus.fireEvent( cpre );
                                                 hide();
                                             }

                                         } );
        addContextMenuItem( itemPaste );
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void clearCopy() {
        sourceRowIndex = -1;
        itemPaste.setEnabled( false );
    }

}
