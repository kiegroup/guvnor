/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.rest.backend.cmd;

import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.UpdateOrganizationalUnitRequest;
import org.kie.api.executor.CommandContext;

public class UpdateOrgUnitCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        UpdateOrganizationalUnitRequest jobRequest = (UpdateOrganizationalUnitRequest) request;

        JobResult result = null;
        try { 
            result = helper.updateOrganizationalUnit( jobRequest.getJobId(),
                                                      jobRequest.getOrganizationalUnitName(),
                                                      jobRequest.getOwner(),
                                                      jobRequest.getDefaultGroupId() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----updateOrganizationalUnit--- , OrganizationalUnit name: {}, OrganizationalUnit owner: {} {} [{}]",
                    jobRequest.getOrganizationalUnitName(), jobRequest.getOwner(), jobRequest.getDefaultGroupId(), status );
        }
        return result;
    }
}
