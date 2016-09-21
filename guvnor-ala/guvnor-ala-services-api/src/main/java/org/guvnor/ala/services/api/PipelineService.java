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

package org.guvnor.ala.services.api;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.guvnor.ala.services.exceptions.BusinessException;

import static javax.ws.rs.core.MediaType.*;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;

/*
 * Pipeline Service interface. It allows us to create and run new Pipelines
 * URL: {app-context}/pipelines/
 */
@Path( "pipelines" )
public interface PipelineService {

    /*
     * Get all the Pipeline Configurations registered in the service
     * @return PipelineConfigsList with the list of pipeline configurations
     * @see PipelineConfigsList
     */
    @GET
    @Produces( value = APPLICATION_JSON )
    @Consumes( value = APPLICATION_JSON )
    PipelineConfigsList getPipelineConfigs(@QueryParam("page") @DefaultValue("0") Integer page, 
                            @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
                            @QueryParam("sort") String sort, @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder) 
                    throws BusinessException;

    /*
     * Register a new Pipeline with the provided configuration
     * @param PipelineConfig
     * @return String with the pipeline id
     * @see PipelineConfig
     */
    @POST
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    String newPipeline( @NotNull PipelineConfig config ) throws BusinessException;

    /*
     * Run/Execute a registered Pipeline 
     * @param String id of the pipeline to be executed
     * @param Input map to be used for the pipeline execution
     */
    @POST
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    @Path( "{id}" )
    void runPipeline( @PathParam( "id" ) String id, @NotNull Input input ) throws BusinessException;

}
