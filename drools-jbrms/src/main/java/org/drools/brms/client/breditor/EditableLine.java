package org.drools.brms.client.breditor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/** This encapsulates a DSL line component of a rule. */
public class EditableLine extends Composite {
    
    /** The main panel of the composite. */
    private Panel panel;
   
    /** 
     * This is the list of widgets that are used to display/capture data 
     * Should be Label, TextBox or Button (for editing mode).
     */
    private Widget[] widgets;
    
    public EditableLine(String dslLine) {
        widgets = makeWidgets( dslLine );
        //widgets = items;
        panel = new HorizontalPanel();
        initWidget( panel ); 
        makeReadOnly();
        
    }
    
    public void makeReadOnly() {
        panel.clear();
        panel.add( new Label(toString()) );
    }
    
    public void makeEditable() {
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
            } else if (element instanceof FieldEditor){
                result = result + ((FieldEditor) element).getText();
            }            
        }
        return result;
    }


    /** 
     * This will take a DSL line item, and split it into widget thingamies for displaying.
     * One day, if this is too complex, this will have to be done on the server side.
     */
    public static Widget[] makeWidgets(String dslLine) {
        List widgets = new ArrayList();
        char[] chars = dslLine.toCharArray();
        FieldEditor currentBox = null;
        Label currentLabel = null;
        for ( int i = 0; i < chars.length; i++ ) {
            char c = chars[i];
            if (c == '{') {
                currentLabel = null;
                currentBox = new FieldEditor(); 
                widgets.add( currentBox );
                
            } else if (c == '}') {
                currentBox.setVisibleLength( currentBox.getText().length() );
                currentBox = null;
            } else {
                if (currentBox == null && currentLabel == null) {
                    currentLabel = new Label();
                    widgets.add( currentLabel );
                }
                if (currentLabel != null) {
                    currentLabel.setText( currentLabel.getText() + c );
                } else if (currentBox != null) {
                    currentBox.setText( currentBox.getText() + c );
                }
                
            }
        }
        Widget[] result = new Widget[widgets.size()];
        for(int i=0; i < result.length; i++) {
            result[i] = (Widget) widgets.get( i );
        }
        return result;
    }



     
}
