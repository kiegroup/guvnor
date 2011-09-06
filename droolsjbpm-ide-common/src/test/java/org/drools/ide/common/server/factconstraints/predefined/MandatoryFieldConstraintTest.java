/*
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

package org.drools.ide.common.server.factconstraints.predefined;

import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import org.drools.builder.ResourceType;
import org.drools.ide.common.client.factconstraints.ConstraintConfiguration;
import org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl;
import org.drools.ide.common.server.factconstraints.Constraint;
import org.drools.io.ResourceFactory;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierConfigurationImpl;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MandatoryFieldConstraintTest {

    private ConstraintConfiguration conf;

    @Before
    public void setup() {
        conf = new SimpleConstraintConfigurationImpl();
        conf.setFactType("Person");
        conf.setFieldName("age");
    }

    @Test
    public void testUsingVerifier() {

        String ruleToVerify = "";
        int fail = 0;

        
        ruleToVerify += "package org.drools.factconstraint.test\n\n";
        ruleToVerify += "import org.drools.factconstraint.model.*\n";

        ruleToVerify += "global Number globalAge;\n";

        //OK
        ruleToVerify += "rule \"rule1\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 20)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";

        //FAIL
        ruleToVerify += "rule \"rule2\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == \"Peter\")\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";
        fail++;
        
        //FAIL
        ruleToVerify += "rule \"rule3\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == \"Peter\")\n";
        ruleToVerify += "       Person(age == 20)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;
        
        //OK
        ruleToVerify += "rule \"rule4\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Object() from collect(Person(age == 20))\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        
        //FAIL: binding is not a restriction
        ruleToVerify += "rule \"rule5\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person($age: age)\n";
        ruleToVerify += "       Person(age == $age)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;
        
        //OK
        ruleToVerify += "rule \"rule6\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == globalAge)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        
        //OK
        ruleToVerify += "rule \"rule7\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Car(brand == \"Ford\")\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        
        //FAIL
        ruleToVerify += "rule \"rule8\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == globalAge)\n";
        ruleToVerify += "       Person()\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;
        
        //OK
        ruleToVerify += "rule \"rule9\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       $n: Object() from collect(Person(age == 20))\n";
        ruleToVerify += "       Person(age == $n)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        
        //FAIL
        ruleToVerify += "rule \"rule10\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       $n: Number() from collect(Person(age == 20))\n";
        ruleToVerify += "       Person(name == \"Peter\")\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;
        
        //FAIL
        ruleToVerify += "rule \"rule11\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person()\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";
        fail++;

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        //VerifierConfiguration conf = new DefaultVerifierConfiguration();
        VerifierConfiguration vconf = new VerifierConfigurationImpl();

        Constraint cons = new MandatoryFieldConstraint();
        System.out.println("\n\n\n"+cons.getVerifierRule(conf)+"\n\n\n");
        vconf.getVerifyingResources().put(ResourceFactory.newByteArrayResource(cons.getVerifierRule(conf).getBytes()), ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vconf);

        verifier.addResourcesToVerify(ResourceFactory.newByteArrayResource(ruleToVerify.getBytes()),
                ResourceType.DRL);

        if (verifier.hasErrors()) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }
            throw new RuntimeException("Error building verifier");
        }

        assertFalse(verifier.hasErrors());

        boolean noProblems = verifier.fireAnalysis();
        assertTrue(noProblems);

        VerifierReport result = verifier.getResult();

        Collection<VerifierMessageBase> errors = result.getBySeverity(Severity.ERROR);

        List<String> ruleNames = new ArrayList<String>();
        int counter = 0;
        for (VerifierMessageBase message : errors) {
            ruleNames.addAll(message.getImpactedRules().values());
            System.out.println(message);
            counter++;
        }

        assertEquals(fail, counter);

        int[] failingRuleNumbers = {2, 3, 5, 8, 10, 11};
        
        for (int i = 0; i < failingRuleNumbers.length; i++) {
            int j = failingRuleNumbers[i];
            ruleNames.contains("rule"+j);
        }
        
        verifier.dispose();
    }
}
