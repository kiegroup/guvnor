package org.kie.guvnor.explorer.backend.server.loaders;

import org.kie.guvnor.explorer.model.Item;
import org.uberfire.backend.vfs.Path;

import java.util.List;

/**
 * Loader of items at a Path into a View
 */
public interface ItemsLoader {

    List<Item> load( final Path path,
                     final Path projectRoot );

}
