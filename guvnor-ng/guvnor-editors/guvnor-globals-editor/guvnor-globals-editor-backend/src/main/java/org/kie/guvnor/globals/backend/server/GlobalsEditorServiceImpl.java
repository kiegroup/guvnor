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

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
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
import org.kie.guvnor.services.file.CopyService;
import org.kie.guvnor.services.file.DeleteService;
import org.kie.guvnor.services.file.RenameService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class GlobalsEditorServiceImpl implements GlobalsEditorService {

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
    private Event<InvalidateDMOPackageCacheEvent> invalidatePackageDMOEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private SourceServices sourceServices;

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final GlobalsModel content,
                        final String comment ) {
        final Path newPath = paths.convert( paths.convert( context ).resolve( fileName ),
                                            false );

        ioService.write( paths.convert( newPath ),
                         GlobalsPersistence.getInstance().marshal( content ),
                         makeCommentedOption( comment ) );

        resourceAddedEvent.fire( new ResourceAddedEvent( newPath ) );
        return newPath;
    }

    @Override
    public GlobalsModel load( final Path path ) {
        final String drl = ioService.readAllString( paths.convert( path ) );
        //TODO {manstis} getResourceOpenedEvent().fire( new ResourceOpenedEvent( path ) );
        return GlobalsPersistence.getInstance().unmarshal( drl );
    }

    @Override
    public GlobalsEditorContent loadContent( final Path path ) {
        //De-serialize model
        final GlobalsModel model = load( path );
        final DataModelOracle oracle = dataModelService.getDataModel( path );

        return new GlobalsEditorContent( model,
                                         oracle );
    }

    @Override
    public Path save( final Path context,
                      final String fileName,
                      final GlobalsModel content,
                      final String comment ) {
        final Path newPath = paths.convert( paths.convert( context ).resolve( fileName ),
                                            false );

        ioService.write( paths.convert( newPath ),
                         GlobalsPersistence.getInstance().marshal( content ),
                         makeCommentedOption( comment ) );

        //Invalidate Package-level DMO cache as Globals have changed.
        invalidatePackageDMOEvent.fire( new InvalidateDMOPackageCacheEvent( newPath ) );

        //TODO assetEditedEvent.fire( new AssetEditedEvent( newPath ) );
        return newPath;
    }

    @Override
    public Path save( final Path resource,
                      final GlobalsModel content,
                      final Metadata metadata,
                      final String comment ) {
        ioService.write( paths.convert( resource ),
                         GlobalsPersistence.getInstance().marshal( content ),
                         metadataService.setUpAttributes( resource,
                                                          metadata ),
                         makeCommentedOption( comment ) );

        //Invalidate Package-level DMO cache as Globals have changed.
        invalidatePackageDMOEvent.fire( new InvalidateDMOPackageCacheEvent( resource ) );

        //TODO assetEditedEvent.fire( new AssetEditedEvent( resource ) );
        return resource;
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
