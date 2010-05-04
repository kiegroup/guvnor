package org.drools.factconstraints.server.factory;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.server.Constraint;
import org.drools.factconstraints.server.predefined.IntegerConstraint;
import org.drools.factconstraints.server.predefined.MatchesConstraint;
import org.drools.factconstraints.server.predefined.NotMatchesConstraint;
import org.drools.factconstraints.server.predefined.NotNullConstraint;
import org.drools.factconstraints.server.predefined.RangeConstraint;

public class ConstraintsFactory {
	private final static ConstraintsFactory INSTANCE = new ConstraintsFactory();
	
	public static ConstraintsFactory getInstance() {
		return INSTANCE;
	}
	
	private ConstraintsFactory() {}
	
	public Constraint buildConstraint(ConstraintConfiguration config) {
		if (NotNullConstraint.NAME.equals(config.getConstraintName())) {
			return new NotNullConstraint();
		} else if (IntegerConstraint.NAME.equals(config.getConstraintName())) {
			return new IntegerConstraint();
		} else if (RangeConstraint.NAME.equals(config.getConstraintName())) {
			return new RangeConstraint();
		} else if (NotMatchesConstraint.NAME.equals(config.getConstraintName())) {
			return new NotMatchesConstraint();
		} if (MatchesConstraint.NAME.equals(config.getConstraintName())) {
			return new MatchesConstraint();
		} else {
			throw new IllegalArgumentException("Constraint unknown: " + config.getConstraintName());
		}
	}
	
	public String getVerifierRule(ConstraintConfiguration config) {
		return buildConstraint(config).getVerifierRule(config);
	}
}
