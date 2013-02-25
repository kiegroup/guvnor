package org.kie.guvnor.builder;

import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;

public class JavaSourceService
        extends BaseSourceService<String> {

    private static final String PATTERN = ".java";

    protected JavaSourceService() {
        super( "/src/main/java" );
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    public String getSource( final Path path,
                             final String model ) {
        return model;
    }
}
