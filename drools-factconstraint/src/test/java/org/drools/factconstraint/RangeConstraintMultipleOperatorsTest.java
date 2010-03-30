package org.drools.factconstraint;

import java.util.Collection;
import org.drools.builder.ResourceType;
import org.drools.factconstraint.server.Constraint;
import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.config.SimpleConstraintConfigurationImpl;
import org.drools.factconstraints.server.predefined.RangeConstraint;
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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class RangeConstraintMultipleOperatorsTest {

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
        //System.out.println("Validation Rule:\n" + cons.getVerifierRule(conf) + "\n\n");

    }

    @After
    public void dispose() {
        if (verifier != null) {
            verifier.dispose();
        }
    }

    @Test
    public void test() {

        String rulesToVerify = "";
        int fail = 0;
        int warn = 0;

        //OK
        rulesToVerify += "package org.drools.factconstraint.test\n\n";
        rulesToVerify += "import org.drools.factconstraint.model.*\n";
        rulesToVerify += "rule \"rule1\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > 1, age < 100)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";

        //FAIL
        rulesToVerify += "rule \"rule2\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > -1,  age < 60)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        fail++;

        //FAIL
        rulesToVerify += "rule \"rule3\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > -1,  age < 150)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        fail++;

        //FAIL-Impossible
        rulesToVerify += "rule \"rule4\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > 150,  age < -1)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        fail++;

        //FAIL
        rulesToVerify += "rule \"rule5\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > -10,  age < -1)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        fail++;

        //FAIL
        rulesToVerify += "rule \"rule6\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > 18,  age < 150)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        fail++;

        //FAIL-Impossible
        rulesToVerify += "rule \"rule7\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > 18,  age < -1)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        fail++;

        //FAIL
        rulesToVerify += "rule \"rule8\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > 130,  age < 150)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        fail++;

        //FAIL-Impossible
        rulesToVerify += "rule \"rule9\"\n";
        rulesToVerify += "   when\n";
        rulesToVerify += "       Person(age > 130,  age < 18)\n";
        rulesToVerify += "   then\n";
        rulesToVerify += "       System.out.println(\"Rule fired\");\n";
        rulesToVerify += "end\n\n";
        fail++;

        VerifierReport result = this.verify(rulesToVerify);

        Collection<VerifierMessageBase> errors = result.getBySeverity(Severity.ERROR);
        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.WARNING);

        System.out.println(warnings);
        System.out.println(errors);

        Assert.assertEquals(warn, warnings.size());
        Assert.assertEquals(fail, errors.size());
    }

    private VerifierReport verify(String rulesToVerify) {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        //VerifierConfiguration conf = new DefaultVerifierConfiguration();
        VerifierConfiguration conf = new VerifierConfigurationImpl();

        conf.getVerifyingResources().put(ResourceFactory.newClassPathResource("RangeConstraintMultiOperator.drl"), ResourceType.DRL);

        //conf.getVerifyingResources().put(ResourceFactory.newByteArrayResource(cons.getVerifierRule().getBytes()), ResourceType.DRL);

        verifier = vBuilder.newVerifier(conf);

        verifier.addResourcesToVerify(ResourceFactory.newByteArrayResource(rulesToVerify.getBytes()),
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

        return verifier.getResult();

    }
}
