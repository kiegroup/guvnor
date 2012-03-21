package org.drools.guvnor.client.common;


import org.drools.guvnor.client.resources.Images;

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


public class LazyStackPanelHeader extends AbstractLazyStackPanelHeader {

    private static Images images = GWT.create( Images.class );

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

    public void expand() {
        if ( !expanded ) {
            onTitleClicked();
        }
    }

    public void collapse() {
        if ( expanded ) {
            onTitleClicked();
        }
    }

    private void setIconImage() {
        if ( expanded ) {
            icon.setResource( images.collapse() );
        } else {
            icon.setResource( images.expand() );
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
