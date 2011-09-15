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

import java.io.File;

import javax.inject.Inject;

import org.bouncycastle.crypto.engines.IDEAEngine;
import org.drools.core.util.KeyStoreHelper;
import org.drools.guvnor.server.files.FileManagerService;
import org.drools.guvnor.server.files.WebDAVImpl;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.guvnor.server.security.RoleBasedPermissionResolver;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.picketlink.idm.impl.api.PasswordCredential;

@RunWith(Arquillian.class)
public abstract class GuvnorTestBase {

    @Deployment
    public static WebArchive createDeployment() {
        // TODO FIXME do not hardcode the version number
        WebArchive webArchive = ShrinkWrap.create(ExplodedImporter.class, "guvnor-webapp-5.3.0-SNAPSHOT.war")
                .importDirectory(new File("target/guvnor-webapp-5.3.0-SNAPSHOT/"))
                .as(WebArchive.class)
                .addAsResource(new File("target/test-classes/"), ArchivePaths.create(""))
                .addAsLibraries(
                        DependencyResolvers.use(MavenDependencyResolver.class)
                                .includeDependenciesFromPom("pom.xml")
                                .resolveAsFiles(new ScopeFilter("test")));
        // System.out.println(webArchive.toString(org.jboss.shrinkwrap.api.formatter.Formatters.VERBOSE));
        return webArchive;
    }

    @Inject
    protected RulesRepository rulesRepository;

    @Inject
    protected ServiceImplementation serviceImplementation;

    @Inject
    protected RepositoryAssetService repositoryAssetService;

    @Inject
    protected RepositoryPackageService repositoryPackageService;

    @Inject
    protected RepositoryCategoryService repositoryCategoryService;

    @Inject
    protected Identity identity;

    @Inject
    protected Credentials credentials;

    protected boolean autoLoginAsAdmin = true;

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @BeforeClass
    public static void setUpGuvnorTestBase() {
        System.setProperty( KeyStoreHelper.PROP_SIGN, "false" );
    }

    @Before
    public void loginAsAdmin() {
        if (autoLoginAsAdmin) {
            credentials.setUsername("admin");
            credentials.setCredential(new PasswordCredential("admin"));
            identity.login();
        }
    }

    @After
    public void logoutAsAdmin() {
        if (autoLoginAsAdmin) {
            identity.logout();
            credentials.clear();
        }
    }

//    protected void setUpMockIdentity() {
//        MockIdentity mockIdentity = new MockIdentity();
//        mockIdentity.setIsLoggedIn( true );
//        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
//        resolver.setEnableRoleBasedAuthorization( false );
//        mockIdentity.addPermissionResolver( new RoleBasedPermissionResolver() );
//        setUpMockIdentity( mockIdentity );
//    }
//
//    public void setUpMockIdentity(MockIdentity mockIdentity) {
//        mockIdentity.inject();
//        mockIdentity.create();
//    }

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    // TODO seam3upgrade
    @Deprecated
    public ServiceImplementation getServiceImplementation() {
        throw new UnsupportedOperationException("Use injection instead");
    }

    // TODO seam3upgrade
    @Deprecated
    public RepositoryCategoryService getRepositoryCategoryService() {
        throw new UnsupportedOperationException("Use injection instead");
    }

}
