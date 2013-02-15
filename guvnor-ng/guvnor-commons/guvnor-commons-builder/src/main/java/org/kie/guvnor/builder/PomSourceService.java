package org.kie.guvnor.builder;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.commons.service.source.SourceContext;
import org.kie.guvnor.project.backend.server.POMContentHandler;
import org.kie.guvnor.project.model.POM;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Source provider for KModule.xml
 */
public class PomSourceService
        extends BaseSourceService<POM> {

    private static final String PATTERN = "pom.xml";

    private static final String DESTINATION = "pom.xml";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private POMContentHandler pomContentHandler;

    protected PomSourceService() {
        super("");
    }

    @Override
    public SourceContext getSource(final Path path) {
        final String source = ioService.readAllString(path);
        final ByteArrayInputStream is = new ByteArrayInputStream(source.getBytes());
        final BufferedInputStream bis = new BufferedInputStream(is);
        final SourceContext context = new SourceContext(bis,
                DESTINATION);
        return context;
    }

    @Override
    public String getSource(Path path, POM model) {
        try {
            return pomContentHandler.toString(model);
        } catch (IOException e) {
            e.printStackTrace();  //TODO -Rikkola-
        }
        return null;
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

}
