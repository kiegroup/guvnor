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

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.backend.server.ResourceResolver;
import org.guvnor.common.services.project.model.Project;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.Files;

import static org.guvnor.common.services.project.backend.server.ProjectResourcePaths.*;

public class MavenResourceResolver
        extends ResourceResolver<Project> {

    @Override
    public Project resolveProject( final Path resource ) {
        try {
            //Null resource paths cannot resolve to a Project
            if ( resource == null ) {
                return null;
            }

            //Check if resource is the project root
            org.uberfire.java.nio.file.Path path = Paths.convert( resource ).normalize();

            //A project root is the folder containing the pom.xml file. This will be the parent of the "src" folder
            if ( Files.isRegularFile( path ) ) {
                path = path.getParent();
            }
            if ( hasPom( path ) ) {
                return makeProject( path );
            }
            while ( path.getNameCount() > 0 && !path.getFileName().toString().equals( SOURCE_FILENAME ) ) {
                path = path.getParent();
            }
            if ( path.getNameCount() == 0 ) {
                return null;
            }
            path = path.getParent();
            if ( path.getNameCount() == 0 || path == null ) {
                return null;
            }
            if ( !hasPom( path ) ) {
                return null;
            }
            return makeProject( path );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Project simpleProjectInstance( final org.uberfire.java.nio.file.Path nioProjectRootPath ) {

        final Path projectRootPath = Paths.convert( nioProjectRootPath );

        return new Project( projectRootPath,
                            Paths.convert( nioProjectRootPath.resolve( POM_PATH ) ),
                            projectRootPath.getFileName() );
    }

}
