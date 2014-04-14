/*
 * Copyright 2013 JBoss Inc
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
package org.guvnor.common.services.project.context;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;

/**
 * An event raised when the Project Context changes
 */
@Portable
public class ProjectContextChangeEvent {

    private final OrganizationalUnit ou;
    private final Repository repository;
    private final Project project;
    private final Package pkg;

    public ProjectContextChangeEvent() {
        ou = null;
        repository = null;
        project = null;
        pkg = null;
    }

    public ProjectContextChangeEvent( final OrganizationalUnit ou ) {
        this( ou,
              null );
    }

    public ProjectContextChangeEvent( final OrganizationalUnit ou,
                                      final Repository repository ) {
        this( ou,
              repository,
              null );
    }

    public ProjectContextChangeEvent( final OrganizationalUnit ou,
                                      final Repository repository,
                                      final Project project ) {
        this( ou,
              repository,
              project,
              null );
    }

    public ProjectContextChangeEvent( final OrganizationalUnit ou,
                                      final Repository repository,
                                      final Project project,
                                      final Package pkg ) {
        this.ou = ou;
        this.repository = repository;
        this.project = project;
        this.pkg = pkg;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return ou;
    }

    public Repository getRepository() {
        return repository;
    }

    public Project getProject() {
        return project;
    }

    public Package getPackage() {
        return pkg;
    }

}
