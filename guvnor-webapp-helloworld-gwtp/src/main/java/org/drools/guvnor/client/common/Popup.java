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

public abstract class Popup extends PopupPanel {

    private boolean dragged       = false;
    private int     dragStartX;
    private int     dragStartY;

    private Command afterShowEvent;
    private boolean fixedLocation = false;

    public Popup() {
        setGlassEnabled( true );
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

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setHorizontalAlignment( VerticalPanel.ALIGN_RIGHT );

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

        verticalPanel.add( titleBar );

        Widget content = getContent();

        content.setWidth( "100%" );
        verticalPanel.add( content );
        add( verticalPanel );

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
