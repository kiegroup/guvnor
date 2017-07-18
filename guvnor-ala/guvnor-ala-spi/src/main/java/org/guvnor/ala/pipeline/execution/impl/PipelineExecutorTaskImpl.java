/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.pipeline.execution.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;

import static org.guvnor.ala.pipeline.execution.PipelineExecutor.PIPELINE_EXECUTION_ID;

public class PipelineExecutorTaskImpl
        implements PipelineExecutorTask,
                   Cloneable {

    private PipelineExecutorTaskDef taskDef;

    private String executionId;

    private PipelineExecutorTask.Status pipelineStatus = PipelineExecutorTask.Status.SCHEDULED;

    /**
     * Holds the execution status for the pipeline stages. The state status can change during the pipeline execution.
     */
    private Map<Stage, Status> stageStatus = new HashMap<>();

    /**
     * Holds the execution error for the stages in case there were errors.
     */
    private Map<Stage, Throwable> stageError = new HashMap<>();

    /**
     * Holds the pipeline error in case the pipeline failed.
     */
    private Throwable pipelineError;

    private Optional<?> output = Optional.empty();

    public PipelineExecutorTaskImpl(PipelineExecutorTaskDef taskDef,
                                    String executionId) {
        this.taskDef = taskDef;
        setId(executionId);
        taskDef.getPipeline().getStages().forEach(stage -> setStageStatus(stage,
                                                                          Status.SCHEDULED));
    }

    @Override
    public PipelineExecutorTaskDef getTaskDef() {
        return taskDef;
    }

    @Override
    public String getId() {
        return executionId;
    }

    private void setId(String executionId) {
        this.executionId = executionId;
        getTaskDef().getInput().put(PIPELINE_EXECUTION_ID,
                                    executionId);
    }

    public Status getPipelineStatus() {
        return pipelineStatus;
    }

    public void setPipelineStatus(Status pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
    }

    public void setStageStatus(Stage stage,
                               Status status) {
        stageStatus.put(stage,
                        status);
    }

    public Status getStageStatus(Stage stage) {
        return stageStatus.get(stage);
    }

    public void setStageError(Stage stage,
                              Throwable error) {
        stageError.put(stage,
                       error);
    }

    public Throwable getStageError(Stage stage) {
        return stageError.get(stage);
    }

    public void setPipelineError(Throwable error) {
        this.pipelineError = error;
    }

    public Throwable getPipelineError() {
        return pipelineError;
    }

    @Override
    public Optional<?> getOutput() {
        return output;
    }

    public void setOutput(Object obj) {
        this.output = Optional.ofNullable(obj);
    }

    public void clearErrors() {
        stageError.clear();
        pipelineError = null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        PipelineExecutorTaskImpl clone = new PipelineExecutorTaskImpl(taskDef,
                                                                      executionId);

        clone.setPipelineStatus(this.getPipelineStatus());
        stageStatus.entrySet().forEach(entry -> clone.setStageStatus(entry.getKey(), entry.getValue()));
        stageError.entrySet().forEach(entry -> clone.setStageError(entry.getKey(), entry.getValue()));
        clone.setPipelineError(pipelineError);
        clone.setOutput(output.orElse(null));
        return clone;
    }
}