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

package org.kie.guvnor.backend.server;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.kie.commons.io.IOSearchService;
import org.kie.commons.io.IOService;
import org.kie.commons.io.attribute.DublinCoreView;
import org.kie.commons.java.nio.base.version.VersionAttributeView;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.kie.guvnor.services.backend.metadata.attribute.OtherMetaView;
import org.kie.guvnor.services.config.AppConfigService;
import org.kie.guvnor.services.repositories.Repository;
import org.kie.guvnor.services.repositories.RepositoryService;
import org.kie.kieora.backend.lucene.LuceneIndexEngine;
import org.kie.kieora.backend.lucene.LuceneSearchIndex;
import org.kie.kieora.backend.lucene.LuceneSetup;
import org.kie.kieora.backend.lucene.fields.SimpleFieldFactory;
import org.kie.kieora.backend.lucene.metamodels.InMemoryMetaModelStore;
import org.kie.kieora.backend.lucene.setups.NIOLuceneSetup;
import org.kie.kieora.engine.MetaIndexEngine;
import org.kie.kieora.engine.MetaModelStore;
import org.kie.kieora.io.IOSearchIndex;
import org.kie.kieora.io.IOServiceIndexedImpl;
import org.kie.kieora.search.SearchIndex;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;

import static org.kie.commons.io.FileSystemType.Bootstrap.*;

@Singleton
public class AppSetup {

    private final ActiveFileSystems fileSystems = new ActiveFileSystemsImpl();

    private final IOService         ioService;
    private final IOSearchService   ioSearchService;
    private final RepositoryService repositoryService;
    private final AppConfigService  appConfigService;
    private final LuceneSetup       luceneSetup;

    @Inject
    public AppSetup( final RepositoryService repositoryService,
                     final AppConfigService appConfigService ) {
        this.repositoryService = repositoryService;
        this.appConfigService = appConfigService;

        this.luceneSetup = new NIOLuceneSetup();
        final MetaModelStore metaModelStore = new InMemoryMetaModelStore();
        final MetaIndexEngine indexEngine = new LuceneIndexEngine( metaModelStore, luceneSetup, new SimpleFieldFactory() );
        final SearchIndex searchIndex = new LuceneSearchIndex( luceneSetup );
        this.ioService = new IOServiceIndexedImpl( indexEngine, DublinCoreView.class, VersionAttributeView.class, OtherMetaView.class );
        this.ioSearchService = new IOSearchIndex( searchIndex, this.ioService );
    }

    @PreDestroy
    private void cleanup() {
        luceneSetup.dispose();
    }

    @PostConstruct
    public void onStartup() {
        final Collection<Repository> repositories = repositoryService.getRepositories();

        for ( final Repository repository : repositories ) {
            if ( repository.isValid() ) {
                final String alias = repository.getAlias();
                final String scheme = repository.getScheme();
                final boolean bootstrap = repository.getBootstrap();

                final URI fsURI = URI.create( scheme + "://" + alias );

                final Map<String, Object> env = new HashMap<String, Object>();
                for ( Map.Entry<String, String> e : repository.getEnvironment().entrySet() ) {
                    env.put( e.getKey(), e.getValue() );
                }

                FileSystem fs = null;

                try {
                    if ( bootstrap ) {
                        fs = ioService.newFileSystem( fsURI, env, BOOTSTRAP_INSTANCE );
                    } else {
                        fs = ioService.newFileSystem( fsURI, env );
                    }

                } catch ( FileSystemAlreadyExistsException ex ) {
                    fs = ioService.getFileSystem( fsURI );
                }

                if ( bootstrap ) {
                    fileSystems.addBootstrapFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
                        put( "default://" + alias, alias );
                    }}, fs.supportedFileAttributeViews() ) );

                } else {
                    fileSystems.addFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
                        put( scheme + "://" + alias, alias );
                    }}, fs.supportedFileAttributeViews() ) );
                }
            }
        }
    }

    @Produces
    @Named("fs")
    public ActiveFileSystems fileSystems() {
        return fileSystems;
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named("ioSearchStrategy")
    public IOSearchService ioSearchService() {
        return ioSearchService;
    }

}
