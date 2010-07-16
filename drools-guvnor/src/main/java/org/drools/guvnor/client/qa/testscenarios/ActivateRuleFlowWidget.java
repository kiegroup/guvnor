/**
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

package org.drools.guvnor.client.qa.testscenarios;

import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.testing.ActivateRuleFlowGroup;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:29:06
 * To change this template use File | Settings | File Templates.
 */
public class ActivateRuleFlowWidget extends Composite {
    private Constants            constants = ((Constants) GWT.create( Constants.class ));

    private final ScenarioWidget parent;

    public ActivateRuleFlowWidget(List retList,
                                  Scenario sc,
                                  ScenarioWidget parent) {
        FlexTable outer = new FlexTable();
        render( retList,
                outer,
                sc );

        this.parent = parent;

        initWidget( outer );
    }

    private void render(final List retList,
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
                         new SmallLabel( constants.ActivateRuleFlowGroup() ) );
        outer.getFlexCellFormatter().setColSpan( 0,
                                                 0,
                                                 2 );

        int row = 1;
        for ( Iterator iterator = retList.iterator(); iterator.hasNext(); ) {
            final ActivateRuleFlowGroup acticateRuleFlowGroup = (ActivateRuleFlowGroup) iterator.next();
            outer.setWidget( row,
                             0,
                             new SmallLabel( acticateRuleFlowGroup.name ) );
            Image del = new ImageButton( "images/delete_item_small.gif",
                                         "Remove this rule flow activation.",
                                         new ClickListener() {
                                             public void onClick(Widget w) {
                                                 retList.remove( acticateRuleFlowGroup );
                                                 sc.fixtures.remove( acticateRuleFlowGroup );
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
