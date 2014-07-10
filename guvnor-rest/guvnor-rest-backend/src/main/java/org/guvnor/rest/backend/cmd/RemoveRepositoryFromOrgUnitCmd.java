package org.guvnor.rest.backend.cmd;

import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.RemoveRepositoryFromOrganizationalUnitRequest;
import org.kie.internal.executor.api.CommandContext;

public class RemoveRepositoryFromOrgUnitCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        RemoveRepositoryFromOrganizationalUnitRequest jobRequest = (RemoveRepositoryFromOrganizationalUnitRequest) request;

        JobResult result = null;
        try { 
            result = helper.removeRepositoryFromOrganizationalUnit( jobRequest.getJobId(), jobRequest.getOrganizationalUnitName(), jobRequest.getRepositoryName() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----removeRepositoryFromOrganizationalUnit--- , OrganizationalUnit name: {}, repository name: {} [{}]",
                    jobRequest.getOrganizationalUnitName(), jobRequest.getRepositoryName(), status);
        }
        return result;
    }
}
