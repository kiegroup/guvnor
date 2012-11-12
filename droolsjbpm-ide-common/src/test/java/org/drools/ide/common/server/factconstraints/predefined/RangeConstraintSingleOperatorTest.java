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
import org.drools.ide.common.server.factconstraints.predefined.RangeConstraint;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierConfigurationImpl;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;

import static org.junit.Assert.*;

public class RangeConstraintSingleOperatorTest {

    private Verifier verifier;
    private Constraint cons;
    private ConstraintConfiguration conf;

    @Before
    public void setup() {
        cons = new RangeConstraint();
        conf = new SimpleConstraintConfigurationImpl();
        conf.setFactType("Person");
        conf.setFieldName("age");

        conf.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MIN, "0");
        conf.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MAX, "120");
        System.out.println("Validation Rule:\n" + cons.getVerifierRule(conf) + "\n\n");
    }

    @After
    public void dispose(){
        if (verifier != null){
            verifier.dispose();
        }
    }

    @Test
    public void testEq() {

        String rulesToVerify = "";
        int fail = 0;

        //FAIL
        rulesToVerify += "package org.kie.factconstraint.test\n\n";
        rulesToVerify += "import org.kie.factconstraint.model.*\n";
        rulesToVerify += "rule \"rule1\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age == -5)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        fail++;

        //OK
        rulesToVerify += "rule \"rule2\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age == 10)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";

        //FAIL
        rulesToVerify += "rule \"rule3\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age == 130)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n";
        fail++;

        VerifierReport result = this.verify(rulesToVerify);
        
        Collection<VerifierMessageBase> errors = result.getBySeverity(Severity.ERROR);

        System.out.println(errors);

        assertEquals(fail, errors.size());
    }

    @Test
    public void testNotEq() {

        String rulesToVerify = "";
        int warning = 0;

        //FAIL
        rulesToVerify += "package org.kie.factconstraint.test\n\n";
        rulesToVerify += "import org.kie.factconstraint.model.*\n";
        rulesToVerify += "rule \"rule1\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age != -5)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        warning++;

        //OK
        rulesToVerify += "rule \"rule2\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age != 10)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";

        //FAIL
        rulesToVerify += "rule \"rule3\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age != 130)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n";
        warning++;

        VerifierReport result = this.verify(rulesToVerify);

        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.WARNING);

        System.out.println(warnings);

        assertEquals(warning, warnings.size());
    }

    @Test
    public void testGT() {

        String rulesToVerify = "";
        int warning = 0;

        //FAIL
        rulesToVerify += "package org.kie.factconstraint.test\n\n";
        rulesToVerify += "import org.kie.factconstraint.model.*\n";
        rulesToVerify += "rule \"rule1\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > -5)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        warning++;

        //FAIL
        rulesToVerify += "rule \"rule2\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > 10)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        warning++;

        //FAIL
        rulesToVerify += "rule \"rule3\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > 130)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n";
        warning++;

        VerifierReport result = this.verify(rulesToVerify);

        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.WARNING);

        System.out.println(warnings);

        assertEquals(warning, warnings.size());
    }

    @Test
    public void testGE() {

        String rulesToVerify = "";
        int warning = 0;

        //FAIL
        rulesToVerify += "package org.kie.factconstraint.test\n\n";
        rulesToVerify += "import org.kie.factconstraint.model.*\n";
        rulesToVerify += "rule \"rule1\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age >= -5)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        warning++;

        //FAIL
        rulesToVerify += "rule \"rule2\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age >= 10)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        warning++;

        //FAIL
        rulesToVerify += "rule \"rule3\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age >= 130)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n";
        warning++;

        VerifierReport result = this.verify(rulesToVerify);

        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.WARNING);

        System.out.println(warnings);

        assertEquals(warning, warnings.size());
    }

    @Test
    public void testLT() {

        String rulesToVerify = "";
        int warning = 0;

        //FAIL
        rulesToVerify += "package org.kie.factconstraint.test\n\n";
        rulesToVerify += "import org.kie.factconstraint.model.*\n";
        rulesToVerify += "rule \"rule1\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age < -5)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        warning++;

        //FAIL
        rulesToVerify += "rule \"rule2\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age < 10)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        warning++;

        //FAIL
        rulesToVerify += "rule \"rule3\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age < 130)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n";
        warning++;

        VerifierReport result = this.verify(rulesToVerify);

        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.WARNING);

        System.out.println(warnings);

        assertEquals(warning, warnings.size());
    }

    @Test
    public void testLE() {

        String rulesToVerify = "";
        int warning = 0;

        //FAIL
        rulesToVerify += "package org.kie.factconstraint.test\n\n";
        rulesToVerify += "import org.kie.factconstraint.model.*\n";
        rulesToVerify += "rule \"rule1\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age <= -5)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        warning++;

        //FAIL
        rulesToVerify += "rule \"rule2\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age <= 10)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        warning++;

        //FAIL
        rulesToVerify += "rule \"rule3\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age <= 130)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n";
        warning++;

        VerifierReport result = this.verify(rulesToVerify);

        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.WARNING);

        System.out.println(warnings);

        assertEquals(warning, warnings.size());
    }

    private VerifierReport verify(String rulesToVerify){
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        //VerifierConfiguration conf = new DefaultVerifierConfiguration();
        VerifierConfiguration vconf = new VerifierConfigurationImpl();

        vconf.getVerifyingResources().put(ResourceFactory.newByteArrayResource(cons.getVerifierRule(this.conf).getBytes()), ResourceType.DRL);

        verifier = vBuilder.newVerifier(vconf);

        verifier.addResourcesToVerify(ResourceFactory.newByteArrayResource(rulesToVerify.getBytes()),
                ResourceType.DRL);

        boolean noProblems = verifier.fireAnalysis();

        if (verifier.hasErrors()) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }
            throw new RuntimeException("Error building verifier");
        }

        assertTrue(noProblems);

        return verifier.getResult();

    }

}
