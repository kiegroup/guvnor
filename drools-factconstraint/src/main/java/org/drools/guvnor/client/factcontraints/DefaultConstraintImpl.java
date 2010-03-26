package org.drools.guvnor.client.factcontraints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author esteban.aliverti@gmail.com
 * @author baunax@gmail.com
 */
public abstract class DefaultConstraintImpl implements Constraint {
	private long ruleNum = 0;
    private String factType;
    private String fieldName;
    private Map<String, String> arguments = new HashMap<String, String>();
    /*
    private String verifierPackageTemplate = "";
    private String verifierImportsTemplate = "";
    private String verifierGlobalsTemplate = "";
    private String verifierRuleNameTemplate = "";
    private String verifierRuleWhenTemplate = "";
    private String verifierFieldPatternTemplate = "";
    private String verifierRestrictionPatternTemplate = "";
    private String verifierRuleThenTemplate = "";
    private String verifierActionTemplate = "";
    private String verifierRuleEndTemplate = "";
    */

    public DefaultConstraintImpl() {

    }

    private String concatRule() {
        StringBuilder rule = new StringBuilder();

        rule.append(this.getVerifierPackagePrefixTemplate());
        rule.append(this.getVerifierPackageTemplate());
        rule.append(this.getVerifierPackageSufixTemplate());

        rule.append(this.getVerifierImportsPrefixTemplate());
        rule.append(this.getVerifierImportsTemplate());
        rule.append(this.getVerifierImportsSufixTemplate());

        rule.append(this.getVerifierGlobalsPrefixTemplate());
        rule.append(this.getVerifierGlobalsTemplate());
        rule.append(this.getVerifierGlobalsSufixTemplate());

        rule.append(this.getVerifierRuleNamePrefixTemplate());
        rule.append(this.getVerifierRuleNameTemplate());
        rule.append(this.getVerifierRuleNameSufixTemplate());

        rule.append(this.getVerifierRuleWhenTemplate());

        rule.append(this.getVerifierFieldPatternPrefixTemplate());
        rule.append(this.getVerifierFieldPatternTemplate());
        rule.append(this.getVerifierFieldPatternSufixTemplate());

        rule.append(this.getVerifierRestrictionPatternPrefixTemplate());
        rule.append(this.getVerifierRestrictionPatternTemplate());
        rule.append(this.getVerifierRestrictionPatternSufixTemplate());

        rule.append(this.getVerifierRuleThenTemplate());

        rule.append(this.getVerifierActionPrefixTemplate());
        rule.append(this.getVerifierActionTemplate());
        rule.append(this.getVerifierActionSufixTemplate());

        rule.append(this.getVerifierRuleEndTemplate());
        rule.append(this.getVerifierRuleEndSufixTemplate());


        return rule.toString();

    }

    protected String createVerifierRuleTemplate(String ruleName, List<String> constraints, String message) {
        if (ruleName == null) {
            ruleName = "Constraint_rule";
            
        }
        ruleName += "_" + ruleNum++; 
        String rule = this.concatRule().replace("${ruleName}", ruleName);
        rule = rule.replace("${factType}", this.getFactType());
        rule = rule.replace("${fieldName}", this.getFieldName());
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

    protected Object getMandatoryArgument(String key) throws ArgumentNotSetException {
        if (!this.arguments.containsKey(key)) {
            throw new ArgumentNotSetException("The argument " + key + " doesn't exist.");
        }

        Object value = this.getArgumentValue(key);

        if (value == null) {
            throw new ArgumentNotSetException("The argument " + key + " is null.");
        }

        return value;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFactType() {
        return factType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Set<String> getArgumentKeys() {
        return this.arguments.keySet();
    }

    public Object getArgumentValue(String key) {
        return this.arguments.get(key);
    }

    public void setArgumentValue(String key, String value) {
        this.arguments.put(key, value);
    }

    public ValidationResult validate(Object value) {
        ValidationResult result = new ValidationResult();
        result.setSuccess(true);

        return result;
    }

    public String getVerifierRule() {
        return null;
    }

    public String getConstraintName() {
    	return getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1);
    }
    
    /* Action */
    protected String getVerifierActionTemplate() {
        StringBuilder verifierActionTemplate = new StringBuilder();
        verifierActionTemplate.append("      Map<String,String> impactedRules = new HashMap<String,String>();\n");
//        verifierActionTemplate.append("      impactedRules.put( $restriction.getPath(), $restriction.getRuleName());\n");
//        verifierActionTemplate.append("      impactedRules.put( $r.getPath(), $r.getName());\n");
        verifierActionTemplate.append("      result.add(new VerifierMessage(\n");
        verifierActionTemplate.append("                        impactedRules,\n");
        verifierActionTemplate.append("                        Severity.ERROR,\n");
        verifierActionTemplate.append("                        MessageType.NOT_SPECIFIED,\n");
        verifierActionTemplate.append("                        $restriction,\n");
        verifierActionTemplate.append("                        \"${message}\" ) );\n");

//        verifierActionTemplate.append("      System.out.println(\"doubleValue= \"+$restriction.getDoubleValue());\n");
//        verifierActionTemplate.append("      System.out.println(\"intValue= \"+$restriction.getIntValue());\n");

        return verifierActionTemplate.toString();
    }

    protected String getVerifierActionPrefixTemplate() {
        return "";
    }

    protected String getVerifierActionSufixTemplate() {
        return "";
    }

    /* Field Pattern */
    protected String getVerifierFieldPatternTemplate() {
        StringBuilder verifierFieldPatternTemplate = new StringBuilder();
        verifierFieldPatternTemplate.append("      $field :Field(\n");
        verifierFieldPatternTemplate.append("          objectTypeName == \"${factType}\",\n");
        verifierFieldPatternTemplate.append("          name == \"${fieldName}\"\n");
        verifierFieldPatternTemplate.append("      )\n");
        return verifierFieldPatternTemplate.toString();
    }

    protected String getVerifierFieldPatternPrefixTemplate() {
        return "";
    }

    protected String getVerifierFieldPatternSufixTemplate() {
        return "";
    }

    /* Globals*/
    protected String getVerifierGlobalsTemplate() {
        return "global VerifierReport result;\n";
    }

    protected String getVerifierGlobalsPrefixTemplate() {
        return "";
    }

    protected String getVerifierGlobalsSufixTemplate() {
        return "";
    }

    /* Imports */
    protected String getVerifierImportsTemplate() {
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

    protected String getVerifierImportsPrefixTemplate() {
        return "";
    }

    protected String getVerifierImportsSufixTemplate() {
        return "";
    }

    protected String getVerifierPackageTemplate() {
        return "package org.drools.verifier.consequence\n";
    }

    protected String getVerifierPackagePrefixTemplate() {
        return "";
    }

    protected String getVerifierPackageSufixTemplate() {
        return "";
    }

    /* Restriction Pattern */
    protected String getVerifierRestrictionPatternTemplate() {
        StringBuilder verifierRestrictionPatternTemplate = new StringBuilder();
        verifierRestrictionPatternTemplate.append("      $restriction :LiteralRestriction(\n");
        verifierRestrictionPatternTemplate.append("            fieldPath == $field.path,\n");
        verifierRestrictionPatternTemplate.append("            ${constraints}\n");
        verifierRestrictionPatternTemplate.append("      )\n");
        
        return verifierRestrictionPatternTemplate.toString();
    }

    protected String getVerifierRestrictionPatternPrefixTemplate() {
        return "";
    }

    protected String getVerifierRestrictionPatternSufixTemplate() {
        return "";
    }

    /* end */
    protected String getVerifierRuleEndTemplate() {
        return "end\n";
    }

    protected String getVerifierRuleEndSufixTemplate() {
        return "";
    }

    /* Rule Name */
    protected String getVerifierRuleNameTemplate() {
        return "rule \"${ruleName}\"\n";
    }

    protected String getVerifierRuleNamePrefixTemplate() {
        return "";
    }

    protected String getVerifierRuleNameSufixTemplate() {
        return "";
    }

    /* then */
    protected String getVerifierRuleThenTemplate() {
        return "  then\n";
    }

    /* when */
    protected String getVerifierRuleWhenTemplate() {
        return "  when\n";
    }

    
}
