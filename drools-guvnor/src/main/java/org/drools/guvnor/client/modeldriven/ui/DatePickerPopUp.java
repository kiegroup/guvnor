package org.drools.guvnor.client.modeldriven.ui;

import java.util.Date;

import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Toni Rikkola
 *
 */
public class DatePickerPopUp extends PopupPanel {
    private Constants constants = ((Constants) GWT.create( Constants.class ));

    protected ListBox   years     = new ListBox();
    protected ListBox   months    = new ListBox();
    protected ListBox   dates     = new ListBox();

    //            private ListBox   hours     = new ListBox();
    //            private ListBox   minutes   = new ListBox();

    public DatePickerPopUp(ClickListener okClickListener) {
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
        okButton.addClickListener( okClickListener );
        horizontalPanel.add( okButton );

        add( horizontalPanel );
    }

    /**
    * Sets the current year, month ect to dropdowns.
    */
    public void setDropdowns(Date date) {
        // Set year
        years.clear();
        int year = date.getYear() + 1900 - 50;
        for ( int i = 0; i < 100; i++ ) {
            years.addItem( Integer.toString( year ) );
            if ( year == (date.getYear() + 1900) ) {
                years.setSelectedIndex( i );
            }
            year++;
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
