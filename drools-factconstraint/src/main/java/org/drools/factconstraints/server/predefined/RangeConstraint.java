package org.drools.factconstraints.server.predefined;

import java.util.Arrays;
import java.util.List;

import org.drools.factconstraint.server.Constraint;
import org.drools.factconstraints.client.ArgumentNotSetException;
import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.ValidationResult;

/**
 * 
 * @author esteban.aliverti@gmail.com
 */
public class RangeConstraint implements Constraint {

    private static final long serialVersionUID = 501L;
    public static final String NAME = "RangeConstraint";
    public static final String RANGE_CONSTRAINT_MIN = "Min.value";
    public static final String RANGE_CONSTRAINT_MAX = "Max.value";
    private static String template;

    static {
        StringBuilder rules = new StringBuilder();
        rules.append("package org.drools.verifier.consequence\n");

        rules.append("import org.drools.verifier.components.*;\n");
        rules.append("import java.util.Map;\n");
        rules.append("import java.util.HashMap;\n");
        rules.append("import org.drools.verifier.report.components.VerifierMessage;\n");
        rules.append("import org.drools.verifier.data.VerifierReport;\n");
        rules.append("import org.drools.verifier.report.components.Severity;\n");
        rules.append("import org.drools.verifier.report.components.MessageType;\n");
        rules.append("import org.drools.base.evaluators.Operator;\n");

        rules.append("global VerifierReport result;\n");

        rules.append("declare RangeConstraintCandidate{0}\n");
        rules.append("    restriction : NumberRestriction\n");
        rules.append("    greaterValue : double\n");
        rules.append("    lessValue : double\n");
        rules.append("end\n");


        rules.append("function void addResult{0}(VerifierReport report, NumberRestriction restriction, Severity severity, String message){\n");
        rules.append("    Map<String,String> impactedRules = new HashMap<String,String>();\n");
        rules.append("    report.add(new VerifierMessage(\n");
        rules.append("        impactedRules,\n");
        rules.append("        severity,\n");
        rules.append("        MessageType.NOT_SPECIFIED,\n");
        rules.append("        restriction,\n");
        rules.append("        message ) );\n");
        rules.append("}\n");

        rules.append("rule \"Range_Field_Constraint_Base_{0}\"\n");
        rules.append("    when\n");
        rules.append("        $field :Field(\n");
        rules.append("          objectTypeName == \"{1}\",\n");
        rules.append("          name == \"{2}\"\n");
        rules.append("        )\n");
        rules.append("    then\n");
        rules.append("end\n");

        rules.append("/* Single operators */\n");

        rules.append("rule \"Range_Field_Constraint_==_{0}\" extends \"Range_Field_Constraint_Base_{0}\"\n");
        rules.append("  when\n");
        rules.append("     ($restriction :NumberRestriction(\n");
        rules.append("            fieldPath == $field.path,\n");
        rules.append("            operator == Operator.EQUAL,\n");
        rules.append("            (value < {3} || > {4}))\n");
        rules.append("      )\n");
        rules.append("  then\n");
        rules.append("      addResult{0}(result, $restriction, Severity.ERROR, \"The value must be between {3} and {4}\");\n");
        rules.append("end\n");

        rules.append("rule \"Range_Field_Constraint_!=_{0}\" extends \"Range_Field_Constraint_Base_{0}\"\n");
        rules.append("  when\n");
        rules.append("      ($restriction :NumberRestriction(\n");
        rules.append("            fieldPath == $field.path,\n");
        rules.append("            operator == Operator.NOT_EQUAL,\n");
        rules.append("            (value < {3} || > {4}))\n");
        rules.append("      )\n");
        rules.append("  then\n");
        rules.append("    addResult{0}(result, $restriction, Severity.WARNING, \"Impossible value. Possible values are from {3} to {4}\");\n");
        rules.append("end\n");

        rules.append("rule \"Range_Field_Constraint_>_{0}\" extends \"Range_Field_Constraint_Base_{0}\"\n");
        rules.append("  when\n");
        rules.append("      ($restriction :NumberRestriction(\n");
        rules.append("            fieldPath == $field.path,\n");
        rules.append("            $rulePath: rulePath,\n");
        rules.append("            (operator == Operator.GREATER || == Operator.GREATER_OR_EQUAL))\n");
        rules.append("      )\n");
        rules.append("      not (NumberRestriction(\n");
        rules.append("          fieldPath == $field.path,\n");
        rules.append("          rulePath == $rulePath,\n");
        rules.append("          (operator == Operator.LESS || == Operator.LESS_OR_EQUAL)\n");
        rules.append("          )\n");
        rules.append("      )\n");
        rules.append("  then\n");
        rules.append("    addResult{0}(result, $restriction, Severity.WARNING, \"Missing range\");\n");
        rules.append("end\n");

        rules.append("rule \"Range_Field_Constraint_<_{0}\"  extends \"Range_Field_Constraint_Base_{0}\"\n");
        rules.append("  when\n");
        rules.append("      ($restriction :NumberRestriction(\n");
        rules.append("            fieldPath == $field.path,\n");
        rules.append("            $rulePath: rulePath,\n");
        rules.append("            (operator == Operator.LESS || == Operator.LESS_OR_EQUAL))\n");
        rules.append("      )\n");
        rules.append("      not (NumberRestriction(\n");
        rules.append("          fieldPath == $field.path,\n");
        rules.append("          rulePath == $rulePath,\n");
        rules.append("          (operator == Operator.GREATER || == Operator.GREATER_OR_EQUAL)\n");
        rules.append("          )\n");
        rules.append("      )\n");
        rules.append("  then\n");
        rules.append("      addResult{0}(result, $restriction, Severity.WARNING, \"Missing range\");\n");
        rules.append("end\n");


        rules.append("/* Multi operator */\n");

        rules.append("rule \"identifyRangeConstraintCandidate{0}\" extends \"Range_Field_Constraint_Base_{0}\"\n");
        rules.append("when\n");
        rules.append("  ($restriction1 :NumberRestriction(\n");
        rules.append("      $rulePath: rulePath,\n");
        rules.append("      fieldPath == $field.path,\n");
        rules.append("      (operator == Operator.GREATER || == Operator.GREATER_OR_EQUAL),\n");
        rules.append("      $op1: operator,\n");
        rules.append("      $value1: value))\n");
        rules.append("  ($restriction2 :NumberRestriction(\n");
        rules.append("      fieldPath == $field.path,\n");
        rules.append("      rulePath == $rulePath,\n");
        rules.append("      (operator == Operator.LESS || == Operator.LESS_OR_EQUAL),\n");
        rules.append("      $op2: operator,\n");
        rules.append("      $value2: value))\n");
        rules.append("then\n");

        rules.append("    RangeConstraintCandidate{0} rcc = new RangeConstraintCandidate{0}();\n");
        rules.append("    rcc.setRestriction($restriction1);\n");
        rules.append("    rcc.setGreaterValue($value1.doubleValue());\n");
        rules.append("    rcc.setLessValue($value2.doubleValue());\n");

        rules.append("    insert (rcc);\n");

        rules.append("end\n");

        rules.append("/*\n");
        rules.append(" GM = the value is greater than max ( > max)\n");
        rules.append(" LM = the value is less than min (< min)\n");
        rules.append(" VV  = the value is inside the range (>= min && <= max)\n");
        rules.append("*/\n");
        rules.append("rule \"processGMGM{0}\"\n");
        rules.append("when\n");
        rules.append("    $r: RangeConstraintCandidate{0}(greaterValue > {4} && lessValue > {4})\n");
        rules.append("then\n");
        rules.append("    addResult{0}(result, $r.getRestriction(), Severity.WARNING, \"Both sides are outside the range\");\n");
        rules.append("    retract ($r);\n");
        rules.append("end\n");

        rules.append("rule \"processGMVV{0}\"\n");
        rules.append("when\n");
        rules.append("    $r: RangeConstraintCandidate{0}(greaterValue > {4} && lessValue >= {3} && lessValue <={4})\n");
        rules.append("then\n");
        rules.append("    addResult{0}(result, $r.getRestriction(), Severity.ERROR, \"Impossible condition\");\n");
        rules.append("    retract ($r);\n");
        rules.append("end\n");

        rules.append("rule \"processGMLM{0}\"\n");
        rules.append("when\n");
        rules.append("    $r: RangeConstraintCandidate{0}(greaterValue > {4} && lessValue < {3})\n");
        rules.append("then\n");
        rules.append("    addResult{0}(result, $r.getRestriction(), Severity.ERROR, \"Impossible condition\");\n");
        rules.append("    retract ($r);\n");
        rules.append("end\n");

        rules.append("rule \"processVVGM{0}\"\n");
        rules.append("when\n");
        rules.append("    $r: RangeConstraintCandidate{0}(greaterValue >= {3} && greaterValue <={4} && lessValue > {4})\n");
        rules.append("then\n");
        rules.append("    addResult{0}(result, $r.getRestriction(), Severity.WARNING, \"Right side is outside the range\");\n");
        rules.append("    retract ($r);\n");
        rules.append("end\n");

        rules.append("rule \"processVVLM{0}\"\n");
        rules.append("when\n");
        rules.append("    $r: RangeConstraintCandidate{0}(greaterValue >= {3} && greaterValue <={4} && lessValue < {3})\n");
        rules.append("then\n");
        rules.append("    addResult{0}(result, $r.getRestriction(), Severity.ERROR, \"Impossible condition\");\n");
        rules.append("    retract ($r);\n");
        rules.append("end\n");

        rules.append("rule \"processLMGM{0}\"\n");
        rules.append("when\n");
        rules.append("    $r: RangeConstraintCandidate{0}(greaterValue < {3} && lessValue > {4})\n");
        rules.append("then\n");
        rules.append("    addResult{0}(result, $r.getRestriction(), Severity.WARNING, \"Both sides are outside the range\");\n");
        rules.append("    retract ($r);\n");
        rules.append("end\n");

        rules.append("rule \"processLMVV{0}\"\n");
        rules.append("when\n");
        rules.append("    $r: RangeConstraintCandidate{0}(greaterValue < {3} && lessValue >= {3} && lessValue <={4})\n");
        rules.append("then\n");
        rules.append("    addResult{0}(result, $r.getRestriction(), Severity.WARNING, \"Left side is outside the range\");\n");
        rules.append("    retract ($r);\n");
        rules.append("end\n");

        rules.append("rule \"processLMLM{0}\"\n");
        rules.append("when\n");
        rules.append("    $r: RangeConstraintCandidate{0}(greaterValue < {3} && lessValue < {3})\n");
        rules.append("then\n");
        rules.append("    addResult{0}(result, $r.getRestriction(), Severity.WARNING, \"Both sides are outside the range\");\n");
        rules.append("    retract ($r);\n");
        rules.append("end\n");

        template = rules.toString();
    }

    public String getConstraintName() {
        return NAME;
    }

    public String getVerifierRule(ConstraintConfiguration config) {        
        return template.replaceAll("\\{0\\}", String.valueOf(System.nanoTime())).replaceAll("\\{1\\}", config.getFactType()).replaceAll("\\{2\\}", config.getFieldName()).replaceAll("\\{3\\}", this.getMin(config)).replaceAll("\\{4\\}", this.getMax(config));
    }

    public ValidationResult validate(Object value, ConstraintConfiguration config) {
        ValidationResult result = new ValidationResult();

        try {
            if (value == null || !(value instanceof Number || value instanceof String)) {
                result.setSuccess(false);
                if (value == null) {
                    result.setMessage("The value is null"); // TODO: I18N
                } else {
                    result.setMessage("Invalid value type " + value.getClass().getName()); // TODO:
                    // I18N
                }
            } else {
                double min = Double.parseDouble(getMin(config));
                double max = Double.parseDouble(getMax(config));
                double d = Double.parseDouble(value.toString());
                result.setSuccess(d > min && d < max);
                if (!result.isSuccess()) {
                    result.setMessage("The value should be between " + min + " and " + max); // TODO:
                    // I18N
                }
            }
        } catch (Throwable t) {
            result.setSuccess(false);
            result.setMessage(t.getMessage()); // TODO: I18N
        }

        return result;
    }

    public String getMin(ConstraintConfiguration conf) {
        try {
            return (String) this.getMandatoryArgument(RANGE_CONSTRAINT_MIN, conf);
        } catch (ArgumentNotSetException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getMax(ConstraintConfiguration conf) {
        try {
            return (String) this.getMandatoryArgument(RANGE_CONSTRAINT_MAX, conf);
        } catch (ArgumentNotSetException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<String> getArgumentKeys() {
        return Arrays.asList(new String[]{RANGE_CONSTRAINT_MIN, RANGE_CONSTRAINT_MAX});
    }

    private Object getMandatoryArgument(String key, ConstraintConfiguration conf) throws ArgumentNotSetException {
        if (!conf.containsArgument(key)) {
            throw new ArgumentNotSetException("The argument " + key + " doesn't exist.");
        }

        Object value = conf.getArgumentValue(key);

        if (value == null) {
            throw new ArgumentNotSetException("The argument " + key + " is null.");
        }

        return value;
    }
}
