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
package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;

/**
 * A {@link Cell} used to render a checkbox. The value of the checkbox may be
 * toggled using the ENTER key as well as via mouse click. This implementation
 * is specific to the Internet Explorer family of web-browsers as the mouse
 * interaction needs to be handled as click events not change events.
 */
public class CheckboxCellImplIE extends CheckboxCellImpl {

    /**
     * Construct a new {@link CheckboxCellImplIE}
     * 
     */
    public CheckboxCellImplIE() {
        super( "click",
               "keydown" );
    }

    @Override
    public void onBrowserEvent(Context context,
                               Element parent,
                               Boolean value,
                               NativeEvent event,
                               ValueUpdater<Boolean> valueUpdater) {
        String type = event.getType();

        boolean enterPressed = "keydown".equals( type ) && event.getKeyCode() == KeyCodes.KEY_ENTER;
        if ( "click".equals( type ) || enterPressed ) {
            InputElement input = parent.getFirstChild().cast();
            Boolean isChecked = input.isChecked();

            /*
             * Toggle the value if the enter key was pressed and the cell handles
             * selection or doesn't depend on selection. If the cell depends on
             * selection but doesn't handle selection, then ignore the enter key and
             * let the SelectionEventManager determine which keys will trigger a
             * change.
             */
            if ( enterPressed ) {
                isChecked = !isChecked;
                input.setChecked( isChecked );
            }

            /*
             * Save the new value. However, if the cell depends on the selection, then
             * do not save the value because we can get into an inconsistent state.
             */
            if ( value != isChecked ) {
                setViewData( context.getKey(),
                             isChecked );
            }

            if ( valueUpdater != null ) {
                valueUpdater.update( isChecked );
            }
        }
    }

}
