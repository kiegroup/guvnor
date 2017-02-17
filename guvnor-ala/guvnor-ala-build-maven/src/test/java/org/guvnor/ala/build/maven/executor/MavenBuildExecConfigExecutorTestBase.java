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

package org.guvnor.ala.build.maven.executor;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.build.maven.model.impl.MavenProjectBinaryBuildImpl;
import org.guvnor.ala.build.maven.util.MavenBuildTestBase;
import org.guvnor.ala.config.BinaryConfig;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class MavenBuildExecConfigExecutorTestBase
        extends MavenBuildTestBase {

    @Mock
    protected Project project;

    @Mock
    protected MavenBuild mavenBuild;

    protected void prepareMavenBuild( ) {
        ArrayList< String > goals = new ArrayList<>( );
        goals.add( "clean" );
        goals.add( "package" );

        when( mavenBuild.getGoals( ) ).thenReturn( goals );
        when( mavenBuild.getProperties( ) ).thenReturn( new Properties( ) );
        when( mavenBuild.getProject( ) ).thenReturn( project );

        when( project.getTempDir( ) ).thenReturn( targetProjectPath.toString( ) );
        when( project.getExpectedBinary( ) ).thenReturn( getTestProjectJar( ) );
    }

    protected void verifyBinary( Optional< BinaryConfig > optional ) {
        assertTrue( optional.get( ) instanceof MavenProjectBinaryBuildImpl );
        MavenProjectBinaryBuildImpl mavenProjectBinaryBuild = ( MavenProjectBinaryBuildImpl ) optional.get( );
        assertFalse( mavenProjectBinaryBuild.getMavenBuildResult( ).hasExceptions( ) );
        assertTrue( Files.exists( targetProjectPath.resolve( "target/" + getTestProjectJar( ) ) ) );
        assertEquals( 1, mavenProjectBinaryBuild.getMavenBuildResult( )
                .getBuildMessages( )
                .stream( )
                .filter( buildMessage -> BUILD_SUCCESS.equals( buildMessage.getMessage( ) ) )
                .count( ) );
    }
}