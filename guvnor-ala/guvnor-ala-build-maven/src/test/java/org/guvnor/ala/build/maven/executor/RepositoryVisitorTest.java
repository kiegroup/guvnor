
/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.build.maven.executor;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.maven.project.MavenProject;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitHub;
import org.guvnor.ala.source.git.GitRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.kie.scanner.embedder.MavenProjectLoader;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.commit;

public class RepositoryVisitorTest {

    private File tempPath;

    @Before
    public void setUp() throws IOException {
        tempPath = Files.createTempDirectory( "yyy" ).toFile();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly( tempPath );
    }

    @Test
    public void repositoryVisitorDiffDeletedTest() throws IOException {
        final GitHub gitHub = new GitHub();
        final GitRepository repository = ( GitRepository ) gitHub.getRepository( "mbarkley/appformer-playground", new HashMap<String, String>() {
            {
                put( "out-dir", tempPath.getAbsolutePath() );
            }
        } );
        final Source source = repository.getSource( "master" );

        final InputStream pomStream = org.uberfire.java.nio.file.Files.newInputStream( source.getPath().resolve( "users-new" ).resolve( "pom.xml" ) );
        MavenProject project = MavenProjectLoader.parseMavenPom( pomStream );

        RepositoryVisitor repositoryVisitor = new RepositoryVisitor( source.getPath().resolve( "users-new" ), project.getName() );

        System.out.println( "Root: " + repositoryVisitor.getRoot().getAbsolutePath() );

        Map<String, String> identityHash = repositoryVisitor.getIdentityHash();

        final URI originRepo = URI.create( "git://" + repository.getName() );
        final JGitFileSystemProvider provider = ( JGitFileSystemProvider ) FileSystemProviders.resolveProvider( originRepo );

        final JGitFileSystem origin = ( JGitFileSystem ) provider.getFileSystem( originRepo );

        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {
            {
                put( "/users-new/file.txt", tempFile( "temp" ) );
                put( "/users-new/pom.xml", tempFile( "hi there" + UUID.randomUUID().toString() ) );
            }
        } );

        provider.delete( source.getPath().resolve( "users-new" ).resolve( "demo.iml" ) );

        RepositoryVisitor newRepositoryVisitor = new RepositoryVisitor( source.getPath().resolve( "users-new" ), repositoryVisitor.getRoot().getAbsolutePath().trim(), false );

        System.out.println( "Root: " + newRepositoryVisitor.getRoot().getAbsolutePath() );
        Map<String, String> newIdentityHash = newRepositoryVisitor.getIdentityHash();

        MapDifference<String, String> difference = Maps.difference( identityHash, newIdentityHash );

        Map<String, MapDifference.ValueDifference<String>> entriesDiffering = difference.entriesDiffering();
        System.out.println( " Size of Differences: " + entriesDiffering.size() );
        for ( String key : entriesDiffering.keySet() ) {
            System.out.println( "Different Value: " + key );
        }
        assertEquals( 1, entriesDiffering.size() );
        assertNotNull( entriesDiffering.get( "/users-new/pom.xml" ) );

        Map<String, String> deletedFiles = difference.entriesOnlyOnLeft();
        System.out.println( " Size of Deleted Files: " + deletedFiles.size() );
        for ( String key : deletedFiles.keySet() ) {
            System.out.println( "Deleted File: " + key );
        }
        assertEquals( 1, deletedFiles.size() );
        assertNotNull( deletedFiles.get( "/users-new/demo.iml" ) );
        Map<String, String> addedFiles = difference.entriesOnlyOnRight();
        System.out.println( " Size of added Files: " + addedFiles.size() );
        for ( String key : addedFiles.keySet() ) {
            System.out.println( "Added File: " + key );
        }
        assertEquals( 1, addedFiles.size() );
        assertNotNull( addedFiles.get( "/users-new/file.txt" ) );
    }

    public File tempFile( final String content ) throws IOException {
        final File file = File.createTempFile( "bar", "foo" );
        final OutputStream out = new FileOutputStream( file );

        if ( content != null && !content.isEmpty() ) {
            out.write( content.getBytes() );
            out.flush();
        }

        out.close();
        return file;
    }

}
