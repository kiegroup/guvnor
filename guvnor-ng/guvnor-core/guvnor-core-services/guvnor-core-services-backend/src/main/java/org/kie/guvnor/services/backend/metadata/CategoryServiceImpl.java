package org.kie.guvnor.services.backend.metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.thoughtworks.xstream.XStream;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.services.metadata.CategoriesService;
import org.kie.guvnor.services.metadata.model.Categories;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

@Service
@ApplicationScoped
public class CategoryServiceImpl implements CategoriesService {

    private final XStream xt = new XStream();

    @Inject
    private VFSService vfs;

    @Inject
    private Paths paths;

    @Override
    public void save( final Path path,
                      final Categories content ) {
        vfs.write( path, xt.toXML( content ) );
    }

    @Override
    public Categories getContent( final Path path ) {
        try {
            final String content = vfs.readAllString( path );
            final Categories categories;
            if ( content.trim().equals( "" ) ) {
                categories = new Categories();
            } else {
                categories = (Categories) xt.fromXML( content );
            }
            return categories;
        } catch ( final Exception ex ) {
            return new Categories();
        }
    }

    @Override
    public Categories getCategoriesFromResouce( final Path resource ) {
        final org.kie.commons.java.nio.file.Path categoriesPath = paths.convert( resource ).getRoot().resolve( "categories.xml" );

        return getContent( paths.convert( categoriesPath ) );
    }
}
