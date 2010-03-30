package org.drools.factconstraints.server.predefined;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.base.evaluators.Operator;
import org.drools.factconstraint.server.DefaultMultiRulesConstraintImpl;
import org.drools.factconstraints.client.ArgumentNotSetException;
import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.ValidationResult;
import org.drools.factconstraints.client.config.SimpleConstraintConfigurationImpl;
import org.drools.verifier.report.components.Severity;

/**
 * 
 * @author esteban.aliverti@gmail.com
 */
public class RangeConstraint extends DefaultMultiRulesConstraintImpl {

	private static final long serialVersionUID = 501L;
	public static final String NAME = "RangeConstraint";

	public static final String RANGE_CONSTRAINT_MIN = "Min.value";
	public static final String RANGE_CONSTRAINT_MAX = "Max.value";

	public static final String CURRENT_OPERATOR = "currOperator";
	
//	private Operator currentOperator;

	@Override
	protected String internalVerifierRule(ConstraintConfiguration config, Map<String, Object> context) {

		this.resetRuleCount(context);

		StringBuilder rules = new StringBuilder();
		for (Operator operator : RangeConstraint.supportedOperators) {
			setCurrentOperator(context, operator);
			rules.append(this.createVerifierRuleTemplate(config, context, 
					"Range_Field_Constraint_" + operator.getOperatorString(), 
					Collections.<String> emptyList(), this.getResultMessage(config, context)));
			this.incrementRuleCount(context);
		}

		return rules.toString();
	}

	private String getResultMessage(ConstraintConfiguration conf, Map<String, Object> context) {
		if (getCurrentOperator(context).getOperatorString().equals(Operator.NOT_EQUAL.getOperatorString())) {
			return "The value must be between " + getMin(conf) + " and " + getMax(conf); // I18N
		} else {
			return "The value must be between " + getMin(conf) + " and " + getMax(conf); // I18N
		}
	}

	@Override
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

	@Override
	protected String getVerifierRestrictionPatternTemplate(ConstraintConfiguration config, Map<String, Object> context) {
		StringBuilder restrictionPattern = new StringBuilder();

		restrictionPattern.append("      ($restriction :LiteralRestriction(\n");
		restrictionPattern.append("            fieldPath == $field.path,\n");
		restrictionPattern.append(this.getOperatorPattern(context));
		restrictionPattern.append("            ((valueType == Field.INT && (intValue < ")
			.append(getMin(config)).append(" || > ").append(getMax(config)).append(")) ");
		restrictionPattern.append("             || \n");
		restrictionPattern.append("            (valueType == Field.DOUBLE && (doubleValue < ")
			.append(getMin(config)).append(" || > ").append(getMax(config)).append(")) ");
		restrictionPattern.append("      )))\n");

		return restrictionPattern.toString();
	}

	private String getOperatorPattern(Map<String, Object> context) {
		return "            operator.operatorString == '" + getCurrentOperator(context).getOperatorString() + "',\n";
	}

	@Override
	protected String getVerifierActionTemplate(ConstraintConfiguration config, Map<String, Object> context) {
		StringBuilder verifierActionTemplate = new StringBuilder();

		if (getCurrentOperator(context).getOperatorString().equals(Operator.EQUAL.getOperatorString())) {
			verifierActionTemplate.append(this.addResult(Severity.ERROR));
		} else if (getCurrentOperator(context).getOperatorString().equals(Operator.NOT_EQUAL.getOperatorString())) {
			verifierActionTemplate.append(this.addResult(Severity.WARNING));
		} else {
			return super.getVerifierActionTemplate(config, context);
		}

		return verifierActionTemplate.toString();
	}

	public List<String> getArgumentKeys() {
		return Arrays.asList(new String[] { RANGE_CONSTRAINT_MIN, RANGE_CONSTRAINT_MAX });
	}
	
	private Operator getCurrentOperator(Map<String, Object> context) {
		return (Operator) context.get(CURRENT_OPERATOR);
	}
	
	private void setCurrentOperator(Map<String, Object> context, Operator operator) {
		context.put(CURRENT_OPERATOR, operator);
	}
	
	public static ConstraintConfiguration getEmptyConfiguration() {
		ConstraintConfiguration config = new SimpleConstraintConfigurationImpl();
		config.setArgumentValue("Min.value", "0");
		config.setArgumentValue("Max.value", "0");
		return config;
	}
}
