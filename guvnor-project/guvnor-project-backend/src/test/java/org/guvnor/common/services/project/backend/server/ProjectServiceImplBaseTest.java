package org.guvnor.common.services.project.backend.server;

import java.util.Random;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

public class ProjectServiceImplBaseTest {

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    protected BeanManager beanManager;
    protected Paths paths;
    
    protected final Random random = new Random();

    @Before
    public void setUp() throws Exception {
        //Bootstrap WELD container
        StartMain startMain = new StartMain( new String[ 0 ] );
        beanManager = startMain.go().getBeanManager();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );
        paths = (Paths) beanManager.getReference( pathsBean,
                                                  Paths.class,
                                                  cc );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }
    
    protected <T> T getService(Class<T> serviceClass) { 
        Bean projectServiceBean = (Bean) beanManager.getBeans( serviceClass ).iterator().next();
        CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        T service = (T) beanManager.getReference( projectServiceBean, serviceClass, cc ); 
        return service;
    }
}
