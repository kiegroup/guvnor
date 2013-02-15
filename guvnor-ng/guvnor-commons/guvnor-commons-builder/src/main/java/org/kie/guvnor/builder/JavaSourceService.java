package org.kie.guvnor.builder;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.commons.service.source.SourceContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

public class JavaSourceService
        extends BaseSourceService<String> {

    private static final String PATTERN = ".java";

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    protected JavaSourceService() {
        super("/src/main/java");
    }

    @Override
    public SourceContext getSource(final Path path) {
        String source = ioService.readAllString(path);

        return new SourceContext(new BufferedInputStream(new ByteArrayInputStream(source.getBytes())), stripProjectPrefix(path));
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    public String getSource(Path path, String model) {
        return model;
    }
}
