package org.drools.brms.client.breditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This is a single field editor for the DSL based business rule editor.
 * @author Michael Neale
 */
public class FieldEditor extends Composite {

    private TextBox box;
    private HorizontalPanel panel = new HorizontalPanel();
    
    public FieldEditor() {
        box = new TextBox();
        box.setStyleName( "dsl-field-TextBox" );
        
        panel.add( new HTML("&nbsp;") );
        panel.add( box );
        panel.add( new HTML("&nbsp;") );
        initWidget( panel );
    }
    
    
    public void setText(String t) {
        box.setText( t );
    }
    
    public void setVisibleLength(int l) {
        box.setVisibleLength( l );
    }
    
    public String getText() {
        return box.getText();
    }
}
