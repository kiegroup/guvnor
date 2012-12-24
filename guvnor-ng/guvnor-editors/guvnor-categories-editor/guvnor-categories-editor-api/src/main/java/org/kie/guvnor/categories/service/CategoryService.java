package org.kie.guvnor.categories.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.guvnor.categories.model.Categories;
import org.uberfire.backend.vfs.Path;

@Remote
public interface CategoryService {

    void save( final Path path,
               final Categories content );

    Categories getContent( final Path path );

    Categories getCategoriesFromResouce( final Path resource );
}
