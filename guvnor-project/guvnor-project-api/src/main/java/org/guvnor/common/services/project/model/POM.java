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

package org.guvnor.common.services.project.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.List;

@Portable
public class POM {

    private static final String MODEL_VERSION = "4.0.0";

    private GAV parent;
    private GAV gav;
    private String name;
    private String description;
    
    private boolean multiModule;

    private List<Dependency> dependencies = new ArrayList<Dependency>();
    private List<Repository> repositories = new ArrayList<Repository>();
    private List<String> modules = new ArrayList<String>();

    public POM() {
        this.gav = new GAV();
    }

    // Kept this for backwards compatibility
    public POM(GAV gav) {
        this(null, null, gav);
    }

    public POM(String name, String description, GAV gav) {
        super();
        this.name = name;
        this.description = description;
        this.gav = gav;
        this.multiModule = false;
    }
    
    public POM(String name, String description, GAV gav, boolean multiModule) {
        super();
        this.name = name;
        this.description = description;
        this.gav = gav;
        this.multiModule = multiModule;
    }

    public GAV getGav() {
        return gav;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void addRepository(Repository repository) {
        repositories.add(repository);
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public String getModelVersion() {
        return MODEL_VERSION;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public GAV getParent() {
      return parent;
    }

    public void setParent(GAV parent) {
      this.parent = parent;
    }

    public List<String> getModules() {
      return modules;
    }

    public void setModules(List<String> modules) {
      this.modules = modules;
    }

    public boolean isMultiModule() {
      return multiModule;
    }

    public void setMultiModule(boolean multiModule) {
      this.multiModule = multiModule;
    }

    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        POM pom = ( POM ) o;

        if ( multiModule != pom.multiModule ) {
            return false;
        }
        if ( dependencies != null ? !dependencies.equals( pom.dependencies ) : pom.dependencies != null ) {
            return false;
        }
        if ( description != null ? !description.equals( pom.description ) : pom.description != null ) {
            return false;
        }
        if ( gav != null ? !gav.equals( pom.gav ) : pom.gav != null ) {
            return false;
        }
        if ( modules != null ? !modules.equals( pom.modules ) : pom.modules != null ) {
            return false;
        }
        if ( name != null ? !name.equals( pom.name ) : pom.name != null ) {
            return false;
        }
        if ( parent != null ? !parent.equals( pom.parent ) : pom.parent != null ) {
            return false;
        }
        if ( repositories != null ? !repositories.equals( pom.repositories ) : pom.repositories != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( gav != null ? gav.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( description != null ? description.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( multiModule ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( dependencies != null ? dependencies.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( repositories != null ? repositories.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( modules != null ? modules.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
