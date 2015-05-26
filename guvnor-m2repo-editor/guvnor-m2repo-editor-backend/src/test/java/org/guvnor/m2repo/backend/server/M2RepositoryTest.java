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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void testDeployArtifactAndGetArtifactFile() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend",
                           "0.0.1-SNAPSHOT" );

        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
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
                String pom = GuvnorM2Repository.loadPOMFromJar( jarPath );
                assertNotNull( pom );
                break;
            }
        }

        assertTrue( "Did not find expected file after calling M2Repository.addFile()",
                    found );
      
        // Test get artifact file
        File file = repo.getArtifactFileFromRepository(gav);
        assertNotNull( "Empty file for artifact", file );
        JarFile jarFile = new JarFile(file);
        int count = 0;
       
        String lastEntryName = null;
        for( Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) { 
            ++count;
            JarEntry entry = entries.nextElement();
            assertNotEquals( "Endless loop.", lastEntryName, entry.getName() );
        }
        assertTrue( "Empty jar file!", count > 0 );
    }

    @Test
    public void testListFiles() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend",
                           "0.0.1-SNAPSHOT" );

        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        gav = new GAV( "org.jboss.arquillian.core",
                       "arquillian-core-api",
                       "1.0.2.Final" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
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
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        gav = new GAV( "org.jboss.arquillian.core",
                       "arquillian-core-api",
                       "1.0.2.Final" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //filter with version number
        Collection<File> files = repo.listFiles( "1.0.2" );
        boolean found1 = false;
        for ( File file : files ) {
            String fileName = file.getName();
            if ( fileName.startsWith( "arquillian-core-api-1.0.2" ) && fileName.endsWith( ".jar" ) ) {
                found1 = true;
            }
        }
        assertTrue( "Did not find expected file after calling M2Repository.addFile()",
                    found1 );
        
/*        //filter with group id
        files = repo.listFiles("org.kie.guvnor");
        found1 = false;
        for(File file : files) {
            String fileName = file.getName();
            if("guvnor-m2repo-editor-backend-test.jar".equals(fileName)) {
                found1 = true;
            } 
        }        
        assertTrue("Did not find expected file after calling M2Repository.addFile()", found1);*/

        //filter with artifact id
        files = repo.listFiles( "arquillian-core-api" );
        found1 = false;
        for ( File file : files ) {
            String fileName = file.getName();
            if ( fileName.startsWith( "arquillian-core-api-1.0.2" ) && fileName.endsWith( ".jar" ) ) {
                found1 = true;
            }
        }
        assertTrue( "Did not find expected file after calling M2Repository.addFile()",
                    found1 );
    }

    @Test
    public void testUploadFileWithGAV() throws Exception {

        //Create a mock FileItem setting an InputStream to test with
        @SuppressWarnings("serial")
        class TestFileItem implements FileItem {

            public InputStream getInputStream() throws IOException {
                return this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
            }

            public String getContentType() {
                return null;
            }

            public String getName() {
                return null;
            }

            public boolean isInMemory() {
                return false;
            }

            public long getSize() {
                return 0;
            }

            public byte[] get() {
                return null;
            }

            public String getString( String encoding ) throws UnsupportedEncodingException {
                return null;
            }

            public String getString() {
                return null;
            }

            public void write( File file ) throws Exception {
            }

            public void delete() {
            }

            public String getFieldName() {
                return null;
            }

            public void setFieldName( String name ) {
            }

            public boolean isFormField() {
                return false;
            }

            public void setFormField( boolean state ) {
            }

            public OutputStream getOutputStream() throws IOException {
                return null;
            }

            public FileItemHeaders getHeaders() {
                return null;
            }

            public void setHeaders( FileItemHeaders fileItemHeaders ) {
            }
        }
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        //Create a shell M2RepoService and set the M2Repository
        M2RepoServiceImpl service = new M2RepoServiceImpl();
        java.lang.reflect.Field repositoryField = M2RepoServiceImpl.class.getDeclaredField( "repository" );
        repositoryField.setAccessible( true );
        repositoryField.set( service,
                             new GuvnorM2Repository() );

        //Make private method accessible for testing
        HttpPostHelper helper = new HttpPostHelper();
        java.lang.reflect.Method helperMethod = HttpPostHelper.class.getDeclaredMethod( "uploadFile", FormData.class );
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
        FileItem file = new TestFileItem();
        uploadItem.setFile( file );

        assert ( helperMethod.invoke( helper,
                                      uploadItem ).equals( "OK" ) );
    }

    @Test
    public void testListFilesWithSortOnNameAscending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Name ascending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_NAME,
                                           true );
        assertEquals( 2,
                      files.size() );
        final String fileName0 = files.get( 0 ).getName();
        final String fileName1 = files.get( 1 ).getName();
        assertTrue( fileName0.startsWith( "guvnor-m2repo-editor-backend1" ) );
        assertTrue( fileName1.startsWith( "guvnor-m2repo-editor-backend2" ) );
    }

    @Test
    public void testListFilesWithSortOnNameDescending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Name descending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_NAME,
                                           false );
        assertEquals( 2,
                      files.size() );
        final String fileName0 = files.get( 0 ).getName();
        final String fileName1 = files.get( 1 ).getName();
        assertTrue( fileName0.startsWith( "guvnor-m2repo-editor-backend2" ) );
        assertTrue( fileName1.startsWith( "guvnor-m2repo-editor-backend1" ) );
    }

    @Test
    public void testListFilesWithSortOnPathAscending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Path ascending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_PATH,
                                           true );
        assertEquals( 2,
                      files.size() );
        final String filePath0 = files.get( 0 ).getPath();
        final String filePath1 = files.get( 1 ).getPath();
        assertTrue( filePath0.contains( "guvnor-m2repo-editor-backend1" ) );
        assertTrue( filePath1.contains( "guvnor-m2repo-editor-backend2" ) );
    }

    @Test
    public void testListFilesWithSortOnPathDescending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Name descending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_PATH,
                                           false );
        assertEquals( 2,
                      files.size() );
        final String filePath0 = files.get( 0 ).getPath();
        final String filePath1 = files.get( 1 ).getPath();
        assertTrue( filePath0.contains( "guvnor-m2repo-editor-backend2" ) );
        assertTrue( filePath1.contains( "guvnor-m2repo-editor-backend1" ) );
    }

    @Test
    public void testListFilesWithSortOnLastModifiedAscending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Wait a bit before deploying other file (to ensure different Last Modified times)
        Thread.sleep( 2000 );

        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Last Modified ascending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_NAME,
                                           true );
        assertEquals( 2,
                      files.size() );
        final Long fileTime0 = files.get( 0 ).lastModified();
        final Long fileTime1 = files.get( 1 ).lastModified();
        assertTrue( fileTime0.compareTo( fileTime1 ) < 0 );
    }

    @Test
    public void testListFilesWithSortOnLastModifiedDescending() throws Exception {
        GuvnorM2Repository repo = new GuvnorM2Repository();
        repo.init();

        GAV gav = new GAV( "org.kie.guvnor",
                           "guvnor-m2repo-editor-backend1",
                           "0.0.1-SNAPSHOT" );
        InputStream is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Wait a bit before deploying other file (to ensure different Last Modified times)
        Thread.sleep( 2000 );

        gav = new GAV( "org.kie.guvnor",
                       "guvnor-m2repo-editor-backend2",
                       "0.0.1-SNAPSHOT" );
        is = this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test.jar" );
        repo.deployArtifact( is,
                             gav,
                             false );

        //Sort by Last Modified descending
        List<File> files = repo.listFiles( null,
                                           JarListPageRequest.COLUMN_NAME,
                                           false );
        assertEquals( 2,
                      files.size() );
        final Long fileTime0 = files.get( 0 ).lastModified();
        final Long fileTime1 = files.get( 1 ).lastModified();
        assertTrue( fileTime0.compareTo( fileTime1 ) > 0 );
    }

}
