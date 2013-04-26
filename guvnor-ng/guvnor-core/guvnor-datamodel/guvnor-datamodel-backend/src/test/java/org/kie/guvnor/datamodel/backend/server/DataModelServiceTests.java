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

/**
 * Tests for DataModelService
 */
public class DataModelServiceTests {

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
    public void testPackageDataModelOracle() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest1/src/main/java/p1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final PackageDataModelOracle oracle = dataModelService.getDataModel( packagePath );

        assertNotNull( oracle );
        assertEquals( 2,
                      oracle.getAllFactTypes().length );
        assertEquals( "p1.Bean1",
                      oracle.getAllFactTypes()[ 0 ] );
        assertEquals( "p2.Bean2",
                      oracle.getAllFactTypes()[ 1 ] );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "Bean1",
                      oracle.getFactTypes()[ 0 ] );
        assertEquals( 3,
                      oracle.getFieldCompletions( "Bean1" ).length );
        assertEquals( "this",
                      oracle.getFieldCompletions( "Bean1" )[ 0 ] );
        assertEquals( "field1",
                      oracle.getFieldCompletions( "Bean1" )[ 1 ] );
        assertEquals( "field2",
                      oracle.getFieldCompletions( "Bean1" )[ 2 ] );

        assertEquals( 1,
                      oracle.getExternalFactTypes().length );
        assertEquals( "p2.Bean2",
                      oracle.getExternalFactTypes()[ 0 ] );
    }

    @Test
    public void testProjectDataModelOracle() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest1/src/main/java/p1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getFactTypes().length );
        assertEquals( "p1.Bean1",
                      oracle.getFactTypes()[ 0 ] );
        assertEquals( "p2.Bean2",
                      oracle.getFactTypes()[ 1 ] );

        assertEquals( 3,
                      oracle.getFieldCompletions( "p1.Bean1" ).length );
        assertEquals( "this",
                      oracle.getFieldCompletions( "p1.Bean1" )[ 0 ] );
        assertEquals( "field1",
                      oracle.getFieldCompletions( "p1.Bean1" )[ 1 ] );
        assertEquals( "field2",
                      oracle.getFieldCompletions( "p1.Bean1" )[ 2 ] );

        assertEquals( 2,
                      oracle.getFieldCompletions( "p2.Bean2" ).length );
        assertEquals( "this",
                      oracle.getFieldCompletions( "p2.Bean2" )[ 0 ] );
        assertEquals( "field1",
                      oracle.getFieldCompletions( "p2.Bean2" )[ 1 ] );
    }

    @Test
    @Ignore("See https://issues.jboss.org/browse/DROOLS-110")
    public void testProjectDataModelOracleJavaDefaultPackage() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest2/src/main/java" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "Bean1",
                      oracle.getFactTypes()[ 0 ] );

        assertEquals( 3,
                      oracle.getFieldCompletions( "Bean1" ).length );
        assertEquals( "this",
                      oracle.getFieldCompletions( "Bean1" )[ 0 ] );
        assertEquals( "field1",
                      oracle.getFieldCompletions( "Bean1" )[ 1 ] );
        assertEquals( "field2",
                      oracle.getFieldCompletions( "Bean1" )[ 2 ] );
    }

}
