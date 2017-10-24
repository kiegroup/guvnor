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
package org.guvnor.common.services.project.model;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.util.URIUtil;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
/**
 * Each Project is in a single repository.
 * The workbench requires that there is a single parent pom.xml file in the repository root.
 * There can be child Modules, but they are optional.
 * A Project can have several brances, but the Project model focuses on only one of them.
 * <BR>
 * The Project model contains the Repository field and OrganizationalUnit, but these are here only here for convenience.
 * <b>The real Project root is the Branch root.</b>
 * Please do not use the Repository root path, this can point to any branch even to those that are not used.
 */
public class Project
        implements Cacheable {

    private Repository repository;
    private Branch branch;
    private Module mainModule;
    private OrganizationalUnit organizationalUnit;
    private boolean requiresRefresh = true;

    public Project() {
    }

    public Project(final OrganizationalUnit organizationalUnit,
                   final Repository repository,
                   final Branch branch,
                   final Module mainModule) {
        this.organizationalUnit = checkNotNull("organizationalUnit",
                                               organizationalUnit);
        this.repository = checkNotNull("repository",
                                       repository);
        this.branch = checkNotNull("branch",
                                   branch);
        this.mainModule = mainModule;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    /**
     * This is here for convenience. In case you quickly need the repository information.
     * Please do not use the repository root path unless you are sure you need it. The Branch root is the Project root.
     * @return Repository where the project is.
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Branch is where the Project is located.
     * To change the branch, please recreate and reload the Project. You get way less bugs in UI code this way.
     * @return Currently active branch.
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * @return The Module that exists in the Project root.
     */
    public Module getMainModule() {
        return mainModule;
    }

    public String getEncodedIdentifier() {
        return URIUtil.encodeQueryString(repository.getIdentifier());
    }

    @Override
    public boolean requiresRefresh() {
        return requiresRefresh;
    }

    /**
     * Name resolution sources in priority order: root pom.xml module name, root pom.xml artifactId and if everything else fails we use the repository alias.
     * @return Resolved name of the Project.
     */
    public String getName() {
        if (mainModule != null) {
            final String moduleName = mainModule.getModuleName();
            if (moduleName != null && !mainModule.getModuleName().trim().isEmpty()) {
                return mainModule.getModuleName();
            } else {
                return repository.getAlias();
            }
        } else {
            return repository.getAlias();
        }
    }

    @Override
    public void markAsCached() {
        this.requiresRefresh = false;
    }

    /**
     * Short cut for the Project root.
     * @return The root path of the active branch.
     */
    public Path getRootPath() {
        return branch.getPath();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Project project = (Project) o;

        if (requiresRefresh != project.requiresRefresh) {
            return false;
        }
        if (!repository.equals(project.repository)) {
            return false;
        }
        if (!branch.equals(project.branch)) {
            return false;
        }
        if (mainModule != null ? !mainModule.equals(project.mainModule) : project.mainModule != null) {
            return false;
        }
        return organizationalUnit.equals(project.organizationalUnit);
    }

    @Override
    public int hashCode() {
        int result = ~~repository.hashCode();
        result = 31 * result + ~~branch.hashCode();
        result = 31 * result + (mainModule != null ? ~~mainModule.hashCode() : 0);
        result = 31 * result + ~~organizationalUnit.hashCode();
        result = 31 * result + (requiresRefresh ? 1 : 0);
        return result;
    }
}
