package org.drools.brms.client.ruleeditor;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.RulesFeature;
import org.drools.brms.client.breditor.BREditor;
import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.TextData;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
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
    private Command closeCommand;
    
    
    final private SimplePanel   panel = new SimplePanel();
    protected RuleAsset asset;
    
    /**
     * @param UUID The resource to open.
     * @param format The type of resource (may determine what editor is used).
     * @param name The name to be displayed.
     */
    public RuleViewer(RulesFeature parent, String UUID, String format,  String name) {
        this.resourceUUID = UUID;
        this.name = name;
        this.format = format;
        
        //just pad it out a bit, so it gets the layout right - it will be loaded later.
        FlexTable layout = new FlexTable();
        layout.setWidget( 0, 0, new Label("Loading ...") );
      
        //may use format here to determine which service to use in future
        RepositoryServiceFactory.getService().loadRuleAsset( this.resourceUUID, new AsyncCallback() {
            public void onFailure(Throwable e) {
                ErrorPopup.showMessage( e.getMessage() );
            }
            public void onSuccess(Object o) {
                asset = (RuleAsset) o;                
                doWidgets();
            }
            
        });
        
        panel.add( layout );
		initWidget(panel);
	}
    
    
    /**
     * This will actually load up the data (this is called by the callback 
     * when we get the data back from the server,
     * also determines what widgets to load up).
     */
    private void doWidgets() {
        final MetaDataWidget metaWidget = new MetaDataWidget(this.name, false);
        
        final FlexTable layout = new FlexTable();
        
        //now the main layout table
        FlexCellFormatter formatter =  layout.getFlexCellFormatter();
        layout.setWidget( 0, 0, metaWidget );
        formatter.setRowSpan( 0, 0, 3 );
        formatter.setWidth( 0, 0, "40%" );        
        
        //and now the action widgets (checkin/close etc).
        ActionToolbar toolbar = new ActionToolbar(asset.metaData, null, null);
        toolbar.setCloseCommand(new Command() {
            public void execute() {
                closeCommand.execute();
            }            
        });
        
        layout.setWidget( 0, 1, toolbar );
        formatter.setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE );
        
        //REMEMBER: subsequent rows have only one column, doh that is confusing ! 
        //GAAAAAAAAAAAAAAAAAAAAAAAAAAH
        
        //depending on the format, load the appropriate editor
        if (asset.metaData.format.equals( "DSL" )) {
            BREditor ed = new BREditor(asset);
            layout.setWidget( 1, 0, ed );
        } else {
            DefaultRuleContentWidget ed = new DefaultRuleContentWidget(asset);
            layout.setWidget( 1, 0, ed );
        }
                
        
        //the document widget
        final RuleDocumentWidget doco = new RuleDocumentWidget();
        layout.setWidget( 2, 0, doco );
        
        metaWidget.loadData( asset.metaData );
        doco.loadData( asset.metaData );
        
        
        panel.clear();
        panel.setWidget( layout );
    }
    

    /**
     * This needs to be called to allow the opened viewer to close itself.
     * @param c
     */
    public void setCloseCommand(Command c) {
        this.closeCommand = c;
    }



}
