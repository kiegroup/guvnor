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
import org.drools.ide.common.client.factconstraints.ValidationResult;
import org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl;
import org.drools.ide.common.server.factconstraints.Constraint;
import org.drools.ide.common.server.factconstraints.predefined.IntegerConstraint;
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

public class IntegerConstraintTest {

    private ConstraintConfiguration conf;

    @Before
    public void setup() {
        conf = new SimpleConstraintConfigurationImpl();
        conf.setFactType("Person");
        conf.setFieldName("age");
    }

    @Test
    public void testValidConstraint() {
        Constraint cons = new IntegerConstraint();

        ValidationResult result = cons.validate(12, conf);
        assertTrue(result.isSuccess());

        result = cons.validate(new Integer("12"), conf);
        assertTrue(result.isSuccess());

        result = cons.validate("12", conf);
        assertTrue(result.isSuccess());

    }

    @Test
    public void testInvalidConstraint() {
        Constraint cons = new IntegerConstraint();

        ValidationResult result = cons.validate(new Object(), conf);
        assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate("", conf);
        assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate("ABC", conf);
        assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate(null, conf);
        assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate(new Long("12"), conf);
        assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate(12L, conf);
        assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate(12.8, conf);
        assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());
    }

    @Test
    public void testUsingVerifier() {

        String ruleToVerify = "";

        //FAIL
        ruleToVerify += "package org.kie.factconstraint.test\n\n";
        ruleToVerify += "import org.kie.factconstraint.model.*\n";
        ruleToVerify += "rule \"rule1\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 'abc')\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";

        //OK
        ruleToVerify += "rule \"rule2\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 12)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";

        //FAIL
        ruleToVerify += "rule \"rule3\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == '')\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";


        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        //VerifierConfiguration conf = new DefaultVerifierConfiguration();
        VerifierConfiguration vconf = new VerifierConfigurationImpl();

        Constraint cons = new IntegerConstraint();
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

        assertEquals(2, counter);

        verifier.dispose();
    }
}
