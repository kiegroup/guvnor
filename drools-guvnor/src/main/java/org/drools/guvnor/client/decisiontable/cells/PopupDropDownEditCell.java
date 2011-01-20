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
package org.drools.guvnor.client.decisiontable.cells;

import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * A Popup drop-down Editor ;-)
 * 
 * @author manstis
 * 
 */
public class PopupDropDownEditCell extends
        AbstractPopupEditCell<String, String> {

    private final ListBox listBox;

    public PopupDropDownEditCell() {
        super();
        this.listBox = new ListBox();

        // Tabbing out of the ListBox commits changes
        listBox.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                boolean keyTab = event.getNativeKeyCode() == KeyCodes.KEY_TAB;
                boolean keyEnter = event.getNativeKeyCode() == KeyCodes.KEY_ENTER;
                if ( keyEnter
                     || keyTab ) {
                    commit();
                }
            }

        } );

        vPanel.add( listBox );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client
     * .Cell.Context, java.lang.Object,
     * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
     */
    @Override
    public void render(Context context,
                       String value,
                       SafeHtmlBuilder sb) {
        // Get the view data.
        Object key = context.getKey();
        String viewData = getViewData( key );
        if ( viewData != null
             && viewData.equals( value ) ) {
            clearViewData( key );
            viewData = null;
        }

        String s = null;
        if ( viewData != null ) {
            s = viewData;
        } else if ( value != null ) {
            s = value;
        }
        if ( s != null ) {
            sb.append( renderer.render( s ) );
        }
    }

    public void setItems(String[] items) {
        for ( int i = 0; i < items.length; i++ ) {
            String item = items[i].trim();
            if ( item.indexOf( '=' ) > 0 ) {
                String[] splut = ConstraintValueEditorHelper.splitValue( item );
                this.listBox.addItem( splut[1],
                                      splut[0] );
            } else {
                this.listBox.addItem( item,
                                      item );
            }
        }
    }

    // Commit the change
    @Override
    protected void commit() {
        // Hide pop-up
        Element cellParent = lastParent;
        String oldValue = lastValue;
        Context context = lastContext;
        Object key = context.getKey();
        panel.hide();

        String text = null;
        int selectedIndex = listBox.getSelectedIndex();
        if ( selectedIndex >= 0 ) {
            text = listBox.getValue( selectedIndex );
        }

        // Update values
        setViewData( key,
                     text );
        setValue( context,
                  cellParent,
                  oldValue );
        if ( valueUpdater != null ) {
            valueUpdater.update( text );
        }
    }

    // Start editing the cell
    @Override
    protected void startEditing(final Element parent,
                                String value,
                                Context context) {

        Object key = context.getKey();
        String viewData = getViewData( key );
        String text = (viewData == null) ? value : viewData;

        // Select the appropriate item
        boolean emptyValue = (text == null);
        if ( emptyValue ) {
            listBox.setSelectedIndex( 0 );
        } else {
            for ( int i = 0; i < listBox.getItemCount(); i++ ) {
                if ( listBox.getValue( i ).equals( text ) ) {
                    listBox.setSelectedIndex( i );
                    break;
                }
            }
        }

        panel.setPopupPositionAndShow( new PositionCallback() {
            public void setPosition(int offsetWidth,
                                    int offsetHeight) {
                panel.setPopupPosition( parent.getAbsoluteLeft()
                                                + offsetX,
                                        parent.getAbsoluteTop()
                                                + offsetY );

                // Focus the first enabled control
                Scheduler.get().scheduleDeferred( new ScheduledCommand() {

                    public void execute() {
                        listBox.setFocus( true );
                    }

                } );
            }
        } );

    }

}
