package org.drools.guvnor.client.modeldriven.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.explorer.Preferences;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatePicker extends DirtyableComposite {

    DatePickerPopUp               datePickerPopUp = new DatePickerPopUp();
    Panel                         panel           = new SimplePanel();
    TextBox                       textWidget      = new TextBox();
    protected Label               labelWidget     = new Label();

    // Format that the text box uses.
    protected String              visualFormat    = "";
    // Format that the system uses.
    protected final static String defaultFormat   = Preferences.getStringPref( "drools.dateformat" );
    protected DateTimeFormat      formatter       = null;

    private List<ValueChanged>    valueChangeds   = new ArrayList<ValueChanged>();

    public DatePicker(String selectedDate) {
        this( selectedDate,
              defaultFormat );
    }

    public DatePicker(String selectedDate,
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

                datePickerPopUp.setDropdowns();
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

    class DatePickerPopUp extends PopupPanel {
        private Constants constants = ((Constants) GWT.create( Constants.class ));

        private ListBox   years     = new ListBox();
        private ListBox   months    = new ListBox();
        private ListBox   dates     = new ListBox();

        //            private ListBox   hours     = new ListBox();
        //            private ListBox   minutes   = new ListBox();

        public DatePickerPopUp() {
            HorizontalPanel horizontalPanel = new HorizontalPanel();

            // Add years
            // Take the current year and add 50 to each sides
            Date now = new Date();
            int year = now.getYear() + 1900 - 50;
            for ( int i = year; i < (year + 100); i++ ) {
                years.addItem( Integer.toString( i ) );
            }
            years.setSelectedIndex( 50 );
            horizontalPanel.add( years );

            // Add months
            months.addItem( constants.January() );
            months.addItem( constants.February() );
            months.addItem( constants.March() );
            months.addItem( constants.April() );
            months.addItem( constants.May() );
            months.addItem( constants.June() );
            months.addItem( constants.July() );
            months.addItem( constants.August() );
            months.addItem( constants.September() );
            months.addItem( constants.October() );
            months.addItem( constants.November() );
            months.addItem( constants.December() );

            months.addChangeListener( new ChangeListener() {
                public void onChange(Widget arg0) {
                    fillDates();
                }
            } );

            horizontalPanel.add( months );

            // Add dates
            fillDates();
            horizontalPanel.add( dates );

            //                // Hours
            //                for ( int i = 0; i < 24; i++ ) {
            //                    hours.addItem( Integer.toString( i ) );
            //                }
            //                horizontalPanel.add( new Label( " - " ) );
            //                horizontalPanel.add( hours );
            //
            //                // Minutes 
            //                for ( int i = 0; i < 60; i++ ) {
            //                    minutes.addItem( Integer.toString( i ) );
            //                }
            //                horizontalPanel.add( new Label( ":" ) );
            //                horizontalPanel.add( minutes );

            Button okButton = new Button( constants.OK() );
            okButton.addClickListener( new ClickListener() {
                public void onClick(Widget arg0) {
                    // Set the date from the dropdowns
                    Date date = formatter.parse( textWidget.getText() );

                    // years
                    date.setYear( Integer.parseInt( years.getItemText( years.getSelectedIndex() ) ) - 1900 );
                    // months
                    date.setMonth( months.getSelectedIndex() );
                    // days
                    date.setDate( dates.getSelectedIndex() + 1 );

                    textWidget.setText( formatter.format( date ) );
                    labelWidget.setText( textWidget.getText() );

                    valueChanged();
                    makeDirty();
                    panel.clear();
                    panel.add( labelWidget );
                    datePickerPopUp.hide();
                }
            } );
            horizontalPanel.add( okButton );

            add( horizontalPanel );
        }

        /**
        * Sets the current year, month ect to dropdowns.
        */
        public void setDropdowns() {
            Date date = formatter.parse( textWidget.getText() );

            // Set year
            for ( int i = 0; i < years.getItemCount(); i++ ) {
                if ( years.getValue( i ).equals( (date.getYear() + 1900) + "" ) ) {
                    years.setSelectedIndex( i );
                    break;
                }
            }
            // month
            months.setSelectedIndex( date.getMonth() );
            // day
            dates.setSelectedIndex( date.getDate() - 1 );
            //                // hours
            //                hours.setSelectedIndex( date.getHours() );
            //                // minutes
            //                minutes.setSelectedIndex( date.getMinutes() );
        }

        private void fillDates() {
            setVisible( false );

            dates.clear();

            // Check month 
            int days = daysInMonth( months.getSelectedIndex() + 1 );

            for ( int i = 1; i <= days; i++ ) {
                dates.addItem( Integer.toString( i ) );
            }

            setVisible( true );
        }

        private int daysInMonth(int month) {
            switch ( month ) {
                case 2 :
                    // Can be 28 or 29, returns 29 just in case
                    return 29;
                case 4 :
                case 6 :
                case 9 :
                case 11 :
                    return 30;
                default :
                    return 31;
            }
        }
    }

    //    public void addChangeListener(ChangeListener changeListener) {
    //        this.changeListener = changeListener;
    //        a
    //    }

    public String getVisualFormat() {
        return this.visualFormat;
    }

    public String getDateString() {
        DateTimeFormat formatter = DateTimeFormat.getFormat( defaultFormat );
        Date date = this.formatter.parse( textWidget.getText() );
        return formatter.format( date );
    }

    private void valueChanged() {
        for ( ValueChanged changed : valueChangeds ) {
            changed.valueChanged( getDateString() );
        }
    }

    public void addValueChanged(ValueChanged listener) {
        valueChangeds.add( listener );
    }

    public void removeValueChanged(ValueChanged listener) {
        valueChangeds.remove( listener );
    }
}
