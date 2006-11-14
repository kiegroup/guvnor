package org.drools.brms.client.ruleeditor;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 * @author Michael Neale
 */
public class ActionToolbar extends Composite {

    private HorizontalPanel panel = new HorizontalPanel();
    private ClickListener changeState;
    private ClickListener close;
    private ClickListener checkin;
    
    /**
     * TODO: 
     *  * Maybe move current state to here from meta data?
     *  * add check for dirty before closing?
     *  * need to somehow refresh on checkin? (or just close?)
     * 
     */
    public ActionToolbar(ClickListener checkin, ClickListener close, ClickListener changeState) {
        this.checkin = checkin;
        this.close = close;
        this.changeState = changeState;

        ListBox actions = new ListBox();
        actions.addItem( "-- actions --" );
        actions.addItem( "Check in changes", "checkin" );
        actions.addItem( "Change status", "status" );
        
        panel.add( actions );
        
        panel.add( new Label("Current status: ") );
        panel.add( new TextBox() );

        panel.add( new HTML("&nbsp;") );
        
        panel.add( new Label("Close: ") );
        panel.add( new Image("images/remove_item.gif") );
        
        
        

        initWidget( panel );
    }
    
    
}
