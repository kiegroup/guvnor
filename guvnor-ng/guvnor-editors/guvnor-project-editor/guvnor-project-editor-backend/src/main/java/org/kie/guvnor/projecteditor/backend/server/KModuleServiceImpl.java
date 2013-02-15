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

package org.kie.guvnor.projecteditor.backend.server;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.commons.service.source.ViewSourceService;
import org.kie.guvnor.project.backend.server.KModuleContentHandler;
import org.kie.guvnor.project.model.KModuleModel;
import org.kie.guvnor.project.service.KModuleService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class KModuleServiceImpl
        implements KModuleService,
        ViewSourceService<KModuleModel>{

    private IOService ioService;
    private Paths paths;
    private KModuleContentHandler moduleContentHandler;
    private MetadataService metadataService;

    @Inject
    private Identity identity;
    private SourceServices sourceServices;

    public KModuleServiceImpl() {
        // Weld needs this for proxying.
    }

    @Inject
    public KModuleServiceImpl( final @Named("ioStrategy") IOService ioService,
                               MetadataService metadataService,
                               SourceServices sourceServices,
                               final Paths paths,
                               final KModuleContentHandler moduleContentHandler ) {
        this.ioService = ioService;
        this.metadataService = metadataService;
        this.sourceServices = sourceServices;
        this.paths = paths;
        this.moduleContentHandler = moduleContentHandler;
    }

    @Override
    public Path setUpKModuleStructure( final Path pathToPom ) {
        try {
            // Create project structure
            final org.kie.commons.java.nio.file.Path directory = getPomDirectoryPath( pathToPom );

            ioService.createDirectory( directory.resolve( "src/main/java" ) );
            ioService.createDirectory( directory.resolve( "src/main/resources" ) );
            final org.kie.commons.java.nio.file.Path pathToKModuleXML = directory.resolve( "src/main/resources/META-INF/kmodule.xml" );
            saveKModule( pathToKModuleXML, new KModuleModel() );

            ioService.createDirectory( directory.resolve( "src/test/java" ) );
            ioService.createDirectory( directory.resolve( "src/test/resources" ) );

            return paths.convert( pathToKModuleXML );
        } catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public void saveKModule( String commitMessage,
                             final Path path,
                             final KModuleModel model,
                             Metadata metadata ) {
        if ( metadata == null ) {
            ioService.write(
                    paths.convert( path ),
                    moduleContentHandler.toString( model ),
                    makeCommentedOption( commitMessage ) );
        } else {
            ioService.write(
                    paths.convert( path ),
                    moduleContentHandler.toString( model ),
                    metadataService.setUpAttributes( path, metadata ),
                    makeCommentedOption( commitMessage ) );
        }
    }

    @Override
    public KModuleModel loadKModule( final Path path ) {
        return moduleContentHandler.toModel( ioService.readAllString( paths.convert( path ) ) );
    }

    @Override
    public Path pathToRelatedKModuleFileIfAny( final Path pathToPomXML ) {
        final org.kie.commons.java.nio.file.Path directory = getPomDirectoryPath( pathToPomXML );

        final org.kie.commons.java.nio.file.Path pathToKModuleXML = directory.resolve( "src/main/resources/META-INF/kmodule.xml" );

        if ( ioService.exists( pathToKModuleXML ) ) {
            return paths.convert( pathToKModuleXML );
        } else {
            return null;
        }
    }

    private void saveKModule( final org.kie.commons.java.nio.file.Path path,
                              final KModuleModel model ) {
        ioService.write( path, moduleContentHandler.toString( model ) );
    }

    private org.kie.commons.java.nio.file.Path getPomDirectoryPath( final Path pathToPomXML ) {
        return paths.convert( pathToPomXML ).getParent();
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

    @Override
    public String toSource(Path path, KModuleModel model) {
        return sourceServices.getServiceFor(paths.convert(path)).getSource(paths.convert(path), model);
    }
}