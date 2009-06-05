package org.drools.guvnor.client.modeldriven.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.explorer.Preferences;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatePickerLabel extends DatePicker {

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
                                                          labelWidget.setText( textWidget.getText() );

                                                          valueChanged();
                                                          makeDirty();
                                                          panel.clear();
                                                          panel.add( labelWidget );
                                                          datePickerPopUp.hide();
                                                      }
                                                  } );
    
    protected Label               labelWidget   = new Label();

    public DatePickerLabel(String selectedDate) {
        this( selectedDate,
              defaultFormat );
    }

    public DatePickerLabel(String selectedDate,
                      String visualFormat) {
        this.visualFormat = visualFormat;

        if ( visualFormat == null || visualFormat.equals( "default" ) || visualFormat.equals( "" ) ) {
            visualFormat = defaultFormat;
        }

        formatter = DateTimeFormat.getFormat( visualFormat );

        labelWidget.setStyleName( "x-form-field" );

        labelWidget.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                panel.clear();
                panel.add( textWidget );
                datePickerPopUp.setPopupPosition( textWidget.getAbsoluteLeft(),
                                                  textWidget.getAbsoluteTop() + 20 );

                datePickerPopUp.setDropdowns( formatter.parse( textWidget.getText() ) );
                datePickerPopUp.show();
            }
        } );

        // Check if there is a valid date set. If not, set this date.
        try {
            formatter.parse( selectedDate );
        } catch ( Exception e ) {
            selectedDate = formatter.format( new Date() );
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
