package org.kie.guvnor.builder;

import java.io.IOException;
import javax.inject.Inject;

import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.project.backend.server.POMContentHandler;
import org.kie.guvnor.project.model.POM;

/**
 * Source provider for KModule.xml
 */
public class PomSourceService
        extends BaseSourceService<POM> {

    private static final String PATTERN = "pom.xml";

    @Inject
    private POMContentHandler pomContentHandler;

    protected PomSourceService() {
        super( "" );
    }

    @Override
    public String getSource( final Path path,
                             final POM model ) {
        try {
            return pomContentHandler.toString( model );
        } catch ( IOException e ) {
            e.printStackTrace();  //TODO -Rikkola-
        }
        return null;
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

}
