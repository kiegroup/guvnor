/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface ProjectService {

    Collection<Project> getAllProjects();

    Collection<Project> getAllProjects(final OrganizationalUnit organizationalUnit);

    Project newProject(final OrganizationalUnit organizationalUnit,
                       final POM pom);

    Project newProject(final OrganizationalUnit organizationalUnit,
                       final POM pom,
                       final DeploymentMode mode);

    Project resolveProject(final Repository repository);

    Project resolveProject(final Branch branch);

    Project resolveProject(final Module module);

    Project resolveProject(final Path module);

    Project resolveProject(final String name);

    Project resolveProjectByRepositoryAlias(final String repositoryAlias);
}
