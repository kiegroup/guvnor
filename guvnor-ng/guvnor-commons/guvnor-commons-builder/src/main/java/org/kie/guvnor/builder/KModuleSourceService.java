package org.kie.guvnor.builder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.AbstractSourceService;
import org.kie.guvnor.commons.service.source.SourceContext;

/**
 * Source provider for KModule.xml
 */
public class KModuleSourceService extends AbstractSourceService {

    private static final String PATTERN = "src/main/resources/META-INF/kmodule.xml";

    private static final String DESTINATION = "META-INF/kmodule.xml";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public SourceContext getSource( final Path path ) {
        final String kmodule = ioService.readAllString( path );
        final ByteArrayInputStream is = new ByteArrayInputStream( kmodule.getBytes() );
        final BufferedInputStream bis = new BufferedInputStream( is );
        final SourceContext context = new SourceContext( bis,
                                                         DESTINATION );
        return context;
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

}
