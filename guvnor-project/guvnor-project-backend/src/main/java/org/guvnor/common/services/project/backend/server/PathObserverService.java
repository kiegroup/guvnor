/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.project.backend.server;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.project.ProjectFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceDeleted;
import org.uberfire.workbench.events.ResourceDeletedEvent;

@ApplicationScoped
public class PathObserverService {

    //TODO {porcelli} !rename/create!

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ProjectFactory<? extends Project> projectFactory;

    @Inject
    private Event<DeleteProjectEvent> deleteProjectEvent;

    public void onBatchResourceChanges( @Observes final ResourceDeletedEvent event ) {
        if ( event.getPath().getFileName().equals( "pom.xml" ) ) {
            fireDeleteEvent( event.getPath() );
        }
    }

    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        for ( final Map.Entry<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> entry : resourceBatchChangesEvent.getBatch().entrySet() ) {
            if ( entry.getKey().getFileName().equals( "pom.xml" ) && isDelete( entry.getValue() ) ) {
                fireDeleteEvent( entry.getKey() );
            }
        }
    }

    private boolean isDelete( final Collection<ResourceChange> value ) {
        for ( final ResourceChange resourceChange : value ) {
            if ( resourceChange instanceof ResourceDeleted ) {
                return true;
            }
        }
        return false;
    }

    private void fireDeleteEvent( final org.uberfire.backend.vfs.Path _path ) {
        final Path path = ioService.get( URI.create( _path.toURI() ) );
        final Project project = projectFactory.simpleProjectInstance( path.getParent() );
        deleteProjectEvent.fire( new DeleteProjectEvent( project ) );
    }
}
