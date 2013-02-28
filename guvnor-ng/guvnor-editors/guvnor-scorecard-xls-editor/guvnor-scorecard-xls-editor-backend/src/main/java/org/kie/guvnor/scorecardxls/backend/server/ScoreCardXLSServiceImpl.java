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

package org.kie.guvnor.scorecardxls.backend.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.scorecardxls.service.ScoreCardXLSService;
import org.kie.guvnor.services.file.CopyService;
import org.kie.guvnor.services.file.DeleteService;
import org.kie.guvnor.services.file.RenameService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class ScoreCardXLSServiceImpl implements ScoreCardXLSService {

    private static final Logger log = LoggerFactory.getLogger( ScoreCardXLSServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Override
    public InputStream load( final Path path ) {
        //TODO {manstis} assetOpenedEvent.fire( new AssetOpenedEvent( newPath ) );
        return ioService.newInputStream( paths.convert( path ), null );
    }

    @Override
    public OutputStream save( final Path path ) {
        log.info( "USER:" + identity.getName() + " SAVING asset [" + path.getFileName() + "]" );

        System.out.println( "USER:" + identity.getName() + " SAVING asset [" + path.getFileName() + "]" );

        //TODO {manstis} assetUpdatedEvent.fire( new AssetUpdatedEvent( newPath ) );
        return ioService.newOutputStream( paths.convert( path ), makeCommentedOption( "uploaded" ) );
    }

    public OutputStream save( final Path path,
                              final String comment ) {
        log.info( "USER:" + identity.getName() + " SAVING asset [" + path.getFileName() + "]" );

        //TODO {manstis} assetUpdatedEvent.fire( new AssetUpdatedEvent( newPath ) );
        return ioService.newOutputStream( paths.convert( path ),
                                          makeCommentedOption( comment ) );
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        deleteService.delete( path,
                              comment );
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        return renameService.rename( path,
                                     newName,
                                     comment );
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        return copyService.copy( path,
                                 newName,
                                 comment );
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final String content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final String content ) {
        return !validate( path, content ).hasLines();
    }

    @Override
    public AnalysisReport verify( Path path,
                                  String content ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
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
