package org.guvnor.common.services.shared.metadata;

import org.guvnor.common.services.shared.metadata.model.Categories;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface CategoriesService {

    void save( final Path path,
               final Categories content );

    Categories getContent( final Path path );

    Categories getCategoriesFromResource( final Path resource );
}
