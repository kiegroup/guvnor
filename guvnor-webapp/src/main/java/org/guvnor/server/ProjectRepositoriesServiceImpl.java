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

package org.guvnor.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.AbstractProjectRepositoriesServiceImpl;
import org.guvnor.common.services.project.backend.server.ProjectRepositoriesContentHandler;
import org.guvnor.common.services.project.backend.server.ResourceResolver;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.io.IOService;

/**
 * CDI implementation for guvnor's Workbench
 */
@Service
@ApplicationScoped
public class ProjectRepositoriesServiceImpl extends AbstractProjectRepositoriesServiceImpl<Project> {

    protected ResourceResolver<Project> resourceResolver;

    public ProjectRepositoriesServiceImpl() {
        //WELD proxy
    }

    @Inject
    public ProjectRepositoriesServiceImpl( final @Named("ioStrategy") IOService ioService,
                                           final ProjectRepositoryResolver repositoryResolver,
                                           final ResourceResolver<Project> resourceResolver,
                                           final ProjectRepositoriesContentHandler contentHandler,
                                           final CommentedOptionFactory commentedOptionFactory ) {
        super( ioService,
               repositoryResolver,
               contentHandler,
               commentedOptionFactory );
        this.resourceResolver = resourceResolver;
    }

    @Override
    protected Project getProject( final org.uberfire.backend.vfs.Path path ) {
        return resourceResolver.resolveProject( path );
    }

}
