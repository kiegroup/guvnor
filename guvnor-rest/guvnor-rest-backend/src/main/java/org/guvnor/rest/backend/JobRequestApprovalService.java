package org.guvnor.rest.backend;

import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;

public interface JobRequestApprovalService {

    public void requestApproval(JobRequest jobRequest, JobResult result);

}