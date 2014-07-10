package org.guvnor.rest.backend.cmd;

import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.kie.internal.executor.api.CommandContext;

public class CreateOrgUnitCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        CreateOrganizationalUnitRequest jobRequest = (CreateOrganizationalUnitRequest) request;

        JobResult result = null;
        try { 
            result = helper.createOrganizationalUnit( jobRequest.getJobId(), jobRequest.getOrganizationalUnitName(), jobRequest.getOwner(), jobRequest.getRepositories() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----createOrganizationalUnit--- , OrganizationalUnit name: {}, OrganizationalUnit owner: {} [{}]",
                    jobRequest.getOrganizationalUnitName(), jobRequest.getOwner(), status );
        }
        return result;
    }
}
