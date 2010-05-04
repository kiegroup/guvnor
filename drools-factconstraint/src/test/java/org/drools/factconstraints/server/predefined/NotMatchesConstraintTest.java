package org.drools.factconstraints.server.predefined;

import java.util.Collection;

import org.drools.builder.ResourceType;
import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.ValidationResult;
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
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author baunax@gmail.com
 */
public class NotMatchesConstraintTest {

    private ConstraintConfiguration conf;
    private Constraint cons;
    
    @Before
    public void setup() {
        conf = new SimpleConstraintConfigurationImpl();
        conf.setFactType("Person");
        conf.setFieldName("name");
        conf.setArgumentValue(NotMatchesConstraint.NOT_MATCHES_ARGUMENT, "^[A-Z].*$");
        
        cons = new NotMatchesConstraint();
        
        System.out.println("not matches rule: " + cons.getVerifierRule(conf));
    }

    @Test @Ignore
    public void testValidConstraint(){
    	
        ValidationResult result = cons.validate("Bart", conf);
        Assert.assertTrue(result.isSuccess());

        result = cons.validate("", conf);
        Assert.assertFalse(result.isSuccess());

        result = cons.validate("bart", conf);
        Assert.assertFalse(result.isSuccess());

        result = cons.validate(new Long("12"), conf);
        Assert.assertFalse(result.isSuccess());

        result = cons.validate(12L, conf);
        Assert.assertFalse(result.isSuccess());

        result = cons.validate(12.8, conf);
        Assert.assertFalse(result.isSuccess());
    }

    @Test @Ignore
    public void testInvalidConstraint(){

        ValidationResult result = cons.validate(null, conf);
        Assert.assertFalse(result.isSuccess());
        System.out.println("Message: "+result.getMessage());

    }

    @Test
    public void testUsingVerifier() {

        String ruleToVerify = "";

        //OK
        ruleToVerify += "package org.drools.factconstraint.test\n\n";
        ruleToVerify += "import org.drools.factconstraint.model.*\n";
        ruleToVerify += "rule \"rule1\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == \"John\")\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n\n";

        //FAIL
        ruleToVerify += "rule \"rule2\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == '')\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";

        //FAIL
        ruleToVerify += "rule \"pepe\"\n";
        ruleToVerify += "dialect \"mvel\"\n";
        ruleToVerify += "    when\n";
        ruleToVerify += "        Person( name == 'pepe' )\n";
        ruleToVerify += "    then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        
        //FAIL
        ruleToVerify += "rule \"rule3\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == 'bart')\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        
        //FAIL
        ruleToVerify += "rule \"rule4\"\n";
        ruleToVerify += "   when\n";
        ruleToVerify += "       Person(name == '1bart')\n";
        ruleToVerify += "   then\n";
        ruleToVerify += "       System.out.println(\"Rule fired\");\n";
        ruleToVerify += "end\n";
        
        System.out.println(ruleToVerify);
        
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

        Assert.assertFalse(verifier.hasErrors());

        boolean noProblems = verifier.fireAnalysis();
        Assert.assertTrue(noProblems);

        VerifierReport result = verifier.getResult();

        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.ERROR);

        System.out.println(warnings);

        Assert.assertEquals(4, warnings.size());

        verifier.dispose();
    }

}
