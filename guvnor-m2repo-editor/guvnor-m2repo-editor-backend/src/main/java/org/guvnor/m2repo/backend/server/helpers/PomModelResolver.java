package org.guvnor.m2repo.backend.server.helpers;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.api.builder.ReleaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class PomModelResolver {

    private static final Logger log = LoggerFactory.getLogger(PomModelResolver.class);

    public static PomModel resolve(InputStream fileData) {
        //Attempt to load JAR's POM information from it's pom.xml file
        PomModel pomModel = null;
        try {
            String pomXML = GuvnorM2Repository.loadPOMFromJar(fileData);
            if (pomXML != null) {
                pomModel = PomModel.Parser.parse("pom.xml",
                        new ByteArrayInputStream(pomXML.getBytes()));
            }
        } catch (Exception e) {
            log.info("Failed to parse pom.xml for GAV information. Falling back to pom.properties.",
                    e);
        }

        //Attempt to load JAR's POM information from it's pom.properties file
        if (pomModel == null) {
            try {
                fileData.reset();
                String pomProperties = GuvnorM2Repository.loadPOMPropertiesFromJar(fileData);
                if (pomProperties != null) {
                    final ReleaseId releaseId = ReleaseIdImpl.fromPropertiesString(pomProperties);
                    if (releaseId != null) {
                        pomModel = new PomModel();
                        pomModel.setReleaseId(releaseId);
                    }
                }
            } catch (Exception e) {
                log.info("Failed to parse pom.properties for GAV information.");
            }
        }
        return pomModel;
    }
}
