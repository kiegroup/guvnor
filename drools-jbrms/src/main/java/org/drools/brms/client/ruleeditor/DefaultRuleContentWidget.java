package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.rpc.TextData;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;


/**
 * This is the default rule editor widget (just text editor based) - more to come later.
 * @author michael neale
 */
public class DefaultRuleContentWidget extends Composite {
	
	private TextArea text;
	
	public DefaultRuleContentWidget(final TextData data) {
		text = new TextArea();
		text.setWidth("100%");
		text.setHeight("100%");
		text.setVisibleLines(10);
		text.setText(data.content);
        
        text.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                data.content = text.getText();
                data.dirty = true;
            }
        });
        
		initWidget(text);
	}
}
