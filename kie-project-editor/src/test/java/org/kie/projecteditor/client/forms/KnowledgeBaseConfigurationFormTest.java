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

import org.junit.Before;
import org.junit.Test;
import org.kie.projecteditor.shared.model.AssertBehaviorOption;
import org.kie.projecteditor.shared.model.EventProcessingOption;
import org.kie.projecteditor.shared.model.KSessionModel;
import org.kie.projecteditor.shared.model.KnowledgeBaseConfiguration;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class KnowledgeBaseConfigurationFormTest {


    private KnowledgeBaseConfigurationForm form;
    private KnowledgeBaseConfigurationFormView view;

    @Before
    public void setUp() throws Exception {
        view = mock(KnowledgeBaseConfigurationFormView.class);
        form = new KnowledgeBaseConfigurationForm(view);
    }

    @Test
    public void testCleanUp() throws Exception {
        form.setConfig(new KnowledgeBaseConfiguration());
        verify(view).setName(null);
        verify(view).setNamespace(null);

        ArgumentCaptor<List> statelessSessionModelArgumentCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> statefulModelArgumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(view).setStatefulSessions(statefulModelArgumentCaptor.capture());
        verify(view).setStatelessSessions(statelessSessionModelArgumentCaptor.capture());

        verify(view).setEventProcessingModeStream();
        verify(view, never()).setEventProcessingModeCloud();
        verify(view).setEqualsBehaviorIdentity();
        verify(view, never()).setEqualsBehaviorEquality();

        assertEquals(0, statelessSessionModelArgumentCaptor.getValue().size());
        assertEquals(0, statefulModelArgumentCaptor.getValue().size());
    }

    @Test
    public void testShowSimpleData() throws Exception {
        KnowledgeBaseConfiguration config = new KnowledgeBaseConfiguration();
        config.setFullName("full.name.here.Name");
        config.setName("Name");
        config.setNamespace("full.name.here");

        config.setEqualsBehavior(AssertBehaviorOption.EQUALITY);
        config.setEventProcessingMode(EventProcessingOption.CLOUD);

        config.addKSession(createStatelessKSession());
        config.addKSession(createStatelessKSession());
        config.addKSession(createStatelessKSession());

        config.addKSession(createStatefulKSession());
        config.addKSession(createStatefulKSession());

        form.setConfig(config);
        verify(view).setName("Name");
        verify(view).setNamespace("full.name.here");

        ArgumentCaptor<List> statelessSessionModelArgumentCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> statefulModelArgumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(view).setStatefulSessions(statefulModelArgumentCaptor.capture());
        verify(view).setStatelessSessions(statelessSessionModelArgumentCaptor.capture());

        verify(view, never()).setEventProcessingModeStream();
        verify(view).setEventProcessingModeCloud();
        verify(view, never()).setEqualsBehaviorIdentity();
        verify(view).setEqualsBehaviorEquality();

        assertEquals(3, statelessSessionModelArgumentCaptor.getValue().size());
        assertEquals(2, statefulModelArgumentCaptor.getValue().size());
    }

    private KSessionModel createStatefulKSession() {
        KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setType("stateful");
        return kSessionModel;
    }

    private KSessionModel createStatelessKSession() {
        KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setType("stateless");
        return kSessionModel;
    }

}
