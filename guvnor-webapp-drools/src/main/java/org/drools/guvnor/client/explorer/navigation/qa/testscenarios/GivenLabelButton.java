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

import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;

public class GivenLabelButton extends HorizontalPanel {

    public GivenLabelButton(ExecutionTrace previousEx,
                            Scenario scenario,
                            ExecutionTrace executionTrace,
                            ScenarioWidget scenarioWidget) {

        add(new NewDataButton(previousEx,
                scenario,
                executionTrace,
                scenarioWidget));
        add(new SmallLabel(Constants.INSTANCE.GIVEN()));

    }

}
