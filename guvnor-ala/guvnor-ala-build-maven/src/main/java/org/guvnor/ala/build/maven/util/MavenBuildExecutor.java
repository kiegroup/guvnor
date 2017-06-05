/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.guvnor.ala.build.maven.util;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionResult;
import org.guvnor.ala.exceptions.BuildException;
import org.kie.scanner.embedder.MavenEmbedder;
import org.kie.scanner.embedder.MavenEmbedderException;
import org.kie.scanner.embedder.MavenProjectLoader;
import org.kie.scanner.embedder.MavenRequest;
import org.kie.scanner.embedder.logger.LocalLoggerConsumer;
import org.kie.scanner.embedder.logger.LocalLoggerManager;
import org.slf4j.LoggerFactory;

public final class MavenBuildExecutor {

    protected static final org.slf4j.Logger LOG = LoggerFactory.getLogger( MavenBuildExecutor.class );

    private MavenBuildExecutor() {
    }

    public static void executeMaven( final File pom,
            final Properties properties,
            final String... goals ) {
        executeMaven( pom, System.out, System.err, properties, goals );
    }

    /**
     * Executes a maven build for a project.
     * @param pom File with the pom.xml file for the given project.
     * @param properties additional properties that will passed as System properties to the maven build.
     * @param loggerConsumer logger consumer for enabling the maven build logs consumption.
     * @param goals list of maven goals to execute.
     * @return the maven build result.
     */
    public static MavenExecutionResult executeMaven( final File pom,
                                                     final Properties properties,
                                                     final LocalLoggerConsumer loggerConsumer,
                                                     final String... goals ) {
        try {
            final MavenRequest mavenRequest = MavenProjectLoader.createMavenRequest( false );
            mavenRequest.setGoals( Arrays.asList( goals ) );
            mavenRequest.setPom( pom.getAbsolutePath() );
            if ( properties != null ) {
                mavenRequest.setSystemProperties( properties );
            }
            if ( loggerConsumer != null ) {
                mavenRequest.setMavenLoggerManager( new LocalLoggerManager( loggerConsumer ) );
            }
            final MavenEmbedder mavenEmbedder = new MavenEmbedder( mavenRequest );
            return mavenEmbedder.execute( mavenRequest );

        } catch ( final MavenEmbedderException ex ) {
            throw new BuildException( "Maven found issues trying to build the pom file: "
                    + pom.getAbsolutePath() + ". Look at the Error Logs for more information" );
        }
    }

    public static void executeMaven( final File pom,
            final PrintStream stdout,
            final PrintStream stderr,
            final Properties properties,
            final String... goals ) {

        final PrintStream oldout = System.out;
        final PrintStream olderr = System.err;
        final Properties oldProperties = System.getProperties();
        if ( properties != null ) {
            properties.keySet().forEach( (o) -> {
                if ( properties.getProperty( ( String ) o ) != null ) {
                    System.setProperty( ( String ) o, properties.getProperty( ( String ) o ) );
                }
            } );
        }

        final MavenEmbedder mavenEmbedder = newMavenEmbedder();
        try {
            if ( stdout != null ) {
                System.setOut( stdout );
            }
            if ( stderr != null ) {
                System.setErr( stderr );
            }

            final MavenRequest mavenRequest = MavenProjectLoader.createMavenRequest( false );
            mavenRequest.setGoals( Arrays.asList( goals ) );
            mavenRequest.setPom( pom.getAbsolutePath() );

            final MavenExecutionResult result = mavenEmbedder.execute( mavenRequest );

            if ( result.hasExceptions() ) {
                for ( Throwable t : result.getExceptions() ) {
                    LOG.error( "Error Running Maven", t );
                }
                throw new BuildException( "Maven found issues trying to build the pom file: "
                        + pom.getAbsolutePath() + ". Look at the Error Logs for more information" );
            }
        } catch ( final MavenEmbedderException ex ) {
            throw new BuildException( "Maven coudn't build the project for pom file: " + pom.getAbsolutePath(), ex );
        } finally {
            System.setProperties( oldProperties );
            mavenEmbedder.dispose();
            System.setOut( oldout );
            System.setErr( olderr );
        }

    }

    private static MavenEmbedder newMavenEmbedder() {
        MavenEmbedder mavenEmbedder;
        try {
            mavenEmbedder = new MavenEmbedder( MavenProjectLoader.createMavenRequest( false ) );
        } catch ( MavenEmbedderException e ) {
            throw new RuntimeException( e );
        }
        return mavenEmbedder;
    }

}
