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

import java.util.Collection;

import org.drools.ide.common.client.factconstraints.ConstraintConfiguration;
import org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl;
import org.drools.ide.common.server.factconstraints.Constraint;
import org.drools.ide.common.server.factconstraints.predefined.InvalidFieldConstraint;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierConfigurationImpl;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;

import static org.junit.Assert.*;

public class InvalidFieldConstraintTest {

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

        //FAIL
        ruleToVerify += "package org.kie.factconstraint.test\n\n";
        ruleToVerify += "import org.kie.factconstraint.model.*\n";

        ruleToVerify += "global Number globalAge;\n";

        ruleToVerify += "rule \"rule1\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 20)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";
        fail++;

        //OK
        ruleToVerify += "rule \"rule2\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == \"Peter\")\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";

        //FAIL
        ruleToVerify += "rule \"rule3\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == \"Peter\")\n";
        ruleToVerify += "       Person(age == 20)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;

        //FAIL
        ruleToVerify += "rule \"rule4\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Number() from collect(Person(age == 20))\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;


        //FAIL
        ruleToVerify += "rule \"rule5\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person($age: age)\n";
        ruleToVerify += "       Person(age == $age)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;

        //FAIL
        ruleToVerify += "rule \"rule6\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == globalAge)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        //VerifierConfiguration conf = new DefaultVerifierConfiguration();
        VerifierConfiguration vconf = new VerifierConfigurationImpl();

        Constraint cons = new InvalidFieldConstraint();
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

        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.ERROR);

        int counter = 0;
        for (VerifierMessageBase message : warnings) {
            System.out.println(message);
            counter++;
        }

        assertEquals(fail, counter);

        verifier.dispose();
    }
}
