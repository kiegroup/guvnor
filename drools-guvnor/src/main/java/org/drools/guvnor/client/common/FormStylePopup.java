/*
 * Copyright 2005 JBoss Inc
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This builds on the FormStyleLayout for providing common popup features in a
 * columnar form layout, with a title and a large (ish) icon.
 *
 * @author Michael Neale
 */
public class FormStylePopup {

    private FormStyleLayout form;
    private Popup           dialog;
    private String          title;

    private Integer         width;
    private Integer         height;
    private boolean         modal   = true;
    private int             popLeft = -1;
    private int             popTop;
    private Command         afterShowEvent;

    private boolean         dragged = false;
    private int             dragStartX;
    private int             dragStartY;

    public FormStylePopup(String image,
                          final String title) {

        form = new FormStyleLayout( image,
                                    title );

        this.title = title;

    }

    public FormStylePopup() {
        form = new FormStyleLayout();
    }

    public FormStylePopup(String image,
                          final String title,
                          Integer width) {
        this( image,
              title );
        this.width = width;
    }

    public void clear() {
        this.form.clear();
    }

    public void addAttribute(String label,
                             Widget wid) {
        form.addAttribute( label,
                           wid );
    }

    public void addRow(Widget wid) {
        form.addRow( wid );
    }

    public void hide() {
        if ( dialog != null ) {
            this.dialog.hide();
        }
    }

    public void setPopupPosition(int left,
                                 int top) {
        this.popLeft = left;
        this.popTop = top;
    }

    public void setAfterShow(Command c) {
        this.afterShowEvent = c;
    }

    public void show() {

        dialog = new Popup();

        if ( title != null ) {
            dialog.setTitle( title );
        }
        dialog.setModal( modal );
        if ( width == null ) {
            dialog.setWidth( 430 + "px" );
        } else if ( width != -1 ) {
            dialog.setWidth( width + "px" );
        }

        VerticalPanel p = new VerticalPanel();
        p.setHorizontalAlignment( VerticalPanel.ALIGN_RIGHT );

        final PopupTitleBar titleBar = new PopupTitleBar( this.title );

        titleBar.closeButton.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialog.hide();
            }
        } );
        titleBar.addMouseDownHandler( new MouseDownHandler() {

            public void onMouseDown(MouseDownEvent event) {
                dragged = true;
                dragStartX = event.getRelativeX( dialog.getElement() );
                dragStartY = event.getRelativeY( dialog.getElement() );
                DOM.setCapture( titleBar.getElement() );
            }
        } );
        titleBar.addMouseMoveHandler( new MouseMoveHandler() {

            public void onMouseMove(MouseMoveEvent event) {
                if ( dragged ) {
                    dialog.setPopupPosition( event.getClientX() - dragStartX,
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

        p.add( form );
        dialog.add( p );

        if ( getHeight() != null ) {
            this.dialog.setHeight( getHeight() + "px" );
        }

        if ( popLeft > -1 ) {
            dialog.setPopupPosition( popLeft,
                                     popTop );
            this.dialog.show();
        } else {
            dialog.setPopupPosition( 100,
                                     100 );
            this.dialog.show();

            dialog.center();

            int left = (Window.getClientWidth() - dialog.getOffsetWidth()) >> 1;
            int top = (Window.getClientHeight() - dialog.getOffsetHeight()) >> 1;
            setPopupPosition( Math.max( Window.getScrollLeft() + left,
                                        0 ),
                              Math.max( Window.getScrollTop() + top,
                                        0 ) );
        }

    }

    public boolean isVisible() {
        if ( dialog == null ) return false;
        else return dialog.isVisible();
    }

    public void setModal(boolean m) {
        this.modal = m;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public void setWidth(int i) {
        this.width = new Integer( i );
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    class Popup extends PopupPanel {

        @Override
        public void show() {
            super.show();

            if ( afterShowEvent != null ) {
                afterShowEvent.execute();
            }

        }

    }

}