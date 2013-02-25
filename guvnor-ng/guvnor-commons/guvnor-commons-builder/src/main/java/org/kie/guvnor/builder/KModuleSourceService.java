package org.kie.guvnor.builder;

import javax.inject.Inject;

import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.project.backend.server.KModuleContentHandler;
import org.kie.guvnor.project.model.KModuleModel;

/**
 * Source provider for KModule.xml
 */
public class KModuleSourceService
        extends BaseSourceService<KModuleModel> {

    //NOTE: Platform specific separators are handled by Path already
    private static final String PATTERN = "src/main/resources/META-INF/kmodule.xml";

    @Inject
    private KModuleContentHandler moduleContentHandler;

    protected KModuleSourceService() {
        super( "/src/main/resources" );
    }

    @Override
    public String getSource( final Path path,
                             final KModuleModel model ) {
        return moduleContentHandler.toString( model );
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

}
