package org.drools.guvnor.client.modeldriven.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.explorer.Preferences;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class DatePicker extends DirtyableComposite {
    protected Panel               panel         = new HorizontalPanel();
    protected TextBox             textWidget    = new TextBox();

    // Format that the text box uses.
    protected String              visualFormat  = "";
    // Format that the system uses.
    protected final static String defaultFormat = Preferences.getStringPref( "drools.dateformat" );
    protected DateTimeFormat      formatter     = null;

    protected List<ValueChanged>  valueChangeds = new ArrayList<ValueChanged>();

    public String getVisualFormat() {
        return this.visualFormat;
    }

    public String getDateString() {
        DateTimeFormat formatter = DateTimeFormat.getFormat( defaultFormat );
        Date date = this.formatter.parse( textWidget.getText() );
        return formatter.format( date );
    }

    public Date getDate() {
        if ( textWidget.getText() == null || "".equals( textWidget.getText() ) ) {
            return null;
        } else {
            return this.formatter.parse( textWidget.getText() );
        }
    }

    protected void valueChanged() {
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
