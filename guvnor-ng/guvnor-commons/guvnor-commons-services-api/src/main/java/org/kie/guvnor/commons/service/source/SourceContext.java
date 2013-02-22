package org.kie.guvnor.commons.service.source;

import org.kie.commons.validation.PortablePreconditions;

import java.io.InputStream;

/**
 * DTO for parameters needed to write a source file to KieFileSystem
 */
public class SourceContext {

    private final InputStream inputStream;
    private final String destinationPath;

    public SourceContext( final InputStream inputStream,
                          final String destinationPath ) {
        PortablePreconditions.checkNotNull( "inputStream", inputStream );
        PortablePreconditions.checkNotNull( "destinationPath", destinationPath );
        this.inputStream = inputStream;
        this.destinationPath = destinationPath;
    }

    public InputStream getInputSteam() {
        return this.inputStream;
    }

    public String getDestination() {
        return this.destinationPath;
    }

}
