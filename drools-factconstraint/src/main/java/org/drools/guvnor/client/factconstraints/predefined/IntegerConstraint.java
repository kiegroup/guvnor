package org.drools.guvnor.client.factconstraints.predefined;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.factcontraints.DefaultConstraintImpl;
import org.drools.guvnor.client.factcontraints.ValidationResult;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class IntegerConstraint extends DefaultConstraintImpl {


    public IntegerConstraint(){
    }

    @Override
    public String getVerifierRule() {
        List<String> constraints = new ArrayList<String>();
        constraints.add("valueType != Field.INT");

        return this.createVerifierRuleTemplate("Integer_Field_Constraint", constraints, "The value must be an integer"); //I18N
    }

    @Override
    public ValidationResult validate(Object value) {
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
