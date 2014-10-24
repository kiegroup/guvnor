package org.guvnor.common.services.project.backend.server;

import java.net.URL;
import java.util.Random;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import org.guvnor.common.services.backend.file.PomFileFilter;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.Repository;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.weld.environment.se.StartMain;
import org.jgroups.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.util.Paths;

import static org.junit.Assert.*;

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
