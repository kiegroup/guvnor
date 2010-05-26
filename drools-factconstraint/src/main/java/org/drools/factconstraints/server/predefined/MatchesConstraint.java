package org.drools.factconstraints.server.predefined;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.server.DefaultFieldConstraintImpl;

public class MatchesConstraint extends DefaultFieldConstraintImpl {

	public static final String MATCHES_ARGUMENT = "matches";
	private static final long serialVersionUID = 501L;
	public static final String NAME = "Matches";

	@Override
	protected String internalVerifierRule(ConstraintConfiguration config, Map<String, Object> context) {
		List<String> constraints = new ArrayList<String>();
		constraints.add("valueAsString not matches \"" + config.getArgumentValue(MATCHES_ARGUMENT) + "\"");

		return this.createVerifierRuleTemplate(config, context, 
				"Matches_Field_Constraint", constraints,
				"The value must match: " + config.getArgumentValue(MATCHES_ARGUMENT)); // I18N
	}

}
