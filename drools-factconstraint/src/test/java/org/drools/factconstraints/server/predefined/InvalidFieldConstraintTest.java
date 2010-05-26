package org.drools.factconstraints.server.predefined;

import java.util.Collection;

import org.drools.builder.ResourceType;
import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.config.SimpleConstraintConfigurationImpl;
import org.drools.factconstraints.server.Constraint;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
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
        ruleToVerify += "package org.drools.factconstraint.test\n\n";
        ruleToVerify += "import org.drools.factconstraint.model.*\n";

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

        Assert.assertFalse(verifier.hasErrors());

        boolean noProblems = verifier.fireAnalysis();
        Assert.assertTrue(noProblems);

        VerifierReport result = verifier.getResult();

        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.ERROR);

        int counter = 0;
        for (VerifierMessageBase message : warnings) {
            System.out.println(message);
            counter++;
        }

        Assert.assertEquals(fail, counter);

        verifier.dispose();
    }
}
