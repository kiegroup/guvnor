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
package org.drools.guvnor.server;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.KeyStoreHelper;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.guvnor.server.security.RoleBasedPermissionResolver;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

/**
 * @author rikkola
 *
 */
public class GuvnorTestBase {

    protected ServiceImplementation getServiceImplementation() {
        return (ServiceImplementation) Component.getInstance( "org.drools.guvnor.client.rpc.RepositoryService" );
    }

    protected void setUpSeam() {

        RulesRepository repository = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
        ServiceImplementation serviceImplementation = new ServiceImplementation();
        serviceImplementation.repository = repository;

        // setting it to false as most unit tests in this file assume no signing
        System.setProperty( KeyStoreHelper.PROP_SIGN,
                            "false" );
        Map<String, Object> application = new HashMap<String, Object>();
        //        application.put( "repository",
        //                         repository );
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();

        //        Contexts.getEventContext().set( "repository",
        //                                        repository );

        Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                          serviceImplementation );
    }

    protected void setUpMockIdentity() {
        MockIdentity mockIdentity = new MockIdentity();
        mockIdentity.setIsLoggedIn( true );
        mockIdentity.inject();
        mockIdentity.create();
        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( false );
        mockIdentity.addPermissionResolver( new RoleBasedPermissionResolver() );
    }

    protected void tearAllDown() {
        Contexts.removeFromAllContexts( "repository" );
        Contexts.removeFromAllContexts( "org.drools.guvnor.client.rpc.RepositoryService" );

        if ( Contexts.isApplicationContextActive() ) {

            Lifecycle.endApplication();
        }

        MailboxService.getInstance().stop();
        TestEnvironmentSessionHelper.shutdown();
    }
}
