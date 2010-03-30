package org.drools.factconstraint.server;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.base.evaluators.Operator;
import org.drools.factconstraints.client.ArgumentNotSetException;
import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.ValidationResult;
import org.drools.verifier.report.components.Severity;

/**
 *
 * @author esteban.aliverti@gmail.com
 * @author baunax@gmail.com
 */
public abstract class DefaultConstraintImpl implements Constraint {

	private static final long serialVersionUID = 501L;
	private long ruleNum = 0;

    public static List<Operator> supportedOperators = new ArrayList<Operator>();
    static{
        supportedOperators.add(Operator.EQUAL);
        supportedOperators.add(Operator.NOT_EQUAL);
        supportedOperators.add(Operator.GREATER);
        supportedOperators.add(Operator.GREATER_OR_EQUAL);
        supportedOperators.add(Operator.LESS);
        supportedOperators.add(Operator.LESS_OR_EQUAL);
    }

    private String concatRule(ConstraintConfiguration config, Map<String, Object> context) {
        StringBuilder rule = new StringBuilder();
        
        rule.append(this.getVerifierPackagePrefixTemplate(config, context));
        rule.append(this.getVerifierPackageTemplate(config, context));
        rule.append(this.getVerifierPackageSufixTemplate(config, context));

        rule.append(this.getVerifierImportsPrefixTemplate(config, context));
        rule.append(this.getVerifierImportsTemplate(config, context));
        rule.append(this.getVerifierImportsSufixTemplate(config, context));

        rule.append(this.getVerifierGlobalsPrefixTemplate(config, context));
        rule.append(this.getVerifierGlobalsTemplate(config, context));
        rule.append(this.getVerifierGlobalsSufixTemplate(config, context));

        rule.append(this.getVerifierRuleNamePrefixTemplate(config, context));
        rule.append(this.getVerifierRuleNameTemplate(config, context));
        rule.append(this.getVerifierRuleNameSufixTemplate(config, context));

        rule.append(this.getVerifierRuleWhenTemplate(config, context));

        rule.append(this.getVerifierFieldPatternPrefixTemplate(config, context));
        rule.append(this.getVerifierFieldPatternTemplate(config, context));
        rule.append(this.getVerifierFieldPatternSufixTemplate(config, context));

        rule.append(this.getVerifierRestrictionPatternPrefixTemplate(config, context));
        rule.append(this.getVerifierRestrictionPatternTemplate(config, context));
        rule.append(this.getVerifierRestrictionPatternSufixTemplate(config, context));

        rule.append(this.getVerifierRuleThenTemplate(config, context));

        rule.append(this.getVerifierActionPrefixTemplate(config, context));
        rule.append(this.getVerifierActionTemplate(config, context));
        rule.append(this.getVerifierActionSufixTemplate(config, context));

        rule.append(this.getVerifierRuleEndTemplate(config, context));
        rule.append(this.getVerifierRuleEndSufixTemplate(config, context));

        return rule.toString();

    }

    protected String createVerifierRuleTemplate(ConstraintConfiguration config, Map<String, Object> context, String ruleName, List<String> constraints, String message) {
        if (ruleName == null) {
            ruleName = "Constraint_rule";

        }
        ruleName += "_" + ruleNum++;
        String rule = this.concatRule(config, context).replace("${ruleName}", ruleName);
        rule = rule.replace("${factType}", config.getFactType());
        rule = rule.replace("${fieldName}", config.getFieldName());
        if (constraints != null && !constraints.isEmpty()) {
            String constraintsTxt = "";
            String delimiter = "";
            for (String c : constraints) {
                constraintsTxt += delimiter + c + "\n";
                if (delimiter.equals("")) {
                    delimiter = ",";
                }
            }
            rule = rule.replace("${constraints}", constraintsTxt);
        }
        rule = rule.replace("${message}", (message == null || message.equals("")) ? "Invalid Value" : message);

        return rule;
    }

    protected Object getMandatoryArgument(String key, ConstraintConfiguration conf) throws ArgumentNotSetException {
        if (!conf.containsArgument(key)) {
            throw new ArgumentNotSetException("The argument " + key + " doesn't exist.");
        }

        Object value = conf.getArgumentValue(key);

        if (value == null) {
            throw new ArgumentNotSetException("The argument " + key + " is null.");
        }

        return value;
    }

    public ValidationResult validate(Object value, ConstraintConfiguration config) {
        ValidationResult result = new ValidationResult();
        result.setSuccess(true);

        return result;
    }

    protected Map<String, Object> createContext() {
    	return new HashMap<String, Object>(); 
    }
    
    public final String getVerifierRule(ConstraintConfiguration config) {
        return internalVerifierRule(config, createContext());
    }

    abstract protected String internalVerifierRule(ConstraintConfiguration config, Map<String, Object> context);
    
    public String getConstraintName() {
    	return getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1);
    }

    /* Action */
    protected String getVerifierActionTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        StringBuilder verifierActionTemplate = new StringBuilder();

          //by default, add an ERROR
          verifierActionTemplate.append(this.addResult(Severity.ERROR));

//        verifierActionTemplate.append("      System.out.println(\"doubleValue= \"+$restriction.getDoubleValue());\n");
//        verifierActionTemplate.append("      System.out.println(\"intValue= \"+$restriction.getIntValue());\n");

        return verifierActionTemplate.toString();
    }

    protected String addResult(Severity  severity){
        StringBuilder addString = new StringBuilder();
        addString.append("      result.add(new VerifierMessage(\n");
        addString.append("                        impactedRules,\n");
        if (severity.compareTo(Severity.ERROR) == 0){
            addString.append("                        Severity.ERROR,\n");
        }else if(severity.compareTo(Severity.NOTE) == 0){
            addString.append("                        Severity.NOTE,\n");
        }else if(severity.compareTo(Severity.WARNING) == 0){
            addString.append("                        Severity.WARNING,\n");
        }
        addString.append("                        MessageType.NOT_SPECIFIED,\n");
        addString.append("                        $restriction,\n");
        addString.append("                        \"${message}\" ) );\n");
        return addString.toString();
    }

    protected String getVerifierActionPrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        StringBuilder verifierActionPrefixTemplate = new StringBuilder();
        verifierActionPrefixTemplate.append("      Map<String,String> impactedRules = new HashMap<String,String>();\n");
//        verifierActionTemplate.append("      impactedRules.put( $restriction.getPath(), $restriction.getRuleName());\n");
//        verifierActionTemplate.append("      impactedRules.put( $r.getPath(), $r.getName());\n");
        return verifierActionPrefixTemplate.toString();
    }

    protected String getVerifierActionSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    /* Field Pattern */
    protected String getVerifierFieldPatternTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        StringBuilder verifierFieldPatternTemplate = new StringBuilder();
        verifierFieldPatternTemplate.append("      $field :Field(\n");
        verifierFieldPatternTemplate.append("          objectTypeName == \"${factType}\",\n");
        verifierFieldPatternTemplate.append("          name == \"${fieldName}\"\n");
        verifierFieldPatternTemplate.append("      )\n");
        return verifierFieldPatternTemplate.toString();
    }

    protected String getVerifierFieldPatternPrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    protected String getVerifierFieldPatternSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    /* Globals*/
    protected String getVerifierGlobalsTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "global VerifierReport result;\n";
    }

    protected String getVerifierGlobalsPrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    protected String getVerifierGlobalsSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    /* Imports */
    protected String getVerifierImportsTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        StringBuilder verifierImportsTemplate = new StringBuilder();
        verifierImportsTemplate.append("import org.drools.verifier.components.*;\n");
        verifierImportsTemplate.append("import java.util.Map;\n");
        verifierImportsTemplate.append("import java.util.HashMap;\n");
        verifierImportsTemplate.append("import org.drools.verifier.report.components.VerifierMessage;\n");
        verifierImportsTemplate.append("import org.drools.verifier.data.VerifierReport;\n");
        verifierImportsTemplate.append("import org.drools.verifier.report.components.Severity;\n");
        verifierImportsTemplate.append("import org.drools.verifier.report.components.MessageType;\n");

        return verifierImportsTemplate.toString();

    }

    protected String getVerifierImportsPrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    protected String getVerifierImportsSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    protected String getVerifierPackageTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "package org.drools.verifier.consequence\n";
    }

    protected String getVerifierPackagePrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    protected String getVerifierPackageSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    /* Restriction Pattern */
    protected String getVerifierRestrictionPatternTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        StringBuilder verifierRestrictionPatternTemplate = new StringBuilder();
        verifierRestrictionPatternTemplate.append("      $restriction :LiteralRestriction(\n");
        verifierRestrictionPatternTemplate.append("            fieldPath == $field.path,\n");
        verifierRestrictionPatternTemplate.append("            ${constraints}\n");
        verifierRestrictionPatternTemplate.append("      )\n");

        return verifierRestrictionPatternTemplate.toString();
    }

    protected String getVerifierRestrictionPatternPrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    protected String getVerifierRestrictionPatternSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    /* end */
    protected String getVerifierRuleEndTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "end\n";
    }

    protected String getVerifierRuleEndSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    /* Rule Name */
    protected String getVerifierRuleNameTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "rule \"${ruleName}\"\n";
    }

    protected String getVerifierRuleNamePrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    protected String getVerifierRuleNameSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "";
    }

    /* then */
    protected String getVerifierRuleThenTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "  then\n";
    }

    /* when */
    protected String getVerifierRuleWhenTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        return "  when\n";
    }
    
    public List<String> getArgumentKeys() {
    	return new ArrayList<String>();
    }
}
