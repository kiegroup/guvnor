package org.drools.guvnor.client.util;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class AddButton extends Composite
    implements
    HasClickHandlers {

    private Image plusButton = new Image( "images/new_item.gif" );

    private Label textLabel  = new Label();

    public AddButton() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add( plusButton );
        panel.add( textLabel );

        initWidget( panel );
        setStyleName( "guvnor-cursor" );
    }

    public void setText(String text) {
        textLabel.setText( text );
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        textLabel.addClickHandler( handler );
        return plusButton.addClickHandler( handler );
    }
}
