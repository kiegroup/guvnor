package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.DirtableComposite;
import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * This holds the editor and viewer for rule documentation.
 * It will update the model when the text is changed.
 * @author Michael Neale
 *
 */
public class RuleDocumentWidget extends DirtableComposite {

	private TextArea text;
	
	public RuleDocumentWidget(MetaData data) {
//        
//        HorizontalPanel horiz = new HorizontalPanel();
//        
        
		text = new TextArea();
        
//        horiz.add( text );
//        Image max = new Image("images/max_min.gif");
//        max.setTitle( "Show/hide the documentation panel." );
//        max.addClickListener( new ClickListener() {
//            public void onClick(Widget w) {
//                text.setVisible( !text.isVisible() );
//            }            
//        });
//        horiz.add( max );
//        horiz.setWidth( "100%" );
        text.setWidth( "100%" );
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
                makeDirty();
            }            
        });
        if (data.description == null || "".equals(data.description )) {
            text.setText( "<documentation>" );
        }
    }
    
    
	
}
