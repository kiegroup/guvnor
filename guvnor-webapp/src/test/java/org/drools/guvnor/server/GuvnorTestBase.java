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
import org.drools.guvnor.server.files.FileManagerUtils;
import org.drools.guvnor.server.files.WebDAVImpl;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.guvnor.server.security.RoleBasedPermissionResolver;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.junit.After;
import org.junit.Before;

public abstract class GuvnorTestBase {

    private static RulesRepository repository;

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Before
    public void setUpGuvnorTestBase() {
        setUpSeam();
        setUpRepository();
        setUpMockIdentity();
    }

    protected void setUpSeam() {
        System.setProperty( KeyStoreHelper.PROP_SIGN,
                            "false" );
        Map<String, Object> application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
    }

    protected void setUpRepository() {
        ServiceImplementation serviceImplementation = new ServiceImplementation();
        serviceImplementation.setRulesRepository( getRulesRepository() );

        RepositoryAssetService repositoryAssetService = new RepositoryAssetService();
        repositoryAssetService.setRulesRepository( getRulesRepository() );

        RepositoryPackageService repositoryPackageService = new RepositoryPackageService();
        repositoryPackageService.setRulesRepository( getRulesRepository() );

        RepositoryCategoryService repositoryCategoryService = new RepositoryCategoryService();
        repositoryCategoryService.setRulesRepository( getRulesRepository() );

        Contexts.getSessionContext().set( "repository",
                                          repository );
        Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                          serviceImplementation );
        Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.AssetService",
                                          repositoryAssetService );
        Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.PackageService",
                                          repositoryPackageService );
        Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.CategoryService",
                                          repositoryCategoryService );
    }

    protected RulesRepository getRulesRepository() {
        if ( repository == null ) {
            repository = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
        }
        return repository;
    }

    protected void setUpMockIdentity() {
        MockIdentity mockIdentity = new MockIdentity();
        mockIdentity.setIsLoggedIn( true );
        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( false );
        mockIdentity.addPermissionResolver( new RoleBasedPermissionResolver() );
        setUpMockIdentity( mockIdentity );
    }

    public void setUpMockIdentity(MockIdentity mockIdentity) {
        mockIdentity.inject();
        mockIdentity.create();
    }

    @After
    public void tearDownGuvnorTestBase() {
        repository = null;
        Contexts.removeFromAllContexts( "repository" );
        Contexts.removeFromAllContexts( "org.drools.guvnor.client.rpc.RepositoryService" );
        Contexts.removeFromAllContexts( "org.drools.guvnor.client.rpc.AssetService" );
        Contexts.removeFromAllContexts( "org.drools.guvnor.client.rpc.PackageService" );
        Contexts.removeFromAllContexts( "org.drools.guvnor.client.rpc.CategoryService" );
        Contexts.removeFromAllContexts( "fileManager" );
        if ( Contexts.getApplicationContext() != null ) Contexts.getApplicationContext().flush();
        if ( Contexts.getEventContext() != null ) Contexts.getEventContext().flush();
        if ( Contexts.getSessionContext() != null ) Contexts.getSessionContext().flush();
        if ( Contexts.isApplicationContextActive() && Contexts.getBusinessProcessContext() != null ) Contexts.getBusinessProcessContext().flush();
        if ( Contexts.getConversationContext() != null ) Contexts.getConversationContext().flush();

        if ( Contexts.isApplicationContextActive() ) {
            Lifecycle.endApplication();
        }

        MailboxService.getInstance().stop();
        TestEnvironmentSessionHelper.shutdown();
    }

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    public ServiceImplementation getServiceImplementation() {
        return (ServiceImplementation) Component.getInstance( "org.drools.guvnor.client.rpc.RepositoryService" );
    }

    protected RepositoryAssetService getRepositoryAssetService() {
        return (RepositoryAssetService) Component.getInstance( "org.drools.guvnor.client.rpc.AssetService" );
    }

    protected RepositoryPackageService getRepositoryPackageService() {
        return (RepositoryPackageService) Component.getInstance( "org.drools.guvnor.client.rpc.PackageService" );
    }

    protected RepositoryCategoryService getRepositoryCategoryService() {
        return (RepositoryCategoryService) Component.getInstance( "org.drools.guvnor.client.rpc.CategoryService" );
    }

    protected void setUpFileManagerUtils() {
        Contexts.getSessionContext().set( "fileManager",
                                          getFileManagerUtils() );
    }

    protected FileManagerUtils getFileManagerUtils() {
        FileManagerUtils fileManager = new FileManagerUtils();
        fileManager.setRepository( getRulesRepository() );
        return fileManager;
    }

    public WebDAVImpl getWebDAVImpl() throws Exception {
        return new WebDAVImpl( getRulesRepository() );
    }

}
