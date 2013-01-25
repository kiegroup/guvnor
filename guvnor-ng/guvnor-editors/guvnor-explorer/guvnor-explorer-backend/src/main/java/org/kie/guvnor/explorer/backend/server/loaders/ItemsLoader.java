package org.kie.guvnor.explorer.backend.server.loaders;

import java.util.List;

import org.kie.guvnor.explorer.model.Item;
import org.uberfire.backend.vfs.Path;

/**
 * Loader of items at a Path into a View
 */
public interface ItemsLoader {

    List<Item> load( final Path path );

}
