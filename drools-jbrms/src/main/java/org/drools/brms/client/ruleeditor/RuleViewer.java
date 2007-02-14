package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * The main layout parent/controller the rule viewer.
 * 
 * @author Michael Neale
 */
public class RuleViewer extends Composite {

    private Command           closeCommand;

    protected RuleAsset       asset;

    private final FlexTable layout;
    private boolean readOnly;

    public RuleViewer(RuleAsset asset) {
        this(asset, false);
    }
    
    /**
     * @param UUID The resource to open.
     * @param format The type of resource (may determine what editor is used).
     * @param name The name to be displayed.
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
    public RuleViewer(RuleAsset asset, boolean historicalReadOnly) {
        this.asset = asset;
        this.readOnly = historicalReadOnly;
        layout = new FlexTable();
        
        doWidgets();
        
        initWidget( this.layout );
        
        LoadingPopup.close();
    }

    /**
     * This will actually load up the data (this is called by the callback 
     * when we get the data back from the server,
     * also determines what widgets to load up).
     */
    private void doWidgets() {
        this.layout.clear();
        
        final MetaDataWidget metaWidget = new MetaDataWidget( this.asset.metaData,
                                                              readOnly, this.asset.uuid, new Command() {

                                                                public void execute() {
                                                                    refreshDataAndView();
                                                                }
            
        });


        metaWidget.setWidth( "100%" );
        metaWidget.setHeight( "100%" );
        //now the main layout table
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        layout.setWidget( 0,
                          0,
                          metaWidget );
        formatter.setRowSpan( 0,
                              0,
                              3 );
        formatter.setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
        formatter.setWidth( 0,
                            0,
                            "40%" );

        //and now the action widgets (checkin/close etc).
        ActionToolbar toolbar = new ActionToolbar( asset,
                                                   new Command() {
                                                       public void execute() {
                                                           doCheckin();
                                                       }
                                                   },
                                                   new Command() {
                                                       public void execute() {
                                                           toggleMetaDataWidget();
                                                       }
                                                   },
                                                   readOnly);
        toolbar.setCloseCommand( new Command() {
            public void execute() {
                closeCommand.execute();
            }
        } );

        layout.setWidget( 0,
                          1,
                          toolbar );
        formatter.setAlignment( 0,
                                1,
                                HasHorizontalAlignment.ALIGN_RIGHT,
                                HasVerticalAlignment.ALIGN_MIDDLE );

        //REMEMBER: subsequent rows have only one column, doh that is confusing ! 
        //GAAAAAAAAAAAAAAAAAAAAAAAAAAH

        layout.setWidget( 1, 0, EditorLauncher.getEditorViewer(asset));
        

        //the document widget
        final RuleDocumentWidget doco = new RuleDocumentWidget(asset.metaData);
        layout.setWidget( 2,
                          0,
                          doco );

    }

    /**
     * This responds to the checkin command.
     */
    void doCheckin() {
        this.layout.clear();
        
        LoadingPopup.showMessage( "Saving, please wait..." );
        RepositoryServiceFactory.getService().checkinVersion( this.asset, new AsyncCallback() {

            public void onFailure(Throwable err) {
                ErrorPopup.showMessage( err.getMessage() );               
            }

            public void onSuccess(Object o) {
                String uuid = (String)o;
                if (uuid == null) {
                    ErrorPopup.showMessage( "Failed to check in the item. Please contact your system administrator." );
                    return;
                }
                
                refreshDataAndView( );
                
            }


            
        });
    }


    /**
     * This will reload the contents from the database, and refresh the widgets.
     */
    public void refreshDataAndView() {
        
        RepositoryServiceFactory.getService().loadRuleAsset( asset.uuid, new AsyncCallback() {
            public void onFailure(Throwable t) {
                ErrorPopup.showMessage( t.getMessage() );
            }
            public void onSuccess(Object a) {
                asset = (RuleAsset) a;
                doWidgets();
                LoadingPopup.close();
            }
        });
    }
    
    /**
     * Calling this will toggle the visibility of the meta-data widget (effectively zooming
     * in the rule asset).
     */
    public void toggleMetaDataWidget() {
       boolean vis = layout.getFlexCellFormatter().isVisible( 0, 0 );
       this.layout.getFlexCellFormatter().setVisible( 0, 0, !vis ); 
    }
    

    /**
     * This needs to be called to allow the opened viewer to close itself.
     * @param c
     */
    public void setCloseCommand(Command c) {
        this.closeCommand = c;
    }

}
