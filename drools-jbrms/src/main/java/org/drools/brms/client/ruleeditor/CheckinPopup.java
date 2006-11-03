package org.drools.brms.client.ruleeditor;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * 
 * A popup and confirmation dialog for committing an asset.
 * 
 * @author Michael Neale
 *
 */
public class CheckinPopup extends PopupPanel {

    final private FlexTable layout = new FlexTable();
    final private TextArea comment = new TextArea();
    final private ClickListener okClick;    
    
    public CheckinPopup(ClickListener okClick) {
        super(true);        
        this.okClick = okClick;
        
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        
        layout.setWidget( 0, 0, new Image() );
        layout.setWidget( 1, 0, new Label("Comment:") );
        layout.setWidget( 1, 1, comment );
        Button ok = new Button();
        ok.addClickListener( new ClickListener() {

            public void onClick(Widget w) {

                
            }
            
        });
        layout.setWidget( 2, 0,  ok);
        
        
        add( layout );
    }
    
}
