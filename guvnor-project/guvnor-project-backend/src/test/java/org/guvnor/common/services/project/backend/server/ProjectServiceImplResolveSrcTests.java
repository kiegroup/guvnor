package org.guvnor.common.services.project.backend.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ProjectService;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;

/**
 * Tests for ProjectServiceImpl resolveSrcPackage
 */
public class ProjectServiceImplResolveSrcTests extends ProjectServiceImplBaseTest {

    @Test
    public void testResolveSrcPackageWithNonProjectPath() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL testUrl = this.getClass().getResource( "/" );
        final org.uberfire.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( testNioPath );

        //Test a non-Project Path resolves to null
        final Package result = projectService.resolvePackage( testPath );
        assertNull( result );
    }

    @Test
    public void testResolveSrcPackageWithRootPath() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root resolves to null
        final Package result = projectService.resolvePackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveSrcPackageWithSrcPath() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root/src resolves to null
        final Package result = projectService.resolvePackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveSrcPackageWithMainPath() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root/src/main resolves to null
        final Package result = projectService.resolvePackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveSrcPackageDefaultJava() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/java" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/java" );
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/main/java resolves as the default package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageMainSrcPath().toURI() );
    }

    @Test
    public void testResolveSrcPackageDefaultResources() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/resources" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/resources" );
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/main/resources resolves as the default package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageMainResourcesPath().toURI() );
    }

    @Test
    public void testResolveSrcPackageWithJavaFileInPackage() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/java/org/kie/test/project/backend" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/java/org/kie/test/project/backend/Bean.java" );
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Java file resolves to the containing package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageMainSrcPath().toURI() );
    }

    @Test
    public void testResolveSrcPackageWithResourcesFileInPackage() throws Exception {

        final ProjectService projectService = getService(ProjectService.class);

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/resources/org/kie/test/project/backend" );
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/resources/org/kie/test/project/backend/rule1.drl" );
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Resources file resolves to the containing package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageMainResourcesPath().toURI() );
    }

}
