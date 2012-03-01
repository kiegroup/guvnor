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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.Scenario;


public class FactAssignmentFieldWidget implements IsWidget {

    private final DirtyableFlexTable widget;

    public FactAssignmentFieldWidget(FactData factType,
                                     FixtureList definitionList,
                                     Scenario scenario,
                                     SuggestionCompletionEngine suggestionCompletionEngine,
                                     ScenarioParentWidget parent,
                                     ExecutionTrace executionTrace) {

        widget = new DirtyableFlexTable();
        widget.setStyleName(GuvnorResources.INSTANCE.guvnorCss().greyBorderWithRoundCorners());
        new FactDataWidgetFactory(
                scenario,
                suggestionCompletionEngine,
                definitionList,
                executionTrace,
                parent,
                widget).build(factType.getType(), factType);

    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
