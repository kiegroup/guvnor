package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 * @author Michael Neale
 */
public class ActionToolbar extends Composite {

    private FlexTable layout = new FlexTable();
    private Command closeCommand;
    
    private MetaData      metaData;
    private Command checkin;

    
    public ActionToolbar(final MetaData meta, 
                         final Command checkin, 
                         final Command minimiseMaximise, 
                         boolean readOnly) {

        this.metaData = meta;
        this.checkin = checkin;
        String status = metaData.state;

        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        HorizontalPanel saveControls = new HorizontalPanel();
        HTML state = new HTML("<b>Status: <i>[" + status + "]</i></b>");
        saveControls.add( state );
        
        if (!readOnly) {
        controls( minimiseMaximise,
                  formatter,
                  saveControls );

        }
        
        initWidget( layout );
        setWidth( "100%" );
    }

    private void controls(final Command minimiseMaximise,
                          FlexCellFormatter formatter,
                          HorizontalPanel saveControls) {
        Image editState = new Image("images/edit.gif");
        editState.setTitle( "Change state (NOT IMPLEMENTED YET)." );
        saveControls.add( editState );
        
        
        layout.setWidget( 0, 0, saveControls );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE );
        
        
        
        Image save = new Image("images/save_edit.gif");
        save.setTitle( "Check in changes." );        
        save.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                doCheckinConfirm();
            }
        });
        
        saveControls.add( save );
        
        HorizontalPanel windowControls = new HorizontalPanel();
        
        Image maxMinImage = new Image("images/max_min.gif");
        maxMinImage.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                minimiseMaximise.execute();                
            }            
        });
        
        windowControls.add( maxMinImage );
        
        
        
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
        
        windowControls.add( closeImg );
        
        layout.setWidget( 0, 1, windowControls );
        formatter.setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE );
    }
    
    /**
     * Called when user wants to checkin.
     */
    protected void doCheckinConfirm() {
        
        final CheckinPopup pop = new CheckinPopup(200, getAbsoluteTop(), "Check in changes.");
        pop.setCommand( new Command() {

            public void execute() {
                metaData.checkinComment = pop.getCheckinComment();
                checkin.execute();
                
            }

        });
        pop.show();
//        final FormStylePopup pop = new FormStylePopup("images/checkin.gif", "Check in changes.");
//        final TextArea comment = new TextArea();
//        comment.setWidth( "100%" );
//        Button save = new Button("Save");
//        pop.addAttribute( "Comment", comment );
//        pop.addAttribute( "", save);
//        
//        bindCommentField( comment );
//        
//        
//        save.addClickListener( new ClickListener() {
//            public void onClick(Widget w) {
//                
//                checkin.execute();
//                pop.hide();
//            }
//        });
//        
//        pop.setStyleName( "ks-popups-Popup" );
//        pop.setPopupPosition( 200, getAbsoluteTop() );
//        pop.show();        
        
    }

//    private void bindCommentField(final TextArea comment) {
//        comment.addChangeListener( new ChangeListener() {
//            public void onChange(Widget w) {
//                metaData.checkinComment = comment.getText();
//            }
//        });
//    }

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
