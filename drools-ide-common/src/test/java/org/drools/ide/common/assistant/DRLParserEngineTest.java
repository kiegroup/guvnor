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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.ide.common.assistant.engine.DRLParserEngine;
import org.drools.ide.common.assistant.info.drl.DRLContentTypeEnum;
import org.drools.ide.common.assistant.info.drl.DRLRuleRefactorInfo;
import org.drools.ide.common.assistant.info.drl.RuleBasicContentInfo;
import org.drools.ide.common.assistant.info.drl.RuleLineContentInfo;

public class DRLParserEngineTest extends TestCase {

	private String rule;
	private DRLParserEngine engine;
	private DRLRuleRefactorInfo info;

	@Override
	protected void setUp() throws Exception {
		rule = 	"package org.drools.assistant.test;\n\n";
		rule += "import org.drools.assistant.test.model.Company;\n";
		rule += "import org.drools.assistant.test.model.Employee;\n\n";
		rule += "import function org.drools.assistant.model.Class1.anotherFunction \n";
		rule += "import		function org.drools.assistant.model.Class1.mathFunction \n";
		rule += "global     org.drools.assistant.test.model.Class2    results \n";
		rule += "global org.drools.assistant.test.model.Class3 current\n"; 
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
		rule += "	$employee : Employee($company : company, $age : age > 80, salary > 400)\n";
		rule += "	$result : Company(company == $company, retireAge <= $age)\n"; 
		rule += "then\n";
		rule += "	System.out.println(\"can retire\")\n";
		rule += "end\n";

		engine = new DRLParserEngine(rule);

	}

	public void testExecuteEngine() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(123);
		Assert.assertEquals(true, content!=null);
	}

	public void testImport() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(9);
		Assert.assertEquals(true, content!=null);
	}

	public void testNothingInteresting() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(199);
		Assert.assertEquals(true, content==null);
	}

	public void testInsideTheRuleName() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(670);
		Assert.assertEquals(true, content==null);
	}

	public void testInsideLHSRule() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(790);
		Assert.assertEquals(true, content!=null);
	}

	public void testInsideRHSRule() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(830);
		Assert.assertEquals(true, content!=null);
	}

	public void testSampleDRL() {
		rule = "package com.sample\n\n";
		rule += "import com.sample.DroolsTest.Message;\n";
		rule += "import com.sample.Prueba;\n\n";
		rule += "\trule \"Hello World\"\n";
		rule += "\twhen\n";
		rule += "\t\tm : Message( status == Message.HELLO, myMessage : message )\n";
		rule += "\t\tPrueba()\n";
		rule += "\tthen\n";
		rule += "\t\tSystem.out.println( myMessage );\n";
		rule += "\t\tm.setMessage( \"Goodbye cruel world\" );\n";
		rule += "\t\tm.setStatus( Message.GOODBYE );\n";
		rule += "\t\tupdate( m );\n";
		rule += "end\n";
		rule += "rule \"GoodBye World\"\n";
		rule += "\twhen\n";
		rule += "\t\tm : Message( status == Message.GOODBYE, myMessage : message )\n";
		rule += "\t\tPrueba()\n";
		rule += "\tthen\n";
		rule += "\t\tSystem.out.println( myMessage );\n";
		rule += "\t\tm.setMessage( \"Bon Giorno\" );\n";
		rule += "end";

		engine = new DRLParserEngine(rule);
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(173);

		Assert.assertEquals(true, content!=null);
		Assert.assertEquals(DRLContentTypeEnum.RULE_LHS_LINE, content.getType());
		Assert.assertEquals("rule \"Hello World\"", ((RuleLineContentInfo)content).getRule().getRuleName());
		Assert.assertEquals("\t\tPrueba()", content.getContent());

		content = info.getContentAt(343);
		Assert.assertEquals(true, content!=null);
		Assert.assertEquals(DRLContentTypeEnum.RULE_LHS_LINE, content.getType());
		Assert.assertEquals("rule \"GoodBye World\"", ((RuleLineContentInfo)content).getRule().getRuleName());
		Assert.assertEquals("\twhen", content.getContent());
	}

}
