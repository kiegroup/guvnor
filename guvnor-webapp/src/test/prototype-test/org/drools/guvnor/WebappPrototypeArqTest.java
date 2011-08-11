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

package org.drools.guvnor;

import java.io.File;
import javax.inject.Inject;

import org.drools.repository.RulesRepository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class WebappPrototypeArqTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
            .addAsResource(new File("../guvnor-webapp/target/classes"), "WEB-INF/classes")
            .addAsLibraries(
                    DependencyResolvers.use(MavenDependencyResolver.class)
                            .includeDependenciesFromPom("pom.xml")
            // Debug on breakpoint on TomcatContainer.java:321: org.apache.catalina.startup.Embedded.createConnector
            // Then evaluate expression "embedded.getClass().getProtectionDomain().getCodeSource()" and see that it still has gwt-dev.jar
                            .exclusion("com.google.gwt:gwt-dev") // Not applied - bug?
                            .resolveAsFiles()
            );
    }


    @Inject
    private RulesRepository repository;

    @Test
    public void theRepoIsNotNull() {
        assertNotNull(repository);
    }

    @Test
    public void theRepoIsNotNull2() {
        assertNotNull(repository);
    }

}
