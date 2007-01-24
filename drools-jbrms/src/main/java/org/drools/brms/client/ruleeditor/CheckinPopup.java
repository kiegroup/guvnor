package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.FormStylePopup;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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
public class CheckinPopup {

    
    private TextArea comment;
    private Button save;
    private FormStylePopup pop;

    
    public CheckinPopup(int left, int top, String message) {
        pop = new FormStylePopup("images/checkin.gif", message);
        comment = new TextArea();
        comment.setWidth( "100%" );
        save = new Button("Save");
        pop.addAttribute( "Comment", comment );
        pop.addAttribute( "", save);
                
        
        pop.setStyleName( "ks-popups-Popup" );
        pop.setPopupPosition( left, top );
    
    }
    
    public void setCommand(final Command checkin) {
        save.addClickListener( new ClickListener() {
            public void onClick(Widget w) {                
                checkin.execute();
                pop.hide();
            }
        });
    }

    public void show() {
        pop.show();
    }
    
    public String getCheckinComment() {
        return comment.getText();
    }
    
}
