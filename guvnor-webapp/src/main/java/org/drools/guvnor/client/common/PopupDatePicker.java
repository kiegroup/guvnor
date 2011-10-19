/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.common;

import java.util.Date;

import org.drools.guvnor.client.configurations.ApplicationPreferences;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * A Date Picker that renders its value as a Label. When the Label is clicked on
 * a pop-up Date Picker is shown from which the value can be changed. Unlike GWT's
 * DateBox you cannot enter the value as text. This was preferred to prevent 
 * the user from not entering a date which is possible with DateBox.
 */
public class PopupDatePicker extends Composite
    implements
    HasValue<Date>,
    HasValueChangeHandlers<Date> {

    private final Label          lblDate;
    private final PopupPanel     panel;

    private static final String  DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    private Date                 date;
    private final DatePicker     datePicker;
    private final DateTimeFormat format;

    public PopupDatePicker() {

        this.lblDate = new Label();
        this.lblDate.getElement().getStyle().setCursor( Cursor.POINTER );
        this.format = DateTimeFormat.getFormat( DATE_FORMAT );
        this.datePicker = new DatePicker();

        // Pressing ESCAPE dismisses the pop-up loosing any changes
        this.panel = new PopupPanel( true,
                                     true ) {
            @Override
            protected void onPreviewNativeEvent(NativePreviewEvent event) {
                if ( Event.ONKEYUP == event.getTypeInt() ) {
                    if ( event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE ) {
                        panel.hide();
                    }
                }
            }

        };

        // Closing the pop-up commits the change
        panel.addCloseHandler( new CloseHandler<PopupPanel>() {

            public void onClose(CloseEvent<PopupPanel> event) {
                setValue( datePicker.getValue() );
            }
        } );

        // Hide the panel and update our value when a date is selected
        datePicker.addValueChangeHandler( new ValueChangeHandler<Date>() {

            public void onValueChange(ValueChangeEvent<Date> event) {
                setValue( event.getValue() );
                panel.hide();
            }
        } );

        panel.add( datePicker );

        //Clicking on the label causes the DatePicker to be shown
        lblDate.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                datePicker.setValue( getValue() );
                panel.setPopupPositionAndShow( new PositionCallback() {
                    public void setPosition(int offsetWidth,
                                            int offsetHeight) {
                        panel.setPopupPosition( lblDate.getAbsoluteLeft(),
                                                lblDate.getAbsoluteTop() + lblDate.getOffsetHeight() );
                    }
                } );
            }

        } );

        initWidget( lblDate );
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
        return datePicker.addValueChangeHandler( handler );
    }

    public Date getValue() {
        return date;
    }

    public void setValue(Date value) {
        this.date = value;
        this.datePicker.setValue( value );
        lblDate.setText( format.format( value ) );
    }

    public void setValue(Date value,
                         boolean fireEvents) {
        this.date = value;
        this.datePicker.setValue( value,
                                  fireEvents );
        lblDate.setText( format.format( value ) );
    }

}
