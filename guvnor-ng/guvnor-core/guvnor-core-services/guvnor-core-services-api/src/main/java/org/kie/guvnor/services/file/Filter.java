package org.kie.guvnor.services.file;

import org.kie.commons.java.nio.file.Path;

/**
 * Allow filtering of Resources from a build
 */
public interface Filter {

    boolean accept( final Path path );

}
