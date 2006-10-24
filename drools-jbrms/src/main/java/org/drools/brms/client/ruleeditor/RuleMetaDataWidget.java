package org.drools.brms.client.ruleeditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This displays the metadata for a versionable asset.
 * Also can be editable.
 * 
 * @author Michael Neale
 */
public class RuleMetaDataWidget extends Composite {

	private FlexTable layout = new FlexTable();
    private FlexCellFormatter formatter = layout.getFlexCellFormatter();
	private int numInLayout = 0;
	
	
	public RuleMetaDataWidget() {
        initWidget( layout );
        
        addHeader("images/new_wiz.gif", "Underage price 1");
        addAttribute(new Label("type"), new TextBox());
        addAttribute(new Label("description"), new TextArea() );
        
	}

    
    /**
     * Adds a header at the top.
     */
    private void addHeader(String image, String title) {
        layout.setWidget( 0, 0, new Image(image) );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        
        layout.setWidget( 0, 1, new Label(title) );
        numInLayout++;
    }


    /**
     * Add a widget to the "form".
     */
    private void addAttribute(Widget label,
                     Widget editor) {
        layout.setWidget( numInLayout, 0, label );
        formatter.setAlignment( numInLayout, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP );
        layout.setWidget( numInLayout, 1, editor );
        formatter.setAlignment( numInLayout, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        
        numInLayout++;
        
    }




	
	
}
