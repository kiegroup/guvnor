/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.decisiontable.widget.events.SetGuidedDecisionTableModelEvent;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.uberfire.security.Identity;

/**
 * A Vertical Decision Table composed of a VerticalDecoratedGridWidget
 */
public class VerticalDecisionTableWidget extends AbstractDecisionTableWidget {

    private DecisionTableControlsWidget ctrls;

    public VerticalDecisionTableWidget(GuidedDecisionTable52 model,
                                       SuggestionCompletionEngine sce,
                                       Identity identity,
                                       boolean isReadOnly,
                                       EventBus eventBus) {
        super( model,
               sce,
               identity,
               isReadOnly,
               eventBus );

        VerticalPanel vp = new VerticalPanel();

        // Construct the widget from which we're composed
        widget = new VerticalDecoratedDecisionTableGridWidget( resources,
                                                               cellFactory,
                                                               cellValueFactory,
                                                               dropDownManager,
                                                               isReadOnly,
                                                               eventBus );
        vp.add( widget );

        ctrls = new DecisionTableControlsWidget( this,
                                                 model,
                                                 identity,
                                                 isReadOnly );
        vp.add( ctrls );

        initWidget( vp );

        //Fire event for UI components to set themselves up
        SetGuidedDecisionTableModelEvent sme = new SetGuidedDecisionTableModelEvent( model );
        eventBus.fireEvent( sme );
    }

    @Override
    protected void setEnableOtherwiseButton(boolean isEnabled) {
        ctrls.setEnableOtherwiseButton( isEnabled );
    }

}
