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

import static org.guvnor.rest.backend.cmd.AbstractJobCommand.JOB_REQUEST_KEY;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.rest.backend.cmd.AddRepositoryToOrgUnitCmd;
import org.guvnor.rest.backend.cmd.CompileProjectCmd;
import org.guvnor.rest.backend.cmd.CreateOrCloneRepositoryCmd;
import org.guvnor.rest.backend.cmd.CreateOrgUnitCmd;
import org.guvnor.rest.backend.cmd.CreateProjectCmd;
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
    private ExecutorService executorService;

    public void createOrCloneRepositoryRequest( CreateOrCloneRepositoryRequest jobRequest ) {
        executorService.scheduleRequest(CreateOrCloneRepositoryCmd.class.getName(), getContext(jobRequest));
    }

    public void removeRepositoryRequest( RemoveRepositoryRequest jobRequest ) {
        executorService.scheduleRequest(RemoveRepositoryCmd.class.getName(), getContext(jobRequest));
    }

    public void createProjectRequest( CreateProjectRequest jobRequest ) {
        executorService.scheduleRequest(CreateProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void compileProjectRequest( CompileProjectRequest jobRequest ) {
        executorService.scheduleRequest(CompileProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void installProjectRequest( InstallProjectRequest jobRequest ) {
        executorService.scheduleRequest(InstallProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void testProjectRequest( TestProjectRequest jobRequest ) {
        executorService.scheduleRequest(TestProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void deployProjectRequest( DeployProjectRequest jobRequest ) {
        executorService.scheduleRequest(DeployProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void createOrganizationalUnitRequest( CreateOrganizationalUnitRequest jobRequest ) {
        executorService.scheduleRequest(CreateOrgUnitCmd.class.getName(), getContext(jobRequest));
    }

    public void addRepositoryToOrganizationalUnitRequest( AddRepositoryToOrganizationalUnitRequest jobRequest ) {
        executorService.scheduleRequest(AddRepositoryToOrgUnitCmd.class.getName(), getContext(jobRequest));
    }

    public void removeRepositoryFromOrganizationalUnitRequest( RemoveRepositoryFromOrganizationalUnitRequest jobRequest ) {
        executorService.scheduleRequest(RemoveRepositoryFromOrgUnitCmd.class.getName(), getContext(jobRequest));
    }

    public void removeOrganizationalUnitRequest( RemoveOrganizationalUnitRequest jobRequest ) {
        executorService.scheduleRequest(RemoveOrgUnitCmd.class.getName(), getContext(jobRequest));
    }
        
    protected CommandContext getContext(JobRequest jobRequest) {
        CommandContext ctx = new CommandContext();
        ctx.setData(JOB_REQUEST_KEY, jobRequest);
        ctx.setData("retries", 0);

        return ctx;
    }

}
