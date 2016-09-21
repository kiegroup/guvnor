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

import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.guvnor.ala.services.exceptions.BusinessException;
import org.guvnor.ala.services.rest.factories.ProviderFactory;
import org.guvnor.ala.services.rest.factories.RuntimeFactory;
import org.guvnor.ala.services.rest.factories.RuntimeManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RestRuntimeProvisioningServiceImpl implements RuntimeProvisioningService {

    protected static final Logger LOG = LoggerFactory.getLogger( RestRuntimeProvisioningServiceImpl.class );

    @Inject
    private RuntimeRegistry registry;

    @Inject
    private BeanManager beanManager;

    @Inject
    private ProviderFactory providerFactory;

    @Inject
    private RuntimeFactory runtimeFactory;

    @Inject
    private RuntimeManagerFactory runtimeManagerFactory;

    @PostConstruct
    public void cacheBeans() {
        LOG.info( "> Initializing ProviderTypes. " );
        final Set<Bean<?>> beans = beanManager.getBeans( ProviderType.class, new AnnotationLiteral<Any>() {
        } );
        for ( final Bean b : beans ) {
            try {
                // I don't want to register the CDI proxy, I need a fresh instance :(
                ProviderType pt = ( ProviderType ) b.getBeanClass().newInstance();
                LOG.info( "> Registering ProviderType: " + pt.getProviderTypeName() );
                registry.registerProviderType( pt );
            } catch ( InstantiationException | IllegalAccessException ex ) {
                LOG.error( "Something went wrong with registering Provider Types!", ex );
            }
        }
    }

    @Override
    public ProviderTypeList getProviderTypes( Integer page, Integer pageSize, String sort, boolean sortOrder ) throws BusinessException {
        return new ProviderTypeList( registry.getProviderTypes( page, pageSize, sort, sortOrder ) );
    }

    @Override
    public ProviderList getProviders( Integer page, Integer pageSize, String sort, boolean sortOrder ) throws BusinessException {
        return new ProviderList( registry.getProviders( page, pageSize, sort, sortOrder ) );
    }

    @Override
    public RuntimeList getRuntimes( Integer page, Integer pageSize, String sort, boolean sortOrder ) throws BusinessException {
        return new RuntimeList( registry.getRuntimes( page, pageSize, sort, sortOrder ) );
    }

    @Override
    public void registerProvider( ProviderConfig conf ) throws BusinessException {
        final Optional<Provider> newProvider = providerFactory.newProvider( conf );
        if ( newProvider.isPresent() ) {
            registry.registerProvider( newProvider.get() );
        }
    }

    @Override
    public void unregisterProvider( String name ) throws BusinessException {
        registry.unregisterProvider( name );
    }

    @Override
    public String newRuntime( RuntimeConfig conf ) throws BusinessException {
        final Optional<Runtime> newRuntime = runtimeFactory.newRuntime( conf );
        if ( newRuntime.isPresent() ) {
            return newRuntime.get().getId();
        }
        return null;
    }

    @Override
    public void destroyRuntime( String runtimeId ) throws BusinessException {
        Runtime runtimeById = registry.getRuntimeById( runtimeId );
        runtimeFactory.destroyRuntime( runtimeById );
    }

    @Override
    public void startRuntime( String runtimeId ) throws BusinessException {
        Runtime runtimeById = registry.getRuntimeById( runtimeId );
        runtimeManagerFactory.startRuntime( runtimeById );
    }

    @Override
    public void stopRuntime( String runtimeId ) throws BusinessException {
        Runtime runtimeById = registry.getRuntimeById( runtimeId );
        runtimeManagerFactory.stopRuntime( runtimeById );
    }

    @Override
    public void restartRuntime( String runtimeId ) throws BusinessException {
        Runtime runtimeById = registry.getRuntimeById( runtimeId );
        runtimeManagerFactory.restartRuntime( runtimeById );
    }

}
