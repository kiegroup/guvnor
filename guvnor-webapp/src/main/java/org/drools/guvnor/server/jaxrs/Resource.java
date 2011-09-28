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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@RequestScoped
public abstract class Resource {
    @Context
    UriInfo uriInfo;

    @Inject
    protected ServiceImplementation service;
    @Inject
    protected RepositoryPackageService packageService;
    @Inject
    protected RepositoryAssetService assetService;
    @Inject
    protected RulesRepository repository;
    @Inject
    protected FileManagerService fileManagerService;

}
