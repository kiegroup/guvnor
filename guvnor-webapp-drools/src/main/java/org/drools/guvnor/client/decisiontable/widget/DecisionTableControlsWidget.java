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
package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.messages.ConstantsCore;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Simple container for controls to manipulate a Decision Table
 */
public class DecisionTableControlsWidget extends Composite {

    protected static final ConstantsCore messages = GWT.create( ConstantsCore.class );

    private AbstractDecisionTableWidget dtable;

    private Button                      addRowButton;
    private Button                      otherwiseButton;
    private Button                      analyzeButton;

    public DecisionTableControlsWidget() {
        this( false );
    }

    public DecisionTableControlsWidget(final boolean isReadOnly) {
        Panel panel = new HorizontalPanel();

        // Add row button
        addRowButton = new Button( messages.AddRow(),
                                   new ClickHandler() {
                                       public void onClick(ClickEvent event) {
                                           if ( dtable != null ) {
                                               dtable.appendRow();
                                           }
                                       }
                                   } );
        addRowButton.setEnabled( !isReadOnly );
        panel.add( addRowButton );

        otherwiseButton = new Button( messages.Otherwise(),
                                      new ClickHandler() {
                                          public void onClick(ClickEvent event) {
                                              if ( dtable != null ) {
                                                  dtable.makeOtherwiseCell();
                                              }
                                          }
                                      } );
        otherwiseButton.setEnabled( false );
        panel.add( otherwiseButton );

        // Add row button
        analyzeButton = new Button( messages.Analyze(),
                                    new ClickHandler() {
                                        public void onClick(ClickEvent event) {
                                            if ( dtable != null ) {
                                                dtable.analyze();
                                            }
                                        }
                                    } );
        analyzeButton.setEnabled( !isReadOnly );
        panel.add( analyzeButton );

        initWidget( panel );
    }

    /**
     * Enable the "Otherwise" button
     */
    void setEnableOtherwiseButton(boolean isEnabled) {
        otherwiseButton.setEnabled( isEnabled );
    }

    /**
     * Inject DecisionTable to which these controls relate
     * 
     * @param dtable
     */
    void setDecisionTableWidget(AbstractDecisionTableWidget dtable) {
        this.dtable = dtable;
    }

}
