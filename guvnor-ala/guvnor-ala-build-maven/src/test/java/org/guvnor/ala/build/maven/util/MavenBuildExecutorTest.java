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

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionResult;
import org.guvnor.ala.build.maven.model.MavenBuildMessage;
import org.junit.Before;
import org.junit.Test;
import org.kie.scanner.embedder.logger.LocalLoggerConsumer;

import static org.junit.Assert.*;

public class MavenBuildExecutorTest
        extends MavenBuildTestBase {

    @Override
    protected String getTestProject( ) {
        return "MavenBuildExecutorTest";
    }

    @Override
    protected String getTestProjectJar( ) {
        return "maven-build-executor-test-1.0.0.jar";
    }

    private List< MavenBuildMessage > messages = new ArrayList<>( );

    private LocalLoggerConsumer localLoggerConsumer;

    @Before
    public void setUp( ) throws Exception {
        setUpTestProject( );
        messages = new ArrayList<>( );
        localLoggerConsumer = new LocalLoggerConsumer( ) {
            @Override
            public void debug( String message, Throwable throwable ) {
                messages.add( new MavenBuildMessage( MavenBuildMessage.Level.DEBUG, message ) );
            }

            @Override
            public void info( String message, Throwable throwable ) {
                messages.add( new MavenBuildMessage( MavenBuildMessage.Level.INFO, message ) );
            }

            @Override
            public void warn( String message, Throwable throwable ) {
                messages.add( new MavenBuildMessage( MavenBuildMessage.Level.WARNING, message ) );
            }

            @Override
            public void error( String message, Throwable throwable ) {
                messages.add( new MavenBuildMessage( MavenBuildMessage.Level.ERROR, message ) );
            }

            @Override
            public void fatalError( String message, Throwable throwable ) {
                messages.add( new MavenBuildMessage( MavenBuildMessage.Level.FATAL_ERROR, message ) );
            }
        };
    }

    @Test
    public void testMavenBuild( ) throws Exception {
        MavenExecutionResult result = MavenBuildExecutor.executeMaven( pomFile.toFile( ), new Properties( ), localLoggerConsumer, "clean", "package" );
        assertFalse( result.hasExceptions( ) );
        assertTrue( Files.exists( generatedBinaryPath ) );
        assertTrue( hasMessageWithString( messages, MavenBuildMessage.Level.INFO, "Building jar: " + generatedBinaryPath ) );
        clearTempDir( );
    }

    @Test
    public void testMavenBuildWithErrors( ) throws Exception {
        //make appear two wrong java files intentionally
        renameFile( targetProjectPath, "src/main/java/org/kie/test/PojoWithErrors1.java.template", "PojoWithErrors1.java" );
        renameFile( targetProjectPath, "src/main/java/org/kie/test/PojoWithErrors2.java.template", "PojoWithErrors2.java" );

        MavenExecutionResult result = MavenBuildExecutor.executeMaven( pomFile.toFile( ), new Properties( ), localLoggerConsumer, "clean", "package" );
        assertTrue( result.hasExceptions( ) );
        assertFalse( Files.exists( targetProjectPath.resolve( "target/" + getTestProjectJar( ) ) ) );
        //well known build error must exist.
        assertTrue( hasMessageWithString( messages, MavenBuildMessage.Level.ERROR, COMPILATION_ERROR ) );
        //there should be errors for the two wrong java files.
        assertTrue( hasMessageWithString( messages, MavenBuildMessage.Level.ERROR, "PojoWithErrors1.java" ) );
        assertTrue( hasMessageWithString( messages, MavenBuildMessage.Level.ERROR, "PojoWithErrors2.java" ) );
        clearTempDir( );
    }
}