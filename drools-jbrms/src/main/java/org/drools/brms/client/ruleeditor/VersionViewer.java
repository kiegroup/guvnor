package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This shows a historical read only view of an asset, as a popup.
 * Why a popup? well, people can drag it around how they want.
 * 
 * @author Michael Neale
 */
public class VersionViewer extends DialogBox {

    private String versionUUID;
    private String headUUID;
    private Command refresh;

    public VersionViewer(final MetaData head, String versionUUID, String headUUID, Command refresh) {
        super(false);
        
        this.versionUUID = versionUUID;
        this.headUUID = headUUID;
        this.refresh = refresh;
        
        setStyleName( "version-Popup" );        
        
        LoadingPopup.showMessage( "Loading version" );
        
        RepositoryServiceFactory.getService().loadRuleAsset( versionUUID, new GenericCallback() {

            public void onSuccess(Object data) {
                                
                RuleAsset asset = (RuleAsset) data;
                asset.metaData.name = head.name;

                
                setText( "Version number [" + asset.metaData.versionNumber + "] of ["
                         + asset.metaData.name + "]");
                
                FlexTable layout = new FlexTable();
                FlexCellFormatter formatter = layout.getFlexCellFormatter();
                
                Button restore = new Button("Restore this version");
                restore.addClickListener( new ClickListener() {
                    public void onClick(Widget w) {
                        restore(w);
                    }

                });
                layout.setWidget( 0, 0, restore );
                formatter.setHorizontalAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT );
                
                Button close = new Button("Close");
                close.addClickListener( new ClickListener() {
                    public void onClick(Widget w) {
                        hide();                        
                    }                    
                });
                
                layout.setWidget( 0, 1, close );
                
                formatter.setHorizontalAlignment( 0, 1, HasHorizontalAlignment.ALIGN_RIGHT );
                
                RuleViewer viewer = new RuleViewer(asset, true);
                
                
                viewer.setWidth( "100%" );
                layout.setWidget( 1, 0, viewer );
                formatter.setColSpan( 1, 1, 2 );
                layout.setWidth( "100%" );
                
                setWidget(layout);
            }
            
        });
        
        
    }


    private void restore(Widget w) {
        
        final CheckinPopup pop = new CheckinPopup(w.getAbsoluteLeft() + 10, 
                                                  w.getAbsoluteTop() + 10,
                                                  "Restore this version?");
        pop.setCommand( new Command() {
            public void execute() {
                RepositoryServiceFactory.getService().restoreVersion( versionUUID, headUUID, pop.getCheckinComment(), new GenericCallback() {
                    public void onSuccess(Object data) {
                        hide();
                        refresh.execute();                        
                    }
                });                
            }            
        });
        pop.show();
    }                    



}
