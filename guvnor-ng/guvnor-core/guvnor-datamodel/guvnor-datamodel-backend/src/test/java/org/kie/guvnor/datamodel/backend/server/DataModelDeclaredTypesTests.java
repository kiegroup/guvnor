package org.kie.guvnor.datamodel.backend.server;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.kie.guvnor.datamodel.oracle.ProjectDataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.kie.guvnor.datamodel.backend.server.DataModelOracleTestUtils.assertContains;

/**
 * Tests for DataModelService
 */
public class DataModelDeclaredTypesTests {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private BeanManager beanManager;
    private Paths paths;

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

    @Test
    public void testPackageDeclaredTypes() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendDeclaredTypesTest1/src/main/java/p1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final PackageDataModelOracle oracle = dataModelService.getDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 3,
                      oracle.getAllFactTypes().length );
        assertContains( "p1.Bean1",
                        oracle.getAllFactTypes() );
        assertContains( "p1.DRLBean",
                        oracle.getAllFactTypes() );
        assertContains( "p2.Bean2",
                        oracle.getAllFactTypes() );

        assertFalse( oracle.isDeclaredType( "Bean1" ) );
        assertTrue( oracle.isDeclaredType( "DRLBean" ) );
        assertFalse( oracle.isDeclaredType( "p2.Bean2" ) );
    }

    @Test
    public void testProjectDeclaredTypes() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendDeclaredTypesTest1/src/main/java/p1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 3,
                      oracle.getFactTypes().length );
        assertContains( "p1.Bean1",
                        oracle.getFactTypes() );
        assertContains( "p1.DRLBean",
                        oracle.getFactTypes() );
        assertContains( "p2.Bean2",
                        oracle.getFactTypes() );

        assertFalse( oracle.isDeclaredType( "p1.Bean1" ) );
        assertTrue( oracle.isDeclaredType( "p1.DRLBean" ) );
        assertFalse( oracle.isDeclaredType( "p2.Bean2" ) );
    }

}
