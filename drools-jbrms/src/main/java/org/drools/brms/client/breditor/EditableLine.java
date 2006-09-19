package org.drools.brms.client.breditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/** This encapsulates a DSL line component of a rule. */
public class EditableLine extends Composite {
    
    /** The main panel of the composite. */
    private Panel panel;
   
    /**
     * These buttons are for use by the host panel only.
     */
    public Image removeButton = new Image("images/clear_item.gif");
    public Image shuffleUpButton = new Image("images/shuffle_up.gif");
    public Image shuffleDownButton = new Image("images/shuffle_down.gif");
    /** 
     * This is the list of widgets that are used to display/capture data 
     * Should be Label, TextBox or Button (for editing mode).
     */
    private Widget[] widgets;
    
    public EditableLine(Widget[] items) {
        widgets = items;
        panel = new HorizontalPanel();
        initWidget( panel ); 
        makeReadOnly();
        
    }
    
    public void makeReadOnly() {
        panel.clear();
        panel.add( new Label(toString()) );
        this.removeButton.setVisible( false );
        this.shuffleUpButton.setVisible( false );
        this.shuffleDownButton.setVisible( false );
    }
    
    public void makeEditable() {
        panel.clear();        
        for ( int i = 0; i < widgets.length; i++ ) {
            panel.add( widgets[i] );            
        }
        this.removeButton.setVisible( true );
        //this.shuffleDownButton.setVisible( true );
        //this.shuffleUpButton.setVisible( true );        
    }
    
    /**
     * Returns the content.
     */
    public String toString() {
        String result = "";
        for ( int i=0; i < widgets.length; i++ ) {
            Widget element = widgets[i];
            if (element instanceof Label) {
                result = result + ((Label) element).getText();
            } else if (element instanceof TextBox){
                result = result + ((TextBox) element).getText();
            }            
        }
        return result;
    }
    


     
}
