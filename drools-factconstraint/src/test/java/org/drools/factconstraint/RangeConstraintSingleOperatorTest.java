package org.drools.factconstraint;

import java.util.Collection;
import org.drools.builder.ResourceType;
import org.drools.factconstraints.client.Constraint;
import org.drools.factconstraints.client.predefined.RangeConstraint;
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
public class RangeConstraintSingleOperatorTest {

    private Verifier verifier;
    private Constraint cons;

    @Before
    public void setup() {
        cons = new RangeConstraint();
        cons.setFactType("Person");
        cons.setFieldName("age");

        System.out.println("Validation Rule:\n" + cons.getVerifierRule() + "\n\n");

    }

    @After
    public void dispose(){
        if (verifier != null){
            verifier.dispose();
        }
    }

    @Test
    public void testEq() {

        //age constraint
        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MIN, "0");
        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MAX, "120");
        System.out.println("Validation Rule:\n" + cons.getVerifierRule() + "\n\n");


        String rulesToVerify = "";
        int fail = 0;

        //FAIL
        rulesToVerify += "package org.drools.factconstraint.test\n\n";
        rulesToVerify += "import org.drools.factconstraint.model.*\n";
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

        Assert.assertEquals(fail, errors.size());
    }

    @Test
    public void testNotEq() {

        //age constraint
        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MIN, "0");
        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MAX, "120");
        System.out.println("Validation Rule:\n" + cons.getVerifierRule() + "\n\n");


        String rulesToVerify = "";
        int fail = 0;
        int warning = 0;

        //FAIL
        rulesToVerify += "package org.drools.factconstraint.test\n\n";
        rulesToVerify += "import org.drools.factconstraint.model.*\n";
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

        Assert.assertEquals(warning, warnings.size());
    }

    private VerifierReport verify(String rulesToVerify){
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        //VerifierConfiguration conf = new DefaultVerifierConfiguration();
        VerifierConfiguration conf = new VerifierConfigurationImpl();

        conf.getVerifyingResources().put(ResourceFactory.newByteArrayResource(cons.getVerifierRule().getBytes()), ResourceType.DRL);

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
