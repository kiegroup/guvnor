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

package org.kie.guvnor.services.backend;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.kie.commons.java.nio.file.FileSystems;
import org.kie.guvnor.services.repositories.Repository;
import org.kie.guvnor.services.repositories.RepositoryService;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;
import org.uberfire.backend.vfs.impl.FileSystemImpl;
import org.uberfire.backend.vfs.impl.PathImpl;

import static java.util.Arrays.*;

@Singleton
public class AppSetup {

    private ActiveFileSystems fileSystems = new ActiveFileSystemsImpl();

    @Inject
    private RepositoryService repositoryService;

    @PostConstruct
    public void onStartup() {

        final Collection<Repository> repositories = repositoryService.getRepositories();

        for ( Repository repository : repositories ) {
            if ( repository.isValid() ) {
                final String alias = repository.getAlias();
                final String scheme = repository.getScheme();
                final boolean bootstrap = repository.getBootstrap();

                final URI fsURI = URI.create( scheme + "://" + alias );

                final Map<String, Object> env = new HashMap<String, Object>();
                for ( Map.Entry<String, String> e : repository.getEnvironment().entrySet() ) {
                    env.put( e.getKey(),
                             e.getValue() );
                }

                try {
                    FileSystems.newFileSystem( fsURI,
                                               env );
                } catch ( FileSystemAlreadyExistsException ex ) {
                }

                if ( bootstrap ) {
                    final Path root = new PathImpl( alias, "default://" + alias );
                    fileSystems.addBootstrapFileSystem( new FileSystemImpl( asList( root ) ) );
                }
            }
        }

    }

    @Produces
    @Named("fs")
    public ActiveFileSystems fileSystems() {
        return fileSystems;
    }

}
