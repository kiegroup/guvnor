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
package org.drools.guvnor.server.test;

import javax.inject.Inject;

import org.drools.core.util.KeyStoreHelper;
import org.drools.guvnor.server.DroolsServiceImplementation;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryCategoryService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.RulesRepository;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public abstract class GuvnorIntegrationTest {

    public static final String ADMIN_USERNAME = "admin";

    @Inject @Preferred
    protected RulesRepository rulesRepository;

    @Inject
    protected ServiceImplementation serviceImplementation;

    @Inject
    protected RepositoryAssetService repositoryAssetService;

    @Inject
    protected RepositoryModuleService repositoryPackageService;

    @Inject
    protected RepositoryCategoryService repositoryCategoryService;

    @Inject
    protected DroolsServiceImplementation droolsServiceImplementation;

    protected boolean autoLoginAsAdmin = true;

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @BeforeClass
    public static void setUpGuvnorTestBase() {
        System.setProperty( KeyStoreHelper.PROP_SIGN, "false" );
    }

    @Before
    public void autoLoginAsAdmin() {
        // TODO this method seems to be called after the request and the rulesRepository therefore is created...
        if (autoLoginAsAdmin) {
            loginAs(ADMIN_USERNAME);
        }
    }

    @After
    public void autoLogoutAsAdmin() {
        if (autoLoginAsAdmin) {
            logoutAs(ADMIN_USERNAME);
        }
    }

    protected void loginAs(String username) {
        // TODO needs to be done in Uberfire way -Rikkola-
//        credentials.setUsername(username);
//        credentials.setCredential(new PasswordCredential(username));
//        identity.login();
    }

    protected void logoutAs(String username) {
//        identity.logout();
//        credentials.clear();
    }

    // ************************************************************************
    // Helper methods
    // ************************************************************************

}
