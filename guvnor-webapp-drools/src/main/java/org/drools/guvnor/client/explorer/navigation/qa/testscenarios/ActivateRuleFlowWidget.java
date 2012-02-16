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

import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.ActivateRuleFlowGroup;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;

public class ActivateRuleFlowWidget extends Composite {

    private final ScenarioWidget parent;

    public ActivateRuleFlowWidget(FixtureList retList,
                                  Scenario sc,
                                  ScenarioWidget parent) {
        FlexTable outer = new FlexTable();
        render( retList,
                outer,
                sc );

        this.parent = parent;

        initWidget( outer );
    }

    private void render(final FixtureList retList,
                        final FlexTable outer,
                        final Scenario sc) {
        outer.clear();
        outer.getCellFormatter().setStyleName( 0,
                                               0,
                                               "modeller-fact-TypeHeader" );
        outer.getCellFormatter().setAlignment( 0,
                                               0,
                                               HasHorizontalAlignment.ALIGN_CENTER,
                                               HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName( "modeller-fact-pattern-Widget" );
        outer.setWidget( 0,
                         0,
                         new SmallLabel( Constants.INSTANCE.ActivateRuleFlowGroup() ) );
        outer.getFlexCellFormatter().setColSpan( 0,
                                                 0,
                                                 2 );

        int row = 1;
        for ( Fixture fixture : retList ) {
            final ActivateRuleFlowGroup acticateRuleFlowGroup = (ActivateRuleFlowGroup) fixture;
            outer.setWidget( row,
                             0,
                             new SmallLabel( acticateRuleFlowGroup.getName() ) );
            Image del = new ImageButton( Images.INSTANCE.deleteItemSmall(),
                                         Constants.INSTANCE.RemoveThisRuleFlowActivation(),
                                         new ClickHandler() {
                                             public void onClick(ClickEvent w) {
                                                 retList.remove( acticateRuleFlowGroup );
                                                 sc.getFixtures().remove( acticateRuleFlowGroup );
                                                 render( retList,
                                                         outer,
                                                         sc );
                                                 parent.renderEditor();
                                             }
                                         } );
            outer.setWidget( row,
                             1,
                             del );

            row++;
        }
    }
}
