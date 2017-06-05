/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.build.maven.util;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.guvnor.ala.build.maven.model.MavenBuildMessage;

public abstract class MavenBuildTestBase {

    public static final String BUILD_SUCCESS = "BUILD SUCCESS";

    public static final String COMPILATION_ERROR = "COMPILATION ERROR";

    protected Path tempDir;

    protected Path targetProjectPath;

    protected Path pomFile;

    protected Path generatedBinaryPath;

    protected abstract String getTestProject( );

    protected abstract String getTestProjectJar( );

    public URL getPomURL( ) {
        return this.getClass( ).getResource( "/" + getTestProject( ) + "/pom.xml" );
    }

    public void setUpTestProject( ) throws Exception {
        final URL pomUrl = getPomURL( );
        final Path testProjectPath = Paths.get( pomUrl.toURI( ) ).getParent( );
        tempDir = Files.createTempDirectory( getClass( ).getSimpleName( ) );
        targetProjectPath = tempDir.resolve( getTestProject( ) );
        FileUtils.copyDirectory( testProjectPath.toFile( ), targetProjectPath.toFile( ) );
        pomFile = targetProjectPath.resolve( "pom.xml" );
        generatedBinaryPath = targetProjectPath.resolve( "target/" + getTestProjectJar( ) );
    }

    protected void clearTempDir( ) throws Exception {
        FileUtils.deleteDirectory( tempDir.toFile( ) );
    }

    protected void renameFile( Path rootPath, String sourceFile, String newName ) throws Exception {
        Path sourcePath = rootPath.resolve( sourceFile );
        Path targetPath = sourcePath.resolveSibling( newName );
        Files.move( sourcePath, targetPath );
    }

    protected boolean hasMessageWithString( List< MavenBuildMessage > messages, MavenBuildMessage.Level level, String message ) {
        return messages.stream( )
                .filter( msg -> level.equals( msg.getLevel( ) ) && msg.getMessage( ).contains( message ) )
                .findFirst( )
                .isPresent( );
    }
}