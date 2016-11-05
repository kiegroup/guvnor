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

package org.guvnor.ala.registry;


import java.util.List;
import org.guvnor.ala.pipeline.Pipeline;

/*
 * Represents the PipelineRegistry where all the Pipelines are registered
*/
public interface PipelineRegistry {

    /*
     * Register a Pipeline
     * @param Pipeline the pipeline to be registered
    */
    void registerPipeline( Pipeline pipeline );

    /*
     * Get Pipeline by Name
     * @param String pipelineId 
     * @return the selected Pipeline
    */
    Pipeline getPipelineByName( String pipelineId );

     /*
     * Get All the registered Pipelines
     * @param int page number
     * @param int page size
     * @param String sort column
     * @param boolean sortOrder true: ascending, false descending
     * @return List<Pipeline> with all the available pipelines. 
    */
    public List<Pipeline> getPipelines( int page, int pageSize, String sort, boolean sortOrder );

}
