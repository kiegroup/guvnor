package org.guvnor.server;

import org.drools.compiler.kproject.xml.PomModel;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.guvnor.m2repo.backend.server.helpers.PomModelResolver;

import javax.inject.Inject;
import java.io.*;

public class Deployer {


    @Inject
    private ExtendedM2RepoService extendedM2RepoService;

    public void deploy(File targetFolder) throws IOException {

        for (File file : getJars(targetFolder)) {


            BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));

            fileInputStream.mark(fileInputStream.available());

            PomModel pomModel = PomModelResolver.resolve(fileInputStream);

            fileInputStream.reset();

            extendedM2RepoService.deployJar(
                    fileInputStream,
                    new GAV(pomModel.getReleaseId().getGroupId(),
                            pomModel.getReleaseId().getArtifactId(),
                            pomModel.getReleaseId().getVersion()
                    ));

            fileInputStream.close();
        }


    }

    private File[] getJars(File targetFolder) {
        return targetFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("jar");
            }
        });
    }
}
