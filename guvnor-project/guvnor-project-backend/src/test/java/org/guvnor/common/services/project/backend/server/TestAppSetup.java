/*
* Copyright 2012 JBoss Inc
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

package org.guvnor.common.services.project.backend.server;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import org.guvnor.m2repo.service.M2RepoService;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

import static org.mockito.Mockito.*;

@Singleton
@Alternative
public class TestAppSetup {

    private static final IOService DEFAULT_IO_SERVICE = new IOServiceDotFileImpl();

    public static IOService ioService = DEFAULT_IO_SERVICE;

    public static void reset() {
        ioService = DEFAULT_IO_SERVICE;
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Alternative
    public M2RepoService m2RepoService() {
        return mock( M2RepoService.class );
    }

    @Produces
    @Alternative
    public SessionInfo sessionInfo() {
        return mock( SessionInfo.class );
    }

    @Produces
    @Alternative
    @Named("uf")
    public ServletContext servletContext() {
        return mock( ServletContext.class );
    }

    @Produces
    public AuthorizationManager getAuthManager() {
        return new RuntimeAuthorizationManager();
    }

}
