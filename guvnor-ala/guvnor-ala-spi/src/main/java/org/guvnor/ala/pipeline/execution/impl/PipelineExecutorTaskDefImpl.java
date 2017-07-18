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

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;

public class PipelineExecutorTaskDefImpl
        implements PipelineExecutorTaskDef {

    private Pipeline pipeline;

    private Input input;

    private ProviderId providerId;

    private ProviderType providerType;

    public PipelineExecutorTaskDefImpl(Pipeline pipeline,
                                       Input input) {
        this.pipeline = pipeline;
        this.input = input;
    }

    public PipelineExecutorTaskDefImpl(final Pipeline pipeline,
                                       final Input input,
                                       final ProviderId providerId) {
        this.pipeline = pipeline;
        this.input = input;
        this.providerId = new InternalProviderId(providerId.getId(),
                                                 providerId.getProviderType());
    }

    public PipelineExecutorTaskDefImpl(final Pipeline pipeline,
                                       final Input input,
                                       final ProviderType providerType) {
        this.pipeline = pipeline;
        this.input = input;
        this.providerType = new InternalProviderType(providerType.getProviderTypeName(),
                                                     providerType.getVersion());
    }

    @Override
    public Pipeline getPipeline() {
        return pipeline;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

    @Override
    public ProviderType getProviderType() {
        return providerId != null ? providerId.getProviderType() : providerType;
    }
}