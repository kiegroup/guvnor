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

package org.guvnor.ala.registry;

import java.util.Collection;

import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;

/**
 * Registry for storing the pipeline execution traces produced by pipelines launched by the PipelineExecutorTaskManager.
 */
public interface PipelineExecutorRegistry {

    void register(final PipelineExecutorTrace trace);

    void deregister(final String taskId);

    PipelineExecutorTrace getExecutorTrace(String pipelineExecutionId);

    Collection<PipelineExecutorTrace> getExecutorTraces();

    Collection<PipelineExecutorTrace> getExecutorTraces(final String pipelineId);
}