package org.guvnor.m2repo.backend.server;/*
 * Copyright 2015 JBoss Inc
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

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.apache.maven.execution.MavenSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.guvnor.common.services.project.model.GAV;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.scanner.Aether;
import org.kie.scanner.embedder.MavenProjectLoader;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class GuvnorM2RepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(GuvnorM2RepositoryTest.class);

    private GuvnorM2Repository repo;
    private RepositorySystem repositorySystem = mock(RepositorySystem.class);
    private RepositorySystemSession repositorySystemSession = mock(RepositorySystemSession.class);

    @BeforeClass
    public static void setupClass() {
        System.setProperty("settings.security", "src/test/resources/settings-security.xml");
        System.setProperty("kie.maven.settings.custom", "src/test/resources/settings.xml");

    }

    @Before
    public void setup() throws Exception {
        log.info("Deleting existing Repositories instance..");

        File dir = new File("repositories");

        repo = new GuvnorM2Repository();
        repo.init();

        Aether.getAether().setSystem(repositorySystem);
        Aether.getAether().setSession(repositorySystemSession);
        try {
            when(repositorySystem.install(any(RepositorySystemSession.class), any(InstallRequest.class)))
                    .thenAnswer(new Answer<InstallResult>() {
                        @Override
                        public InstallResult answer(InvocationOnMock invocation) throws Throwable {
                            return new InstallResult((InstallRequest) invocation.getArguments()[1]);
                        }
                    });
        } catch (InstallationException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testDeployArtifactWitdeployArtifacthDistributionManagement() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV("org.kie.guvnor", "guvnor-m2repo-editor-backend", "0.0.1-SNAPSHOT");

        InputStream is = this.getClass()
                .getResourceAsStream("guvnor-m2repo-editor-backend-test-with-distribution-management.jar");
        repo.deployArtifact(is, gav, true);

        verify(repositorySystem, times(1)).deploy(any(RepositorySystemSession.class),
                argThat(new BaseMatcher<DeployRequest>() {

                    @Override
                    public void describeTo(Description description) {
                    }

                    @Override
                    public boolean matches(Object item) {
                        DeployRequest request = (DeployRequest) item;
                        return "guvnor-m2-repo".equals(request.getRepository().getId());
                    }

                    @Override
                    public void describeMismatch(Object item, Description mismatchDescription) {
                    }

                }));
        verify(repositorySystem, times(1)).deploy(any(RepositorySystemSession.class),
                argThat(new BaseMatcher<DeployRequest>() {

                    @Override
                    public void describeTo(Description description) {
                    }

                    @Override
                    public boolean matches(Object item) {
                        DeployRequest request = (DeployRequest) item;
                        String string = "example.project.http";
                        RemoteRepository repo = request.getRepository();
                        boolean equals = string.equals(repo.getId());
                        if (!equals) {
                            return false;
                        }
                        String string2 = "ChainedAuthentication";
                        Authentication auth = repo.getAuthentication();
                        Class<? extends Authentication> class1 = auth.getClass();
                        boolean equals2 = string2.equals(class1.getSimpleName());
                        try {
                            Field declaredField = class1.getDeclaredField("authentications");
                            declaredField.setAccessible(true);
                            Authentication[] object = (Authentication[]) declaredField.get(auth);
                            Authentication authentication = object[1];
                            MavenSession mavenSession = MavenProjectLoader.newMavenEmbedder(false).getMavenSession();
                            Class<? extends Authentication> class2 = authentication.getClass();
                            boolean equals3 = "SecretAuthentication".equals(class2.getSimpleName());
                            if (equals3) {
                                Field valueField = class2.getDeclaredField("value");
                                valueField.setAccessible(true);
                                // length of plaintext password, obviously not
                                // length of encrypted password
                                assertEquals("Plaintext pw (repopw) length expected.", 6, ((char[]) valueField.get(authentication)).length);
                            }
                            return "StringAuthentication".equals(object[0].getClass().getSimpleName()) && equals3;
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void describeMismatch(Object item, Description mismatchDescription) {
                    }

                }));
    }

}
