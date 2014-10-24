package org.guvnor.rest.backend.cmd;

import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectRequest;
import org.kie.internal.executor.api.CommandContext;

public class CreateProjectCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        CreateProjectRequest jobRequest = (CreateProjectRequest) request;

        JobResult result = null;
        try { 
            result = helper.createProject( jobRequest.getJobId(), jobRequest.getRepositoryName(), jobRequest.getProjectName() ,
                    jobRequest.getProjectGroupId(), jobRequest.getProjectVersion() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            String groupId = jobRequest.getProjectGroupId() == null ? jobRequest.getProjectName() : jobRequest.getProjectGroupId();
            String version = jobRequest.getProjectVersion() == null ? "1.0" : jobRequest.getProjectVersion();
            logger.debug( "-----createProject--- , repositoryName: {}, project : {}:{}:{} [{}]", 
                    jobRequest.getRepositoryName(), jobRequest.getProjectName(), groupId, version, status);
        }
        return result;
    }
}
