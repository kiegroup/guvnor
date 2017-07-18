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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.guvnor.ala.registry.PipelineExecutorRegistry;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.PipelineStageItem;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.RuntimeQuery;
import org.guvnor.ala.services.api.RuntimeQueryResultItem;
import org.guvnor.ala.services.api.itemlist.PipelineStageItemList;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.guvnor.ala.services.api.itemlist.RuntimeQueryResultItemList;
import org.guvnor.ala.services.exceptions.BusinessException;
import org.guvnor.ala.services.rest.factories.ProviderFactory;
import org.guvnor.ala.services.rest.factories.RuntimeFactory;
import org.guvnor.ala.services.rest.factories.RuntimeManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.validation.PortablePreconditions;

@ApplicationScoped
public class RestRuntimeProvisioningServiceImpl implements RuntimeProvisioningService {

    private static final Logger LOG = LoggerFactory.getLogger(RestRuntimeProvisioningServiceImpl.class);

    private BeanManager beanManager;

    private ProviderFactory providerFactory;

    private RuntimeRegistry runtimeRegistry;

    private RuntimeFactory runtimeFactory;

    private RuntimeManagerFactory runtimeManagerFactory;

    private PipelineExecutorRegistry pipelineExecutorRegistry;

    public RestRuntimeProvisioningServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public RestRuntimeProvisioningServiceImpl(final BeanManager beanManager,
                                              final ProviderFactory providerFactory,
                                              final RuntimeRegistry runtimeRegistry,
                                              final RuntimeFactory runtimeFactory,
                                              final RuntimeManagerFactory runtimeManagerFactory,
                                              final PipelineExecutorRegistry pipelineExecutorRegistry) {
        this.beanManager = beanManager;
        this.providerFactory = providerFactory;
        this.runtimeRegistry = runtimeRegistry;
        this.runtimeFactory = runtimeFactory;
        this.runtimeManagerFactory = runtimeManagerFactory;
        this.pipelineExecutorRegistry = pipelineExecutorRegistry;
    }

    @PostConstruct
    public void cacheBeans() {
        LOG.info("> Initializing ProviderTypes. ");
        final Set<Bean<?>> beans = beanManager.getBeans(ProviderType.class,
                                                        new AnnotationLiteral<Any>() {
                                                        });
        for (final Bean b : beans) {
            try {
                // I don't want to register the CDI proxy, I need a fresh instance :(
                ProviderType pt = (ProviderType) b.getBeanClass().newInstance();
                LOG.info("> Registering ProviderType: " + pt.getProviderTypeName());
                runtimeRegistry.registerProviderType(pt);
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.error("Something went wrong with registering Provider Types!",
                          ex);
            }
        }
    }

    @Override
    public ProviderTypeList getProviderTypes(Integer page,
                                             Integer pageSize,
                                             String sort,
                                             boolean sortOrder) throws BusinessException {
        return new ProviderTypeList(runtimeRegistry.getProviderTypes(page,
                                                                     pageSize,
                                                                     sort,
                                                                     sortOrder));
    }

    @Override
    public ProviderList getProviders(Integer page,
                                     Integer pageSize,
                                     String sort,
                                     boolean sortOrder) throws BusinessException {
        return new ProviderList(runtimeRegistry.getProviders(page,
                                                             pageSize,
                                                             sort,
                                                             sortOrder));
    }

    @Override
    public RuntimeList getRuntimes(Integer page,
                                   Integer pageSize,
                                   String sort,
                                   boolean sortOrder) throws BusinessException {
        return new RuntimeList(runtimeRegistry.getRuntimes(page,
                                                           pageSize,
                                                           sort,
                                                           sortOrder));
    }

    @Override
    public void registerProvider(ProviderConfig conf) throws BusinessException {
        final Optional<Provider> newProvider = providerFactory.newProvider(conf);
        if (newProvider.isPresent()) {
            runtimeRegistry.registerProvider(newProvider.get());
        }
    }

    @Override
    public void unregisterProvider(String name) throws BusinessException {
        runtimeRegistry.unregisterProvider(name);
    }

    @Override
    public String newRuntime(RuntimeConfig conf) throws BusinessException {
        final Optional<Runtime> newRuntime = runtimeFactory.newRuntime(conf);
        if (newRuntime.isPresent()) {
            return newRuntime.get().getId();
        }
        return null;
    }

    @Override
    public void destroyRuntime(String runtimeId) throws BusinessException {
        Runtime runtimeById = runtimeRegistry.getRuntimeById(runtimeId);
        runtimeFactory.destroyRuntime(runtimeById);
    }

    @Override
    public void startRuntime(String runtimeId) throws BusinessException {
        Runtime runtimeById = runtimeRegistry.getRuntimeById(runtimeId);
        runtimeManagerFactory.startRuntime(runtimeById);
    }

    @Override
    public void stopRuntime(String runtimeId) throws BusinessException {
        Runtime runtimeById = runtimeRegistry.getRuntimeById(runtimeId);
        runtimeManagerFactory.stopRuntime(runtimeById);
    }

    @Override
    public void restartRuntime(String runtimeId) throws BusinessException {
        Runtime runtimeById = runtimeRegistry.getRuntimeById(runtimeId);
        runtimeManagerFactory.restartRuntime(runtimeById);
    }

    @Override
    public RuntimeQueryResultItemList executeQuery(RuntimeQuery query) throws BusinessException {
        PortablePreconditions.checkNotNull("query",
                                           query);

        Collection<PipelineExecutorTrace> pipelineTraces = pipelineExecutorRegistry.getExecutorTraces().stream()
                .filter(PipelineExecutorTraceFilter.newInstance()
                                .withPipelineExecutionId(query.getPipelineExecutionId())
                                .withProviderId(query.getProviderId())
                                .withPipelineId(query.getPipelineId()))
                .collect(Collectors.toList());

        if (query.getPipelineId() != null) {
            //only runtimes that were generated by pipelines will be considered.
            return new RuntimeQueryResultItemList(buildResultItemList(pipelineTraces));
        } else {
            List<RuntimeQueryResultItem> items = buildResultItemList(pipelineTraces);

            List<Runtime> runtimes = runtimeRegistry.getRuntimes(0,
                                                                 1000,
                                                                 "id",
                                                                 true);
            runtimes.forEach(runtime -> {
                if (query.getProviderId() == null || query.getProviderId().equals(runtime.getProviderId().getId())) {
                    if (items.stream().filter(item -> runtime.getId().equals(item.getRuntimeId()) &&
                            runtime.getProviderId().getId().equals(item.getProviderId())).count() == 0) {
                        items.add(RuntimeQueryResultItemBuilder.newInstance().with(runtime).build());
                    }
                }
            });

            return new RuntimeQueryResultItemList(items);
        }
    }

    private List<RuntimeQueryResultItem> buildResultItemList(Collection<PipelineExecutorTrace> pipelineExecutorTraces) {
        return pipelineExecutorTraces.stream()
                .map(trace -> RuntimeQueryResultItemBuilder.newInstance()
                        .with(trace)
                        .build()).collect(Collectors.toList());
    }

    static class RuntimeQueryResultItemBuilder {

        private PipelineExecutorTrace pipelineExecutorTrace;

        private Runtime runtime;

        private RuntimeQueryResultItemBuilder() {
        }

        public static RuntimeQueryResultItemBuilder newInstance() {
            return new RuntimeQueryResultItemBuilder();
        }

        public RuntimeQueryResultItemBuilder with(PipelineExecutorTrace pipelineExecutorTrace) {
            this.pipelineExecutorTrace = pipelineExecutorTrace;
            return this;
        }

        public RuntimeQueryResultItemBuilder with(Runtime runtime) {
            this.runtime = runtime;
            return this;
        }

        public RuntimeQueryResultItem build() {
            RuntimeQueryResultItem item = new RuntimeQueryResultItem();

            if (pipelineExecutorTrace != null) {
                if (pipelineExecutorTrace.getTask().getTaskDef().getProviderId() != null) {
                    item.setProviderId(pipelineExecutorTrace.getTask().getTaskDef().getProviderId().getId());
                    item.setProviderTypeName(pipelineExecutorTrace.getTask().getTaskDef().getProviderId().getProviderType().getProviderTypeName());
                    item.setProviderVersion(pipelineExecutorTrace.getTask().getTaskDef().getProviderId().getProviderType().getVersion());
                }

                item.setPipelineId(pipelineExecutorTrace.getPipelineId());
                item.setPipelineExecutionId(pipelineExecutorTrace.getTaskId());
                item.setPipelineStatus(pipelineExecutorTrace.getTask().getPipelineStatus().name());
                if (pipelineExecutorTrace.getTask().getPipelineError() != null) {
                    item.setPipelineError(pipelineExecutorTrace.getTask().getPipelineError().toString());
                }

                List<PipelineStageItem> stageItems = pipelineExecutorTrace.getTask().getTaskDef().getPipeline().getStages().stream()
                        .map(stage -> {
                            String stageError = null;
                            if (pipelineExecutorTrace.getTask().getStageError(stage) != null) {
                                stageError = pipelineExecutorTrace.getTask().getStageError(stage).getMessage();
                            }
                            String stageStatus = null;
                            if (pipelineExecutorTrace.getTask().getStageStatus(stage) != null) {
                                stageStatus = pipelineExecutorTrace.getTask().getStageStatus(stage).name();
                            }
                            return new PipelineStageItem(stage.getName(),
                                                         stageStatus,
                                                         stageError);
                        }).collect(Collectors.toList());
                item.setPipelineStageItems(new PipelineStageItemList(stageItems));

                if (pipelineExecutorTrace.getTask().getOutput() != null &&
                        pipelineExecutorTrace.getTask().getOutput().orElse(null) instanceof Runtime) {
                    Runtime runtime = (Runtime) pipelineExecutorTrace.getTask().getOutput().get();
                    item.setRuntimeId(runtime.getId());
                    item.setRuntimeName(runtime.getName());
                    item.setRuntimeStatus(runtime.getState().getState());
                    item.setRuntimeEndpoint(RuntimeEndpointBuilder.newInstance().with(runtime).build());
                } else {
                    item.setRuntimeName(pipelineExecutorTrace.getTask().getTaskDef().getInput().get(RuntimeConfig.RUNTIME_NAME));
                }
            } else if (runtime != null) {
                item.setProviderId(runtime.getProviderId().getId());
                item.setRuntimeId(runtime.getId());
                item.setRuntimeName(runtime.getName());
                item.setRuntimeStatus(runtime.getState().getState());
                item.setRuntimeEndpoint(RuntimeEndpointBuilder.newInstance().with(runtime).build());
            }
            return item;
        }
    }

    static class RuntimeEndpointBuilder {

        private Runtime runtime;

        private RuntimeEndpointBuilder() {
        }

        public static RuntimeEndpointBuilder newInstance() {
            return new RuntimeEndpointBuilder();
        }

        private RuntimeEndpointBuilder with(Runtime runtime) {
            this.runtime = runtime;
            return this;
        }

        public String build() {
            if (runtime.getEndpoint() != null) {
                StringBuilder endpoint = new StringBuilder();
                endpoint.append("http://")
                        .append(runtime.getEndpoint().getHost())
                        .append(":")
                        .append(runtime.getEndpoint().getPort());
                if (runtime.getEndpoint().getContext() != null) {
                    endpoint.append("/")
                            .append(runtime.getEndpoint().getContext());
                }
                return endpoint.toString();
            } else {
                return null;
            }
        }
    }

    static class PipelineExecutorTraceFilter implements Predicate<PipelineExecutorTrace> {

        private String providerId;

        private String pipelineId;

        private String pipelineExecutionId;

        private PipelineExecutorTraceFilter() {

        }

        public static PipelineExecutorTraceFilter newInstance() {
            return new PipelineExecutorTraceFilter();
        }

        public PipelineExecutorTraceFilter withProviderId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public PipelineExecutorTraceFilter withPipelineId(String pipelineId) {
            this.pipelineId = pipelineId;
            return this;
        }

        public PipelineExecutorTraceFilter withPipelineExecutionId(String pipelineExecutionId) {
            this.pipelineExecutionId = pipelineExecutionId;
            return this;
        }

        @Override
        public boolean test(PipelineExecutorTrace pipelineExecutorTrace) {
            if (pipelineExecutionId != null) {
                return pipelineExecutionId.equals(pipelineExecutorTrace.getTaskId());
            }
            if (providerId != null) {
                if (pipelineExecutorTrace.getTask().getTaskDef().getProviderId() == null) {
                    return false;
                } else if (!providerId.equals(pipelineExecutorTrace.getTask().getTaskDef().getProviderId().getId())) {
                    return false;
                }
            }
            if (pipelineId != null) {
                if (!pipelineId.equals(pipelineExecutorTrace.getPipelineId())) {
                    return false;
                }
            }
            return true;
        }
    }
}
