package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This form style class is to be extended to provide
 * "form style" dialogs (eg in a popup).
 * 
 * @author Michael Neale
 */
public class FormStyleLayout extends Composite {
    
    private FlexTable layout = new FlexTable();
    private FlexCellFormatter formatter = layout.getFlexCellFormatter();
    private int numInLayout = 0;

    public FormStyleLayout(String image, String title) {
        addHeader( image, title );
        initWidget( layout );
    }
    
    /**
     * Add a widget to the "form".
     */
    protected void addAttribute(String lbl,
                     Widget editor) {
        Label label = new Label(lbl);
        layout.setWidget( numInLayout, 0, label );
        formatter.setAlignment( numInLayout, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP );
        layout.setWidget( numInLayout, 1, editor );
        formatter.setAlignment( numInLayout, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        
        numInLayout++;
        
    }
    
    /**
     * Adds a header at the top.
     */
    protected void addHeader(String image, String title) {
        layout.setWidget( 0, 0, new Image(image) );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        
        Label name = new Label(title);
        name.setStyleName( "resource-name-Label" );
        
        layout.setWidget( 0, 1, name );
        numInLayout++;
    }    
    
}
