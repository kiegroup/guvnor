/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.backend.server.AbstractDeleteProjectObserverBridge;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.project.ProjectFactory;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;

/**
 * CDI implementation for guvnor's Workbench
 */
@ApplicationScoped
public class DeleteProjectObserverBridge extends AbstractDeleteProjectObserverBridge<Project> {

    private ProjectFactory<Project> projectFactory;

    public DeleteProjectObserverBridge() {
        //Zero-arg constructor for CDI proxying
    }

    @Inject
    public DeleteProjectObserverBridge( final @Named("ioStrategy") IOService ioService,
                                        final Event<DeleteProjectEvent> deleteProjectEvent,
                                        final ProjectFactory<Project> projectFactory ) {
        super( ioService,
               deleteProjectEvent );
        this.projectFactory = PortablePreconditions.checkNotNull( "projectFactory",
                                                                  projectFactory );
    }

    public void onBatchResourceChanges( final @Observes ResourceDeletedEvent event ) {
        super.onBatchResourceChanges( event );
    }

    public void onBatchResourceChanges( final @Observes ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        super.onBatchResourceChanges( resourceBatchChangesEvent );
    }

    @Override
    protected Project getProject( final Path path ) {
        return projectFactory.simpleProjectInstance( path );
    }

}
