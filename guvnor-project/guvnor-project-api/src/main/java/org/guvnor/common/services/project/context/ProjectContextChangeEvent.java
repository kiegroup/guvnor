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

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * An event raised when the Project Context changes
 */
@Portable
public class ProjectContextChangeEvent {

    private final OrganizationalUnit ou;
    private final Project project;
    private final Module module;
    private final Package pkg;

    public ProjectContextChangeEvent() {
        ou = null;
        project = null;
        module = null;
        pkg = null;
    }

    public ProjectContextChangeEvent(final OrganizationalUnit ou) {
        this.ou = ou;
        this.project = null;
        this.module = null;
        this.pkg = null;
    }

    public ProjectContextChangeEvent(final Project project) {
        this(project,
             null);
    }

    public ProjectContextChangeEvent(final Project project,
                                     final Module module) {
        this(project,
             module,
             null);
    }

    public ProjectContextChangeEvent(final Project project,
                                     final Module module,
                                     final Package pkg) {
        this.ou = project.getOrganizationalUnit();
        this.project = project;
        this.module = module;
        this.pkg = pkg;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return ou;
    }

    public Project getProject() {
        return project;
    }

    public Module getModule() {
        return module;
    }

    public Package getPackage() {
        return pkg;
    }
}
