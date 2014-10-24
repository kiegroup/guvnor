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
import org.uberfire.security.authz.RuntimeResource;

/**
 * An item representing a project
 */
@Portable
public class Project implements RuntimeResource {

    protected Path rootPath;
    protected Path pomXMLPath;
    protected String projectName;
    protected Collection<String> modules = new ArrayList<String>();

    private Collection<String> roles = new ArrayList<String>();

    // only loaded by ProjectService.getProjects()
    private POM pom;
    
    public Project() {
        //For Errai-marshalling
    }

    public Project( final Path rootPath,
                    final Path pomXMLPath,
                    final String projectName ) {
        this.rootPath = PortablePreconditions.checkNotNull( "rootPath",
                                                            rootPath );
        this.pomXMLPath = PortablePreconditions.checkNotNull( "pomXMLPath",
                                                              pomXMLPath );
        this.projectName = PortablePreconditions.checkNotNull( "projectName",
                                                               projectName );
    }
    
    public Project( final Path rootPath,
                    final Path pomXMLPath,
                    final String projectName, Collection<String> modules  ) {
        this(rootPath, pomXMLPath, projectName);
        this.modules = modules;
    }

    public Path getRootPath() {
        return this.rootPath;
    }

    public Path getPomXMLPath() {
        return this.pomXMLPath;
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

    public Collection<String> getModules() {
      return modules;
    }
    
    public POM getPom() {
        return pom;
    }

    public void setPom( POM pom ) {
        this.pom = pom;
    }

    @Override
    public int hashCode() {
      int hash = 5;
      hash = 17 * hash + (this.rootPath != null ? this.rootPath.hashCode() : 0);
      hash = 17 * hash + (this.pomXMLPath != null ? this.pomXMLPath.hashCode() : 0);
      hash = 17 * hash + (this.projectName != null ? this.projectName.hashCode() : 0);
      hash = 17 * hash + (this.modules != null ? this.modules.hashCode() : 0);
      hash = 17 * hash + (this.roles != null ? this.roles.hashCode() : 0);
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Project other = (Project) obj;
      if (this.rootPath != other.rootPath && (this.rootPath == null || !this.rootPath.equals(other.rootPath))) {
        return false;
      }
      if (this.pomXMLPath != other.pomXMLPath && (this.pomXMLPath == null || !this.pomXMLPath.equals(other.pomXMLPath))) {
        return false;
      }
      if ((this.projectName == null) ? (other.projectName != null) : !this.projectName.equals(other.projectName)) {
        return false;
      }
      if (this.modules != other.modules && (this.modules == null || !this.modules.equals(other.modules))) {
        return false;
      }
      if (this.roles != other.roles && (this.roles == null || !this.roles.equals(other.roles))) {
        return false;
      }
      return true;
    }




}
