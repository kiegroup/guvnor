/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.services.rest;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskManager;
import org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTaskDefImpl;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.PipelineService;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;
import org.guvnor.ala.services.exceptions.BusinessException;

@ApplicationScoped
public class RestPipelineServiceImpl implements PipelineService {

    private PipelineExecutorTaskManager executorTaskManager;

    private PipelineRegistry pipelineRegistry;

    private RuntimeRegistry runtimeRegistry;

    public RestPipelineServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public RestPipelineServiceImpl(PipelineExecutorTaskManager executorTaskManager,
                                   PipelineRegistry pipelineRegistry,
                                   RuntimeRegistry runtimeRegistry) {
        this.executorTaskManager = executorTaskManager;
        this.pipelineRegistry = pipelineRegistry;
        this.runtimeRegistry = runtimeRegistry;
    }

    @Override
    public PipelineConfigsList getPipelineConfigs(Integer page,
                                                  Integer pageSize,
                                                  String sort,
                                                  boolean sortOrder) throws BusinessException {
        final List<PipelineConfig> configs =
                pipelineRegistry.getPipelines(page,
                                              pageSize,
                                              sort,
                                              sortOrder).stream()
                        .filter(p -> p.getConfig() != null)
                        .map(Pipeline::getConfig)
                        .collect(Collectors.toList());

        return new PipelineConfigsList(configs);
    }

    @Override
    public PipelineConfigsList getPipelineConfigs(String providerTypeName,
                                                  String providerTypeVersion,
                                                  Integer page,
                                                  Integer pageSize,
                                                  String sort,
                                                  boolean sortOrder) throws BusinessException {
        final List<PipelineConfig> configs =
                pipelineRegistry.getPipelines(providerTypeName,
                                              providerTypeVersion,
                                              page,
                                              pageSize,
                                              sort,
                                              sortOrder)
                        .stream()
                        .filter(p -> p.getConfig() != null)
                        .map(Pipeline::getConfig)
                        .collect(Collectors.toList());

        return new PipelineConfigsList(configs);
    }

    @Override
    public List<String> getPipelineNames(String providerTypeName,
                                         String providerTypeVersion,
                                         Integer page,
                                         Integer pageSize,
                                         String sort,
                                         boolean sortOrder) throws BusinessException {
        return pipelineRegistry.getPipelines(providerTypeName,
                                             providerTypeVersion,
                                             page,
                                             pageSize,
                                             sort,
                                             sortOrder)
                .stream()
                .map(Pipeline::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String newPipeline(PipelineConfig config) throws BusinessException {
        final Pipeline pipeline = PipelineFactory.startFrom(null).build(config);
        pipelineRegistry.registerPipeline(pipeline);
        return config.getName();
    }

    @Override
    public String newPipeline(PipelineConfig config,
                              ProviderType providerType) throws BusinessException {
        final Pipeline pipeline = PipelineFactory.startFrom(null).build(config);
        pipelineRegistry.registerPipeline(pipeline,
                                          providerType);
        return config.getName();
    }

    @Override
    public String runPipeline(final String name,
                              final Input input,
                              final boolean async) throws BusinessException {
        final Pipeline pipeline = pipelineRegistry.getPipelineByName(name);
        if (pipeline == null) {
            throw new BusinessException("Pipeline: " + name + " was not found.");
        }
        String providerName = input.get(ProviderConfig.PROVIDER_NAME);
        Provider provider = null;
        ProviderType providerType = null;
        PipelineExecutorTaskDef taskDef;

        if (providerName != null && !providerName.isEmpty()) {
            provider = runtimeRegistry.getProvider(providerName);
        }
        if (provider == null) {
            providerType = pipelineRegistry.getProviderType(name);
        }

        if (provider != null) {
            taskDef = new PipelineExecutorTaskDefImpl(pipeline,
                                                      input,
                                                      provider);
        } else if (providerType != null) {
            taskDef = new PipelineExecutorTaskDefImpl(pipeline,
                                                      input,
                                                      providerType);
        } else {
            taskDef = new PipelineExecutorTaskDefImpl(pipeline,
                                                      input);
        }

        return executorTaskManager.execute(taskDef,
                                           async ? PipelineExecutorTaskManager.ExecutionMode.ASYNCHRONOUS :
                                                   PipelineExecutorTaskManager.ExecutionMode.SYNCHRONOUS);
    }
}