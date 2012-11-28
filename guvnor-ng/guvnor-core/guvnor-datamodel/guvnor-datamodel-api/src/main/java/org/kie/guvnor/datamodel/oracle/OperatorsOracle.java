/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.datamodel.oracle;

/**
 *
 */
public class OperatorsOracle {

    private static final String[] EXPLICIT_LIST_OPERATORS = new String[]{ "in", "not in" };

    /**
     * Check whether an operator requires a list of values (i.e. the operator is
     * either "in" or "not in"). Operators requiring a list of values can only
     * be compared to literal values.
     * @param operator
     * @return True if the operator requires a list values
     */
    public static boolean operatorRequiresList( String operator ) {
        if ( operator == null || operator.equals( "" ) ) {
            return false;
        }
        for ( String explicitListOperator : EXPLICIT_LIST_OPERATORS ) {
            if ( operator.equals( explicitListOperator ) ) {
                return true;
            }
        }
        return false;
    }

}
