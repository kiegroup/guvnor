package org.guvnor.common.services.shared.rest;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class JobResult {

    private JobRequest.Status status;
    private String jodId;
    private String result;
    private Date completedTime;
    
    public JobRequest.Status getStatus() {
        return status;
    }
    public void setStatus(JobRequest.Status status) {
        this.status = status;
    }
    public String getJodId() {
        return jodId;
    }
    public void setJodId(String jodId) {
        this.jodId = jodId;
    }
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
    public Date getCompletedTime() {
        return completedTime;
    }
    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

}
