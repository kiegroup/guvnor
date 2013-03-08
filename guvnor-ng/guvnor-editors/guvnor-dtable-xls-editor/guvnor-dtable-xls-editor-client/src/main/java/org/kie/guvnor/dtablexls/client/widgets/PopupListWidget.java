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
package org.kie.guvnor.dtablexls.client.widgets;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A popup that can contain a list of items
 */
public class PopupListWidget extends Composite {

    interface PopupListWidgetBinder
            extends
            UiBinder<Widget, PopupListWidget> {

    }

    private static PopupListWidgetBinder uiBinder = GWT.create( PopupListWidgetBinder.class );

    @UiField
    Modal popup;

    @UiField
    protected VerticalPanel list;

    @UiField
    Button okButton;

    public PopupListWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
        popup.setDynamicSafe( true );
        popup.setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
    }

    public void show() {
        popup.show();
    }

    public void addListItem( Widget w ) {
        this.list.add( w );
    }

    @UiHandler("okButton")
    public void onOKButtonClick( final ClickEvent e ) {
        popup.hide();
    }

}
