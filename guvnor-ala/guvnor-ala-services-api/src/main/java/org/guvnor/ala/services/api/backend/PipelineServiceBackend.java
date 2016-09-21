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

package org.guvnor.ala.services.api.backend;

import org.guvnor.ala.pipeline.Input;

import org.jboss.errai.bus.server.annotations.Remote;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;
import org.guvnor.ala.services.exceptions.BusinessException;

/*
 * Pipeline Service Backend interface. It allows us to create and run new Pipelines
 * Backend @Remote implementation to be used in CDI environments with Errai
 */
@Remote
public interface PipelineServiceBackend {

    /*
     * Get all the Pipeline Configurations registered in the service
     * @return PipelineConfigsList with the list of pipeline configurations
     * @see PipelineConfigsList
     */
    PipelineConfigsList getPipelineConfigs( Integer page, Integer pageSize,
            String sort, boolean sortOrder ) throws BusinessException;

    /*
     * Register a new Pipeline with the provided configuration
     * @param PipelineConfig
     * @return String with the pipeline id
     * @see PipelineConfig
     */
    String newPipeline( final PipelineConfig pipelineConfig ) throws BusinessException;

    /*
     * Run/Execute a registered Pipeline 
     * @param String id of the pipeline to be executed
     * @param Input map to be used for the pipeline execution
     */
    void runPipeline( final String id, final Input input ) throws BusinessException;

}
