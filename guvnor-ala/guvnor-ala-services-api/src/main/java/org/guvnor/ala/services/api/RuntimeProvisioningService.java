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
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;

import org.guvnor.ala.services.exceptions.BusinessException;

import static javax.ws.rs.core.MediaType.*;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;

/*
 * Runtime Provisioning Service interface. It allows us to register and interact with different Provisioning Providers and
 *  to create new Runtimes on these providers.
 * URL: {app-context}/runtime/
 */
@Path( "runtime" )
public interface RuntimeProvisioningService {

    /*
     * Get all the registered ProviderTypes
     * @return ProviderTypeList containing all the registered provider types
     * @see ProviderTypeList
     * @throw BusinessException in case of an internal exception
     */
    @GET
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    @Path( "providertypes" )
    ProviderTypeList getProviderTypes( @QueryParam( "page" ) @DefaultValue( "0" ) Integer page,
            @QueryParam( "pageSize" ) @DefaultValue( "10" ) Integer pageSize,
            @QueryParam( "sort" ) String sort, @QueryParam( "sortOrder" ) @DefaultValue( "true" ) boolean sortOrder )
            throws BusinessException;

    /*
     * Get all the registered Providers
     * @return ProviderList containing all the registered providers
     * @see ProviderList
     * @throw BusinessException in case of an internal exception
     */
    @GET
    @Produces( value = APPLICATION_JSON )
    @Path( "providers" )
    ProviderList getProviders( @QueryParam( "page" ) @DefaultValue( "0" ) Integer page,
            @QueryParam( "pageSize" ) @DefaultValue( "10" ) Integer pageSize,
            @QueryParam( "sort" ) String sort, @QueryParam( "sortOrder" ) @DefaultValue( "true" ) boolean sortOrder )
            throws BusinessException;

    /*
     * Register a new Provider
     * @param ProviderConfig used to create the new Provider
     * @throw BusinessException in case of an internal exception
     */
    @POST
    @Consumes( value = APPLICATION_JSON )
    @Path( "providers" )
    void registerProvider( @NotNull ProviderConfig conf ) throws BusinessException;

    /*
     * Unregister an existing Provider
     * @param String provider name
     * @throw BusinessException in case of an internal exception
     */
    @DELETE
    @Path( "providers" )
    void unregisterProvider( @FormParam( value = "name" ) String name ) throws BusinessException;

    /*
     * Create a new Runtime
     * @param RuntimeConfig containing the configuration used to create the new Runtime
     * @throw BusinessException in case of an internal exception
     */
    @POST
    @Path( "runtimes/" )
    @Consumes( value = APPLICATION_JSON )
    @Produces( value = APPLICATION_JSON )
    String newRuntime( @NotNull RuntimeConfig conf ) throws BusinessException;

    /*
     * Destroy an existing  Runtime
     * @param String runtimeId of the runtime to destroy
     * @throw BusinessException in case of an internal exception
     */
    @DELETE
    @Consumes( value = APPLICATION_JSON )
    @Path( "runtimes/{id}" )
    void destroyRuntime( @PathParam( value = "id" ) String runtimeId ) throws BusinessException;

    /*
     * Get All Runtimes
     * @return RuntimeList containing all the registered Runtimes 
     * @throw BusinessException in case of an internal exception
     */
    @GET
    @Produces( value = APPLICATION_JSON )
    @Path( "runtimes/" )
    RuntimeList getRuntimes( @QueryParam( "page" ) @DefaultValue( "0" ) Integer page,
            @QueryParam( "pageSize" ) @DefaultValue( "10" ) Integer pageSize,
            @QueryParam( "sort" ) String sort, @QueryParam( "sortOrder" ) @DefaultValue( "true" ) boolean sortOrder )
            throws BusinessException;

    /*
     * Start a given Runtime
     * @param String runtimeId of the runtime to be started
     * @throw BusinessException in case of an internal exception
     */
    @POST
    @Path( "runtimes/{id}/state" )
    void startRuntime( @PathParam( value = "id" ) String runtimeId ) throws BusinessException;

    /*
     * Stop a given Runtime
     * @param String runtimeId of the runtime to be stopped
     * @throw BusinessException in case of an internal exception
     */
    @DELETE
    @Path( "runtimes/{id}/state" )
    void stopRuntime( @PathParam( value = "id" ) String runtimeId ) throws BusinessException;

    /*
     * Restart a given Runtime
     * @param String runtimeId of the runtime to be restarted
     * @throw BusinessException in case of an internal exception
     */
    @PUT
    @Path( "runtimes/{id}/state" )
    void restartRuntime( @PathParam( value = "id" ) String runtimeId ) throws BusinessException;

}
