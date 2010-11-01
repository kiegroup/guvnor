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

package org.drools.ide.common.assistant;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.ide.common.assistant.option.AssistantOption;
import org.drools.ide.common.assistant.option.ReplaceAssistantOption;
import org.drools.ide.common.assistant.processor.AbstractRuleAssistantProcessor;
import org.drools.ide.common.assistant.processor.DRLRefactorProcessor;

public class DRLAssistantTest extends TestCase {

	private AbstractRuleAssistantProcessor ruleAssistant;
	private String rule;

	@Override
	protected void setUp() throws Exception {
		ruleAssistant = new DRLRefactorProcessor();
		rule = "package org.drools.assistant.test;\n\n";
		rule += "import org.drools.assistant.test.model.Company;\n";
		rule += "IMPORT org.drools.assistant.test.model.Employee;\n\n";
		rule += "import function org.drools.assistant.model.Class1.anotherFunction \n";
		rule += "import		function org.drools.assistant.model.Class1.mathFunction \n";
		rule += "global     org.drools.assistant.test.model.Class2    results \n";
		rule += "GLOBAL org.drools.assistant.test.model.Class3 current\n"; 
		rule += "expander help-expander.dsl\n";
		rule += "query \"all clients\"\n"; 
		rule += "	result : Clients()\n";
		rule += "end\n";
		rule += "query \"new query\"\n";
		rule += "	objects : Clients()\n";
		rule += "end\n";
		rule += "function String hello(String name) {\n";
		rule += "    return \"Hello \"+name+\"!\";\n";
		rule += "}\n";
		rule += "function String helloWithAge(String name, Integer age) {\n";
		rule += "    return \"Hello2 \"+name+\"! \" + age;\n";
		rule += "}\n";
		rule += "rule   \"My Test Rule\"\n";
		rule += "when\n";
		rule += "	$employee : Employee($company : company, $company1 : oldcompany, $age : age > 80, salary > 400)\n";
		rule += "	$result : Company(company==$company, retireAge <= $age)\n"; 
		rule += "then\n";
		rule += "	System.out.println(\"can retire\")\n";
		rule += "end\n";
		rule += "rule   \"My Second Rule\"\n";
		rule += "when\n";
		rule += "	Driver(licence = 1234, $name : name)\n";
		rule += "	$car : Car(company : $company, ownerLicense == licence, year == 2009)\n"; 
		rule += "then\n";
		rule += "	System.out.println(\"licence 1234 has a new car\")\n";
		rule += "end\n";
	}

	public void testAssignSalaryFieldToVariable() throws Exception {
		List<AssistantOption> options = ruleAssistant.getRuleAssistant(rule, 780);
		assertEquals(options.size(), 1);
		ReplaceAssistantOption assistantOption = (ReplaceAssistantOption) options.get(0);
		Assert.assertEquals("\t$employee : Employee($company : company, $company1 : oldcompany, $age : age > 80, salary $ : > 400)", assistantOption.getContent());
	}

	public void testDontAssignFieldInsideRHS() throws Exception {
		List<AssistantOption> options = ruleAssistant.getRuleAssistant(rule, 840);
		assertEquals(options.size(), 0);
	}

	public void testAssignLicenseFromSecondRule() throws Exception {
		List<AssistantOption> options = ruleAssistant.getRuleAssistant(rule, 930);
		assertEquals(options.size(), 1);
		ReplaceAssistantOption assistantOption = (ReplaceAssistantOption) options.get(0);
		Assert.assertEquals("\tDriver($licence : licence = 1234, $name : name)", assistantOption.getContent());
	}

}
