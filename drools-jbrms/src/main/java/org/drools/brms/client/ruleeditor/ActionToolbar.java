package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
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
    private Command checkin;
    /**
     * TODO: 
     *  * need to somehow refresh on checkin? (or just close?)
     * 
     */
    public ActionToolbar(final MetaData meta, 
                         final Command checkin,
                         final ClickListener changeState, 
                         final Command minimiseMaximise) {

        this.metaData = meta;
        this.checkin = checkin;
        String status = metaData.state;

        HTML state = new HTML("<b>Status: <i>[" + status + "]</i></b>");
        panel.add( state );
        
        Image editState = new Image("images/edit.gif");
        editState.setTitle( "Change state (NOT IMPLEMENTED YET)." );
        panel.add( editState );
        
        Image save = new Image("images/save_edit.gif");
        save.setTitle( "Check in changes." );        
        save.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                doCheckinConfirm();
            }
        });
        
        Image closeImg = new Image("images/close.gif");
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
        
        Image maxMinImage = new Image("images/max_min.gif");
        maxMinImage.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                minimiseMaximise.execute();                
            }            
        });
        
        
        panel.add( save );
        panel.add( closeImg );
        panel.add( new HTML("&nbsp;") );
        panel.add( maxMinImage );
        initWidget( panel );
    }
    
    /**
     * Called when user wants to checkin.
     */
    protected void doCheckinConfirm() {
        final FormStylePopup pop = new FormStylePopup("images/checkin.gif", "Check in changes.");
        final TextArea comment = new TextArea();
        comment.setWidth( "100%" );
        Button save = new Button("Save");
        pop.addAttribute( "Comment", comment );
        pop.addAttribute( "", save);
        
        bindCommentField( comment );
        
        
        save.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                
                checkin.execute();
                pop.hide();
            }
        });
        
        pop.setStyleName( "ks-popups-Popup" );
        pop.setPopupPosition( 200, getAbsoluteTop() );
        pop.show();        
        
    }

    private void bindCommentField(final TextArea comment) {
        comment.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                metaData.checkinComment = comment.getText();
            }
        });
    }

    /**
     * Called when user wants to close, but there is "dirtyness".
     */
    protected void doCloseUnsavedWarning() {
        final FormStylePopup pop = new FormStylePopup("images/warning-large.png", "WARNING: Un-committed changes.");
        Button dis = new Button("Discard");
        pop.addAttribute( "Are you sure you want to discard changes?", dis );
        
        dis.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                closeCommand.execute();
                pop.hide();
            }
        });
        
        pop.setStyleName( "warning-Popup" );
        
        pop.setPopupPosition( 200, getAbsoluteTop() );
        pop.show();
        
    }

    /**
     * This needs to be set to allow the current viewer to be closed.
     */
    public void setCloseCommand(Command c) {
        this.closeCommand = c;
    }
    

    
}
