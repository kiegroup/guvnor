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

package org.drools.ide.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.drools.Service;
import org.drools.compiler.BusinessRuleProvider;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRDRLPersistence;
import org.drools.ide.common.server.util.BRXMLPersistence;
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
