package org.drools.factconstraints.server.predefined;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.ValidationResult;
import org.drools.factconstraints.server.DefaultConstraintImpl;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class IntegerConstraint extends DefaultConstraintImpl {
	private static final long serialVersionUID = 501L;
	public static final String NAME = "IntegerConstraint";
	
    public IntegerConstraint(){}

    @Override
    protected String internalVerifierRule(ConstraintConfiguration config,
    		Map<String, Object> context) {
        List<String> constraints = new ArrayList<String>();
        constraints.add("valueType != Field.INT");

        return this.createVerifierRuleTemplate(config, context, "Integer_Field_Constraint", constraints, "The value must be an integer"); //I18N
    }

    @Override
    public ValidationResult validate(Object value, ConstraintConfiguration config) {
        ValidationResult result = new ValidationResult();

        if (value == null){
            result.setSuccess(false);
            result.setMessage("The value is null"); //TODO: I18N
        }else if (value instanceof Integer){
            result.setSuccess(true);
        }else if (value instanceof String){
            try{
                Integer.parseInt((String)value);
                result.setSuccess(true);
            } catch(NumberFormatException ex){
                result.setSuccess(false);
                result.setMessage(ex.getMessage()); //TODO: I18N
            }
        }else{
            result.setSuccess(false);
            result.setMessage("Invalid value type "+value.getClass().getName()); //TODO: I18N
        }

        return result;
    }
}
