/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ide.common.server.factconstraints.factory;

import org.drools.ide.common.client.factconstraints.ConstraintConfiguration;
import org.drools.ide.common.server.factconstraints.Constraint;
import org.drools.ide.common.server.factconstraints.predefined.IntegerConstraint;
import org.drools.ide.common.server.factconstraints.predefined.InvalidFieldConstraint;
import org.drools.ide.common.server.factconstraints.predefined.MandatoryFieldConstraint;
import org.drools.ide.common.server.factconstraints.predefined.MatchesConstraint;
import org.drools.ide.common.server.factconstraints.predefined.NotMatchesConstraint;
import org.drools.ide.common.server.factconstraints.predefined.NotNullConstraint;
import org.drools.ide.common.server.factconstraints.predefined.RangeConstraint;

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
        }else if (MatchesConstraint.NAME.equals(config.getConstraintName())) {
            return new MatchesConstraint();
        }else if (InvalidFieldConstraint.NAME.equals(config.getConstraintName())) {
            return new InvalidFieldConstraint();
        }else if (MandatoryFieldConstraint.NAME.equals(config.getConstraintName())) {
            return new MandatoryFieldConstraint();
        } else {
            throw new IllegalArgumentException("Constraint unknown: " + config.getConstraintName());
        }
    }

    public String getVerifierRule(ConstraintConfiguration config) {
        return buildConstraint(config).getVerifierRule(config);
    }
}
