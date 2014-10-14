/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.guvnor.asset.management.backend.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.guvnor.asset.management.model.BuildProjectStructureEvent;
import org.guvnor.asset.management.model.ConfigureRepositoryEvent;
import org.guvnor.asset.management.model.PromoteChangesEvent;

import org.guvnor.asset.management.model.ReleaseProjectEvent;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.bus.server.annotations.Service;


@Service
@ApplicationScoped
public class AssetManagementServiceImpl implements AssetManagementService {
    @Inject
    private Event<ConfigureRepositoryEvent> configureRepositoryEvent;
    @Inject
    private Event<BuildProjectStructureEvent> buildProjectStructureEvent;
    @Inject
    private Event<PromoteChangesEvent> promoteChangesEvent;
    @Inject
    private Event<ReleaseProjectEvent> releaseProjectEvent;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Instance<ProjectService<?>> projectService;

    private boolean supportRuntimeDeployment;

    public AssetManagementServiceImpl() {
    }
    
    @PostConstruct
    public void init(){
        String supportRuntime = "true";
        List<ConfigGroup> globalConfigGroups = configurationService.getConfiguration( ConfigType.GLOBAL );
        boolean globalSettingsDefined = false;
        for ( ConfigGroup globalConfigGroup : globalConfigGroups ) {
            if ( "settings".equals( globalConfigGroup.getName() ) ) {
                supportRuntime = globalConfigGroup.getConfigItemValue("support.runtime.deploy");
                break;
            }
        }
        supportRuntimeDeployment = Boolean.parseBoolean(supportRuntime);

    }

    @Override
    public void configureRepository(String repository, String sourceBranch, String devBranch, String releaseBranch, String version){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("RepositoryName", repository);
        params.put("SourceBranchName", sourceBranch);
        params.put("DevBranchName", devBranch);
        params.put("RelBranchName", releaseBranch);
        params.put("Version", version);
        configureRepositoryEvent.fire(new ConfigureRepositoryEvent(params));
        
    }

    @Override
    public void buildProject(String repository, String branch, String project, String userName, String password, String serverURL, Boolean deployToRuntime) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ProjectURI", repository+"/"+project);
        params.put("BranchName", branch);
	    params.put("Username", userName);
	    params.put("Password", encodePassword(password));
	    params.put("ExecServerURL", serverURL);
	    params.put("DeployToRuntime", Boolean.TRUE.equals(deployToRuntime));
        buildProjectStructureEvent.fire(new BuildProjectStructureEvent(params));
    }

    @Override
    public void promoteChanges(String repository, String sourceBranch, String destBranch) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("RepositoryName", repository);
        params.put("SourceBranchName", sourceBranch);
        params.put("TargetBranchName", destBranch);
        promoteChangesEvent.fire(new PromoteChangesEvent(params));
    }

    @Override
    public void releaseProject(String repository, String branch, String userName, String password, String serverURL, Boolean deployToRuntime, String version) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ProjectURI", repository);
        params.put("ToReleaseBranch", branch);
        params.put("ToReleaseVersion", version);
        params.put("Username", userName);
        params.put("Password", encodePassword(password));
        params.put("ExecServerURL", serverURL);
        params.put("ValidForRelease", Boolean.TRUE);
        params.put("DeployToRuntime", Boolean.TRUE.equals(deployToRuntime));

        releaseProjectEvent.fire(new ReleaseProjectEvent(params));
    }

    @Override
    public boolean supportRuntimeDeployment() {
        return supportRuntimeDeployment;
    }

    @Override
    public Set<Project> getProjects(Repository repository, String branch) {

        return projectService.get().getProjects(repository, branch);
    }

    private String encodePassword(String password) {
        if (password == null) {
            return null;
        }

        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(password.getBytes()));
    }
}
