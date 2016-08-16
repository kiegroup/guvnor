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
import java.util.Optional;

import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;

/*
 * Represents the RuntimeRegistry where all the ProviderTypes, Providers and Runtimes are registered
 */
public interface RuntimeRegistry {

    /*
    * Register a new Provider Type
    * @param ProviderType to be registered
    * @see ProviderType
     */
    void registerProviderType( final ProviderType pt );

    /*
    * Return the list of registered Provider Types
    * @return List<ProviderType> with all the Provider Types registered
     */
    List<ProviderType> getProviderTypes( Integer page, Integer pageSize, String sort, boolean sortOrder );

    /*
    * Return a Provider Type by Name
    * @param a String representing the provider type name
    * @return the selected ProviderType
    * @see ProviderType
     */
    ProviderType getProviderType( final String providerTypeName );

    /*
    * Unregister the provider type
    * @param ProviderType to be unregistered
    * @see ProviderType
     */
    void unregisterProviderType( final ProviderType providerType );

    /*
    * Register a new Provider
    * @param Provider to be registered
    * @see Provider
     */
    void registerProvider( final Provider provider );

    /*
    * Return a Provider Type by Name
    * @param a String representing the provider name
    * @return the selected Provider
    * @see Provider
     */
    Provider getProvider( final String providerName );

    /*
    * Return the list of registered Provider
    * @return List<Provider> with all the Providers registered
    * @see Provider
     */
    List<Provider> getProviders( Integer page, Integer pageSize, String sort, boolean sortOrder );

    /*
    * Return the list of registered Provider filtering by type
    * @param ProviderType to filter by
    * @return List<Provider> with all the Providers matching with the provider type
    * @see Provider
    * @see ProviderType
     */
    List<Provider> getProvidersByType( final ProviderType type );

    /*
    * Unregister the provider
    * @param Provider to be unregistered
    * @see Provider
     */
    void unregisterProvider( final Provider provider );

    /*
    * Unregister the provider by provider name
    * @param String the provider name to be unregistered
    * @see Provider
     */
    void unregisterProvider( final String providerName );

    /*
    * Register a new Runtime
    * @param Runtime to be registered
    * @see Runtime
     */
    void registerRuntime( final Runtime runtime );

    /*
    * Return the list of registered Runtimes
    * @return List<Runtime> with all the Runtimes registered in the system
    * @see Runtime
     */
    List<Runtime> getRuntimes( Integer page, Integer pageSize, String sort, boolean sortOrder );

    /*
    * Return the list of registered Runtimes filtering by provider type
    * @param ProviderType to filter by
    * @return List<Runtime> with all the Runtimes matching with the provider type
    * @see ProviderType
    * @see Runtime
     */
    List<Runtime> getRuntimesByProvider( final ProviderType provider );

    /*
    * Return the Runtime based on the Runtime id
    * @param String to filter by
    * @return Runtime matching the provided id
    * @see Runtime
     */
    Runtime getRuntimeById( final String id );

    /*
    * Unregister the provider
    * @param RuntimeId to be unregistered
    * @see RuntimeId
     */
    void unregisterRuntime( final RuntimeId runtime );

    /*
    * Get provider based on ProviderId and Class type
    * @param ProviderId 
    * @param Class<T> 
    * @return Provider 
    * @see RuntimeId
     */
    <T extends Provider> Optional<T> getProvider( final ProviderId providerId,
            final Class<T> clazz );
}
