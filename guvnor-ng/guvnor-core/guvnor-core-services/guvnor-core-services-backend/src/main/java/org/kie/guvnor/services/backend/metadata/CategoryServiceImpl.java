package org.kie.guvnor.services.backend.metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.file.FileAlreadyExistsException;
import org.kie.commons.java.nio.file.InvalidPathException;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.guvnor.services.exceptions.FileAlreadyExistsPortableException;
import org.kie.guvnor.services.exceptions.GenericPortableException;
import org.kie.guvnor.services.exceptions.InvalidPathPortableException;
import org.kie.guvnor.services.exceptions.NoSuchFilePortableException;
import org.kie.guvnor.services.exceptions.SecurityPortableException;
import org.kie.guvnor.services.metadata.CategoriesService;
import org.kie.guvnor.services.metadata.model.Categories;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class CategoryServiceImpl implements CategoriesService {

    private final XStream xt = new XStream();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Override
    public void save( final Path path,
                      final Categories content ) {
        try {
            ioService.write( paths.convert( path ),
                             xt.toXML( content ) );

        } catch ( InvalidPathException e ) {
            throw new InvalidPathPortableException( path.toURI() );

        } catch ( SecurityException e ) {
            throw new SecurityPortableException( path.toURI() );

        } catch ( IllegalArgumentException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( FileAlreadyExistsException e ) {
            throw new FileAlreadyExistsPortableException( path.toURI() );

        } catch ( IOException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( UnsupportedOperationException e ) {
            throw new GenericPortableException( e.getMessage() );

        }
    }

    @Override
    public Categories getContent( final Path path ) {
        try {
            final String content = ioService.readAllString( paths.convert( path ) );
            final Categories categories;

            if ( content.trim().equals( "" ) ) {
                categories = new Categories();
            } else {
                categories = (Categories) xt.fromXML( content );
            }

            return categories;

        } catch ( NoSuchFileException e ) {
            throw new NoSuchFilePortableException( path.toURI() );

        } catch ( IllegalArgumentException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( IOException e ) {
            throw new GenericPortableException( e.getMessage() );

        }
    }

    @Override
    public Categories getCategoriesFromResource( final Path resource ) {
        final org.kie.commons.java.nio.file.Path categoriesPath = paths.convert( resource ).getRoot().resolve( "categories.xml" );

        return getContent( paths.convert( categoriesPath ) );
    }
}
