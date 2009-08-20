package org.drools.guvnor.client.modeldriven.ui;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Toni Rikkola
 *
 */
public class DatePickerLabel extends DatePicker {

    protected Label labelWidget = new Label();

    public DatePickerLabel(String selectedDate) {
        this( selectedDate,
              defaultFormat );
    }

    public DatePickerLabel(String selectedDate,
                           String visualFormat) {
        solveVisualFormat( visualFormat );

        visualFormatFormatter = DateTimeFormat.getFormat( this.visualFormat );

        datePickerPopUp = new DatePickerPopUp( new ClickListener() {
                                                   public void onClick(Widget arg0) {
                                                       Date date = fillDate();

                                                       textWidget.setText( visualFormatFormatter.format( date ) );
                                                       labelWidget.setText( textWidget.getText() );

                                                       valueChanged();
                                                       makeDirty();
                                                       panel.clear();
                                                       panel.add( labelWidget );
                                                       datePickerPopUp.hide();
                                                   }
                                               },
                                               visualFormatFormatter );

        labelWidget.setStyleName( "x-form-field" );

        labelWidget.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                panel.clear();
                panel.add( textWidget );
                datePickerPopUp.setPopupPosition( textWidget.getAbsoluteLeft(),
                                                  textWidget.getAbsoluteTop() + 20 );

                datePickerPopUp.setDropdowns( visualFormatFormatter,
                                              textWidget.getText() );
                datePickerPopUp.show();
            }
        } );

        // Check if there is a valid date set. If not, set this date.
        try {
            DateTimeFormat formatter = DateTimeFormat.getFormat( defaultFormat );
            Date date = formatter.parse( selectedDate );
            selectedDate = visualFormatFormatter.format( date );
        } catch ( Exception e ) {
            selectedDate = visualFormatFormatter.format( new Date() );
        }

        if ( selectedDate != null && !selectedDate.equals( "" ) ) {
            textWidget.setText( selectedDate );
            labelWidget.setText( selectedDate );
        }

        textWidget.addFocusListener( new FocusListener() {
            public void onFocus(Widget arg0) {
            }

            public void onLostFocus(Widget arg0) {
                TextBox box = (TextBox) arg0;
                textWidget.setText( box.getText() );
                labelWidget.setText( box.getText() );
                valueChanged();
                makeDirty();
                panel.clear();
                panel.add( labelWidget );
                datePickerPopUp.hide();
            }
        } );

        panel.add( labelWidget );
        initWidget( panel );
    }

}
