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

package org.guvnor.common.services.project.service;

import java.util.Set;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.workingset.client.model.WorkingSetSettings;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
@Remote
public interface ProjectService<T extends Project> {

    WorkingSetSettings loadWorkingSetConfig( final Path project );

    /**
     * Given a Resource path resolve it to the containing Project Path. A Project path is the folder containing pom.xml
     * @param resource
     * @return Path to the folder containing the Project's pom.xml file or null if the resource was not in a Project
     */
    T resolveProject( final Path resource );
    
    Project resolveParentProject( final Path resource );
    
    Project resolveToParentProject( final Path resource );

    /**
     * Gets a list of the  projects in a particular repository
     * @param repository
     * @param branch the branch where we are looking for the projects
     * @return
     */
    Set<Project> getProjects( final Repository repository, final String branch );

    /**
     * Given a Resource path resolve it to the containing Package Path. A Package path is the folder containing the resource.
     * The folder must be within a valid Project structure and at least reference /src/main/java, /src/main/resources,
     * src/test/java or src/test/resources (or deeper).
     * @param resource
     * @return Path to the folder containing the resource file or null if the resource is not in a Package.
     */
    org.guvnor.common.services.project.model.Package resolvePackage( final Path resource );

    /**
     * Given a Project resolves the calculation of all the packages for this project.
     * @param project
     * @return Collection containing all the packages for the project.
     */
    Set<Package> resolvePackages( final Project project );

    Set<Package> resolvePackages( final Package pkg );

    Package resolveDefaultPackage( final Project project );

    Package resolveParentPackage( final Package pkg );

    /**
     * Return true if the file is the Project's pom.xml file
     * @param resource
     * @return
     */
    boolean isPom( Path resource );

    /**
     * Creates a new project to the given path.
     * @param repository
     * @param pom
     * @param baseURL the base URL where the Guvnor is hosted in web container
     * @return
     */
    T newProject( final Repository repository,
                  final POM pom,
                  final String baseURL );


                        
    /**
     * Creates a new package as a child of the provide package.
     * @param pkg
     * @param packageName
     * @return
     */
    Package newPackage( final Package pkg,
                        final String packageName );

    /**
     * Add a group to a project; limiting access to users with the group
     * @param project The Project
     * @param group The required group
     */
    void addGroup( final Project project,
                   final String group );

    /**
     * Remove a group from a project
     * @param project The Project
     * @param group The group
     */
    void removeGroup( final Project project,
                      final String group );

    Path rename( final Path pathToPomXML,
                 final String newName,
                 final String comment );

    void delete( final Path pathToPomXML,
                 final String comment );

    void copy( final Path pathToPomXML,
               final String newName,
               final String comment );

}
