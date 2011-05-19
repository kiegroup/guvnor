/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.modeldriven.ui;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;

/**
 * Drop-down for Operators associated with ConnectiveConstraints including
 * supplementary controls for CEP operator parameters
 */
public class ConnectiveConstraintOperatorWidget extends AbstractOperatorWidget {

    public ConnectiveConstraintOperatorWidget(String[] operators,
                                              ConnectiveConstraint bfc) {
        super( operators,
               bfc );
    }

    @Override
    protected boolean isCEPOperator(String value) {
        return SuggestionCompletionEngine.isCEPConnectiveOperator( value );
    }

}
