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

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryCategoryService;
import org.drools.guvnor.server.RepositoryPackageService;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.files.FileManagerService;
import org.drools.repository.RulesRepository;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

public class HackInjectCXFNonSpringJaxrsServlet extends CXFNonSpringJaxrsServlet {

    @Inject
    protected ServiceImplementation serviceImplementation;
    @Inject
    protected RepositoryPackageService repositoryPackageService;
    @Inject
    protected RepositoryAssetService repositoryAssetService;
    @Inject
    protected RepositoryCategoryService repositoryCategoryService;
    @Inject
    protected RulesRepository rulesRepository;
    @Inject
    protected FileManagerService fileManagerService;

    @Inject
    private Identity identity;

    @Inject
    private Credentials credentials;

    @Override
    protected List<?> getProviders(ServletConfig servletConfig) throws ServletException {
        List<?> providers = super.getProviders(servletConfig);
        for (Object provider : providers) {
            if (provider instanceof CXFAuthenticationHandler) {
                CXFAuthenticationHandler handler = (CXFAuthenticationHandler) provider;
                handler.inject(identity, credentials);
            }
        }
        return providers;
    }

    @Override
    protected Object createSingletonInstance(Class<?> cls, Map<String, String> props, ServletConfig sc) throws ServletException {
        Object singletonInstance = super.createSingletonInstance(cls, props, sc);
        if (singletonInstance instanceof Resource) {
            Resource resource = (Resource) singletonInstance;
            resource.inject(serviceImplementation, repositoryPackageService, repositoryAssetService,
                    repositoryCategoryService, rulesRepository,
                    fileManagerService);
        }
        return singletonInstance;
    }

}
