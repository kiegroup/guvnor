package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.breditor.BREditor;
import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The main layout parent/controller the rule viewer.
 * 
 * @author Michael Neale
 */
public class RuleViewer extends Composite {
	
	private String resourceUUID;
    private String name;
    private String format;
    
    
    /**
     * @param UUID The resource to open.
     * @param format The type of resource (will determine what editor is used).
     * @param name The name to be displayed.
     */
    public RuleViewer(String UUID, String format,  String name) {
        this.resourceUUID = UUID;
        this.name = name;
        this.format = format;
        
		HorizontalPanel horiz = new HorizontalPanel();	
		horiz.setWidth("100%");
		horiz.setHeight("100%");
		VerticalPanel ruleAndDoc = new VerticalPanel();
		
        MetaData data = loadMetaData();
        data.name = name;
        
		horiz.add(new MetaDataWidget(data, false));
		horiz.add(ruleAndDoc);
		
		ruleAndDoc.setWidth("100%");
		ruleAndDoc.setHeight("100%");
		//ruleAndDoc.add(new DefaultRuleContentWidget("when\n\tPerson(age < 42)\nthen\n\tpanic();"));
        BREditor ed = new BREditor();
        ed.setWidth( "100%" );
        ruleAndDoc.add( ed);
		ruleAndDoc.add(new RuleDocumentWidget(data));
		
		initWidget(horiz);
	}

    private MetaData loadMetaData() {
        return new MetaData();
    }

    /**
     * This will kick off the loading of the data.
     */
    public void load() {
        
        
    }

}
