package org.drools.factconstraints.server.predefined;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.ValidationResult;
import org.drools.factconstraints.server.DefaultFieldConstraintImpl;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class InvalidFieldConstraint extends DefaultFieldConstraintImpl {
	private static final long serialVersionUID = 501L;
	public static final String NAME = "IvalidFieldConstraint";

    public InvalidFieldConstraint(){}

    @Override
    protected String internalVerifierRule(ConstraintConfiguration config,
    		Map<String, Object> context) {
        return this.createVerifierRuleTemplate(config, context, "Invalid_Field_Constraint", null, "The field can not be used in this rule"); //I18N
    }

    @Override
    public ValidationResult validate(Object value, ConstraintConfiguration config) {
        return null;
    }

    @Override
    protected String getFieldRestrictionClassName(){
        return "Restriction";
    }

}
