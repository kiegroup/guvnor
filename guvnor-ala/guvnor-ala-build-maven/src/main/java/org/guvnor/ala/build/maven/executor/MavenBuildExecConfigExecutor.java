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

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.inject.Inject;

import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.MavenProject;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.MavenBuildExecConfig;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.build.maven.model.MavenBuildMessage;
import org.guvnor.ala.build.maven.model.MavenBuildResult;
import org.guvnor.ala.build.maven.model.impl.MavenProjectBinaryBuildImpl;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.exceptions.BuildException;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.kie.scanner.embedder.logger.LocalLoggerConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.FileSystems;
import org.uberfire.java.nio.file.Path;

import static org.guvnor.ala.build.maven.util.MavenBuildExecutor.*;

public class MavenBuildExecConfigExecutor implements BiFunctionConfigExecutor<MavenBuild, MavenBuildExecConfig, BinaryConfig> {

    private static final Logger logger = LoggerFactory.getLogger( MavenBuildExecConfigExecutor.class );

    private static final String BUILD_FAILURE = "BUILD FAILURE";

    private static final String BUILD_SUCCESS = "BUILD SUCCESS";

    private BuildRegistry buildRegistry;

    public MavenBuildExecConfigExecutor( ) {
        //Empty constructor for Weld proxying
    }

    @Inject
    public MavenBuildExecConfigExecutor( final BuildRegistry buildRegistry ) {
        this.buildRegistry = buildRegistry;
    }

    @Override
    public Optional< BinaryConfig > apply( final MavenBuild mavenBuild,
                                           final MavenBuildExecConfig mavenBuildExecConfig ) {

        Optional< BinaryConfig > result;

        final Project project = mavenBuild.getProject( );

        final MavenBuildResult buildResult = new MavenBuildResult( );

        final MavenExecutionResult executionResult = build( project, mavenBuild.getGoals( ),
                mavenBuild.getProperties( ), new LocalLoggerConsumer( ) {
                    @Override
                    public void debug( String message, Throwable throwable ) {
                        buildResult.addBuildMessage( new MavenBuildMessage( MavenBuildMessage.Level.DEBUG, message ) );
                    }

                    @Override
                    public void info( String message, Throwable throwable ) {
                        buildResult.addBuildMessage( new MavenBuildMessage( MavenBuildMessage.Level.INFO, message ) );
                    }

                    @Override
                    public void warn( String message, Throwable throwable ) {
                        buildResult.addBuildMessage( new MavenBuildMessage( MavenBuildMessage.Level.WARNING, message ) );
                    }

                    @Override
                    public void error( String message, Throwable throwable ) {
                        buildResult.addBuildMessage( new MavenBuildMessage( MavenBuildMessage.Level.ERROR, message ) );
                    }

                    @Override
                    public void fatalError( String message, Throwable throwable ) {
                        buildResult.addBuildMessage( new MavenBuildMessage( MavenBuildMessage.Level.FATAL_ERROR, message ) );
                    }
                } );

        if ( executionResult.hasExceptions( ) && !captureErrors( mavenBuildExecConfig ) ) {
            for ( Throwable t : executionResult.getExceptions( ) ) {
                logger.error( "Error Running Maven", t );
            }
            throw new BuildException( "Maven found issues trying to build the pom file: "
                    + Paths.get( project.getTempDir( ), "pom.xml" ).toString( ) +
                    ". Look at the Error Logs for more information", executionResult.getExceptions( ).get( 0 ) );
        } else if ( executionResult.hasExceptions( ) && captureErrors( mavenBuildExecConfig ) ) {
            for ( Throwable t : executionResult.getExceptions( ) ) {
                logger.error( "Error Running Maven", t );
                buildResult.addBuildException( t.getMessage( ) );
            }
            buildResult.addBuildMessage( new MavenBuildMessage( MavenBuildMessage.Level.INFO, BUILD_FAILURE ) );
            result = Optional.of( new MavenProjectBinaryBuildImpl( buildResult ) );
        } else {
            final MavenProject mavenProject = executionResult.getProject( );
            String groupId = mavenProject.getGroupId( );
            String artifactId = mavenProject.getArtifactId( );
            String version = mavenProject.getVersion( );

            final Path path = FileSystems.getFileSystem( URI.create( "file://default" ) ).getPath( project.getTempDir( ) + "/target/" + project.getExpectedBinary( ) );

            final MavenBinary binary = new MavenProjectBinaryBuildImpl(
                    path,
                    project,
                    groupId,
                    artifactId,
                    version,
                    buildResult );

            buildRegistry.registerBinary( binary );
            buildResult.addBuildMessage( new MavenBuildMessage( MavenBuildMessage.Level.INFO, BUILD_SUCCESS ) );
            result = Optional.of( binary );
        }
        return result;
    }

    @Override
    public Class< ? extends Config > executeFor( ) {
        return MavenBuildExecConfig.class;
    }

    @Override
    public String outputId( ) {
        return "binary";
    }

    @Override
    public String inputId( ) {
        return "maven-exec-config";
    }

    public MavenExecutionResult build( final Project project,
                                       final List< String > goals,
                                       final Properties properties,
                                       final LocalLoggerConsumer consumer ) throws BuildException {
        final File pom = new File( project.getTempDir( ), "pom.xml" );
        return executeMaven( pom, properties, consumer, goals.toArray( new String[]{ } ) );
    }

    private boolean captureErrors( MavenBuildExecConfig mavenBuildExecConfig ) {
        return Boolean.parseBoolean( mavenBuildExecConfig.captureErrors( ) );
    }
}