package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.breditor.BREditor;
import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.TextData;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * The main layout parent/controller the rule viewer.
 * 
 * @author Michael Neale
 */
public class RuleViewer extends Composite {
	
	private final String resourceUUID;
    private final String name;
    private final String format;
    private MetaData metaData;
    
    
    private FlexTable layout = new FlexTable();
    protected RuleAsset asset;
    
    /**
     * @param UUID The resource to open.
     * @param format The type of resource (will determine what editor is used).
     * @param name The name to be displayed.
     */
    public RuleViewer(String UUID, String format,  String name) {
        this.resourceUUID = UUID;
        this.name = name;
        this.format = format;
        
        //just pad it out a bit, so it gets the layout right - it will be loaded later.
        layout.setWidget( 0, 0, new Label("Loading ...") );
        layout.setWidget( 0, 1, new Label("") );
        layout.setWidget( 1, 0, new Label("") );
        layout.setWidget( 1, 1, new Label("") );
        layout.setWidget( 2, 0, new Label("") );
        layout.setWidget( 2, 1, new Label("") );
        
        RepositoryServiceFactory.getService().loadRuleAsset( this.resourceUUID, new AsyncCallback() {
            public void onFailure(Throwable e) {
                ErrorPopup.showMessage( e.getMessage() );
            }
            public void onSuccess(Object o) {
                asset = (RuleAsset) o;                
                loadAssetData();
            }
            
        });
        
		initWidget(layout);
	}
    
    
    /**
     * This will actually load up the data (this is called by the callback 
     * when we get the data back from the server,
     * also determines what widgets to load up).
     */
    private void loadAssetData() {
        metaData = asset.metaData;
        
        
        final MetaDataWidget metaWidget = new MetaDataWidget(this.name, false);
        
        //now the layout table
        FlexCellFormatter formatter =  layout.getFlexCellFormatter();
        layout.setWidget( 0, 0, metaWidget );
        formatter.setRowSpan( 0, 0, 3 );
        formatter.setWidth( 0, 0, "40%" );        
        
        layout.setWidget( 0, 1, new Label("") );
        
        if (metaData.format.equals( "DSL" )) {
            BREditor ed = new BREditor();
            layout.setWidget( 1, 1, ed );
        } else {
            DefaultRuleContentWidget ed = new DefaultRuleContentWidget((TextData) asset.ruleAsset);
            layout.setWidget( 1, 1, ed );
        }

        final RuleDocumentWidget doco = new RuleDocumentWidget();
        layout.setWidget( 2, 1, doco );
        metaWidget.loadData( metaData );
        doco.loadData( metaData );
    }
    





}
