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
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;

import static org.junit.Assert.*;

public class NotNullConstraintTest {

    private ConstraintConfiguration conf;
    private Constraint cons;
    
    @Before
    public void setup() {
        conf = new SimpleConstraintConfigurationImpl();
        conf.setFactType("Person");
        conf.setFieldName("name");
        
        cons = new NotNullConstraint();
    }

    @Test
    public void testValidConstraint(){

        ValidationResult result = cons.validate(new Object(), conf);
        assertTrue(result.isSuccess());

        result = cons.validate("", conf);
        assertTrue(result.isSuccess());

        result = cons.validate("ABC", conf);
        assertTrue(result.isSuccess());

        result = cons.validate(new Long("12"), conf);
        assertTrue(result.isSuccess());

        result = cons.validate(12L, conf);
        assertTrue(result.isSuccess());

        result = cons.validate(12.8, conf);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testInvalidConstraint(){

        ValidationResult result = cons.validate(null, conf);
        assertFalse(result.isSuccess());
        System.out.println("Message: "+result.getMessage());

    }

    @Test
    public void testUsingVerifier() {

        String ruleToVerify = "";

        //OK
        ruleToVerify += "package org.kie.factconstraint.test\n\n";
        ruleToVerify += "import org.kie.factconstraint.model.*\n";
        ruleToVerify += "rule \"rule1\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == 'John McClane')\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";

        //FAIL
        ruleToVerify += "rule \"rule2\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == null)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";

        //OK
        ruleToVerify += "rule \"rule3\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == '')\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";


        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vconf = new VerifierConfigurationImpl();

        vconf.getVerifyingResources().put(ResourceFactory.newByteArrayResource(cons.getVerifierRule(this.conf).getBytes()), ResourceType.DRL);

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

        assertEquals(1,
                counter);

        verifier.dispose();
    }

}
