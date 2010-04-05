package org.drools.factconstraint.server;

import java.io.Serializable;
import java.util.List;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.ValidationResult;

/**
 *
 * @author esteban.aliverti@gmail.com
 * @author baunax@gmail.com
 */
public interface Constraint extends Serializable {
    public List<String> getArgumentKeys();

    public ValidationResult validate(Object value, ConstraintConfiguration config);
    public String getVerifierRule(ConstraintConfiguration config);
    public String getConstraintName();
	
}
