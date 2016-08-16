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

import java.util.List;
import org.guvnor.ala.config.ProviderConfig;

import org.jboss.errai.bus.server.annotations.Remote;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.exceptions.BusinessException;

/*
 * Runtime Provisioning Service Backend interface. It allows us to register and interact with different Provisioning Providers and
 *  to create new Runtimes on these providers.
 * Backend @Remote implementation to be used in CDI environments with Errai
 */
@Remote
public interface RuntimeProvisioningServiceBackend {

    /*
     * Get all the registered ProviderTypes
     * @return ProviderTypeList containing all the registered provider types
     * @see ProviderTypeList
     * @throw BusinessException in case of an internal exception
     */
    List<ProviderType> getProviderTypes( Integer page, Integer pageSize,
            String sort, boolean sortOrder ) throws BusinessException;

    /*
     * Get all the registered Providers
     * @return ProviderList containing all the registered providers
     * @see ProviderList
     * @throw BusinessException in case of an internal exception
     */
    List<Provider> getProviders( Integer page, Integer pageSize,
            String sort, boolean sortOrder ) throws BusinessException;

    /*
     * Register a new Provider
     * @param ProviderConfig used to create the new Provider
     * @throw BusinessException in case of an internal exception
     */
    void registerProvider( final ProviderConfig conf ) throws BusinessException;

    /*
     * Unregister an existing Provider
     * @param String provider name
     * @throw BusinessException in case of an internal exception
     */
    void unregisterProvider( final String name ) throws BusinessException;

    /*
     * Create a new Runtime
     * @param RuntimeConfig containing the configuration used to create the new Runtime
     * @throw BusinessException in case of an internal exception
     */
    String newRuntime( final RuntimeConfig conf ) throws BusinessException;

    /*
     * Destroy an existing  Runtime
     * @param String runtimeId of the runtime to destroy
     * @throw BusinessException in case of an internal exception
     */
    void destroyRuntime( final String runtimeId ) throws BusinessException;

    /*
     * Get All Runtimes
     * @return RuntimeList containing all the registered Runtimes 
     * @throw BusinessException in case of an internal exception
     */
    List<Runtime> getRuntimes( Integer page, Integer pageSize,
            String sort, boolean sortOrder ) throws BusinessException;

    /*
     * Start a given Runtime
     * @param String runtimeId of the runtime to be started
     * @throw BusinessException in case of an internal exception
     */
    void startRuntime( final String runtimeId ) throws BusinessException;

    /*
     * Stop a given Runtime
     * @param String runtimeId of the runtime to be stopped
     * @throw BusinessException in case of an internal exception
     */
    void stopRuntime( final String runtimeId ) throws BusinessException;

    /*
     * Restart a given Runtime
     * @param String runtimeId of the runtime to be restarted
     * @throw BusinessException in case of an internal exception
     */
    void restartRuntime( final String runtimeId ) throws BusinessException;

}
