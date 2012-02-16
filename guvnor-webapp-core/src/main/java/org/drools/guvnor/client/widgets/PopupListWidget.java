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
package org.drools.guvnor.client.widgets;

import org.drools.guvnor.client.common.Popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A popup that can contain a list of items
 */
public class PopupListWidget extends Popup {

    protected int           minimumWidth  = 500;
    protected int           minimumHeight = 200;

    @UiField
    protected ScrollPanel   listContainer;

    @UiField
    protected VerticalPanel list;

    @UiField
    protected Button        cmdOk;

    private Widget          popupContent;

    interface PopupListWidgetBinder
        extends
        UiBinder<Widget, PopupListWidget> {
    }

    private static PopupListWidgetBinder uiBinder = GWT.create( PopupListWidgetBinder.class );

    public PopupListWidget() {
        setModal( true );
        this.popupContent = uiBinder.createAndBindUi( this );
        int height = getPopupHeight();
        int width = getPopupWidth();
        listContainer.setHeight( height + "px" );
        listContainer.setWidth( width + "px" );
    }

    public PopupListWidget(int width,
                           int height) {
        setModal( false );
        this.popupContent = uiBinder.createAndBindUi( this );
        listContainer.setHeight( height + "px" );
        listContainer.setWidth( width + "px" );
    }

    public void addListItem(Widget w) {
        this.list.add( w );
    }

    @Override
    public Widget getContent() {
        return popupContent;
    }

    /**
     * Width of pop-up, 75% of the client width or minimumWidth
     * 
     * @return
     */
    private int getPopupWidth() {
        int w = (int) (Window.getClientWidth() * 0.75);
        if ( w < minimumWidth ) {
            w = minimumWidth;
        }
        return w;
    }

    /**
     * Height of pop-up, 75% of the client height or minimumHeight
     * 
     * @return
     */
    protected int getPopupHeight() {
        int h = (int) (Window.getClientHeight() * 0.75);
        if ( h < minimumHeight ) {
            h = minimumHeight;
        }
        return h;
    }

    @UiHandler("cmdOk")
    protected void cmdOkOnClickEvent(ClickEvent event) {
        hide();
    }

}
