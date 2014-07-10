package org.guvnor.rest.backend.cmd;

import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.kie.internal.executor.api.CommandContext;

public class InstallProjectCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        InstallProjectRequest jobRequest = (InstallProjectRequest) request;

        JobResult result = null;
        try { 
            result = helper.installProject( jobRequest.getJobId(), jobRequest.getRepositoryName(), jobRequest.getProjectName() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----installProject--- , repositoryName: {}, project name: {} [{}]",
                    jobRequest.getRepositoryName(), jobRequest.getProjectName(), status);
        }
        return result;
    }
}
