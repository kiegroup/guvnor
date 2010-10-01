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
package org.drools.guvnor.client.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author rikkola
 *
 */
public abstract class Popup extends PopupPanel {

    private boolean dragged       = false;
    private int     dragStartX;
    private int     dragStartY;

    private Command afterShowEvent;
    private boolean fixedLocation = false;

    public Popup() {
        setWidth( 430 + "px" );
    }

    public void setAfterShow(Command afterShowEvent) {
        this.afterShowEvent = afterShowEvent;
    }

    @Override
    public void show() {

        if ( afterShowEvent != null ) {
            afterShowEvent.execute();
        }

        VerticalPanel p = new VerticalPanel();
        p.setHorizontalAlignment( VerticalPanel.ALIGN_RIGHT );

        final PopupTitleBar titleBar = new PopupTitleBar( getTitle() );

        titleBar.closeButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        } );
        titleBar.addMouseDownHandler( new MouseDownHandler() {

            public void onMouseDown(MouseDownEvent event) {
                dragged = true;
                dragStartX = event.getRelativeX( getElement() );
                dragStartY = event.getRelativeY( getElement() );
                DOM.setCapture( titleBar.getElement() );
            }
        } );
        titleBar.addMouseMoveHandler( new MouseMoveHandler() {

            public void onMouseMove(MouseMoveEvent event) {
                if ( dragged ) {
                    setPopupPosition( event.getClientX() - dragStartX,
                                      event.getClientY() - dragStartY );
                }
            }
        } );
        titleBar.addMouseUpHandler( new MouseUpHandler() {

            public void onMouseUp(MouseUpEvent event) {
                dragged = false;
                DOM.releaseCapture( titleBar.getElement() );
            }
        } );

        p.add( titleBar );

        p.add( getContent() );
        add( p );

        super.show();

        if ( !fixedLocation ) {
            center();
        }
    }

    @Override
    public void setPopupPosition(int left,
                                 int top) {
        super.setPopupPosition( left,
                                top );

        if ( left != 0 && top != 0 ) {
            fixedLocation = true;
        }
    }

    abstract public Widget getContent();

}