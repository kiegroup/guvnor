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

import org.apache.commons.io.FileUtils;
import org.drools.core.util.KeyStoreHelper;
import org.drools.guvnor.server.repository.TestRepositoryStartupService;
import org.drools.repository.RulesRepository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.picketlink.idm.impl.api.PasswordCredential;

@RunWith(Arquillian.class)
public abstract class GuvnorTestBase {

    public static final String ADMIN_USERNAME = "admin";

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive webArchive = ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml")
                .importBuildOutput().importTestBuildOutput()
                .as(WebArchive.class);
        addTestDependencies(webArchive);

        File explodedWarFile = new File("target/guvnor-webapp-soa-5.4.0-SNAPSHOT");
        if (!explodedWarFile.exists()) {
            throw new IllegalStateException("The exploded war file (" + explodedWarFile
                    + ") should exist, run \"mvn package\" first.");
        }
        removeExcludedFiles(webArchive, explodedWarFile);
        // dumpArchive(webArchive);
        return webArchive;
    }

    private static void dumpArchive(WebArchive webArchive) {
        File shrinkwrapParentDir = new File("target/shrinkwrap");
        FileUtils.deleteQuietly(shrinkwrapParentDir);
        shrinkwrapParentDir.mkdirs();
        webArchive.as(ExplodedExporter.class).exportExploded(shrinkwrapParentDir);
        // System.out.println(webArchive.toString(org.jboss.shrinkwrap.api.formatter.Formatters.VERBOSE));
    }

//    @Deployment
//    public static WebArchive createDeploymentWithoutMavenImporter() {
//        // TODO FIXME do not hardcode the version number
//        File explodedWarFile = new File("target/guvnor-webapp-5.4.0-SNAPSHOT");
//        if (!explodedWarFile.exists()) {
//            throw new IllegalStateException("The exploded war file (" + explodedWarFile
//                    + ") should exist, run \"mvn package\" first.");
//        }
//        File mergedBeansXml = writeMergedBeansXmlFile();
//        WebArchive webArchive = ShrinkWrap.create(ExplodedImporter.class, explodedWarFile.getName() + ".war")
//                .importDirectory(explodedWarFile)
//                .as(WebArchive.class)
//                .addAsResource(new File("target/test-classes/"), ArchivePaths.create(""))
//                // Workaround for https://issues.jboss.org/browse/ARQ-585
//                .addAsWebInfResource(mergedBeansXml, ArchivePaths.create("beans.xml"))
////                .addAsLibraries(
////                        DependencyResolvers.use(MavenDependencyResolver.class)
////                                .includeDependenciesFromPom("pom.xml")
////                                // exclusions don't work after includeDependenciesFromPom
////                                // .exclusions("org.jboss.arquillian:*", "org.jboss.shrinkwrap:*")
////                                .resolveAsFiles(new ScopeFilter("test"))
//                ;
//        removeExcludedFiles(webArchive, explodedWarFile);
//        System.out.println(webArchive.toString(org.jboss.shrinkwrap.api.formatter.Formatters.VERBOSE));
//        return webArchive;
//    }

    private static void addTestDependencies(WebArchive webArchive) {
        // Adding all test scope dependencies is bad because it includes arquillian and shrinkwrap
        // For now, we just add what we need... this is not scalable
        webArchive.addClasses(
                // Replace the production one
                TestRepositoryStartupService.class,
                // Stuff we need
                org.apache.commons.httpclient.Credentials.class,
                org.apache.commons.httpclient.UsernamePasswordCredentials.class);
    }

    private static void removeExcludedFiles(WebArchive webArchive, File explodedWarFile) {
        // Workaround because guvnor-webapp and guvnor-gwt-client modules aren't split
        webArchive.delete(ArchivePaths.create("WEB-INF/classes/org/drools/guvnor/gwtutil"));
        // Workaround for JBoss 7 https://issues.jboss.org/browse/WELD-983
        File libDir = new File(explodedWarFile, "WEB-INF/lib");
        for (File file : libDir.listFiles()) {
            String fileName = file.getName();
            if (fileName.startsWith("weld-") && fileName.endsWith(".jar")) {
                webArchive.delete(ArchivePaths.create("WEB-INF/lib/" + fileName));
            }
        }
    }

//    @Deployment
//    public static WebArchive createDeployment() {
//        WebArchive webArchive = ShrinkWrap.create(WebArchive.class)
//                .addAsResource(new File("target/classes/"))
//                .addAsWebInfResource(new File("target/guvnor-webapp-5.3.0-SNAPSHOT/WEB-INF/web.xml"), "web.xml")
//                .addAsWebInfResource(new File("target/guvnor-webapp-5.3.0-SNAPSHOT/WEB-INF/beans.xml"), "beans.xml")
//                .addAsLibraries(
//                        DependencyResolvers.use(MavenDependencyResolver.class)
//                                .includeDependenciesFromPom("pom.xml")
//                                .resolveAsFiles(new ScopeFilter("", "compile", "runtime", "test")));
//
//        return webArchive;
//        // TODO use loadMetadataFromPom instead
//    }

    @Inject
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
        credentials.setUsername(username);
        credentials.setCredential(new PasswordCredential(username));
        identity.login();
    }

    protected void logoutAs(String username) {
        identity.logout();
        credentials.clear();
    }

    // ************************************************************************
    // Helper methods
    // ************************************************************************

}
