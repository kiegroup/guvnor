package org.guvnor.common.services.project.backend.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ProjectService;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;

/**
 * Tests for ProjectServiceImpl resolveTestPackage
 */
public class ProjectServiceImplResolveTestTests extends ProjectServiceImplBaseTest {

    @Test
    public void testResolveTestPackageWithNonProjectPath() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL testUrl = this.getClass().getResource( "/" );
        final org.uberfire.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( testNioPath );

        //Test a non-Project Path resolves to null
        final Package result = projectService.resolvePackage( testPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageWithRootPath() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root resolves to null
        final Package result = projectService.resolvePackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageWithSrcPath() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root/src resolves to null
        final Package result = projectService.resolvePackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageWithMainPath() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root/src/test resolves to null
        final Package result = projectService.resolvePackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageDefaultJava() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/java" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/java" );
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/test/java resolves as the default package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageTestSrcPath().toURI() );
    }

    @Test
    public void testResolveTestPackageDefaultResources() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/resources" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/resources" );
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/test/resources resolves as the default package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageTestResourcesPath().toURI() );
    }

    @Test
    public void testResolveTestPackageWithJavaFileInPackage() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/java/org/kie/test/project/backend" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/java/org/kie/test/project/backend/BeanTest.java" );
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Java file resolves to the containing package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageTestSrcPath().toURI() );
    }

    @Test
    public void testResolveTestPackageWithResourcesFileInPackage() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/resources/org/kie/test/project/backend" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/resources/org/kie/test/project/backend/test.scenario" );
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Resources file resolves to the containing package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageTestResourcesPath().toURI() );
    }

}
