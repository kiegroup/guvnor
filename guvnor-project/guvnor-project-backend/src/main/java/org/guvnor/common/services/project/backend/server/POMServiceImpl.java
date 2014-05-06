package org.guvnor.common.services.project.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Repository;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class POMServiceImpl
        implements POMService {

    private IOService ioService;
    private POMContentHandler pomContentHandler;
    private M2RepoService m2RepoService;
    private MetadataService metadataService;

    private Identity identity;

    private SessionInfo sessionInfo;

    public POMServiceImpl() {
        // For Weld
    }

    @Inject
    public POMServiceImpl( final @Named("ioStrategy") IOService ioService,
                           final POMContentHandler pomContentHandler,
                           final M2RepoService m2RepoService,
                           final MetadataService metadataService,
                           final Identity identity,
                           final SessionInfo sessionInfo ) {
        this.ioService = ioService;
        this.pomContentHandler = pomContentHandler;
        this.m2RepoService = m2RepoService;
        this.metadataService = metadataService;
        this.identity = identity;
        this.sessionInfo = new SafeSessionInfo(sessionInfo);
    }

    @Override
    public Path create( final Path projectRoot,
                        final String baseURL,
                        final POM pomModel ) {
        org.uberfire.java.nio.file.Path pathToPOMXML = null;
        try {
            final Repository repository = new Repository();
            repository.setId( "guvnor-m2-repo" );
            repository.setName( "Guvnor M2 Repo" );
            repository.setUrl( m2RepoService.getRepositoryURL( baseURL ) );
            pomModel.addRepository( repository );

            final org.uberfire.java.nio.file.Path nioRoot = Paths.convert( projectRoot );
            pathToPOMXML = nioRoot.resolve( "pom.xml" );

            if ( ioService.exists( pathToPOMXML ) ) {
                throw new FileAlreadyExistsException( pathToPOMXML.toString() );
            }
            ioService.write( pathToPOMXML,
                             pomContentHandler.toString( pomModel ) );

            //Don't raise a NewResourceAdded event as this is handled at the Project level in ProjectServices

            return Paths.convert( pathToPOMXML );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public POM load( final Path path ) {
        try {

            return pomContentHandler.toModel( loadPomXMLString( path ) );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private String loadPomXMLString( final Path path ) {
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );
        return ioService.readAllString( nioPath );
    }

    @Override
    public Path save( final Path path,
                      final POM content,
                      final Metadata metadata,
                      final String comment ) {
        try {

            if ( metadata == null ) {
                ioService.write( Paths.convert( path ),
                                 pomContentHandler.toString( content, loadPomXMLString( path ) ) );
            } else {
                ioService.write( Paths.convert( path ),
                                 pomContentHandler.toString( content, loadPomXMLString( path ) ),
                                 metadataService.setUpAttributes( path,
                                                                  metadata ) );
            }

            //The pom.xml, kmodule.xml and project.imports are all saved from ProjectScreenPresenter
            //We only raise InvalidateDMOProjectCacheEvent and ResourceUpdatedEvent(pom.xml) events once
            //to avoid duplicating events (and re-construction of DMO).

            //@wmedvede now the InvalidateDMOProjectCacheEvent will be fired in the DataModelResourceChangeObserver
            //Invalidate Project-level DMO cache as POM has changed.
            //invalidateDMOProjectCache.fire( new InvalidateDMOProjectCacheEvent( path ) );

            return path;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
