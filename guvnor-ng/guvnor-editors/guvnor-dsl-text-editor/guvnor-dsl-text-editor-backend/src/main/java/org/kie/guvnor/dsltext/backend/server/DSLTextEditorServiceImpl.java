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

package org.kie.guvnor.dsltext.backend.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.guvnor.dsltext.service.DSLTextEditorService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class DSLTextEditorServiceImpl
        implements DSLTextEditorService {

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
    private Identity identity;

    @Override
    public BuilderResult validate( final Path path,
                                   final String content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final String content ) {
        return !validate( path,
                          content ).hasLines();
    }

    @Override
    public AnalysisReport verify( final Path path,
                                  final String content ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }

    @Override
    public String load( Path path ) {
        return ioService.readAllString( paths.convert( path ) );
    }

    @Override
    public void save( final Path path,
                      final String content,
                      final String comment ) {
        ioService.write( paths.convert( path ),
                         content,
                         makeCommentedOption( comment ) );
    }

    @Override
    public void save( final Path resource,
                      final String content,
                      final Metadata metadata,
                      final String comment ) {

        final org.kie.commons.java.nio.file.Path path = paths.convert( resource );

        Map<String, Object> attrs;

        try {
            attrs = ioService.readAttributes( path );
        } catch ( final NoSuchFileException ex ) {
            attrs = new HashMap<String, Object>();
        }

        if ( metadata != null ) {
            attrs = metadataService.configAttrs( attrs,
                                                 metadata );
        }

        ioService.write( path,
                         content,
                         attrs,
                         makeCommentedOption( comment ) );

        invalidateDMOPackageCache.fire( new InvalidateDMOPackageCacheEvent( resource ) );
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        System.out.println( "USER:" + identity.getName() + " DELETING asset [" + path.getFileName() + "]" );

        ioService.delete( paths.convert( path ) );
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        System.out.println( "USER:" + identity.getName() + " RENAMING asset [" + path.getFileName() + "] to [" + newName + "]" );
        String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName;
        String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName;
        Path targetPath = PathFactory.newPath( path.getFileSystem(), targetName, targetURI );
        ioService.move( paths.convert( path ), paths.convert( targetPath ), new CommentedOption( identity.getName(), comment ) );
        return targetPath;
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        System.out.println( "USER:" + identity.getName() + " COPYING asset [" + path.getFileName() + "] to [" + newName + "]" );
        String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName;
        String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName;
        Path targetPath = PathFactory.newPath( path.getFileSystem(), targetName, targetURI );
        ioService.copy( paths.convert( path ), paths.convert( targetPath ), new CommentedOption( identity.getName(), comment ) );
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
