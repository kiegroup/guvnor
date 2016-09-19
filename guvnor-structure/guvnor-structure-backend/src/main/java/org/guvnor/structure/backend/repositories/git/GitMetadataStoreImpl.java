/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories.git;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.structure.repositories.GitMetadata;
import org.guvnor.structure.repositories.GitMetadataStore;
import org.guvnor.structure.repositories.impl.metadata.GitMetadataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.io.object.ObjectStorage;

public class GitMetadataStoreImpl implements GitMetadataStore {

    private Logger logger = LoggerFactory.getLogger( GitMetadataStoreImpl.class );
    public static final String SEPARATOR = "/";
    public static final String METADATA = "default://system/metadata";

    private ObjectStorage storage;

    @Inject
    public GitMetadataStoreImpl( ObjectStorage storage ) {
        this.storage = storage;
    }

    @PostConstruct
    public void init() {

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Initializing GitMetadataStoreImpl {}", METADATA );
        }
        this.storage.init( METADATA );
    }

    @Override
    public void write( String name ) {
        this.write( name, "" );
    }

    @Override
    public void write( String name,
                       String origin ) {

        GitMetadataImpl repositoryMetadata = (GitMetadataImpl) this.read( name ).orElse( new GitMetadataImpl( name, origin ) );
        GitMetadataImpl newRepositoryMetadata = new GitMetadataImpl( name, origin, repositoryMetadata.getForks() );

        this.removeForkFromOrigin( repositoryMetadata );

        if ( isStorableOrigin( origin ) ) {
            GitMetadataImpl originMetadata = (GitMetadataImpl) this.read( origin ).orElse( new GitMetadataImpl( origin ) );
            List<String> forks = originMetadata.getForks();
            forks.add( name );
            this.storage.write( buildPath( origin ), new GitMetadataImpl( origin, originMetadata.getOrigin(), forks ) );
        }

        this.storage.write( buildPath( name ), newRepositoryMetadata );

    }

    @Override
    public Optional<GitMetadata> read( String name ) {
        try {
            final GitMetadataImpl metadata = this.storage.read( buildPath( name ) );
            return Optional.ofNullable( metadata );
        } catch ( RuntimeException e ) {
            return Optional.empty();
        }
    }

    @Override
    public void delete( String name ) {
        String path = buildPath( name );
        Optional<GitMetadata> optionalMetadata = this.read( name );

        optionalMetadata.ifPresent( metadata -> {
            this.removeForkFromOrigin( metadata );
            this.removeOriginFromForks( metadata );
            this.storage.delete( path );
        } );

    }

    private void removeOriginFromForks( final GitMetadata metadata ) {
        List<GitMetadata> forks = this.getForks( metadata );
        forks.forEach( fork -> {
            GitMetadata newForkImpl = new GitMetadataImpl( fork.getName(), "", fork.getForks() );
            this.storage.write( buildPath( fork.getName() ), newForkImpl );
        } );
    }

    private void removeForkFromOrigin( final GitMetadata metadata ) {
        this.getOrigin( metadata ).ifPresent( origin -> {
            if ( origin.getForks().contains( metadata.getName() ) ) {
                List<String> forks = origin.getForks();
                forks.remove( metadata.getName() );
                GitMetadataImpl newOrigin = new GitMetadataImpl( origin.getName(), origin.getOrigin(), forks );
                this.storage.write( buildPath( origin.getName() ), newOrigin );
            }
        } );
    }

    private Optional<GitMetadata> getOrigin( final GitMetadata metadata ) {
        return this.read( metadata.getOrigin() );
    }

    private List<GitMetadata> getForks( final GitMetadata metadata ) {
        return metadata.getForks().stream().map( path -> this.read( path ).get() ).collect( Collectors.toList() );
    }

    private boolean isStorableOrigin( final String origin ) {
        return origin != null && origin.matches( "(^\\w+\\/\\w+$)" );
    }

    private String buildPath( String name ) {
        String path = SEPARATOR + name;
        if ( name.indexOf( SEPARATOR ) == 0 ) {
            path = name;
        }
        if ( path.lastIndexOf( SEPARATOR ) == path.length() - 1 ) {
            path = path.substring( 0, path.length() );
        }
        return path + ".metadata";
    }

}
