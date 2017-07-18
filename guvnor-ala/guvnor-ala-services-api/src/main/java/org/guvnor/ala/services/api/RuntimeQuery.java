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

package org.guvnor.ala.services.api;

/**
 * This class models the query parameters for a query against the runtime system to provide information about the
 * currently provisioned Runtimes, like WildlfyRuntimes, etc.
 */
public class RuntimeQuery {

    /**
     * Filter the results for a particular provider. If null, no provider filtering will be applied.
     */
    private String providerId;

    /**
     * Filter the results for a particular pipeline. If null, no pipeline filtering will be applied.
     */
    private String pipelineId;

    /**
     * Filter the results for a particular pipeline execution. If null, no pipeline execution id filtering will be applied.
     */
    private String pipelineExecutionId;

    public RuntimeQuery(String providerId,
                        String pipelineId,
                        String pipelineExecutionId) {
        this.providerId = providerId;
        this.pipelineId = pipelineId;
        this.pipelineExecutionId = pipelineExecutionId;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public String getPipelineExecutionId() {
        return pipelineExecutionId;
    }
}
