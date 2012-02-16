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

package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;


public class InsertFactWidget extends FactWidget {

    public InsertFactWidget(String factType,
                            FixtureList definitionList,
                            Scenario scenario,
                            ScenarioWidget parent,
                            ExecutionTrace executionTrace) {
        super( factType,
               definitionList,
               scenario,
               parent,
               executionTrace,
               Constants.INSTANCE.insertForScenario( factType ) );
    }

    public void onDelete() {
        boolean used = false;
    
        for ( Fixture fixture : definitionList ) {
            if ( fixture instanceof FactData ) {
                final FactData factData = (FactData) fixture;
                if ( scenario.isFactDataReferenced( factData ) ) {
                    used = true;
                    break;
                }
            }
        }
        
        if ( used ) {
            ErrorPopup.showMessage( Constants.INSTANCE.CantRemoveThisBlockAsOneOfTheNamesIsBeingUsed() );
        } else {
            super.onDelete();
        }
    }
}
