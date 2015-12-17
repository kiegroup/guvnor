/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.events;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.rpc.SessionInfo;

/**
 * An event representing creation of a new Project
 */
@Portable
public class NewProjectEvent {

    private Project project;
    private String sessionId;
    private String identity;

    public NewProjectEvent() {
    }

    public NewProjectEvent( final Project project,
                            final String sessionId, 
                            final String identity ) {
        this.project = project;
        this.sessionId = sessionId;
        this.identity = identity;
    }

    public Project getProject() {
        return project;
    }

    public String getSessionId() {
        return sessionId;
    }
    
    public String getIdentity() { 
       return identity; 
    }
}
