/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.enums.backend.server;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.data.events.AssetEditedEvent;
import org.kie.guvnor.commons.data.events.AssetOpenedEvent;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.validation.model.BuilderResultLine;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.backend.server.builder.util.DataEnumLoader;
import org.kie.guvnor.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.guvnor.enums.model.EnumModel;
import org.kie.guvnor.enums.model.EnumModelContent;
import org.kie.guvnor.enums.service.EnumService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.security.Identity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Service
@ApplicationScoped
public class EnumServiceImpl implements EnumService {
    private static final Logger log = LoggerFactory.getLogger( EnumServiceImpl.class );

    private static final String FORMAT = "enumeration";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    
    @Inject
    private MetadataService metadataService;

    @Inject
    private Paths paths;

    @Inject
    private Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache;
    
    @Inject
    private Event<AssetEditedEvent> assetEditedEvent;
    
    @Inject
    private Event<AssetOpenedEvent> assetOpenedEvent;
    
    @Inject
    private Identity identity;

    @Override
    public EnumModelContent load( Path path ) {
        assetOpenedEvent.fire( new AssetOpenedEvent( path ) );        
        return new EnumModelContent( new EnumModel( ioService.readAllString( paths.convert( path ) ) ) );
     }

    @Override
    public void save( final Path path,
                      final String content,
                      final String comment ) {
        ioService.write( paths.convert( path ),
                         content,
                         makeCommentedOption( comment ) );
        assetEditedEvent.fire( new AssetEditedEvent( path ) );        
    }

    @Override
    public Path save( final Path context,
                      final String fileName,
                      final String content,
                      final String comment ) {
        final Path newPath = paths.convert( paths.convert( context ).resolve( fileName ), false );

        save( newPath, content, comment );
        assetEditedEvent.fire( new AssetEditedEvent( newPath ) );

        return newPath;
    }

    @Override
    public void save( final Path resource,
                      final String content,
                      final Metadata metadata,
                      final String commitMessage ) {
        final org.kie.commons.java.nio.file.Path path = paths.convert( resource );

        if ( metadata == null ) {
            ioService.write(
                    path,
                    content,
                    makeCommentedOption( commitMessage ) );
        } else {
            ioService.write(
                    path,
                    content,
                    metadataService.setUpAttributes( resource, metadata ),
                    makeCommentedOption( commitMessage ) );
        }

        invalidateDMOPackageCache.fire( new InvalidateDMOPackageCacheEvent( resource ) );
        assetEditedEvent.fire( new AssetEditedEvent( resource ) );
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final String content ) {
        final DataEnumLoader loader = new DataEnumLoader( content );
        if ( !loader.hasErrors() ) {
            return new BuilderResult();
        } else {
            final List<BuilderResultLine> errors = new ArrayList<BuilderResultLine>();
            final List<String> errs = loader.getErrors();

            for ( final String message : errs ) {
                final BuilderResultLine result = new BuilderResultLine().setResourceName( path.getFileName() ).setResourceFormat( FORMAT ).setResourceId( path.toURI() ).setMessage( message );
                errors.add( result );
            }

            final BuilderResult result = new BuilderResult();
            result.addLines( errors );

            return result;
        }
    }

    @Override
    public boolean isValid( final Path path,
                            final String content ) {
        return !validate( path, content ).hasLines();
    }

    @Override
    public AnalysisReport verify( final Path path,
                                  final String content ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        log.info( "USER:" + identity.getName() + " DELETING asset [" + path.getFileName() + "]" );

        ioService.delete( paths.convert( path ) );
        assetEditedEvent.fire( new AssetEditedEvent( path ) );     
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        log.info( "USER:" + identity.getName() + " RENAMING asset [" + path.getFileName() + "] to [" + newName + "]" );
        
        String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName;
        String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName;
        Path targetPath = PathFactory.newPath( path.getFileSystem(), targetName, targetURI );
        ioService.move( paths.convert( path ), paths.convert( targetPath ), new CommentedOption( identity.getName(), comment ) );
        
        assetEditedEvent.fire( new AssetEditedEvent( path ) );     
        return targetPath;
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        log.info( "USER:" + identity.getName() + " COPYING asset [" + path.getFileName() + "] to [" + newName + "]" );
        
        String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName;
        String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName;
        Path targetPath = PathFactory.newPath( path.getFileSystem(), targetName, targetURI );
        ioService.copy( paths.convert( path ), paths.convert( targetPath ), new CommentedOption( identity.getName(), comment ) );
        
        assetEditedEvent.fire( new AssetEditedEvent( path ) );     
        return targetPath;
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }
}
