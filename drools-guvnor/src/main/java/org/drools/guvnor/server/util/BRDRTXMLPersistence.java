package org.drools.guvnor.server.util;

import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.dt.TemplateModel;

/**
 * This class persists the template rule model to XML and back.
 *
 * This is the 'brl' xml format (Business Rule Language).
 *
 * @author baunax@gmail.com
 * @author dieguitoll@gmail.com
 */
public class BRDRTXMLPersistence extends BRXMLPersistence {
	
	private static final BRLPersistence INSTANCE = new BRDRTXMLPersistence();
	
	private BRDRTXMLPersistence() {
		super();
	}
	
	public static BRLPersistence getInstance() {
		return INSTANCE;
	}
	
	@Override
	public String marshal(RuleModel model) {
		((TemplateModel) model).putInSync();
		return super.marshal(model);
	}
	
	@Override
	public TemplateModel unmarshal(String xml) {
		TemplateModel model = (TemplateModel) super.unmarshal(xml);
		model.putInSync();
		return model;
	}
	
	@Override
	protected RuleModel createEmptyModel() {
		return new TemplateModel();
	}
}
