/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.project.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Repository;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;

@Service
@ApplicationScoped
public class POMServiceImpl
        implements POMService {

    private IOService ioService;
    private POMContentHandler pomContentHandler;
    private M2RepoService m2RepoService;
    private MetadataService metadataService;

    @Inject
    private CommentedOptionFactory optionsFactory;

    public POMServiceImpl() {
        // For Weld
    }

    @Inject
    public POMServiceImpl( final @Named("ioStrategy") IOService ioService,
                           final POMContentHandler pomContentHandler,
                           final M2RepoService m2RepoService,
                           final MetadataService metadataService ) {
        this.ioService = ioService;
        this.pomContentHandler = pomContentHandler;
        this.m2RepoService = m2RepoService;
        this.metadataService = metadataService;
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
            POM pom = pomContentHandler.toModel( loadPomXMLString( path ) );

            return pom;

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

            return path;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path save( final Path path,
            final POM content,
            final Metadata metadata,
            final String comment,
            final boolean updateModules ) {

        try {

            ioService.startBatch( new FileSystem[]{Paths.convert( path ).getFileSystem()}, optionsFactory.makeCommentedOption( comment != null ? comment : "" )  );

            List<Pair<POM, org.uberfire.java.nio.file.Path >> modules = new ArrayList<Pair<POM, org.uberfire.java.nio.file.Path >>( );
            if ( updateModules &&
                    content.isMultiModule() &&
                    content.getModules() != null ) {
                POM child;
                org.uberfire.java.nio.file.Path childPath;
                org.uberfire.java.nio.file.Path rootPath = Paths.convert( path );
                rootPath = rootPath.getParent();
                for ( String module : content.getModules() ) {
                    childPath = rootPath.resolve( module ).resolve( "pom.xml" );
                    if ( ioService.exists( childPath ) ) {
                        child = load( Paths.convert( childPath ) );
                        if ( child != null ) {
                            child.setParent( content.getGav() );
                            child.getGav().setGroupId( content.getGav().getGroupId() );
                            child.getGav().setVersion( content.getGav().getVersion() );
                            modules.add( new Pair<POM, org.uberfire.java.nio.file.Path>( child, childPath ) );
                        }
                    }
                }
            }

            if ( metadata == null ) {
                ioService.write( Paths.convert( path ),
                        pomContentHandler.toString( content, loadPomXMLString( path ) ) );
            } else {
                ioService.write( Paths.convert( path ),
                        pomContentHandler.toString( content, loadPomXMLString( path ) ),
                        metadataService.setUpAttributes( path,
                                metadata ) );
            }

            for ( Pair<POM, org.uberfire.java.nio.file.Path> modulePair : modules ) {
                ioService.write( modulePair.getK2(), pomContentHandler.toString( modulePair.getK1(), loadPomXMLString( Paths.convert( modulePair.getK2() ) ) ) );
            }

            return path;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        } finally {
            ioService.endBatch();
        }
    }

}
