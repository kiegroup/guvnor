package org.drools.guvnor.client.modeldriven.ui;

import java.util.Date;

import org.drools.guvnor.client.common.ImageButton;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatePickerTextBox extends DatePicker {

    private ImageButton select = new ImageButton( "images/edit_tiny.gif" );

    public DatePickerTextBox(String selectedDate) {
        this( selectedDate,
              defaultFormat );
    }

    public DatePickerTextBox(String selectedDate,
                             String visualFormat) {
        solveVisualFormat( visualFormat );

        visualFormatFormatter = DateTimeFormat.getFormat( visualFormat );

        datePickerPopUp = new DatePickerPopUp( new ClickListener() {
                                                   public void onClick(Widget arg0) {
                                                       Date date = fillDate();

                                                       textWidget.setText( visualFormatFormatter.format( date ) );

                                                       valueChanged();
                                                       makeDirty();
                                                       datePickerPopUp.hide();
                                                   }
                                               },
                                               visualFormatFormatter );

        select.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                datePickerPopUp.setPopupPosition( textWidget.getAbsoluteLeft(),
                                                  textWidget.getAbsoluteTop() + 20 );

                if ( textWidget.getText() != null && "".equals( textWidget.getText() ) ) {
                    textWidget.setText( visualFormatFormatter.format( new Date() ) );
                }

                datePickerPopUp.setDropdowns( visualFormatFormatter,
                                              textWidget.getText() );
                datePickerPopUp.show();
            }
        } );

        if ( selectedDate != null && !selectedDate.equals( "" ) ) {
            textWidget.setText( selectedDate );

            // Check if there is a valid date set. If not, set this date.
            try {
                visualFormatFormatter.parse( selectedDate );
            } catch ( Exception e ) {
                selectedDate = null;
            }
        }

        textWidget.addFocusListener( new FocusListener() {
            public void onFocus(Widget arg0) {
            }

            public void onLostFocus(Widget arg0) {
                TextBox box = (TextBox) arg0;
                textWidget.setText( box.getText() );
                valueChanged();
                makeDirty();
                datePickerPopUp.hide();
            }
        } );

        panel.add( textWidget );
        panel.add( select );
        initWidget( panel );
    }

    public void clear() {
        textWidget.setText( "" );
    }
}
