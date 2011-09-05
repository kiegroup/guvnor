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
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
//import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
//import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public abstract class AbstractWebappPrototypeArqTest {

    @Deployment
    public static WebArchive createDeployment() {
        // TODO FIXME do not hardcode the version number
        return ShrinkWrap.create(ExplodedImporter.class, "guvnor-webapp-5.3.0-SNAPSHOT.war")
                .importDirectory(new File("target/guvnor-webapp-5.3.0-SNAPSHOT/"))
                .as(WebArchive.class);
    }

//    @Deployment
//    public static WebArchive createDeployment() {
//
//        // TODO gwt dev is in the classpath so it shades tomcat and the arq container can't boot (nevermind the classpath of shrinkwrap!)
//        WebArchive webArchive = ShrinkWrap.create(WebArchive.class)
//                .addAsResource(new File("target/classes/"))
//                .addAsWebInfResource(new File("target/guvnor-webapp-5.3.0-SNAPSHOT/WEB-INF/web.xml"), "web.xml")
//                .addAsWebInfResource(new File("target/guvnor-webapp-5.3.0-SNAPSHOT/WEB-INF/beans.xml"), "beans.xml")
//                .addAsLibraries(
//                        DependencyResolvers.use(MavenDependencyResolver.class)
//                                .includeDependenciesFromPom("pom.xml")
//                                .resolveAsFiles(new ScopeFilter("", "compile", "runtime")));
//
//        return webArchive;
//        // TODO use loadMetadataFromPom instead
//    }

}
