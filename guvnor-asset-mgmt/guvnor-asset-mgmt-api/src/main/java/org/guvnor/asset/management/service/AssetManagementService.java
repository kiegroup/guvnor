/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.asset.management.service;

import java.util.Set;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface AssetManagementService {
    void configureRepository(String repository, String sourceBranch, String devBranch, String releaseBranch, String version);
    
    void buildProject(String repository, String branch, String project,
                            String userName, String password, String serverURL, Boolean deployToRuntime);
    
    void promoteChanges(String repository, String sourceBranch, String destBranch);

    void releaseProject(String repository, String branch,
            String userName, String password, String serverURL, Boolean deployToRuntime, String version);

    boolean supportRuntimeDeployment();

    Set<Project> getProjects(Repository repository, String branch);
}
