/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.maven.cli.MavenCli;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.shared.message.Level;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl
        implements BuildService {

    @Inject
    private ProjectVisitor projectVisitor;

    @Inject
    private Deployer deployer;

    @Override
    public BuildResults build( final Project project ) {
        return new BuildResults();
    }

    @Override
    public BuildResults buildAndDeploy( final Project project ) {

        BuildResults buildResults = new BuildResults();

        try {

            projectVisitor.visit( project );

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream( out );

            MavenCli cli = new MavenCli();
            int result = cli.doMain( new String[]{ "clean", "install" },
                                     projectVisitor.getRootFolder().getAbsolutePath(),
                                     printStream, printStream );

            if ( result != 0 ) {
                BuildMessage message = new BuildMessage();
                message.setId( 123 );
                message.setText( new String( out.toByteArray() ) );
                message.setLevel( Level.ERROR );
                buildResults.addBuildMessage( message );
            }

            deployer.deploy( projectVisitor.getTargetFolder() );

        } catch ( IOException e ) {
            buildResults.addBuildMessage( reportError( e ) );
        } finally {

            try {
                FileUtils.deleteDirectory( projectVisitor.getGuvnorTempFolder() );
            } catch ( IOException e ) {
                buildResults.addBuildMessage( reportError( e ) );
            }
        }

        return buildResults;
    }

    @Override
    public BuildResults buildAndDeploy( final Project project,
                                        final DeploymentMode mode ) {
        return buildAndDeploy( project );
    }

    @Override
    public BuildResults buildAndDeploy( final Project project,
                                        final boolean suppressHandlers ) {
        return new BuildResults();
    }

    @Override
    public BuildResults buildAndDeploy( final Project project,
                                        final boolean suppressHandlers,
                                        final DeploymentMode mode ) {
        return buildAndDeploy( project,
                               suppressHandlers );
    }

    private BuildMessage reportError( IOException e ) {
        BuildMessage message = new BuildMessage();
        message.setText( e.getMessage() );
        message.setLevel( Level.ERROR );
        return message;
    }

    @Override
    public boolean isBuilt( final Project project ) {
        return true;
    }

    @Override
    public IncrementalBuildResults addPackageResource( final Path resource ) {
        return new IncrementalBuildResults();
    }

    @Override
    public IncrementalBuildResults deletePackageResource( final Path resource ) {
        return new IncrementalBuildResults();
    }

    @Override
    public IncrementalBuildResults updatePackageResource( final Path resource ) {
        return new IncrementalBuildResults();
    }

    @Override
    public IncrementalBuildResults applyBatchResourceChanges( final Project project,
                                                              final Map<Path, Collection<ResourceChange>> changes ) {
        return new IncrementalBuildResults();
    }

}
