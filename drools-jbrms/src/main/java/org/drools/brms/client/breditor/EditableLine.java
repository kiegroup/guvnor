package org.drools.brms.client.breditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableLine extends Composite {

    private Panel panel;
    private List widgets = new ArrayList();
    
    
    public EditableLine() {
        widgets.add( new Label("The persons name is ") );
        TextBox box = new TextBox();
        box.setVisibleLength( 4 );
        
        widgets.add( box );
        
        panel = new HorizontalPanel();
        initWidget( panel );        
    }
    
    public void makeReadOnly() {
        panel.clear();
        for ( Iterator iter = widgets.iterator(); iter.hasNext(); ) {
            Widget element = (Widget) iter.next();
            if (element instanceof Label) {                
                panel.add( element );
            } else if (element instanceof TextBox) {
                TextBox box = (TextBox) element;
                panel.add( new Label(box.getText()) );
            }
        }
    }
    
    public void makeEditable() {
        panel.clear();
        for ( Iterator iter = widgets.iterator(); iter.hasNext(); ) {
            Widget element = (Widget) iter.next();
            panel.add( element );
        }        
    }
    
    /**
     * Returns the content.
     */
    public String toString() {
        String result = "";
        for ( Iterator iter = widgets.iterator(); iter.hasNext(); ) {
            Widget element = (Widget) iter.next();
            if (element instanceof Label) {
                result = result + ((Label) element).getText();
            } else {
                result = result + ((TextBox) element).getText();
            }            
        }
        return result;
    }
    
    
    
}
