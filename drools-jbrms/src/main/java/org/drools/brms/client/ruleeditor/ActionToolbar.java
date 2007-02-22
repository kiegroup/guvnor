package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.RulePackageSelector;
import org.drools.brms.client.common.StatusChangePopup;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
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
    private String uuid;
    private HTML state;

    
    public ActionToolbar(final RuleAsset asset,
                          
                         final Command checkin, 
                         final Command minimiseMaximise, 
                         boolean readOnly) {

        this.metaData = asset.metaData;
        this.checkin = checkin;
        this.uuid = asset.uuid;
        this.state = new HTML();
        String status = metaData.state;

        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        HorizontalPanel saveControls = new HorizontalPanel();
        setState(status);
        
        
        saveControls.add( state );
        
        if (!readOnly) {
        controls( minimiseMaximise,
                  formatter,
                  saveControls );

        }
        
        initWidget( layout );
        setWidth( "100%" );
    }

    /**
     * Sets the visible status display.
     */
    private void setState(String status) {
        state.setHTML( "Status: <b>[" + status + "]</b>");        
    }

    private void controls(final Command minimiseMaximise,
                          FlexCellFormatter formatter,
                          HorizontalPanel saveControls) {
        Image editState = new Image("images/edit.gif");
        editState.setTitle( "Change status." );
        editState.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showStatusChanger(w);
            }


        } );
        saveControls.add( editState );
        
        
        layout.setWidget( 0, 0, saveControls );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE );
        
        
        
        //Image save = new Image("images/save_edit.gif");
        Button save = new Button("Save changes");
        save.setTitle( "Check in changes." );        
        save.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                doCheckinConfirm(w);
            }
        });
        
        saveControls.add( save );
        
        Button copy = new Button("Copy");
        copy.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                doCopyDialog(w);
            }
        } );
        
        saveControls.add( copy );
        
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
    
    protected void doCopyDialog(Widget w) {
        final FormStylePopup form = new FormStylePopup("images/rule_asset.gif", "Copy this item");
        final TextBox newName = new TextBox();
        final RulePackageSelector newPackage = new RulePackageSelector();
        form.addAttribute( "New name:", newName );
        form.addAttribute( "New package:", newPackage );
        
        Button ok = new Button("Create copy");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                RepositoryServiceFactory.getService().copyAsset( uuid, newPackage.getSelectedPackage(), newName.getText(), 
                                                                 new GenericCallback() {
                                                                    public void onSuccess(Object data) {
                                                                        completedCopying(newName.getText(), newPackage.getSelectedPackage());
                                                                        form.hide();
                                                                    }


                });
            }
        } );
        form.addAttribute( "", ok );
        
        form.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop());
        form.show();
        
    }
    
    private void completedCopying(String name, String pkg) {
        Window.alert( "Created a new item called [" + name + "] in package: [" + pkg + "] successfully." );
        
    }

    /**
     * Called when user wants to checkin.
     */
    protected void doCheckinConfirm(Widget w) {
        
        final CheckinPopup pop = new CheckinPopup(w.getAbsoluteLeft(), w.getAbsoluteTop(), "Check in changes.");
        pop.setCommand( new Command() {

            public void execute() {
                metaData.checkinComment = pop.getCheckinComment();
                checkin.execute();
                
            }

        });
        pop.show();
      
        
    }

    
    
    /**
     * Show the stats change popup.
     */
    private void showStatusChanger(Widget w) {
        final StatusChangePopup pop = new StatusChangePopup(uuid, false);
        pop.setChangeStatusEvent(new Command() {
            public void execute() {
                setState( pop.getState() );
            }                    
        });
        pop.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
        pop.show();
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
