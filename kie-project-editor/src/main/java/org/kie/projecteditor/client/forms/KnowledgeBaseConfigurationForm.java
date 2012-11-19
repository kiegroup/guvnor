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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.projecteditor.shared.model.KSessionModel;
import org.kie.projecteditor.shared.model.KnowledgeBaseConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;

public class KnowledgeBaseConfigurationForm
        implements IsWidget {

    private final KnowledgeBaseConfigurationFormView view;

    @Inject
    public KnowledgeBaseConfigurationForm(KnowledgeBaseConfigurationFormView view) {
        this.view = view;
    }

    public void setConfig(KnowledgeBaseConfiguration knowledgeBaseConfiguration) {

        view.setName(knowledgeBaseConfiguration.getName());
        view.setNamespace(knowledgeBaseConfiguration.getNamespace());

        setEqualsBehaviour(knowledgeBaseConfiguration);

        setEventProcessingMode(knowledgeBaseConfiguration);

        setSessions(knowledgeBaseConfiguration);
    }

    private void setSessions(KnowledgeBaseConfiguration knowledgeBaseConfiguration) {
        ArrayList<KSessionModel> statefulSessions = new ArrayList<KSessionModel>();
        ArrayList<KSessionModel> statelessSessions = new ArrayList<KSessionModel>();

        for (KSessionModel kSessionModel : knowledgeBaseConfiguration.getKSessionModels()) {
            if (kSessionModel.getType().equals("stateful")) {
                statefulSessions.add(kSessionModel);
            } else if (kSessionModel.getType().equals("stateless")) {
                statelessSessions.add(kSessionModel);
            }
        }

        view.setStatefulSessions(statefulSessions);
        view.setStatelessSessions(statelessSessions);
    }

    private void setEventProcessingMode(KnowledgeBaseConfiguration knowledgeBaseConfiguration) {
        switch (knowledgeBaseConfiguration.getEventProcessingMode()) {
            case CLOUD:
                view.setEventProcessingModeCloud();
                break;

            case STREAM:
            default:
                view.setEventProcessingModeStream();
                break;
        }
    }

    private void setEqualsBehaviour(KnowledgeBaseConfiguration knowledgeBaseConfiguration) {
        switch (knowledgeBaseConfiguration.getEqualsBehavior()) {
            case EQUALITY:
                view.setEqualsBehaviorEquality();
                break;

            case IDENTITY:
            default:
                view.setEqualsBehaviorIdentity();
                break;
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
