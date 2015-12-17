/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.builder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class IncrementalBuilderExecutorManagerFactoryImpl implements IncrementalBuilderExecutorManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger( IncrementalBuilderExecutorManagerFactoryImpl.class );

    private final boolean USE_EXECUTOR_SAFE_MODE = Boolean.parseBoolean( System.getProperty( "org.uberfire.async.executor.safemode", "false" ) );

    @Inject
    private ProjectService<? extends Project> projectService;

    @Inject
    private BuildService buildService;

    @Inject
    private Event<BuildResults> buildResultsEvent;

    @Inject
    private Event<IncrementalBuildResults> incrementalBuildResultsEvent;

    private IncrementalBuilderExecutorManager executorManager = null;

    @Override
    public synchronized IncrementalBuilderExecutorManager getExecutorManager() {
        if ( executorManager == null ) {
            IncrementalBuilderExecutorManager _executorManager = null;

            //Unless overridden, delegate instantiation of the ExecutorService to the container
            //See https://issues.jboss.org/browse/UF-244 and https://issues.jboss.org/browse/WFLY-4198
            //When running in Hosted Mode (on Wildfly 8.1 at present) the System Property should be set
            //to "true"
            if ( !USE_EXECUTOR_SAFE_MODE ) {
                try {
                    _executorManager = InitialContext.doLookup( "java:module/IncrementalBuilderExecutorManager" );
                } catch ( final Exception e ) {
                    LOG.warn( "Unable to instantiate EJB Asynchronous Bean. Falling back to Executors' CachedThreadPool.",
                              e );
                }
            } else {
                LOG.info( "Use of to Executors' CachedThreadPool has been requested; overriding container provisioning." );
            }

            if ( _executorManager == null ) {
                executorManager = new IncrementalBuilderExecutorManager();
                executorManager.setServices( projectService,
                                             buildService,
                                             buildResultsEvent,
                                             incrementalBuildResultsEvent );
            } else {
                executorManager = _executorManager;
            }
        }

        return executorManager;
    }

}
