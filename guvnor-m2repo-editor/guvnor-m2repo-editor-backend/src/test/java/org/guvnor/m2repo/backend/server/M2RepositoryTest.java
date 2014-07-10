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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.helpers.FormData;
import org.guvnor.m2repo.backend.server.helpers.HttpPostHelper;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@Ignore
public class M2RepositoryTest {

    private static final Logger log = LoggerFactory.getLogger( M2RepositoryTest.class );

    @After
    public void tearDown() throws Exception {
        log.info( "Creating a new Repository Instance.." );

        File dir = new File( "repository" );
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
    @Ignore("Fails - ignored for Beta3")
    public void testDeployArtifact() throws Exception {
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
                String pom = GuvnorM2Repository.loadPOMFromJar( path );
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
}
