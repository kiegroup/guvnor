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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;

/**
 * A specialized implementation that also has Project and Package scope
 */
@ApplicationScoped
public class ProjectContext {

    private OrganizationalUnit activeOrganizationalUnit;
    private Repository activeRepository;
    private Project activeProject;
    private Package activePackage;

    public void onProjectContextChanged( @Observes final ProjectContextChangeEvent event ) {
        this.setActiveOrganizationalUnit( event.getOrganizationalUnit() );
        this.setActiveRepository( event.getRepository() );
        this.setActiveProject( event.getProject() );
        this.setActivePackage( event.getPackage() );
    }

    public void setActiveOrganizationalUnit( final OrganizationalUnit activeOrganizationalUnit ) {
        this.activeOrganizationalUnit = activeOrganizationalUnit;
    }

    public OrganizationalUnit getActiveOrganizationalUnit() {
        return this.activeOrganizationalUnit;
    }

    public void setActiveRepository( final Repository activeRepository ) {
        this.activeRepository = activeRepository;
    }

    public Repository getActiveRepository() {
        return this.activeRepository;
    }

    public Project getActiveProject() {
        return this.activeProject;
    }

    public void setActiveProject( final Project activeProject ) {
        this.activeProject = activeProject;
    }

    public Package getActivePackage() {
        return this.activePackage;
    }

    public void setActivePackage( final Package activePackage ) {
        this.activePackage = activePackage;
    }

}
