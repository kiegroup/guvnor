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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.events.PipelineEventListener;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.services.api.PipelineService;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;
import org.guvnor.ala.services.exceptions.BusinessException;

@ApplicationScoped
public class RestPipelineServiceImpl implements PipelineService {

    @Inject
    private PipelineRegistry pipelineRegistry;

    @Inject
    private SourceRegistry sourceRegistry;

    @Inject
    private BuildRegistry buildRegistry;

    @Inject
    @Any
    private Instance<ConfigExecutor> configExecutors;

    @Inject
    private PipelineExecutor executor;

    @Inject
    @Any
    private Instance<PipelineEventListener> _eventListeners;

    private PipelineEventListener[] eventListeners;

    @PostConstruct
    public void init() {
        Iterator<ConfigExecutor> iterator = configExecutors.iterator();
        Collection<ConfigExecutor> configs = new ArrayList<>();
        while ( iterator.hasNext() ) {
            ConfigExecutor configExecutor = iterator.next();
            configs.add( configExecutor );
        }
        executor.init( configs );
        final Collection<PipelineEventListener> eventListeners = new ArrayList<>();
        for ( PipelineEventListener eventListener : _eventListeners ) {
            eventListeners.add( eventListener );
        }
        this.eventListeners = eventListeners.toArray( new PipelineEventListener[]{} );
    }

    @Override
    public PipelineConfigsList getPipelineConfigs( Integer page, Integer pageSize, String sort, boolean sortOrder ) throws BusinessException {
        final List<PipelineConfig> configs = new ArrayList<>();
        pipelineRegistry.getPipelines(page, pageSize, sort, sortOrder).stream().forEach( ( p ) -> {
            configs.add( p.getConfig() );
        } );
        return new PipelineConfigsList( configs );
    }
   

    @Override
    public String newPipeline( PipelineConfig config ) throws BusinessException {
        final Pipeline pipeline = PipelineFactory.startFrom( null ).build( config );
        pipelineRegistry.registerPipeline( pipeline );
        return config.getName();
    }

    @Override
    public void runPipeline( final String name,
                             final Input input ) throws BusinessException {
        final Pipeline pipe = pipelineRegistry.getPipelineByName( name );
        executor.execute( input,
                          pipe,
                          (Consumer) System.out::println,
                          eventListeners );

    }

}