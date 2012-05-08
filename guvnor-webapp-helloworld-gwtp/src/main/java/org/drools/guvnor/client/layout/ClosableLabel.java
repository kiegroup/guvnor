package org.drools.guvnor.client.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ClosableLabel extends Composite
    implements
    HasCloseHandlers<ClosableLabel>, HasMouseDownHandlers  {

    interface ClosableLabelBinder
        extends
        UiBinder<Widget, ClosableLabel> {
    }

    private static ClosableLabelBinder uiBinder = GWT.create( ClosableLabelBinder.class );

    @UiField
    Label                              text;

    @UiField
    Image                              closeButton;

    public ClosableLabel(final String title) {
        initWidget( uiBinder.createAndBindUi( this ) );

        text.setText( title );
    }

    @UiHandler("basePanel")
    void showCloseButton(MouseOverEvent event) {
        closeButton.setVisible( true );
    }

    @UiHandler("basePanel")
    void hideCloseButton(MouseOutEvent event) {
        closeButton.setVisible( false );
    }

    @UiHandler("closeButton")
    void closeTab(ClickEvent clickEvent) {
        CloseEvent.fire( this,
                         this );
    }

    public HandlerRegistration addCloseHandler(CloseHandler<ClosableLabel> handler) {
        return addHandler( handler,
                           CloseEvent.getType() );
    }
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler(handler, MouseDownEvent.getType());
   }
}
