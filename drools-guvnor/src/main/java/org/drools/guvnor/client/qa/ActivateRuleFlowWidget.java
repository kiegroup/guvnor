package org.drools.guvnor.client.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.testing.ActivateRuleFlowGroup;
import org.drools.guvnor.client.modeldriven.testing.RetractFact;
import org.drools.guvnor.client.modeldriven.testing.Scenario;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:29:06
 * To change this template use File | Settings | File Templates.
 */
public class ActivateRuleFlowWidget extends Composite {
    private Constants constants = ((Constants) GWT.create( Constants.class ));

    public ActivateRuleFlowWidget(List retList,
                                  Scenario sc) {
        FlexTable outer = new FlexTable();
        render( retList,
                outer,
                sc );

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
                         new SmallLabel( "Activate rule flow group" ) );
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
                                             }
                                         } );
            outer.setWidget( row,
                             1,
                             del );

            row++;
        }
    }
}
