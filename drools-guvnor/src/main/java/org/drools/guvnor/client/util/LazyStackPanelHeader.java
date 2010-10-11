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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author rikkola
 *
 */
public class LazyStackPanelHeader extends AbstractLazyStackPanelHeader {

    interface LazyStackPanelHeaderBinder
        extends
        UiBinder<Widget, LazyStackPanelHeader> {
    }

    private static LazyStackPanelHeaderBinder uiBinder           = GWT.create( LazyStackPanelHeaderBinder.class );

    @UiField
    Image                                     icon;

    @UiField
    Label                                     titleLabel;

    private ClickHandler                      expandClickHandler = new ClickHandler() {

                                                                     public void onClick(ClickEvent event) {
                                                                         onTitleClicked();
                                                                     }
                                                                 };

    public LazyStackPanelHeader(String headerText) {

        add( uiBinder.createAndBindUi( this ) );

        titleLabel.setText( headerText );

        icon.addClickHandler( expandClickHandler );
        titleLabel.addClickHandler( expandClickHandler );

        setIconImage();

        addOpenHandler( new OpenHandler<AbstractLazyStackPanelHeader>() {
            public void onOpen(OpenEvent<AbstractLazyStackPanelHeader> event) {
                expanded = true;
                setIconImage();
            }
        } );

        addCloseHandler( new CloseHandler<AbstractLazyStackPanelHeader>() {
            public void onClose(CloseEvent<AbstractLazyStackPanelHeader> event) {
                expanded = false;
                setIconImage();
            }
        } );
    }

    private void setIconImage() {
        if ( expanded ) {
            icon.setUrl( "images/collapse.gif" );
        } else {
            icon.setUrl( "images/expand.gif" );
        }

    }

    private void onTitleClicked() {
        if ( expanded ) {
            CloseEvent.fire( this,
                             this );
        } else {
            OpenEvent.fire( this,
                            this );
        }
    }
}
