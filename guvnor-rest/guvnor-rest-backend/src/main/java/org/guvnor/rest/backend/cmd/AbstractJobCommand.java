package org.guvnor.rest.backend.cmd;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.backend.JobResultManager;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.jbpm.executor.cdi.CDIUtils;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJobCommand implements Command {

    protected static final Logger logger = LoggerFactory.getLogger( AbstractJobCommand.class );
            
    public static final String JOB_REQUEST_KEY = "JobRequest";
    public static final String WORKITEM_KEY = "workItem";

    // for command implementations
    
    protected JobRequestHelper getHelper(CommandContext ctx) throws Exception {
        BeanManager beanManager = getBeanManager();
        return CDIUtils.createBean(JobRequestHelper.class, beanManager);
    }

    protected JobRequest getJobRequest(CommandContext ctx) {
        JobRequest jobRequest = (JobRequest) ctx.getData(JOB_REQUEST_KEY);
        if (jobRequest != null) {
            return jobRequest;
        }

        WorkItem workItem = (WorkItem) ctx.getData(WORKITEM_KEY);
        if (workItem != null) {
            return (JobRequest) workItem.getParameter(JOB_REQUEST_KEY);
        }

        throw new RuntimeException("Unable to find JobRequest");
    }

    protected ExecutionResults getEmptyResult() {
        return new ExecutionResults();
    }
    
    // private helper methods
       
    private JobResultManager getJobManager(CommandContext ctx) throws Exception {
        BeanManager beanManager = getBeanManager();
        return CDIUtils.createBean(JobResultManager.class, beanManager);
    }
   
    private BeanManager getBeanManager() { 
        return BeanManagerProvider.getInstance().getBeanManager();
    }
    
    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        try {
            // approval
            JobRequest request = getJobRequest(ctx);
            JobResult result = createResult(request);

            // save job
            logger.debug( "--- job {} ---, status: {}",  result.getJobId(), result.getStatus());
            JobResultManager jobMgr = getJobManager(ctx);
            result.setLastModified(System.currentTimeMillis());
            jobMgr.putJob(result);

            // if approved, process
            if( JobStatus.APPROVED.equals(request.getStatus()) ) {
                try {
                    result = internalExecute(ctx, request);
                } catch( Exception e ) {
                    result.setStatus(JobStatus.SERVER_ERROR);
                    result.setResult("Request failed because of " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    logger.error("{} [{}] failed because of thrown {}: {}",
                            request.getClass().getSimpleName(), request.getJobId(),
                            e.getClass().getSimpleName(), e.getMessage(), e);
                }

                // save job
                logger.debug( "--- job {} ---, status: {}",  result.getJobId(), result.getStatus());
                result.setLastModified(System.currentTimeMillis());
                jobMgr.putJob(result);
            }

            return getEmptyResult();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
  
    private JobResult createResult(JobRequest jobRequest) { 
        final JobResult jobResult = new JobResult();
        jobResult.setJobId( jobRequest.getJobId() );
        jobResult.setStatus( jobRequest.getStatus() ); 
        return jobResult;
    }


    protected abstract JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception;
   
}


