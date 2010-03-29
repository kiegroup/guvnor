package org.drools.guvnor.client.factconstraints.predefined;

import org.drools.guvnor.client.factcontraints.ArgumentNotSetException;
import org.drools.guvnor.client.factcontraints.DefaultConstraintImpl;
import org.drools.guvnor.client.factcontraints.ValidationResult;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class RangeConstraint extends DefaultConstraintImpl {

    public static final String RANGE_CONSTRAINT_MIN = "Min.value";
    public static final String RANGE_CONSTRAINT_MAX = "Max.value";

    private enum Operator{
        EQ,
        NEQ,
        GT,
        LT,
        GE,
        LE
    }

    private Operator currentOperator;

    public RangeConstraint() {
        //set default values
        this.setArgumentValue(RANGE_CONSTRAINT_MIN, "0");
        this.setArgumentValue(RANGE_CONSTRAINT_MAX, "0");
    }

    @Override
    public String getVerifierRule() {
        StringBuilder rules = new StringBuilder();
        for (Operator operator : Operator.values()) {
            this.currentOperator = operator;
            this.createVerifierRuleTemplate("Range_Field_Constraint_"+this.currentOperator, null, "The value must be between " + getMin() + " and " + getMax()); //I18N
        }
    	return rules.toString();
    }

    @Override
    public ValidationResult validate(Object value) {
        ValidationResult result = new ValidationResult();

        try {
            if (value == null || !(value instanceof Number || value instanceof String)) {
                result.setSuccess(false);
                if (value == null) {
                    result.setMessage("The value is null"); //TODO: I18N
                } else {
                    result.setMessage("Invalid value type " + value.getClass().getName()); //TODO: I18N
                }
            } else {
            	double min = Double.parseDouble(getMin()) ;
                double max = Double.parseDouble(getMax());
                double d = Double.parseDouble(value.toString());
                result.setSuccess(d > min && d < max);
                if (!result.isSuccess()) {
                    result.setMessage("The value should be between " + min + " and " + max); //TODO: I18N
                }
            }
        } catch (Throwable t) {
            result.setSuccess(false);
            result.setMessage(t.getMessage()); //TODO: I18N
        }

        return result;
    }

    public String getMin() {
    	try {
			return (String) this.getMandatoryArgument(RANGE_CONSTRAINT_MIN);
		} catch (ArgumentNotSetException e) {
			throw new IllegalStateException(e);
		}
    }
    
    public String getMax() {
    	try {
			return (String) this.getMandatoryArgument(RANGE_CONSTRAINT_MAX);
		} catch (ArgumentNotSetException e) {
			throw new IllegalStateException(e);
		}
    }
    
    @Override
    protected String getVerifierRestrictionPatternTemplate() {
        StringBuilder restrictionPattern = new StringBuilder();

        restrictionPattern.append("      ($restriction :LiteralRestriction(\n");
        restrictionPattern.append("            fieldPath == $field.path,\n");
        restrictionPattern.append("            operator.operatorString == Operator.EQUAL.operatorString,\n");
        restrictionPattern.append("            ((valueType == Field.INT && (intValue < " + getMin() + " || > " + getMax() + ")) ");
        restrictionPattern.append("             || ");
        restrictionPattern.append("            (valueType == Field.DOUBLE && (doubleValue < " + getMin() + " || > " + getMax() + ")) ");
        restrictionPattern.append("      )))\n");

        return restrictionPattern.toString();
    }


//    @Override
//    protected String getVerifierImportsSufixTemplate() {
//        return "import org.drools.base.evaluators.Operator;\n";
//    }
//
//    @Override
//    protected String getVerifierActionSufixTemplate() {
//        return "System.out.println(\"OPERATOR= \"+$restriction.getOperator().getOperatorString()+\". Operator.EQUAL= \"+Operator.EQUAL.getOperatorString());\n";
//    }

}
