/*
 * Copyright 2012 JBoss Inc
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

package org.guvnor.m2repo.backend.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.helpers.FormData;
import org.guvnor.m2repo.backend.server.helpers.HttpPostHelper;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.guvnor.m2repo.model.HTMLFileManagerFields.*;
import static org.junit.Assert.*;

public class M2RepositoryTest {

    private static final Logger log = LoggerFactory.getLogger( M2RepositoryTest.class );

    @Before
    public void setup() throws Exception {
        log.info( "Deleting existing Repositories instance.." );

        File dir = new File( "repositories" );
        log.info( "DELETING test repo: " + dir.getAbsolutePath() );
        deleteDir( dir );
        log.info( "TEST repo was deleted." );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        log.info( "Deleting all Repository instances.." );

        File dir = new File( "repositories" );
        log.info( "DELETING test repo: " + dir.getAbsolutePath() );
        deleteDir( dir );
        log.info( "TEST repo was deleted." );
    }

    public static boolean deleteDir( File dir ) {
        if ( dir.isDirectory() ) {
            String[] children = dir.list();
            for ( int i = 0; i < children.length; i++ ) {
                if ( !deleteDir( new File( dir,
                                           children[ i ] ) ) ) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    @Test
    public void testDeployArtifact() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend",
                           "0.0.1-SNAPSHOT" );

        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        Collection<File> files = repo.listFiles();

        boolean found = false;
        for ( File file : files ) {
            String fileName = file.getName();
            if ( fileName.startsWith( "guvnor-m2repo-editor-backend-0.0.1" ) && fileName.endsWith( ".jar" ) ) {
                found = true;
                String path = file.getPath();
                String jarPath = path.substring( GuvnorM2Repository.M2_REPO_DIR.length() + 1 );
                String pom = GuvnorM2Repository.getPomText( jarPath );
                assertNotNull( pom );
                break;
            }
        }

        assertTrue( "Did not find expected file after calling M2Repository.addFile()",
                    found );

        // Test get artifact file
        File file = repo.getArtifactFileFromRepository( gav );
        assertNotNull( "Empty file for artifact", file );
        JarFile jarFile = new JarFile( file );
        int count = 0;

        String lastEntryName = null;
        for ( Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
            ++count;
            JarEntry entry = entries.nextElement();
            assertNotEquals( "Endless loop.", lastEntryName, entry.getName() );
        }
        assertTrue( "Empty jar file!", count > 0 );
    }

    @Test
    public void testDeployPom() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend",
                           "0.0.1-SNAPSHOT" );

        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-pom.xml" );
        repo.deployPom( is,
                        gav );

        Collection<File> files = repo.listFiles();

        boolean found = false;
        for ( File file : files ) {
            String fileName = file.getName();
            if ( fileName.startsWith( "guvnor-m2repo-editor-backend-0.0.1" ) && fileName.endsWith( ".pom" ) ) {
                found = true;
                String path = file.getPath();
                String jarPath = path.substring( GuvnorM2Repository.M2_REPO_DIR.length() + 1 );
                String pom = GuvnorM2Repository.getPomText( jarPath );
                assertNotNull( pom );
                break;
            }
        }

        assertTrue( "Did not find expected file after calling M2Repository.addFile()",
                    found );
    }

    @Test
    public void testListFiles() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend",
                           "0.0.1-SNAPSHOT" );

        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        gav = new GAV( "org.jboss.arquillian.core",
                       "arquillian-core-api",
                       "1.0.2.Final" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        Collection<File> files = repo.listFiles();

        boolean found1 = false;
        boolean found2 = false;
        for ( File file : files ) {
            String fileName = file.getName();
            if ( fileName.startsWith( "guvnor-m2repo-editor-backend-0.0.1" ) && fileName.endsWith( ".jar" ) ) {
                found1 = true;
            }
            if ( fileName.startsWith( "arquillian-core-api-1.0.2.Final" ) && fileName.endsWith( ".jar" ) ) {
                found2 = true;
            }
        }

        assertTrue( "Did not find expected file after calling M2Repository.addFile()",
                    found1 );
        assertTrue( "Did not find expected file after calling M2Repository.addFile()",
                    found2 );
    }

    @Test
    public void testListFilesWithFilter() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        gav = new GAV( "org.jboss.arquillian.core",
                       "arquillian-core-api",
                       "1.0.2.Final" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //filter with version number
        boolean found1 = false;
        Collection<File> files = repo.listFiles( "1.0.2" );
        final String VERSION_NUMBER_SEARCH_FILTER = "arquillian-core-api-1.0.2";
        for ( File file : files ) {
            String fileName = file.getName();
            if ( fileName.startsWith( VERSION_NUMBER_SEARCH_FILTER ) && fileName.endsWith( ".jar" ) ) {
                found1 = true;
            }
        }
        assertTrue( "Did not find expected file after calling M2Repository.addFile()",
                    found1 );

        for ( File file : files ) {
            String fileName = file.getName();
            if ( !fileName.contains( VERSION_NUMBER_SEARCH_FILTER ) ) {
                Assert.fail( fileName + " doesn't match the filter " + VERSION_NUMBER_SEARCH_FILTER );
            }
        }

        //filter with artifact id
        found1 = false;
        files = repo.listFiles( "arquillian-core-api" );
        final String ARTIFACT_SEARCH_FILTER = "arquillian-core-api";
        for ( File file : files ) {
            String fileName = file.getName();
            if ( fileName.startsWith( ARTIFACT_SEARCH_FILTER ) && fileName.endsWith( ".jar" ) ) {
                found1 = true;
            }
        }
        assertTrue( "Did not find expected file after calling M2Repository.addFile()",
                    found1 );

        for ( File file : files ) {
            String fileName = file.getName();
            if ( !fileName.contains( ARTIFACT_SEARCH_FILTER ) ) {
                Assert.fail( fileName + " doesn't match the filter " + ARTIFACT_SEARCH_FILTER );
            }
        }
    }

    @Test
    public void testUploadJARWithPOM() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //Create a shell M2RepoService and set the M2Repository
        M2RepoServiceImpl service = new M2RepoServiceImpl();
        java.lang.reflect.Field repositoryField = M2RepoServiceImpl.class.getDeclaredField( "repository" );
        repositoryField.setAccessible( true );
        repositoryField.set( service,
                             repo );

        //Make private method accessible for testing
        HttpPostHelper helper = new HttpPostHelper();
        java.lang.reflect.Method helperMethod = HttpPostHelper.class.getDeclaredMethod( "upload",
                                                                                        FormData.class );
        helperMethod.setAccessible( true );

        //Set the repository service created above in the HttpPostHelper
        java.lang.reflect.Field m2RepoServiceField = HttpPostHelper.class.getDeclaredField( "m2RepoService" );
        m2RepoServiceField.setAccessible( true );
        m2RepoServiceField.set( helper,
                                service );

        FormData uploadItem = new FormData();
        FileItem file = new MockFileItem( "guvnor-m2repo-editor-backend-test-with-pom.jar",
                                          this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-with-pom.jar" ) );
        uploadItem.setFile( file );

        assert ( helperMethod.invoke( helper,
                                      uploadItem ).equals( UPLOAD_OK ) );
    }

    @Test
    public void testUploadKJARWithPOM() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //Create a shell M2RepoService and set the M2Repository
        M2RepoServiceImpl service = new M2RepoServiceImpl();
        java.lang.reflect.Field repositoryField = M2RepoServiceImpl.class.getDeclaredField( "repository" );
        repositoryField.setAccessible( true );
        repositoryField.set( service,
                             repo );

        //Make private method accessible for testing
        HttpPostHelper helper = new HttpPostHelper();
        java.lang.reflect.Method helperMethod = HttpPostHelper.class.getDeclaredMethod( "upload",
                                                                                        FormData.class );
        helperMethod.setAccessible( true );

        //Set the repository service created above in the HttpPostHelper
        java.lang.reflect.Field m2RepoServiceField = HttpPostHelper.class.getDeclaredField( "m2RepoService" );
        m2RepoServiceField.setAccessible( true );
        m2RepoServiceField.set( helper,
                                service );

        FormData uploadItem = new FormData();
        FileItem file = new MockFileItem( "guvnor-m2repo-editor-backend-test-with-pom.kjar",
                                          this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-with-pom.jar" ) );
        uploadItem.setFile( file );

        assert ( helperMethod.invoke( helper,
                                      uploadItem ).equals( UPLOAD_OK ) );
    }

    @Test
    public void testUploadJARWithManualGAV() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //Create a shell M2RepoService and set the M2Repository
        M2RepoServiceImpl service = new M2RepoServiceImpl();
        java.lang.reflect.Field repositoryField = M2RepoServiceImpl.class.getDeclaredField( "repository" );
        repositoryField.setAccessible( true );
        repositoryField.set( service,
                             repo );

        //Make private method accessible for testing
        HttpPostHelper helper = new HttpPostHelper();
        java.lang.reflect.Method helperMethod = HttpPostHelper.class.getDeclaredMethod( "upload",
                                                                                        FormData.class );
        helperMethod.setAccessible( true );

        //Set the repository service created above in the HttpPostHelper
        java.lang.reflect.Field m2RepoServiceField = HttpPostHelper.class.getDeclaredField( "m2RepoService" );
        m2RepoServiceField.setAccessible( true );
        m2RepoServiceField.set( helper,
                                service );

        FormData uploadItem = new FormData();
        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend",
                           "0.0.1-SNAPSHOT" );
        uploadItem.setGav( gav );
        FileItem file = new MockFileItem( "guvnor-m2repo-editor-backend-test-without-pom.jar",
                                          this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" ) );
        uploadItem.setFile( file );

        assert ( helperMethod.invoke( helper,
                                      uploadItem ).equals( UPLOAD_OK ) );
    }

    @Test
    public void testUploadKJARWithManualGAV() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //Create a shell M2RepoService and set the M2Repository
        M2RepoServiceImpl service = new M2RepoServiceImpl();
        java.lang.reflect.Field repositoryField = M2RepoServiceImpl.class.getDeclaredField( "repository" );
        repositoryField.setAccessible( true );
        repositoryField.set( service,
                             repo );

        //Make private method accessible for testing
        HttpPostHelper helper = new HttpPostHelper();
        java.lang.reflect.Method helperMethod = HttpPostHelper.class.getDeclaredMethod( "upload",
                                                                                        FormData.class );
        helperMethod.setAccessible( true );

        //Set the repository service created above in the HttpPostHelper
        java.lang.reflect.Field m2RepoServiceField = HttpPostHelper.class.getDeclaredField( "m2RepoService" );
        m2RepoServiceField.setAccessible( true );
        m2RepoServiceField.set( helper,
                                service );

        FormData uploadItem = new FormData();
        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend",
                           "0.0.1-SNAPSHOT" );
        uploadItem.setGav( gav );
        FileItem file = new MockFileItem( "guvnor-m2repo-editor-backend-test.kjar",
                                          this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" ) );
        uploadItem.setFile( file );

        assert ( helperMethod.invoke( helper,
                                      uploadItem ).equals( UPLOAD_OK ) );
    }

    @Test
    public void testUploadPOM() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //Create a shell M2RepoService and set the M2Repository
        M2RepoServiceImpl service = new M2RepoServiceImpl();
        java.lang.reflect.Field repositoryField = M2RepoServiceImpl.class.getDeclaredField( "repository" );
        repositoryField.setAccessible( true );
        repositoryField.set( service,
                             repo );

        //Make private method accessible for testing
        HttpPostHelper helper = new HttpPostHelper();
        java.lang.reflect.Method helperMethod = HttpPostHelper.class.getDeclaredMethod( "upload",
                                                                                        FormData.class );
        helperMethod.setAccessible( true );

        //Set the repository service created above in the HttpPostHelper
        java.lang.reflect.Field m2RepoServiceField = HttpPostHelper.class.getDeclaredField( "m2RepoService" );
        m2RepoServiceField.setAccessible( true );
        m2RepoServiceField.set( helper,
                                service );

        FormData uploadItem = new FormData();
        FileItem file = new MockFileItem( "pom.xml",
                                          this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-pom.xml" ) );
        uploadItem.setFile( file );

        assert ( helperMethod.invoke( helper,
                                      uploadItem ).equals( UPLOAD_OK ) );
    }

    @Test
    public void testListFilesWithSortOnNameAscending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //This installs a JAR and a POM
        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //This installs a JAR and a POM
        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Name ascending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_NAME,
                                           true );
        assertEquals( 4,
                      files.size() );
        final String fileName0 = files.get( 0 ).getName();
        final String fileName2 = files.get( 2 ).getName();
        assertTrue( fileName0.startsWith( "guvnor-m2repo-editor-backend1" ) );
        assertTrue( fileName2.startsWith( "guvnor-m2repo-editor-backend2" ) );
    }

    @Test
    public void testListFilesWithSortOnNameDescending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //This installs a JAR and a POM
        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //This installs a JAR and a POM
        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Name descending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_NAME,
                                           false );
        assertEquals( 4,
                      files.size() );
        final String fileName0 = files.get( 0 ).getName();
        final String fileName2 = files.get( 2 ).getName();
        assertTrue( fileName0.startsWith( "guvnor-m2repo-editor-backend2" ) );
        assertTrue( fileName2.startsWith( "guvnor-m2repo-editor-backend1" ) );
    }

    @Test
    public void testListFilesWithSortOnPathAscending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //This installs a JAR and a POM
        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //This installs a JAR and a POM
        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Path ascending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_PATH,
                                           true );
        assertEquals( 4,
                      files.size() );
        final String filePath0 = files.get( 0 ).getPath();
        final String filePath2 = files.get( 2 ).getPath();
        assertTrue( filePath0.contains( "guvnor-m2repo-editor-backend1" ) );
        assertTrue( filePath2.contains( "guvnor-m2repo-editor-backend2" ) );
    }

    @Test
    public void testListFilesWithSortOnPathDescending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //This installs a JAR and a POM
        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //This installs a JAR and a POM
        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Name descending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_PATH,
                                           false );
        assertEquals( 4,
                      files.size() );
        final String filePath0 = files.get( 0 ).getPath();
        final String filePath2 = files.get( 2 ).getPath();
        assertTrue( filePath0.contains( "guvnor-m2repo-editor-backend2" ) );
        assertTrue( filePath2.contains( "guvnor-m2repo-editor-backend1" ) );
    }

    @Test
    public void testListFilesWithSortOnLastModifiedAscending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //This installs a JAR and a POM
        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Wait a bit before deploying other file (to ensure different Last Modified times)
        Thread.sleep( 2000 );

        //This installs a JAR and a POM
        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Last Modified ascending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_NAME,
                                           true );
        assertEquals( 4,
                      files.size() );
        final Long fileTime0 = files.get( 0 ).lastModified();
        final Long fileTime2 = files.get( 2 ).lastModified();
        assertTrue( fileTime0.compareTo( fileTime2 ) < 0 );
    }

    @Test
    public void testListFilesWithSortOnLastModifiedDescending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //This installs a JAR and a POM
        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Wait a bit before deploying other file (to ensure different Last Modified times)
        Thread.sleep( 2000 );

        //This installs a JAR and a POM
        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Last Modified descending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_NAME,
                                           false );
        assertEquals( 4,
                      files.size() );
        final Long fileTime0 = files.get( 0 ).lastModified();
        final Long fileTime2 = files.get( 2 ).lastModified();
        assertTrue( fileTime0.compareTo( fileTime2 ) > 0 );
    }

    @Test
    public void testListFilesIncludingPom() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //This installs a JAR and a POM
        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //This installs a POM
        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend-parent",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-pom.xml" );
        repo.deployPom( is,
                        gav );

        //Get files
        List<File> files = repo.listFiles( null,
                                           null,
                                           false );
        assertEquals( 3,
                      files.size() );
    }

    //Create a mock FileItem setting an InputStream to test with
    @SuppressWarnings("serial")
    class MockFileItem implements FileItem {

        private final String fileName;
        private final InputStream fileStream;

        MockFileItem( final String fileName,
                      final InputStream fileStream ) {
            this.fileName = fileName;
            this.fileStream = fileStream;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fileStream;
            //return this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public String getName() {
            return fileName;
        }

        @Override
        public boolean isInMemory() {
            return false;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public byte[] get() {
            return null;
        }

        @Override
        public String getString( String encoding ) throws UnsupportedEncodingException {
            return null;
        }

        @Override
        public String getString() {
            return null;
        }

        @Override
        public void write( File file ) throws Exception {
        }

        @Override
        public void delete() {
        }

        @Override
        public String getFieldName() {
            return null;
        }

        @Override
        public void setFieldName( String name ) {
        }

        @Override
        public boolean isFormField() {
            return false;
        }

        @Override
        public void setFormField( boolean state ) {
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public FileItemHeaders getHeaders() {
            return null;
        }

        @Override
        public void setHeaders( FileItemHeaders fileItemHeaders ) {
        }

    }

}
