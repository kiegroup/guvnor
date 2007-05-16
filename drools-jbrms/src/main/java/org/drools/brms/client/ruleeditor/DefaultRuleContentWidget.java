package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;


/**
 * This is the default rule editor widget (just text editor based) - more to come later.
 * @author michael neale
 */
public class DefaultRuleContentWidget extends DirtyableComposite {
	
	private TextArea text;
	final private RuleContentText data;

    final private RuleAsset asset;
    
	public DefaultRuleContentWidget(RuleAsset a) {
        asset = a;
        data = (RuleContentText) asset.content;
        
		text = new TextArea();
		text.setWidth("100%");
		text.setHeight("100%");
		text.setVisibleLines(10);
		text.setText(data.content);
        
        text.setStyleName( "default-text-Area" );
        
        text.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                data.content = text.getText();
                makeDirty();
            }
        }); 
        
		initWidget(text);
	}

}
