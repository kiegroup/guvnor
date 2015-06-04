package org.guvnor.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.inject.Inject;

import org.drools.compiler.kproject.xml.PomModel;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.guvnor.m2repo.backend.server.helpers.PomModelResolver;

public class Deployer {

    @Inject
    private ExtendedM2RepoService extendedM2RepoService;

    public void deploy( File targetFolder ) throws IOException {

        for ( File file : getJars( targetFolder ) ) {

            BufferedInputStream fileInputStream = new BufferedInputStream( new FileInputStream( file ) );

            fileInputStream.mark( fileInputStream.available() );

            PomModel pomModel = PomModelResolver.resolveFromJar( fileInputStream );

            fileInputStream.reset();

            extendedM2RepoService.deployJar(
                    fileInputStream,
                    new GAV( pomModel.getReleaseId().getGroupId(),
                             pomModel.getReleaseId().getArtifactId(),
                             pomModel.getReleaseId().getVersion()
                    ) );

            fileInputStream.close();
        }

    }

    private File[] getJars( File targetFolder ) {
        return targetFolder.listFiles( new FilenameFilter() {
            @Override
            public boolean accept( File dir,
                                   String name ) {
                return name.endsWith( "jar" );
            }
        } );
    }
}
