package org.drools.brms.client.breditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/** This encapsulates a DSL line component of a rule */
public class EditableLine extends Composite {
    
    /** The main panel of the composite */
    private Panel panel;
    
    /** This is the list of widgets that are used to display/capture data 
     * Should be Label, TextBox or Button (for editing mode)
     */
    private Widget[] widgets;
    
    /**
     * Obviously to keep state of the widget when switching modes.
     */
    private boolean readOnly = true;
    
    public EditableLine(Widget[] items) {
        widgets = items;
        
        
        panel = new HorizontalPanel();
        initWidget( panel ); 
        makeReadOnly();
    }

        
    
    public void makeReadOnly() {
        readOnly = true;
        panel.clear();
        
        panel.add( new Label(toString()) );
//        for ( Iterator iter = widgets.iterator(); iter.hasNext(); ) {
//            Widget element = (Widget) iter.next();
//            if (element instanceof Label) {                
//                panel.add( element );
//            } else if (element instanceof TextBox) {
//                TextBox box = (TextBox) element;
//                panel.add( new Label(box.getText()) );
//            } else {
//                panel.add( element );
//            }
//        }
    }
    
    public void makeEditable() {
        readOnly = false;
        panel.clear();
        for ( int i = 0; i < widgets.length; i++ ) {
            panel.add( widgets[i] );
        }
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
