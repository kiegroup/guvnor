package org.drools.brms.client.ruleeditor;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.breditor.BREditor;
import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.TextData;

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
    private MetaData metaData;
    private SimplePanel   panel = new SimplePanel();
    protected RuleAsset asset;
    
    /**
     * @param UUID The resource to open.
     * @param format The type of resource (may determine what editor is used).
     * @param name The name to be displayed.
     */
    public RuleViewer(String UUID, String format,  String name) {
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
        metaData = asset.metaData;
        
        final MetaDataWidget metaWidget = new MetaDataWidget(this.name, false);
        
        FlexTable layout = new FlexTable();
        
        //now the main layout table
        FlexCellFormatter formatter =  layout.getFlexCellFormatter();
        layout.setWidget( 0, 0, metaWidget );
        formatter.setRowSpan( 0, 0, 4 );
        formatter.setWidth( 0, 0, "40%" );        

        
        //and now the action widgets (checkin/close etc).
        layout.setWidget( 1, 1, new ActionToolbar(null, null, null) );
        formatter.setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE );
//        formatter.setWidth( 0, 1, "60%");

//        formatter.setStyleName( 0, 1, "outline-Debug" );
//        formatter.setStyleName( 1, 1, "outline-Debug" );
//        formatter.setStyleName( 2, 1, "outline-Debug" );
//        formatter.setStyleName( 0, 0, "outline-Debug" );
        
        //depending on the format, load the appropriate editor
        if (metaData.format.equals( "DSL" )) {
            BREditor ed = new BREditor();
            layout.setWidget( 2, 1, ed );
        } else {
            DefaultRuleContentWidget ed = new DefaultRuleContentWidget((TextData) asset.content);
            layout.setWidget( 2, 1, ed );
        }
        
        
        
        //the document widget
        final RuleDocumentWidget doco = new RuleDocumentWidget();
        layout.setWidget( 3, 1, doco );
        
        metaWidget.loadData( metaData );
        doco.loadData( metaData );
        
        
        panel.clear();
        panel.setWidget( layout );
    }
    





}
