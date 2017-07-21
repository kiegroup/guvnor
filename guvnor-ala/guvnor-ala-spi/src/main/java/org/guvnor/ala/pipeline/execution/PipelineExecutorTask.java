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

package org.guvnor.ala.pipeline.execution;

import java.util.Optional;

import org.guvnor.ala.pipeline.Stage;

/**
 * This class represents a pipeline execution task.
 */
public interface PipelineExecutorTask {

    /**
     * Indicates the pipeline execution status.
     */
    enum Status {
        SCHEDULED,
        RUNNING,
        FINISHED,
        ERROR,
        STOPPED
    }

    /**
     * @return The task definition that is being executed or was executed.
     */
    PipelineExecutorTaskDef getTaskDef();

    /**
     * The UUID for task.
     */
    String getId();

    /**
     * @return the pipeline execution status.
     */
    Status getPipelineStatus();

    /**
     * @param stage a pipeline Stage for querying the execution status.
     * @return The execution status for the given stage.
     */
    Status getStageStatus(Stage stage);

    /**
     * @param stage a pipeline Stage for querying the execution error.
     * @return The execution error for the Stage in cases where the Stage execution failed, null in any other case.
     */
    Throwable getStageError(Stage stage);

    /**
     * @return The pipeline execution error in cases where execution failed, false in any other case.
     */
    Throwable getPipelineError();

    /**
     * @return An optional object with the pipeline output.
     */
    Optional<?> getOutput();
}