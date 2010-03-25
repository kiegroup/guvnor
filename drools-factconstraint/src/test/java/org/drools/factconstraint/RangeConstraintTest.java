package org.drools.factconstraint;

import java.util.Collection;
import org.drools.builder.ResourceType;
import org.drools.guvnor.client.factconstraints.predefined.RangeConstraint;
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
public class RangeConstraintTest {

    private Constraint cons;

    @Before
    public void setup() {
        cons = new RangeConstraint();
        cons.setFactType("Person");
        cons.setFieldName("age");

        System.out.println("Validation Rule:\n" + cons.getVerifierRule() + "\n\n");

    }

    @Test
    public void testValidConstraint(){

        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MIN, "-0.5");
        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MAX, "100");
        
        ValidationResult result = cons.validate(12);
        Assert.assertTrue(result.isSuccess());

        result = cons.validate(new Integer("12"));
        Assert.assertTrue(result.isSuccess());

        result = cons.validate("12");
        Assert.assertTrue(result.isSuccess());

        result = cons.validate(0.6);
        Assert.assertTrue(result.isSuccess());

        result = cons.validate(new Float("-0.3"));
        Assert.assertTrue(result.isSuccess());

        result = cons.validate("90.76");
        Assert.assertTrue(result.isSuccess());

    }

    @Test
    public void testInvalidConstraint(){

        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MIN, "-0.5");
        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MAX, "100");

        ValidationResult result = cons.validate(new Object());
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: "+result.getMessage());

        result = cons.validate(null);
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: "+result.getMessage());

        result = cons.validate("");
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: "+result.getMessage());

        result = cons.validate("ABC");
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: "+result.getMessage());

        result = cons.validate(new Long("-100"));
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: "+result.getMessage());

        result = cons.validate(-0.5);
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: "+result.getMessage());

        result = cons.validate(100);
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: "+result.getMessage());


    }

    @Test
    public void testUsingVerifier() {

        //age constraint
        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MIN, "0");
        cons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MAX, "120");
        System.out.println("Validation Rule:\n" + cons.getVerifierRule() + "\n\n");

        //salary constraint
        Constraint salaryCons = new RangeConstraint();
        salaryCons.setFactType("Person");
        salaryCons.setFieldName("salary");
        salaryCons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MIN, "0");
        salaryCons.setArgumentValue(RangeConstraint.RANGE_CONSTRAINT_MAX, "1000.6");

        System.out.println("Validation Rule:\n" + salaryCons.getVerifierRule() + "\n\n");


        String ruleToVerify = "";
        int fail = 0;

        //OK
        ruleToVerify += "package org.drools.factconstraint.test\n\n";
        ruleToVerify += "import org.drools.factconstraint.model.*\n";
        ruleToVerify += "rule \"rule1\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 10)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";

        //FAIL - 1
        ruleToVerify += "rule \"rule2\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == -5)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";
        fail++;

        //OK
        ruleToVerify += "rule \"rule3\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 100)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";

        //OK
        ruleToVerify += "rule \"rule4\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(salary == 100)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";

        //OK
        ruleToVerify += "rule \"rule5\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(salary == 89.67)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";

        //FAIL - 2
        ruleToVerify += "rule \"rule6\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(salary == 1000.7)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;

        //FAIL - 3
        ruleToVerify += "rule \"rule7\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(salary == 1024)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;

        //OK
        ruleToVerify += "rule \"rule8\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 45, salary == 1000)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";

        //FAIL: age - 4
        ruleToVerify += "rule \"rule9\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 40, salary == 1011)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;

        //FAIL salary - 5
        ruleToVerify += "rule \"rule10\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 43, salary == 1007)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail++;

        //FAIL both (creates 2 warnings) - 6,7
        ruleToVerify += "rule \"rule11\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 403, salary == 1008)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail+=2;
        
        //FAIL both (creates 2 warnings) - 8,9
        ruleToVerify += "rule \"rule12\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(age == 404, salary == -0.679)\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        fail+=2;

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        //VerifierConfiguration conf = new DefaultVerifierConfiguration();
        VerifierConfiguration conf = new VerifierConfigurationImpl();

        conf.getVerifyingResources().put(ResourceFactory.newByteArrayResource(cons.getVerifierRule().getBytes()), ResourceType.DRL);
        conf.getVerifyingResources().put(ResourceFactory.newByteArrayResource(salaryCons.getVerifierRule().getBytes()), ResourceType.DRL);

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

        System.out.println(warnings);

        Assert.assertEquals(fail, warnings.size());
        verifier.dispose();
    }

}
