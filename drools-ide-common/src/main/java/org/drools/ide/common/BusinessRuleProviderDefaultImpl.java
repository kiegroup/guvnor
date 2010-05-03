package org.drools.ide.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.drools.Service;
import org.drools.compiler.BusinessRuleProvider;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.server.util.BRDRLPersistence;
import org.drools.guvnor.server.util.BRXMLPersistence;
import org.drools.io.Resource;

public class BusinessRuleProviderDefaultImpl implements Service, BusinessRuleProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.compiler.BusinessRuleProvider#getKnowledgeReader(org.drools
	 * .io.Resource, org.drools.lang.Expander)
	 */
	public Reader getKnowledgeReader(Resource ruleResource) throws IOException {
		String brl = loadBrlFile(ruleResource.getReader());
		RuleModel model = BRXMLPersistence.getInstance().unmarshal(brl);
		return new StringReader(BRDRLPersistence.getInstance().marshal(model));
	}

	private String loadBrlFile(final Reader drl) throws IOException {
		final StringBuilder buf = new StringBuilder();
		final BufferedReader input = new BufferedReader(drl);
		String line = null;
		while ((line = input.readLine()) != null) {
			buf.append(line);
			buf.append("\n");
		}
		return buf.toString();
	}
}
