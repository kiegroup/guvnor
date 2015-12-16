/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.rest.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.runtime.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JobResultManager {

    private static final Logger logger = LoggerFactory.getLogger(JobResultManager.class);
    private static AtomicInteger created = new AtomicInteger(0);

    private static class Cache extends LinkedHashMap<String, JobResult> {

        private int maxSize = 1000;

        public Cache(int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, JobResult> stringFutureEntry) {
            return size() > maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }
    }

    private Map<String, JobResult> jobs = null;

    private int maxCacheSize = 10000;

    @Inject
    private Instance<ExecutorService> jobExecutor;

    @PostConstruct
    public void start() {
        if (!created.compareAndSet(0, 1)) {
            throw new IllegalStateException("Only 1 JobResultManager instance is allowed per container!");
        }
        Cache cache = new Cache(maxCacheSize);
        jobs = Collections.synchronizedMap(cache);
    }

    public JobResult getJob(String jobId) {
        JobResult job = jobs.get(jobId);

        if (job != null && !JobStatus.ACCEPTED.equals(job.getStatus())) {
            return job;
        }

        if (!jobExecutor.isUnsatisfied()) {

            List<RequestInfo> jobsFound = jobExecutor.get().getRequestsByBusinessKey(jobId, new QueryContext());

            if (jobsFound != null && !jobsFound.isEmpty()) {
                RequestInfo executorJob = jobsFound.get(0);
                JobResult requestedJob = (JobResult) getItemFromRequestOutput("JobResult", executorJob);
                if (requestedJob == null) {
                    JobRequest jobRequest = (JobRequest) getItemFromRequestInput("JobRequest", executorJob);
                    if (jobRequest != null) {
                        requestedJob = new JobResult();
                        requestedJob.setJobId(jobRequest.getJobId());
                        requestedJob.setStatus(jobRequest.getStatus());
                    }
                }

                // if it was found set it in cache
                if (requestedJob != null) {
                    job = requestedJob;
                    jobs.put(jobId, job);
                }
            }
        }

        return job;
    }

    public void putJob(JobResult job) {
        jobs.put(job.getJobId(), job);
    }

    public JobResult removeJob(String jobId) {
        return jobs.remove(jobId);
    }

    protected Object getItemFromRequestInput(String itemName, RequestInfo requestInfo) {
        CommandContext ctx = null;
        byte[] requestData = requestInfo.getRequestData();
        if (requestData != null) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new ByteArrayInputStream(requestData));
                ctx = (CommandContext) in.readObject();
            } catch (Exception e) {
                logger.debug("Exception while deserializing context data of job with id {}", requestInfo.getId(), e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {

                    }
                }
            }
        }

        if (ctx != null && ctx.getData(itemName) != null) {
            return ctx.getData(itemName);
        }

        return null;
    }

    protected Object getItemFromRequestOutput(String itemName, RequestInfo requestInfo) {
        ExecutionResults execResults = null;
        byte[] responseData = requestInfo.getResponseData();
        if (responseData != null) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new ByteArrayInputStream(responseData));
                execResults = (ExecutionResults) in.readObject();
            } catch (Exception e) {
                logger.debug("Exception while deserializing context data of job with id {}", requestInfo.getId(), e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {

                    }
                }
            }
        }

        if (execResults != null && execResults.getData(itemName) != null) {
            return execResults.getData(itemName);
        }

        return null;
    }
}
