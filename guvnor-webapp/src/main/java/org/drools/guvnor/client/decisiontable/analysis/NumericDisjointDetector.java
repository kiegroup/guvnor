/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.decisiontable.analysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NumericDisjointDetector extends DisjointDetector<NumericDisjointDetector> {

    // TODO support operator "in" and optimize to allowedValueList if not continuous
    // private List<BigDecimal> allowedValueList = null;
    private BigDecimal from = null;
    private boolean fromInclusive;
    private BigDecimal to = null;
    private boolean toInclusive;
    private List<BigDecimal> disallowedList = new ArrayList<BigDecimal>(1);

    public NumericDisjointDetector(BigDecimal value, String operator) {
        if (operator.equals("==")) {
            from = value;
            fromInclusive = true;
            to = value;
            toInclusive = true;
        } else if (operator.equals("!=")) {
            disallowedList.add(value);
        } else if (operator.equals("<")) {
            to = value;
            toInclusive = false;
        } else if (operator.equals("<=")) {
            to = value;
            toInclusive = true;
        } else if (operator.equals(">")) {
            from = value;
            fromInclusive = false;
        } else if (operator.equals(">=")) {
            from = value;
            fromInclusive = true;
        } else {
            hasUnrecognizedConstraint = true;
        }
    }

    public void merge(NumericDisjointDetector other) {
        super.merge(other);
        if (from == null) {
            from = other.from;
            fromInclusive = other.fromInclusive;
        } else if (other.from != null) {
            int comparison = from.compareTo(other.from);
            if (comparison < 0) {
                from = other.from;
                fromInclusive = other.fromInclusive;
            } else if (comparison == 0) {
                fromInclusive = fromInclusive && other.fromInclusive;
            }
        }
        if (to == null) {
            to = other.to;
            toInclusive = other.toInclusive;
        } else if (other.to != null) {
            int comparison = to.compareTo(other.to);
            if (comparison > 0) {
                to = other.to;
                toInclusive = other.toInclusive;
            } else if (comparison == 0) {
                toInclusive = toInclusive && other.toInclusive;
            }
        }
        disallowedList.addAll(other.disallowedList);
        optimizeNotList();
        detectImpossibleMatch();
    }

    private void optimizeNotList() {
        for (Iterator<BigDecimal> notIt = disallowedList.iterator(); notIt.hasNext(); ) {
            BigDecimal notValue =  notIt.next();
            if (from != null) {
                int comparison = notValue.compareTo(from);
                if (comparison <= 0) {
                    notIt.remove();
                }
                if (comparison == 0) {
                    fromInclusive = false;
                }
            }
            if (to != null) {
                int comparison = notValue.compareTo(to);
                if (comparison >= 0) {
                    notIt.remove();
                }
                if (comparison == 0) {
                    toInclusive = false;
                }
            }
        }
    }

    private void detectImpossibleMatch() {
        if ( from != null && to != null ) {
            int comparison = from.compareTo(to);
            if (comparison > 0 || (comparison == 0 && (!fromInclusive || !toInclusive) )) {
                impossibleMatch = true;
            }
        }
    }

}
