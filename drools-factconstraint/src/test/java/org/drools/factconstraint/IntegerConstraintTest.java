package org.drools.factconstraint;

import java.util.Collection;

import org.drools.builder.ResourceType;
import org.drools.guvnor.client.factconstraints.predefined.IntegerConstraint;
import org.drools.guvnor.client.factcontraints.Constraint;
import org.drools.guvnor.client.factcontraints.ValidationResult;
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
public class IntegerConstraintTest {

    private Constraint cons;

    @Before
    public void setup() {
        cons = new IntegerConstraint();
        cons.setFactType("Person");
        cons.setFieldName("age");


        System.out.println("Validation Rule:\n" + cons.getVerifierRule() + "\n\n");

    }

    @Test
    public void testValidConstraint() {

        ValidationResult result = cons.validate(12);
        Assert.assertTrue(result.isSuccess());

        result = cons.validate(new Integer("12"));
        Assert.assertTrue(result.isSuccess());

        result = cons.validate("12");
        Assert.assertTrue(result.isSuccess());

    }

    @Test
    public void testInvalidConstraint() {

        ValidationResult result = cons.validate(new Object());
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate("");
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate("ABC");
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate(null);
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate(new Long("12"));
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate(12L);
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        result = cons.validate(12.8);
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: " + result.getMessage());


    }

    @Test
    public void testUsingVerifier() {

        String ruleToVerify = "";

        //FAIL
        ruleToVerify += "package org.drools.factconstraint.test\n\n";
        ruleToVerify += "import org.drools.factconstraint.model.*\n";
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
        VerifierConfiguration conf = new VerifierConfigurationImpl();

        conf.getVerifyingResources().put(ResourceFactory.newByteArrayResource(cons.getVerifierRule().getBytes()), ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(conf);

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

        Assert.assertEquals(2,
                counter);

        verifier.dispose();
    }
}
