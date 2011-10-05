/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.jaxrs;


import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryPackageService;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.files.FileManagerService;
import org.drools.guvnor.server.files.RepositoryServlet;
import org.drools.repository.RulesRepository;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@RequestScoped
public abstract class Resource {
    
    @Context
    protected UriInfo uriInfo;

    @Inject
    protected ServiceImplementation serviceImplementation;
    @Inject
    protected RepositoryPackageService repositoryPackageService;
    @Inject
    protected RepositoryAssetService repositoryAssetService;
    @Inject
    protected RulesRepository rulesRepository;
    @Inject
    protected FileManagerService fileManagerService;


    // TODO HACK: the @Inject stuff doesn't actually work, but is faked in HackInjectCXFNonSpringJaxrsServlet
    protected void inject(ServiceImplementation serviceImplementation,
            RepositoryPackageService repositoryPackageService, RepositoryAssetService repositoryAssetService,
            RulesRepository rulesRepository, FileManagerService fileManagerService) {
        this.serviceImplementation = serviceImplementation;
        this.repositoryPackageService = repositoryPackageService;
        this.repositoryAssetService = repositoryAssetService;
        this.rulesRepository = rulesRepository;
        this.fileManagerService = fileManagerService;
    }

}
