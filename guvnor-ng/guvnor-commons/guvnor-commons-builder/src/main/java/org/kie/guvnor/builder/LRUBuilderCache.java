package org.kie.guvnor.builder;

import javax.enterprise.context.ApplicationScoped;

import org.kie.guvnor.services.cache.LRUCache;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache for Builders
 */
@ApplicationScoped
public class LRUBuilderCache extends LRUCache<Path, Builder> {

}
