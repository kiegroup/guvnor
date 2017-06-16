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

package org.guvnor.ala.registry.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.guvnor.ala.registry.PipelineExecutorRegistry;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Base implementation of a PipelineExecutorRegistry.
 */
public abstract class BasePipelineExecutorRegistry
        implements PipelineExecutorRegistry {

    protected Map<String, PipelineExecutorTrace> recordsMap = new ConcurrentHashMap<>();

    @Override
    public void register(final PipelineExecutorTrace trace) {
        checkNotNull("trace",
                     trace);
        recordsMap.put(trace.getTaskId(),
                       trace);
    }

    public void deregister(final String pipelineExecutionId) {
        checkNotNull("pipelineExecutionId",
                     pipelineExecutionId);
        recordsMap.remove(pipelineExecutionId);
    }

    @Override
    public PipelineExecutorTrace getExecutorTrace(final String pipelineExecutionId) {
        return recordsMap.get(pipelineExecutionId);
    }

    @Override
    public Collection<PipelineExecutorTrace> getExecutorTraces() {
        return recordsMap.values();
    }
}