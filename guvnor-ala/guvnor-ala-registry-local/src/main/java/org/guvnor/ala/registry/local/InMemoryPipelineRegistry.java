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

package org.guvnor.ala.registry.local;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.registry.local.utils.PageSortUtils;
import org.guvnor.ala.runtime.providers.ProviderType;

/**
 * @TODO: This is an implementation for local testing only. A
 * more robust and distributed implementation should be provided for real
 * use cases. All the lookups mechanisms and structures needs to be improved for
 * performance.
 */
@ApplicationScoped
public class InMemoryPipelineRegistry implements PipelineRegistry {

    private final Map<String, PipelineRegistryEntry> pipelineByName;

    public InMemoryPipelineRegistry() {
        pipelineByName = new ConcurrentHashMap<>();
    }

    @Override
    public void registerPipeline(Pipeline pipeline) {
        pipelineByName.put(pipeline.getName(),
                           new PipelineRegistryEntry(pipeline));
    }

    @Override
    public void registerPipeline(Pipeline pipeline,
                                 ProviderType providerType) {
        pipelineByName.put(pipeline.getName(),
                           new PipelineRegistryEntry(pipeline,
                                                     providerType));
    }

    @Override
    public Pipeline getPipelineByName(String pipelineName) {
        return pipelineByName.get(pipelineName).getPipeline();
    }

    @Override
    public List<Pipeline> getPipelines(int page,
                                       int pageSize,
                                       String sort,
                                       boolean sortOrder) {
        List<Pipeline> values = pipelineByName.values()
                .stream()
                .map(PipelineRegistryEntry::getPipeline).collect(Collectors.toList());
        return sortPagedResult(values,
                               page,
                               pageSize,
                               sort,
                               sortOrder);
    }

    @Override
    public List<Pipeline> getPipelines(String providerType,
                                       String version,
                                       int page,
                                       int pageSize,
                                       String sort,
                                       boolean sortOrder) {
        List<Pipeline> values = pipelineByName.values()
                .stream()
                .filter(entry -> providerType != null &&
                        entry.getProviderType() != null &&
                        providerType.equals(entry.getProviderType().getProviderTypeName()) &&
                        version != null && version.equals(entry.getProviderType().getVersion())
                )
                .map(PipelineRegistryEntry::getPipeline)
                .collect(Collectors.toList());

        return sortPagedResult(values,
                               page,
                               pageSize,
                               sort,
                               sortOrder);
    }

    private List<Pipeline> sortPagedResult(List<Pipeline> values,
                                           int page,
                                           int pageSize,
                                           String sort,
                                           boolean sortOrder) {
        return PageSortUtils.pageSort(values,
                                      (Pipeline p1, Pipeline p2) -> {
                                          switch (sort) {
                                              case "name":
                                                  return p1.getName().compareTo(p2.getName());
                                              default:
                                                  return p1.toString().compareTo(p2.toString());
                                          }
                                      },
                                      page,
                                      pageSize,
                                      sort,
                                      sortOrder);
    }

    @Override
    public ProviderType getProviderType(String pipelineId) {
        PipelineRegistryEntry entry = pipelineByName.get(pipelineId);
        return entry != null ? entry.getProviderType() : null;
    }

    private class PipelineRegistryEntry {

        private Pipeline pipeline;

        private ProviderType providerType;

        public PipelineRegistryEntry(Pipeline pipeline) {
            this.pipeline = pipeline;
        }

        public PipelineRegistryEntry(Pipeline pipeline,
                                     ProviderType providerType) {
            this.pipeline = pipeline;
            this.providerType = providerType;
        }

        public Pipeline getPipeline() {
            return pipeline;
        }

        public ProviderType getProviderType() {
            return providerType;
        }
    }
}
