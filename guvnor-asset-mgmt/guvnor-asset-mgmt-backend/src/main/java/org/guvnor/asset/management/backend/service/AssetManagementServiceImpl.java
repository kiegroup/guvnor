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
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.guvnor.asset.management.model.BuildProjectStructureEvent;
import org.guvnor.asset.management.model.ConfigureRepositoryEvent;
import org.guvnor.asset.management.model.PromoteChangesEvent;

import org.guvnor.asset.management.service.AssetManagementService;
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

    public AssetManagementServiceImpl() {
    }
    
    @PostConstruct
    public void init(){
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
	    params.put("Password", password);
	    params.put("ExecServerURL", serverURL);
	    params.put("DeployToRuntime", deployToRuntime.toString());
        buildProjectStructureEvent.fire(new BuildProjectStructureEvent(params));
    }

    @Override
    public void promoteChanges(String repository, String sourceBranch, String destBranch) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("GitRepositoryName", repository);
        params.put("SourceBranchName", sourceBranch);
        params.put("TargetBranchName", destBranch);
        promoteChangesEvent.fire(new PromoteChangesEvent(params));
    }
    
    
    

   

}
