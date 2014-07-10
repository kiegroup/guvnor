package org.guvnor.rest.backend.cmd;

import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.kie.internal.executor.api.CommandContext;

public class CreateOrCloneRepositoryCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        CreateOrCloneRepositoryRequest jobRequest = (CreateOrCloneRepositoryRequest) request;

        JobResult result = null;
        try { 
        result =  helper.createOrCloneRepository( jobRequest.getJobId(), jobRequest.getRepository() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "----createOrCloneRepository--- , repository name: {} [{}]", 
                    jobRequest.getRepository().getName(), status);
        }
        return result;
    }
}
