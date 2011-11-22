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
import org.drools.guvnor.server.RepositoryCategoryService;
import org.drools.guvnor.server.RepositoryPackageService;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.repository.RulesRepository;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

public class Resource {
    @Context
    UriInfo uriInfo;

    final ServiceImplementation service;
    final RepositoryPackageService packageService;
    final RepositoryAssetService assetService;
    final RepositoryCategoryService repositoryCategoryService;
    final RulesRepository repository;

    public Resource() {
        service = RepositoryServiceServlet.getService();
        packageService = RepositoryServiceServlet.getPackageService();
        assetService = RepositoryServiceServlet.getAssetService();
        repositoryCategoryService = RepositoryServiceServlet.getCategoryService();
        repository = service.getRulesRepository();
    }
}
