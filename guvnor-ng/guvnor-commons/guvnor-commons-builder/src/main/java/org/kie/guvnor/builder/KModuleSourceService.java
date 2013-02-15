package org.kie.guvnor.builder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.commons.service.source.SourceContext;
import org.kie.guvnor.project.backend.server.KModuleContentHandler;
import org.kie.guvnor.project.model.KModuleModel;

/**
 * Source provider for KModule.xml
 */
public class KModuleSourceService
        extends BaseSourceService<KModuleModel> {

    //NOTE: Platform specific separators are handled by Path already
    private static final String PATTERN = "src/main/resources/META-INF/kmodule.xml";

    //KieBuilderImpl only accepts Unix style
    private static final String DESTINATION = "src/main/resources/META-INF/kmodule.xml";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private KModuleContentHandler moduleContentHandler;

    protected KModuleSourceService() {
        super("/src/main/resources");
    }

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
    public String getSource(Path path, KModuleModel model) {
        return moduleContentHandler.toString(model);
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

}
