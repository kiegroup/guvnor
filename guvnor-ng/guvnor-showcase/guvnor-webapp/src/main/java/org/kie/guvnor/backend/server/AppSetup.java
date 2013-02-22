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

import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.kie.guvnor.services.repositories.Repository;
import org.kie.guvnor.services.repositories.RepositoryService;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.kie.commons.io.FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE;

@Singleton
public class AppSetup {

    private final IOService         ioService   = new IOServiceDotFileImpl();
    private final ActiveFileSystems fileSystems = new ActiveFileSystemsImpl();

    @Inject
    private RepositoryService repositoryService;

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

}
