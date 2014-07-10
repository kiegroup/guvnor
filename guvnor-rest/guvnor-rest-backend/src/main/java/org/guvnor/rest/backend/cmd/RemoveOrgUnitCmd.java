package org.guvnor.rest.backend.cmd;

import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.RemoveOrganizationalUnitRequest;
import org.kie.internal.executor.api.CommandContext;

public class RemoveOrgUnitCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        RemoveOrganizationalUnitRequest jobRequest = (RemoveOrganizationalUnitRequest) request;

        JobResult result = null;
        try { 
            result = helper.removeOrganizationalUnit(jobRequest.getJobId(), jobRequest.getOrganizationalUnitName());
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----removeOrganizationalUnit--- , OrganizationalUnit name: {}",
                    jobRequest.getOrganizationalUnitName(), status);
        }
        return result;
    }
}
