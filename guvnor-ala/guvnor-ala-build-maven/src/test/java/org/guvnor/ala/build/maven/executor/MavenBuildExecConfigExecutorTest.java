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

import java.util.Optional;

import org.guvnor.ala.build.maven.config.MavenBuildExecConfig;
import org.guvnor.ala.build.maven.model.MavenBuildMessage;
import org.guvnor.ala.build.maven.model.impl.MavenProjectBinaryBuildImpl;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.exceptions.BuildException;
import org.guvnor.ala.registry.BuildRegistry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class MavenBuildExecConfigExecutorTest
        extends MavenBuildExecConfigExecutorTestBase {

    @Mock
    protected BuildRegistry buildRegistry;

    @Mock
    private MavenBuildExecConfig mavenBuildExecConfig;

    protected MavenBuildExecConfigExecutor executor;

    @Override
    protected String getTestProject( ) {
        return "MavenBuildExecutorTest";
    }

    @Override
    protected String getTestProjectJar( ) {
        return "maven-build-executor-test-1.0.0.jar";
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none( );

    @Before
    public void setUp( ) throws Exception {
        setUpTestProject( );
        executor = new MavenBuildExecConfigExecutor( buildRegistry );
    }

    @Test
    public void testApply( ) throws Exception {
        prepareMavenBuild( );
        Optional< BinaryConfig > result = executor.apply( mavenBuild, mavenBuildExecConfig );
        verifyBinary( result );
        clearTempDir( );
    }

    @Test
    public void testApplyWithBuildErrorsNotCapturingErrors( ) throws Exception {
        //make appear two wrong java files intentionally
        createFailingFiles();
        prepareMavenBuild( );

        expectedException.expect( BuildException.class );
        expectedException.expectMessage( "Maven found issues trying to build the pom file: " + pomFile.toString( ) +
                ". Look at the Error Logs for more information" );
        executor.apply( mavenBuild, mavenBuildExecConfig );
        clearTempDir( );
    }

    @Test
    public void testApplyWithBuildErrorsCapturingErrors( ) throws Exception {
        //make appear two wrong java files intentionally
        createFailingFiles();
        prepareMavenBuild( );

        when( mavenBuildExecConfig.captureErrors( ) ).thenReturn( "true" );

        Optional< BinaryConfig > result = executor.apply( mavenBuild, mavenBuildExecConfig );

        assertTrue( result.get( ) instanceof MavenProjectBinaryBuildImpl );
        MavenProjectBinaryBuildImpl mavenProjectBinaryBuild = ( MavenProjectBinaryBuildImpl ) result.get( );
        assertTrue( mavenProjectBinaryBuild.getMavenBuildResult( ).hasExceptions( ) );
        assertTrue( hasMessageWithString( mavenProjectBinaryBuild.getMavenBuildResult( ).getBuildMessages( ),
                MavenBuildMessage.Level.ERROR, COMPILATION_ERROR ) );
        //there should be errors for the two wrong java files.
        assertTrue( hasMessageWithString( mavenProjectBinaryBuild.getMavenBuildResult( ).getBuildMessages( ),
                MavenBuildMessage.Level.ERROR, "PojoWithErrors1.java" ) );
        assertTrue( hasMessageWithString( mavenProjectBinaryBuild.getMavenBuildResult( ).getBuildMessages( ),
                MavenBuildMessage.Level.ERROR, "PojoWithErrors2.java" ) );
        clearTempDir( );
    }

    private void createFailingFiles() throws Exception {
        renameFile( targetProjectPath, "src/main/java/org/kie/test/PojoWithErrors1.java.template", "PojoWithErrors1.java" );
        renameFile( targetProjectPath, "src/main/java/org/kie/test/PojoWithErrors2.java.template", "PojoWithErrors2.java" );
    }
}