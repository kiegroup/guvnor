package org.drools.brms.client.ruleeditor;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This provides a popup for editing a category (name etc).
 * Mainly this is for creating a new category.
 */
public class NewRuleWizard extends PopupPanel {

    private String type;
    private FlexTable table = new FlexTable(); //Using this table for the form layout
    private TextBox name = new TextBox();
    private TextArea description = new TextArea();
    
    
    /** This is used when creating a new category */
    public NewRuleWizard(String type) {
        super(true);
        this.type = type;
        
        table.setWidget( 0, 0, new Image("images/edit_category.gif") );
        
        table.setWidget( 0, 1, new Label(getTitle( type )));
        
        
        table.setWidget( 1, 0, new Label("Rule name") );
        table.setWidget( 1, 1, name );
        
        description.setVisibleLines( 4 );
        table.setWidget( 2, 0, new Label("Initial Description") );
        table.setWidget( 2, 1, description );
        
        Button ok = new Button("OK");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                ok();
            }
            
        });
        
        table.setWidget( 3, 0, ok );
        
        Button cancel = new Button("Cancel");
        cancel.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                cancel();                
            }
            
        });
        
        table.setWidget( 3, 1, cancel );
        
        add( table );
        setStyleName( "ks-popups-Popup" );
    }

    private String getTitle(String type) {
            return "Create a new " + type;
    }
    
    void ok() {
        
        hide();
    }
    
    void cancel() {
        hide();
    }
    
}
