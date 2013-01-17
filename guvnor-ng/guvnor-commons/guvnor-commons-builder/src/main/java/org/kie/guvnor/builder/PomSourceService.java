package org.kie.guvnor.builder;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.commons.service.source.SourceContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

/**
 * Source provider for KModule.xml
 */
public class PomSourceService
        extends BaseSourceService {

    private static final String PATTERN = "pom.xml";

    private static final String DESTINATION = "pom.xml";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

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
    public String getPattern() {
        return PATTERN;
    }

}
