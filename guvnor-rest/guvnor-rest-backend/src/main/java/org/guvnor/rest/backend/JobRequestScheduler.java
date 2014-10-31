/*
* Copyright 2013 JBoss Inc
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
package org.guvnor.rest.backend;

import java.util.Map;

import static org.guvnor.rest.backend.cmd.AbstractJobCommand.JOB_REQUEST_KEY;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.asset.management.model.ExecuteOperationEvent;
import org.guvnor.rest.backend.cmd.AddRepositoryToOrgUnitCmd;
import org.guvnor.rest.backend.cmd.CompileProjectCmd;
import org.guvnor.rest.backend.cmd.CreateOrCloneRepositoryCmd;
import org.guvnor.rest.backend.cmd.CreateOrgUnitCmd;
import org.guvnor.rest.backend.cmd.CreateProjectCmd;
import org.guvnor.rest.backend.cmd.DeleteProjectCmd;
import org.guvnor.rest.backend.cmd.DeployProjectCmd;
import org.guvnor.rest.backend.cmd.InstallProjectCmd;
import org.guvnor.rest.backend.cmd.RemoveOrgUnitCmd;
import org.guvnor.rest.backend.cmd.RemoveRepositoryCmd;
import org.guvnor.rest.backend.cmd.RemoveRepositoryFromOrgUnitCmd;
import org.guvnor.rest.backend.cmd.TestProjectCmd;
import org.guvnor.rest.client.AddRepositoryToOrganizationalUnitRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.RemoveOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveRepositoryFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveRepositoryRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class observing requests for various functions of the REST service
 */
@ApplicationScoped
public class JobRequestScheduler {

    private static final Logger logger = LoggerFactory.getLogger( JobRequestScheduler.class );

    @Inject
    private Event<ExecuteOperationEvent> excuteOperationEvent;

    public void createOrCloneRepositoryRequest( CreateOrCloneRepositoryRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", CreateOrCloneRepositoryCmd.class.getName());
        params.put("Repository", jobRequest.getRepository());
        params.put("Operation", "createOrCloneRepository");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void removeRepositoryRequest( RemoveRepositoryRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", RemoveRepositoryCmd.class.getName());
        params.put("Repository", jobRequest.getRepositoryName());
        params.put("Operation", "removeRepository");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void createProjectRequest( CreateProjectRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", CreateProjectCmd.class.getName());
        params.put("Repository", jobRequest.getRepositoryName());
        params.put("Project", jobRequest.getProjectName());
        params.put("Operation", "createProject");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void deleteProjectRequest( DeleteProjectRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", DeleteProjectCmd.class.getName());
        params.put("Repository", jobRequest.getRepositoryName());
        params.put("Project", jobRequest.getProjectName());
        params.put("Operation", "deleteProject");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void compileProjectRequest( CompileProjectRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", CompileProjectCmd.class.getName());
        params.put("Repository", jobRequest.getRepositoryName());
        params.put("Project", jobRequest.getProjectName());
        params.put("Operation", "compileProject");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void installProjectRequest( InstallProjectRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", InstallProjectCmd.class.getName());
        params.put("Repository", jobRequest.getRepositoryName());
        params.put("Project", jobRequest.getProjectName());
        params.put("Operation", "installProject");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void testProjectRequest( TestProjectRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", TestProjectCmd.class.getName());
        params.put("Repository", jobRequest.getRepositoryName());
        params.put("Project", jobRequest.getProjectName());
        params.put("Operation", "testProject");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void deployProjectRequest( DeployProjectRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", DeployProjectCmd.class.getName());
        params.put("Repository", jobRequest.getRepositoryName());
        params.put("Project", jobRequest.getProjectName());
        params.put("Operation", "deployProject");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void createOrganizationalUnitRequest( CreateOrganizationalUnitRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", CreateOrgUnitCmd.class.getName());
        params.put("Operation", "createOrgUnit");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void addRepositoryToOrganizationalUnitRequest( AddRepositoryToOrganizationalUnitRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", AddRepositoryToOrgUnitCmd.class.getName());
        params.put("Repository", jobRequest.getRepositoryName());
        params.put("Operation", "addRepositoryToOrgUnit");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void removeRepositoryFromOrganizationalUnitRequest( RemoveRepositoryFromOrganizationalUnitRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", RemoveRepositoryFromOrgUnitCmd.class.getName());
        params.put("Repository", jobRequest.getRepositoryName());
        params.put("Operation", "removeRepositoryFromOrgUnit");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }

    public void removeOrganizationalUnitRequest( RemoveOrganizationalUnitRequest jobRequest ) {
        Map<String, Object> params = getContext(jobRequest).getData();
        params.put("CommandClass", RemoveOrgUnitCmd.class.getName());
        params.put("Operation", "removeOrgUnit");

        excuteOperationEvent.fire(new ExecuteOperationEvent(params));
    }
        
    protected CommandContext getContext(JobRequest jobRequest) {
        CommandContext ctx = new CommandContext();
        ctx.setData(JOB_REQUEST_KEY, jobRequest);
        ctx.setData("Retries", 0);
        ctx.setData("Owner", ExecutorService.EXECUTOR_ID);

        return ctx;
    }

}
