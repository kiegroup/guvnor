package org.drools.brms.client.breditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableLine extends Composite {
    
    /** The main panel of the composite */
    private Panel panel;
    
    /** This is the list of widgets that are used to display/capture data 
     * Should be Label, TextBox or Button (for editing mode)
     */
    private List widgets = new ArrayList();
    
    /**
     * Obviously to keep state of the widget when switching modes.
     */
    private boolean readOnly = true;
    
    public EditableLine() {
        initData();
        
        panel = new HorizontalPanel();
        initWidget( panel ); 
        makeReadOnly();
    }

    private void initData() {
        widgets.add( new Label("The persons name is ") );
        TextBox box = new TextBox();
        box.setVisibleLength( 4 );        
        widgets.add( box );
        
        Button edit = new Button(".");
        edit.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                if (readOnly) {
                    readOnly = false;
                    makeEditable();
                } else {
                    readOnly = true;
                    makeReadOnly();
                }
            }
            
        });
        widgets.add( edit );
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
            } else {
                panel.add( element );
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
