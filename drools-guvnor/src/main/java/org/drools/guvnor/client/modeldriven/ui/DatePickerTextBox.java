package org.drools.guvnor.client.modeldriven.ui;

import java.util.Date;

import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.ValueChanged;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatePickerTextBox extends DatePicker {

    DatePickerPopUp               datePickerPopUp = new DatePickerPopUp( new ClickListener() {
                                                      public void onClick(Widget arg0) {
                                                          // Set the date from the dropdowns
                                                          Date date = formatter.parse( textWidget.getText() );

                                                          // years
                                                          date.setYear( Integer.parseInt( datePickerPopUp.years.getItemText( datePickerPopUp.years.getSelectedIndex() ) ) - 1900 );
                                                          // months
                                                          date.setMonth( datePickerPopUp.months.getSelectedIndex() );
                                                          // days
                                                          date.setDate( datePickerPopUp.dates.getSelectedIndex() + 1 );

                                                          textWidget.setText( formatter.format( date ) );

                                                          valueChanged();
                                                          makeDirty();
                                                          datePickerPopUp.hide();
                                                      }
                                                  } );
    ImageButton                        select          = new ImageButton("images/edit_tiny.gif");

    public DatePickerTextBox(String selectedDate) {
        this( selectedDate,
              defaultFormat );
    }

    public DatePickerTextBox(String selectedDate,
                             String visualFormat) {
        this.visualFormat = visualFormat;

        if ( visualFormat == null || visualFormat.equals( "default" ) || visualFormat.equals( "" ) ) {
            visualFormat = defaultFormat;
        }

        formatter = DateTimeFormat.getFormat( visualFormat );

        select.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                datePickerPopUp.setPopupPosition( textWidget.getAbsoluteLeft(),
                                                  textWidget.getAbsoluteTop() + 20 );

                if ( textWidget.getText() != null && "".equals( textWidget.getText() ) ) {
                    textWidget.setText( formatter.format( new Date() ) );
                }
                
                datePickerPopUp.setDropdowns( formatter.parse( textWidget.getText() ) );
                datePickerPopUp.show();
            }
        } );


        if ( selectedDate != null && !selectedDate.equals( "" ) ) {
            textWidget.setText( selectedDate );
            
            // Check if there is a valid date set. If not, set this date.
            try {
                formatter.parse( selectedDate );
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


}
