package org.drools.guvnor.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
public class PopupTitleBar extends Composite
    implements
    HasAllMouseHandlers {

    interface PopupTitleBarBinder
        extends
        UiBinder<Widget, PopupTitleBar> {
    }

    private static PopupTitleBarBinder uiBinder = GWT.create( PopupTitleBarBinder.class );

    @UiField
    Label                              titleLabel;

    @UiField
    ImageButton                        closeButton;

    public PopupTitleBar(String title) {

        initWidget( uiBinder.createAndBindUi( this ) );

        //        HTMLPanel htmlPanel = (HTMLPanel) getWidget();

        titleLabel.setText( title );

    }

    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler( handler,
                              MouseMoveEvent.getType() );
    }

    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addDomHandler( handler,
                              MouseOutEvent.getType() );
    }

    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler( handler,
                              MouseOverEvent.getType() );
    }

    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler( handler,
                              MouseUpEvent.getType() );
    }

    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return addDomHandler( handler,
                              MouseWheelEvent.getType() );
    }

    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler( handler,
                              MouseDownEvent.getType() );
    }

}
