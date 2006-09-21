package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.breditor.BREditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The main layout for the rule viewer.
 * 
 * @author Michael Neale
 */
public class RuleView extends Composite {
	
	public RuleView() {
		HorizontalPanel horiz = new HorizontalPanel();	
		horiz.setWidth("100%");
		horiz.setHeight("100%");
		VerticalPanel ruleAndDoc = new VerticalPanel();
		
		horiz.add(new RuleMetaDataWidget("Foobar", "mic", "testing"));
		horiz.add(ruleAndDoc);
		
		ruleAndDoc.setWidth("100%");
		ruleAndDoc.setHeight("100%");
		//ruleAndDoc.add(new DefaultRuleContentWidget("when\n\tPerson(age < 42)\nthen\n\tpanic();"));
        BREditor ed = new BREditor();
        ed.setWidth( "100%" );
        ruleAndDoc.add( ed);
		ruleAndDoc.add(new RuleDocumentWidget("This is a rule telling us when to panic."));
		
		initWidget(horiz);
	}

}
