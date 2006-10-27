package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * This holds the editor and viewer for rule documentation.
 * 
 * @author Michael Neale
 *
 */
public class RuleDocumentWidget extends Composite {

	private TextArea text;
	
	public RuleDocumentWidget(final MetaData data) {
        
		text = new TextArea();
        text.setVisibleLines( 10 );
		text.setText(data.description);
        text.setStyleName( "rule-viewer-Documentation" );        
        
        text.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                data.description = text.getText();
            }            
        });
        
		initWidget(text);
	}
	
}
