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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import org.drools.guvnor.client.messages.Constants;

/**
 * Simple container for controls to manipulate a Decision Table
 */
public class DecisionTableControlsWidget extends Composite {

    protected static final Constants messages = GWT.create(Constants.class);

    private AbstractDecisionTableWidget dtable;

    private Button addRowButton;
    private Button otherwiseButton;
    private Button analyzeButton;

    public DecisionTableControlsWidget() {
        Panel panel = new HorizontalPanel();

        // Add row button
        addRowButton = new Button(messages.AddRow(),
                new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        if (dtable != null) {
                            dtable.appendRow();
                        }
                    }
                });
        panel.add(addRowButton);

        otherwiseButton = new Button(messages.Otherwise(),
                new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        if (dtable != null) {
                            dtable.makeOtherwiseCell();
                        }
                    }
                });
        otherwiseButton.setEnabled(false);
        panel.add(otherwiseButton);

        // Add row button
        analyzeButton = new Button(messages.Analyze(),
                new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        if (dtable != null) {
                            dtable.analyze();
                        }
                    }
                });
        panel.add(analyzeButton);

        initWidget(panel);
    }

    /**
     * Retrieve "otherwise" button
     *
     * @return
     */
    Button getOtherwiseButton() {
        return this.otherwiseButton;
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
