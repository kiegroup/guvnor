package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.server.util.BRDRTPersistence;
import org.drools.guvnor.server.util.BRDRTXMLPersistence;
import org.drools.guvnor.server.util.BRLPersistence;

public class TemplateModelHandler extends BRLContentHandler {

	@Override
	protected BRLPersistence getBrlDrlPersistence() {
		return BRDRTPersistence.getInstance();
	}
	
	@Override
	protected BRLPersistence getBrlXmlPersistence() {
		return BRDRTXMLPersistence.getInstance();
	}
	
}
