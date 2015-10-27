/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.common.services.project.backend.server;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.After;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

public class CDITestBase {

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    protected BeanManager beanManager;
    protected Paths paths;

    protected void setUp() throws Exception {
        //Bootstrap WELD container
        StartMain startMain = new StartMain( new String[0] );
        beanManager = startMain.go().getBeanManager();

        //Instantiate Paths used in tests for Path conversion
        paths = getReference( Paths.class );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

    }

    @After
    public void tearDown() throws Exception {
        TestAppSetup.reset();
    }
    
    protected <T> T getReference( Class<T> clazz ) {
        Bean bean = (Bean) beanManager.getBeans( clazz ).iterator().next();
        CreationalContext cc = beanManager.createCreationalContext( bean );
        return (T) beanManager.getReference( bean,
                                             clazz,
                                             cc );
    }

}
