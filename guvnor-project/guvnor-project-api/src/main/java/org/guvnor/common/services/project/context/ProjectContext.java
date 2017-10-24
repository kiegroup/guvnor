/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.uberfire.backend.vfs.Path;

/**
 * Project context contains the active organizational unit, project, module and package.
 * Each field can be null, then there is nothing active.
 * <p>
 * Only the ProjectContextChangeEvent can change this and after each change we need to alert the change handlers.
 */
@ApplicationScoped
public class ProjectContext {

    private OrganizationalUnit activeOrganizationalUnit;
    private Project activeProject;
    private Module activeModule;
    private Package activePackage;

    private Map<ProjectContextChangeHandle, ProjectContextChangeHandler> changeHandlers = new HashMap<ProjectContextChangeHandle, ProjectContextChangeHandler>();

    private Event<ProjectContextChangeEvent> contextChangeEvent;

    public ProjectContext() {
    }

    @Inject
    public ProjectContext(final Event<ProjectContextChangeEvent> contextChangeEvent) {
        this.contextChangeEvent = contextChangeEvent;
    }

    public void onRepositoryRemoved(final @Observes RepositoryRemovedEvent event) {

            if ( activeProject != null && event.getRepository().getAlias().equals(activeProject.getRepository().getAlias())) {
                contextChangeEvent.fire(new ProjectContextChangeEvent(activeOrganizationalUnit));
        }
    }

    public void onOrganizationalUnitUpdated(@Observes final UpdatedOrganizationalUnitEvent event) {
        contextChangeEvent.fire(new ProjectContextChangeEvent(event.getOrganizationalUnit()));
    }

    public void onProjectContextChanged(@Observes final ProjectContextChangeEvent event) {
        this.setActiveOrganizationalUnit(event.getOrganizationalUnit());
        this.setActiveProject(event.getProject());
        this.setActiveModule(event.getModule());
        this.setActivePackage(event.getPackage());

        for (ProjectContextChangeHandler handler : changeHandlers.values()) {
            handler.onChange();
        }
    }

    public Path getActiveRepositoryRoot() {
        return activeProject.getBranch().getPath();
    }

    protected void setActiveOrganizationalUnit(final OrganizationalUnit activeOrganizationalUnit) {
        this.activeOrganizationalUnit = activeOrganizationalUnit;
    }

    public OrganizationalUnit getActiveOrganizationalUnit() {
        return this.activeOrganizationalUnit;
    }

    protected void setActiveProject(final Project activeProject) {
        this.activeProject = activeProject;
    }

    public Project getActiveProject() {
        return this.activeProject;
    }

    public Module getActiveModule() {
        return this.activeModule;
    }

    protected void setActiveModule(final Module activeModule) {
        this.activeModule = activeModule;
    }

    public Package getActivePackage() {
        return this.activePackage;
    }

    protected void setActivePackage(final Package activePackage) {
        this.activePackage = activePackage;
    }

    public ProjectContextChangeHandle addChangeHandler(final ProjectContextChangeHandler changeHandler) {
        ProjectContextChangeHandle handle = new ProjectContextChangeHandle();
        changeHandlers.put(handle,
                           changeHandler);
        return handle;
    }

    public void removeChangeHandler(final ProjectContextChangeHandle projectContextChangeHandle) {
        changeHandlers.remove(projectContextChangeHandle);
    }
}
