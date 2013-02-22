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

package org.kie.guvnor.globals.backend.server;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.globals.backend.server.util.GlobalsPersistence;
import org.kie.guvnor.globals.model.GlobalsEditorContent;
import org.kie.guvnor.globals.model.GlobalsModel;
import org.kie.guvnor.globals.service.GlobalsEditorService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.security.Identity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

@Service
@ApplicationScoped
public class GlobalsEditorServiceImpl
        implements GlobalsEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private SourceServices sourceServices;

    @Inject
    private Event<InvalidateDMOPackageCacheEvent> invalidatePackageDMOEvent;

    @Override
    public GlobalsEditorContent loadContent( final Path path ) {
        //De-serialize model
        final GlobalsModel model = loadGlobalsModel( path );

        final DataModelOracle oracle = dataModelService.getDataModel( path );

        return new GlobalsEditorContent( model,
                                         oracle );
    }

    private GlobalsModel loadGlobalsModel( final Path path ) {
        final String content = ioService.readAllString( paths.convert( path ) );
        return GlobalsPersistence.getInstance().unmarshal( content );
    }

    @Override
    public void save( final Path path,
                      final GlobalsModel model,
                      final String comment ) {
        ioService.write( paths.convert( path ),
                         GlobalsPersistence.getInstance().marshal( model ),
                         makeCommentedOption( comment ) );

        //A change in Globals invalidates the Package-level DMO
        invalidatePackageDMOEvent.fire( new InvalidateDMOPackageCacheEvent( path ) );
    }

    @Override
    public Path save( final Path context,
                      final String fileName,
                      final GlobalsModel content,
                      final String comment ) {
        final Path newPath = paths.convert( paths.convert( context ).resolve( fileName ), false );

        save( newPath, content, comment );

        return newPath;
    }

    @Override
    public void save( final Path path,
                      final GlobalsModel model,
                      final Metadata metadata,
                      final String comment ) {
        ioService.write( paths.convert( path ),
                         GlobalsPersistence.getInstance().marshal( model ),
                         metadataService.setUpAttributes( path,
                                                          metadata ),
                         makeCommentedOption( comment ) );

        //A change in Globals invalidates the Package-level DMO
        invalidatePackageDMOEvent.fire( new InvalidateDMOPackageCacheEvent( path ) );
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

    @Override
    public String toSource( final Path path,
                            final GlobalsModel model ) {
        return sourceServices.getServiceFor( paths.convert( path ) ).getSource( paths.convert( path ),
                                                                                GlobalsPersistence.getInstance().marshal( model ) );

    }

    @Override
    public BuilderResult validate( final Path path,
                                   final GlobalsModel model ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final GlobalsModel model ) {
        return !validate( path,
                          model ).hasLines();
    }

    @Override
    public AnalysisReport verify( final Path path,
                                  final GlobalsModel model ) {
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
