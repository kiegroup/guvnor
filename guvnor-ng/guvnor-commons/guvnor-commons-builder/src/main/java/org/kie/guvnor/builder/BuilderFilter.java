package org.kie.guvnor.builder;

import org.kie.commons.java.nio.file.Path;

/**
 * Allow filtering of Resources from a build
 */
public interface BuilderFilter {

    boolean accept( final Path path );

}
