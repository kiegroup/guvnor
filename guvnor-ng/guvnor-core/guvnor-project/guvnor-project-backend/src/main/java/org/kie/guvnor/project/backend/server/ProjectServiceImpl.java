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

package org.kie.guvnor.project.backend.server;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Paths;
import org.kie.guvnor.commons.data.workingset.WorkingSetSettings;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class ProjectServiceImpl
        implements ProjectService {

    private static final String SOURCE_FILENAME = "src";
    private static final String POM_FILENAME = "pom.xml";
    private static final String KMODULE_FILENAME = "src/main/resources/META-INF/kmodule.xml";

    @Override
    public Collection<Path> listProjectResources( final Path project ) {
        //TODO {porcelli}
        return emptyList();
    }

    @Override
    public WorkingSetSettings loadWorkingSetConfig( final Path project ) {
        //TODO {porcelli}
        return new WorkingSetSettings();
    }

    @Override
    public Path resolveProject( final Path resource ) {

        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //A project root is the folder containing the pom.xml file. This will be the parent of the "src" folder
        org.kie.commons.java.nio.file.Path p = Paths.get( resource.toURI() ).normalize();
        if ( Files.isRegularFile( p ) ) {
            p = p.getParent();
        }
        while ( p.getNameCount() > 0 && !p.getFileName().toString().equals( SOURCE_FILENAME ) ) {
            p = p.getParent();
        }
        if ( p.getNameCount() == 0 ) {
            return null;
        }
        p = p.getParent();
        if ( p.getNameCount() == 0 || p == null ) {
            return null;
        }
        final org.kie.commons.java.nio.file.Path pomPath = p.resolve( POM_FILENAME );
        if ( !Files.exists( pomPath ) ) {
            return null;
        }
        final org.kie.commons.java.nio.file.Path kmodulePath = p.resolve( KMODULE_FILENAME );
        if ( !Files.exists( kmodulePath ) ) {
            return null;
        }
        return PathFactory.newPath( p.toUri().toString() );
    }

}
