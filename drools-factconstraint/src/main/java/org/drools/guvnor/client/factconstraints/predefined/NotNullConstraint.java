package org.drools.guvnor.client.factconstraints.predefined;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.factcontraints.DefaultConstraintImpl;
import org.drools.guvnor.client.factcontraints.ValidationResult;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class NotNullConstraint extends DefaultConstraintImpl {


    public NotNullConstraint(){
    }

    @Override
    public String getVerifierRule() {
        List<String> constraints = new ArrayList<String>();
        constraints.add("valueType == Field.UNKNOWN");

        return this.createVerifierRuleTemplate("Not_null_Field_Constraint", constraints, "The value could not be null"); //I18N
    }

    @Override
    public ValidationResult validate(Object value) {
        ValidationResult result = new ValidationResult();

        if (value == null){
            result.setSuccess(false);
            result.setMessage("The value could not be null"); //TODO: I18N
        }else {
            result.setSuccess(true);
        }

        return result;
    }
    
}
