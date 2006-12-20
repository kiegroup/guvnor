package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * This holds the editor and viewer for rule documentation.
 * It will update the model when the text is changed.
 * @author Michael Neale
 *
 */
public class RuleDocumentWidget extends Composite {

	private TextArea text;
	
	public RuleDocumentWidget(MetaData data) {
		text = new TextArea();
        text.setVisibleLines( 10 );
        text.setStyleName( "rule-viewer-Documentation" );        
        text.setTitle( "This is rule documentation. Human friendly descriptions of the business logic.");
		initWidget(text);
        loadData(data);
	}

    private void loadData(final MetaData data) {
        text.setText(data.description);
        text.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {                
                data.description = text.getText();
                data.dirty = true;
            }            
        });
    }
    
    
	
}
