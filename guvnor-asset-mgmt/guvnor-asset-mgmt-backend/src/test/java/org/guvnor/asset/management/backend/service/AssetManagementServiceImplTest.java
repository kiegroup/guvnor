/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.asset.management.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.guvnor.asset.management.model.BuildProjectStructureEvent;
import org.guvnor.asset.management.model.ConfigureRepositoryEvent;
import org.guvnor.asset.management.model.PromoteChangesEvent;
import org.guvnor.asset.management.model.ReleaseProjectEvent;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.server.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetManagementServiceImplTest {

    @Mock
    private Instance<ProjectService<?>> projectService;

    @Mock
    private ConfigurationService configurationService;

    private AssetManagementService assetManagementService;

    private final List<Object> receivedEvents = new ArrayList<Object>();

    private Event<ReleaseProjectEvent> releaseProjectEvent = new EventSourceMock<ReleaseProjectEvent>() {
        @Override
        public void fire( ReleaseProjectEvent event ) {
            receivedEvents.add(event);
        }
    };

    private Event<PromoteChangesEvent> promoteChangesEvent = new EventSourceMock<PromoteChangesEvent>() {
        @Override
        public void fire( PromoteChangesEvent event ) {
            receivedEvents.add(event);
        }
    };

    private Event<BuildProjectStructureEvent> buildProjectStructureEvent = new EventSourceMock<BuildProjectStructureEvent>() {
        @Override
        public void fire( BuildProjectStructureEvent event ) {
            receivedEvents.add(event);
        }
    };

    private Event<ConfigureRepositoryEvent> configureRepositoryEvent = new EventSourceMock<ConfigureRepositoryEvent>() {

        @Override
        public void fire( ConfigureRepositoryEvent event ) {
            receivedEvents.add(event);
        }

    };

    @Before
    public void setup() {
        receivedEvents.clear();
        assetManagementService = new AssetManagementServiceImpl(configureRepositoryEvent,
                                                                buildProjectStructureEvent,
                                                                promoteChangesEvent,
                                                                releaseProjectEvent,
                                                                configurationService,
                                                                projectService);
    }

    @Test
    public void testConfigureRepository() {

        assetManagementService.configureRepository("test-repo", "master", "dev", "release", "1.0.0");

        assertEquals(1, receivedEvents.size());

        Object event = receivedEvents.get(0);

        assertTrue(event instanceof  ConfigureRepositoryEvent);
        ConfigureRepositoryEvent eventReceived = (ConfigureRepositoryEvent) event;

        Map<String, Object> parameters = eventReceived.getParams();
        assertNotNull(parameters);
        assertEquals(6, parameters.size());

        assertTrue(parameters.containsKey("RepositoryName"));
        assertTrue(parameters.containsKey("SourceBranchName"));
        assertTrue(parameters.containsKey("DevBranchName"));
        assertTrue(parameters.containsKey("RelBranchName"));
        assertTrue(parameters.containsKey("Version"));
        assertTrue(parameters.containsKey("Owner"));

        assertEquals("test-repo", parameters.get("RepositoryName"));
        assertEquals("master", parameters.get("SourceBranchName"));
        assertEquals("dev", parameters.get("DevBranchName"));
        assertEquals("release", parameters.get("RelBranchName"));
        assertEquals("1.0.0", parameters.get("Version"));
        assertEquals("default-executor", parameters.get("Owner"));
    }

    @Test
    public void testBuildProject() {

        assetManagementService.buildProject("test-repo", "master", "my project", "user", "password", "server-url", true);

        assertEquals(1, receivedEvents.size());

        String encodedPassword = "cGFzc3dvcmQ=";

        Object event = receivedEvents.get(0);
        assertTrue(event instanceof  BuildProjectStructureEvent);
        BuildProjectStructureEvent eventReceived = (BuildProjectStructureEvent) event;

        Map<String, Object> parameters = eventReceived.getParams();
        assertNotNull(parameters);
        assertEquals(7, parameters.size());

        assertTrue(parameters.containsKey("ProjectURI"));
        assertTrue(parameters.containsKey("BranchName"));
        assertTrue(parameters.containsKey("Username"));
        assertTrue(parameters.containsKey("Password"));
        assertTrue(parameters.containsKey("ExecServerURL"));
        assertTrue(parameters.containsKey("DeployToRuntime"));
        assertTrue(parameters.containsKey("Owner"));

        assertEquals("test-repo/my project", parameters.get("ProjectURI"));
        assertEquals("master", parameters.get("BranchName"));
        assertEquals("user", parameters.get("Username"));
        assertEquals(encodedPassword, parameters.get("Password"));
        assertEquals("server-url", parameters.get("ExecServerURL"));
        assertEquals(true, parameters.get("DeployToRuntime"));
        assertEquals("default-executor", parameters.get("Owner"));
    }

    @Test
    public void testPromoteChanges() {

        assetManagementService.promoteChanges("test-repo", "master", "release");

        assertEquals(1, receivedEvents.size());

        Object event = receivedEvents.get(0);

        assertTrue(event instanceof  PromoteChangesEvent);
        PromoteChangesEvent eventReceived = (PromoteChangesEvent) event;

        Map<String, Object> parameters = eventReceived.getParams();
        assertNotNull(parameters);
        assertEquals(4, parameters.size());

        assertTrue(parameters.containsKey("RepositoryName"));
        assertTrue(parameters.containsKey("SourceBranchName"));
        assertTrue(parameters.containsKey("TargetBranchName"));
        assertTrue(parameters.containsKey("Owner"));

        assertEquals("test-repo", parameters.get("RepositoryName"));
        assertEquals("master", parameters.get("SourceBranchName"));
        assertEquals("release", parameters.get("TargetBranchName"));
        assertEquals("default-executor", parameters.get("Owner"));
    }

    @Test
    public void testReleaseProject() {

        assetManagementService.releaseProject("test-repo", "master", "user", "password", "server-url", true, "1.0.0");

        assertEquals(1, receivedEvents.size());

        String encodedPassword = "cGFzc3dvcmQ=";

        Object event = receivedEvents.get(0);
        assertTrue(event instanceof  ReleaseProjectEvent);
        ReleaseProjectEvent eventReceived = (ReleaseProjectEvent) event;

        Map<String, Object> parameters = eventReceived.getParams();
        assertNotNull(parameters);
        assertEquals(9, parameters.size());

        assertTrue(parameters.containsKey("ProjectURI"));
        assertTrue(parameters.containsKey("ValidForRelease"));
        assertTrue(parameters.containsKey("Username"));
        assertTrue(parameters.containsKey("Password"));
        assertTrue(parameters.containsKey("ExecServerURL"));
        assertTrue(parameters.containsKey("DeployToRuntime"));
        assertTrue(parameters.containsKey("ToReleaseBranch"));
        assertTrue(parameters.containsKey("ToReleaseVersion"));
        assertTrue(parameters.containsKey("Owner"));

        assertEquals("test-repo", parameters.get("ProjectURI"));
        assertEquals(true, parameters.get("ValidForRelease"));
        assertEquals("user", parameters.get("Username"));
        assertEquals(encodedPassword, parameters.get("Password"));
        assertEquals("server-url", parameters.get("ExecServerURL"));
        assertEquals(true, parameters.get("DeployToRuntime"));
        assertEquals("master", parameters.get("ToReleaseBranch"));
        assertEquals("1.0.0", parameters.get("ToReleaseVersion"));
        assertEquals("default-executor", parameters.get("Owner"));
    }
}
