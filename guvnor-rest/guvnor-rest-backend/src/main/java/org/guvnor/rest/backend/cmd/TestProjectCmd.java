package org.guvnor.rest.backend.cmd;

import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.TestProjectRequest;
import org.kie.internal.executor.api.CommandContext;

public class TestProjectCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        TestProjectRequest jobRequest = (TestProjectRequest) request;

        JobResult result = null;
        try { 
            result = helper.testProject( jobRequest.getJobId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----testProject--- , repositoryName: {}, project name: {} [{}]",
                    jobRequest.getRepositoryName(), jobRequest.getProjectName(), status);
        }
        return result;
    }
}
