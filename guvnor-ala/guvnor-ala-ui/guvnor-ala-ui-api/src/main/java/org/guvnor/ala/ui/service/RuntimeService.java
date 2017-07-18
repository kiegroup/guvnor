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

package org.guvnor.ala.ui.service;

import java.util.Collection;

import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.RuntimesInfo;
import org.guvnor.ala.ui.model.Source;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface RuntimeService {

    /**
     * Gets the information about the runtimes associated to a given provider.
     * @param providerKey a provider que for getting the runtimes.
     * @return a list of RuntimeListItem.
     */
    Collection<RuntimeListItem> getRuntimeItems(final ProviderKey providerKey);

    /**
     * Gets the runtime information for a given pipeline execution.
     * @param pipelineExecutionTraceKey the identifier for a pipeline execution.
     * @return the RuntimeListItem associated to the pipeline execution when exists, false in any other case.
     */
    RuntimeListItem getRuntimeItem(final PipelineExecutionTraceKey pipelineExecutionTraceKey);

    /**
     * Gets the information about the runtimes associates to a given provider and the provider itself.
     * @param providerKey
     * @return a RuntimeInfo with the runtimes associated to the providerKey and the Provider information. Null
     * if no provider exists associated to the providerKey.
     */
    RuntimesInfo getRuntimesInfo(final ProviderKey providerKey);

    /**
     * Gests the pipeline names for the pipelines associated to a given provider type.
     * @param providerTypeKey a provider type key.
     * @return a list with the keys of the associated pipelines.
     */
    Collection<PipelineKey> getPipelines(final ProviderTypeKey providerTypeKey);

    /**
     * Creates a runtime by associating it with a given provider. A provider may have multiple runtimes associated.
     * @param providerKey the provider key for creating the runtime.
     * @param runtimeName a name for the runtime to be created.
     * @param source a source configuration from where to get all the information relative to the sources to use.
     * @param pipelineKey the key of a pipeline to use for performing all the required operations for building the
     * runtime.
     * @return returns the pipeline execution id.
     */
    PipelineExecutionTraceKey createRuntime(final ProviderKey providerKey,
                                            final String runtimeName,
                                            final Source source,
                                            final PipelineKey pipelineKey);
}