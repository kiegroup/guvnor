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

package org.kie.guvnor.workitems.backend.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.services.file.CopyService;
import org.kie.guvnor.services.file.DeleteService;
import org.kie.guvnor.services.file.RenameService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.kie.guvnor.workitems.model.WorkItemsMetaContent;
import org.kie.guvnor.workitems.model.WorkItemsModelContent;
import org.kie.guvnor.workitems.service.WorkItemsEditorService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceOpenedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class WorkItemsEditorServiceImpl implements WorkItemsEditorService {

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
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    private WorkItemsMetaContent workItemsMetaContent;

    @PostConstruct
    public void setupResources() {
        workItemsMetaContent = new WorkItemsMetaContent( loadWorkItemElementDefinitions(),
                                                         loadWorkItemImages() );
    }

    private Map<String, String> loadWorkItemElementDefinitions() {
        final Map<String, String> workItemElementDefinitions = new HashMap<String, String>();
        final List<ConfigGroup> editorConfigGroups = configurationService.getConfiguration( ConfigType.EDITOR );
        for ( ConfigGroup editorConfigGroup : editorConfigGroups ) {
            if ( WORK_ITEMS_EDITOR_SETTINGS.equals( editorConfigGroup.getName() ) ) {
                for ( ConfigItem item : editorConfigGroup.getItems() ) {
                    final String itemName = item.getName();
                    final String itemValue = editorConfigGroup.getConfigItemValue( itemName );
                    workItemElementDefinitions.put( itemName,
                                                    itemValue );
                }
            }
        }
        return workItemElementDefinitions;
    }

    private List<String> loadWorkItemImages() {
        //TODO How do Work Item images function under 6.0?
        return new ArrayList<String>();
    }

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final String content,
                        final String comment ) {
        //Get the template for new Work Item Definitions, stored as a configuration item
        String defaultDefinition = workItemsMetaContent.getWorkItemElementDefinitions().get( WORK_ITEMS_EDITOR_SETTINGS_DEFINITION );
        if ( defaultDefinition == null ) {
            defaultDefinition = "";
        }
        defaultDefinition.replaceAll( "\\|",
                                      "" );

        //Write file to VFS
        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( context ).resolve( fileName );
        final Path newPath = paths.convert( nioPath,
                                            false );

        ioService.createFile( nioPath );
        ioService.write( nioPath,
                         defaultDefinition,
                         makeCommentedOption( comment ) );

        //Signal creation to interested parties
        resourceAddedEvent.fire( new ResourceAddedEvent( newPath ) );

        return newPath;
    }

    @Override
    public String load( final Path path ) {
        final String content = ioService.readAllString( paths.convert( path ) );

        //Signal opening to interested parties
        resourceOpenedEvent.fire( new ResourceOpenedEvent( path ) );

        return content;
    }

    @Override
    public WorkItemsModelContent loadContent( final Path path ) {
        final String definition = load( path );
        return new WorkItemsModelContent( definition );
    }

    @Override
    public WorkItemsMetaContent loadMetaContent() {
        return workItemsMetaContent;
    }

    @Override
    public Path save( final Path resource,
                      final String content,
                      final Metadata metadata,
                      final String comment ) {
        ioService.write( paths.convert( resource ),
                         content,
                         metadataService.setUpAttributes( resource,
                                                          metadata ),
                         makeCommentedOption( comment ) );

        //Signal update to interested parties
        resourceUpdatedEvent.fire( new ResourceUpdatedEvent( resource ) );

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
