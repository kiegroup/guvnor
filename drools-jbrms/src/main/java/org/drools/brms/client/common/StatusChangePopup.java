package org.drools.brms.client.common;

import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Well this one should be pretty obvious what it does. 
 * I feel like I have wasted valuable time writing this comment, but I hope
 * you enjoyed reading it.
 * 
 * @author Michael Neale
 *
 */
public class StatusChangePopup extends DialogBox {

    private boolean isPackage;
    private String uuid;
    private String newStatus;
    private Command changedStatus;

    public StatusChangePopup(String uuid, boolean isPackage) {
        super();
        
        this.uuid = uuid;
        this.isPackage = isPackage;
        
        setHTML( "<img src='images/status_small.gif'/><b>Change status</b>" );
        HorizontalPanel horiz = new HorizontalPanel();
        final ListBox box = new ListBox();
        
        LoadingPopup.showMessage( "Please wait..." );
        RepositoryServiceFactory.getService().listStates( new GenericCallback() {
            public void onSuccess(Object data) {
                String[] list = (String[]) data;
                for ( int i = 0; i < list.length; i++ ) {
                    box.addItem( list[i] );
                }
                LoadingPopup.close();
            }
        });
        
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                newStatus = box.getItemText( box.getSelectedIndex() );
            }            
        });
     
        horiz.add(box);
        Button ok = new Button("Change status");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                String newState = box.getItemText( box.getSelectedIndex() );
                changeState(newState);
                hide();
            }
        });
        horiz.add( ok );
        
        
        Button close = new Button("Cancel");
        close.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                hide();                
            }            
        });
        horiz.add( close );
        
        
        
        setWidget( horiz );
    }

    /** Apply the state change */
    private void changeState(String newState) {
        LoadingPopup.showMessage( "Updating status..." );
        RepositoryServiceFactory.getService().changeState( uuid, newStatus, isPackage, new GenericCallback() {
            public void onSuccess(Object data) {
                changedStatus.execute();
                LoadingPopup.close();
            }
        });        
    }

    /**
     * Get what the state was changed to.
     */
    public String getState() {        
        return this.newStatus;
    }

    /**
     * set the status change event
     */
    public void setChangeStatusEvent(Command command) {
        this.changedStatus = command;        
    }
}
