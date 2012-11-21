/*
 * Copyright 2012 JBoss Inc
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

package org.kie.projecteditor.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.kie.projecteditor.shared.model.KSessionModel;

import javax.enterprise.inject.New;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class KnowledgeBaseConfigurationFormViewImpl
        extends Composite
        implements KnowledgeBaseConfigurationFormView {

    interface KnowledgeBaseConfigurationFormViewImplBinder
            extends
            UiBinder<Widget, KnowledgeBaseConfigurationFormViewImpl> {

    }

    private static KnowledgeBaseConfigurationFormViewImplBinder uiBinder = GWT.create(KnowledgeBaseConfigurationFormViewImplBinder.class);

    @UiField
    TextBox nameTextBox;

    @UiField
    TextBox nameSpaceTextBox;

    @UiField
    RadioButton equalsBehaviorIdentity;

    @UiField
    RadioButton equalsBehaviorEquality;

    @UiField
    RadioButton eventProcessingModeStream;

    @UiField
    RadioButton eventProcessingModeCloud;

    @UiField(provided = true)
    KSessionsPanel statefulSessionsPanel;

    @UiField(provided = true)
    KSessionsPanel statelessSessionsPanel;

    @Inject
    public KnowledgeBaseConfigurationFormViewImpl(@New KSessionsPanel statefulSessionsPanel,
                                                  @New KSessionsPanel statelessSessionsPanel) {
        this.statefulSessionsPanel = statefulSessionsPanel;
        this.statelessSessionsPanel = statelessSessionsPanel;

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setName(String name) {
        nameTextBox.setText(name);
    }

    @Override
    public void setNamespace(String namespace) {
        nameSpaceTextBox.setText(namespace);
    }

    @Override
    public void setEqualsBehaviorEquality() {
        equalsBehaviorEquality.setValue(true);
    }

    @Override
    public void setEqualsBehaviorIdentity() {
        equalsBehaviorIdentity.setValue(true);
    }

    @Override
    public void setEventProcessingModeStream() {
        eventProcessingModeStream.setValue(true);
    }

    @Override
    public void setEventProcessingModeCloud() {
        eventProcessingModeCloud.setValue(true);
    }

    @Override
    public void setStatefulSessions(List<KSessionModel> statefulSessions) {
        statefulSessionsPanel.setSessions(statefulSessions);
    }

    @Override
    public void setStatelessSessions(List<KSessionModel> statefulSessions) {
        statelessSessionsPanel.setSessions(statefulSessions);
    }
}
