package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 * @author Michael Neale
 */
public class ActionToolbar extends Composite {

    private HorizontalPanel panel = new HorizontalPanel();
    private Command closeCommand;
    
    private MetaData      metaData;
    /**
     * TODO: 
     *  * Maybe move current state to here from meta data?
     *  * add check for dirty before closing?
     *  * need to somehow refresh on checkin? (or just close?)
     * 
     */
    public ActionToolbar(final MetaData meta, 
                         final ClickListener checkin,
                         final ClickListener changeState) {

        this.metaData = meta;
        
        String status = metaData.state;


        
        Label state = new Label("Status: [" + status + "]   ");
        panel.add( state );
        
        Image editState = new Image("images/edit.gif");
        editState.setTitle( "Change state." );
        panel.add( editState );
        
        Image save = new Image("images/save_edit.gif");
        save.setTitle( "Check in changes." );        
        
        Image closeImg = new Image("images/remove_item.gif");
        closeImg.setTitle( "Close." );
        closeImg.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                if (metaData.dirty) {
                    doCloseUnsavedWarning( );
                } else {
                    //we can actually close
                    closeCommand.execute(  );
                }
                
            }
            
        });
        
        
        panel.add( save );
        panel.add( closeImg );
        initWidget( panel );
    }
    
    protected void doCloseUnsavedWarning() {
        final FormStylePopup pop = new FormStylePopup("images/warning-large.png", "WARNING: Un-committed changes.");
        Button dis = new Button("Discard");
        pop.addAttribute( "Are you sure you want to disgard changes?", dis );
        
        dis.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                closeCommand.execute();
                pop.hide();
            }
        });
        
        pop.setStyleName( "warning-Popup" );
        pop.setPopupPosition( 100, 200 );
        pop.show();
        
    }

    /**
     * This needs to be set to allow the current viewer to be closed.
     */
    public void setCloseCommand(Command c) {
        this.closeCommand = c;
    }
    

    
}
