/*
 * Copyright 2013 JBoss Inc
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

package org.kie.guvnor.builder;

import java.io.InputStream;
import java.net.URL;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
import org.kie.guvnor.commons.service.builder.model.Results;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.m2repo.service.M2RepoService;
import org.kie.guvnor.project.model.GAV;

import org.uberfire.backend.server.util.Paths;


public class BuildServiceImplTest {
    private BeanManager beanManager;

    @Before
    public void setUp() throws Exception {
        StartMain startMain = new StartMain(new String[0]);
        beanManager = startMain.go().getBeanManager();
        
        setUpGuvnorM2Repo();
    }
    
    @Test
    public void testBuilder() throws Exception {
/*        Weld weld = new Weld();
        WeldContainer weldContainer = weld.initialize();

        SourceServices sourceServices = weldContainer.instance().select(SourceServices.class).get();*/             
        
        Bean sourceServicesBean = (Bean)beanManager.getBeans(SourceServices.class).iterator().next();
        CreationalContext cc = beanManager.createCreationalContext(sourceServicesBean);
        SourceServices sourceServices = (SourceServices)beanManager.getReference(sourceServicesBean, SourceServices.class, cc);

        Bean pathsBean = (Bean)beanManager.getBeans(Paths.class).iterator().next();
        cc = beanManager.createCreationalContext(pathsBean);
        Paths paths = (Paths)beanManager.getReference(pathsBean, Paths.class, cc);
   
        
        URL url = this.getClass().getResource("/GuvnorM2RepoDependencyExample2");
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.kie.commons.java.nio.file.Path path = p.getPath(url.toURI());
        
        final Builder builder = new Builder(path,
                "guvnor-m2repo-dependency-example2",
                paths,
                sourceServices );
        
        final Results results = builder.build();
        
        //assertTrue(results.isEmpty());
    }
    
    private void setUpGuvnorM2Repo() {        
        Bean m2RepoServiceBean = (Bean)beanManager.getBeans(M2RepoService.class).iterator().next();
        CreationalContext cc = beanManager.createCreationalContext(m2RepoServiceBean);
        M2RepoService m2RepoService = (M2RepoService)beanManager.getReference(m2RepoServiceBean, M2RepoService.class, cc);
        
        String m2RepoURL = m2RepoService.getRepositoryURL();
        GAV gav = new GAV("org.kie.example", "guvnor-m2repo-dependency-example1", "1.0");
        
        InputStream is = this.getClass().getResourceAsStream("/guvnor-m2repo-dependency-example1-1.0.jar");
        m2RepoService.deployJar(is, gav);
        
    }
}
