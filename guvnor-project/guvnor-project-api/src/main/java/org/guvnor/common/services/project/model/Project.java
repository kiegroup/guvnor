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
package org.guvnor.common.services.project.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.authz.RuntimeResource;

/**
 * An item representing a project
 */
@Portable
public class Project implements RuntimeResource {

    private Path rootPath;
    private Path pomXMLPath;
    private Path kmoduleXMLPath;
    private Path importsPath;
    private String projectName;

    private Collection<String> roles = new ArrayList<String>();

    public Project() {
        //For Errai-marshalling
    }

    public Project( final Path rootPath,
                    final Path pomXMLPath,
                    final Path kmoduleXMLPath,
                    final Path importsPath,
                    final String projectName ) {
        this.rootPath = PortablePreconditions.checkNotNull( "rootPath",
                                                            rootPath );
        this.pomXMLPath = PortablePreconditions.checkNotNull( "pomXMLPath",
                                                              pomXMLPath );
        this.kmoduleXMLPath = PortablePreconditions.checkNotNull( "kmoduleXMLPath",
                                                                  kmoduleXMLPath );
        this.importsPath = PortablePreconditions.checkNotNull( "importsPath",
                                                               importsPath );
        this.projectName = PortablePreconditions.checkNotNull( "projectName",
                                                               projectName );
    }

    public Path getRootPath() {
        return this.rootPath;
    }

    public Path getPomXMLPath() {
        return this.pomXMLPath;
    }

    public Path getKModuleXMLPath() {
        return this.kmoduleXMLPath;
    }

    public Path getImportsPath() {
        return this.importsPath;
    }

    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public String getSignatureId() {
        return getClass().getName() + "#" + getRootPath().toURI();
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptySet();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Project ) ) {
            return false;
        }

        Project project = (Project) o;

        if ( !rootPath.equals( project.rootPath ) ) {
            return false;
        }
        if ( !pomXMLPath.equals( project.pomXMLPath ) ) {
            return false;
        }
        if ( !kmoduleXMLPath.equals( project.kmoduleXMLPath ) ) {
            return false;
        }
        if ( !importsPath.equals( project.importsPath ) ) {
            return false;
        }
        if ( !projectName.equals( project.projectName ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = rootPath.hashCode();
        result = 31 * result + pomXMLPath.hashCode();
        result = 31 * result + kmoduleXMLPath.hashCode();
        result = 31 * result + importsPath.hashCode();
        result = 31 * result + projectName.hashCode();
        return result;
    }

}
