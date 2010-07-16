/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.util;

import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel;
import org.drools.ide.common.server.util.BRLPersistence;
import org.drools.ide.common.server.util.BRXMLPersistence;

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
