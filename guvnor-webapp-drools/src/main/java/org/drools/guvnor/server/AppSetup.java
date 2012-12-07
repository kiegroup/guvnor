package org.drools.guvnor.server;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;
import org.uberfire.backend.vfs.impl.FileSystemImpl;

import static java.util.Arrays.*;
import static org.kie.commons.io.FileSystemType.Bootstrap.*;
import static org.uberfire.backend.vfs.PathFactory.*;

@Singleton
public class AppSetup {

    private final IOService ioService = new IOServiceDotFileImpl();
    private final ActiveFileSystems activeFileSystems = new ActiveFileSystemsImpl();

    @PostConstruct
    public void onStartup() {
        final String gitURL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final String userName = "guvnorngtestuser1";
        final String password = "test1234";
        final URI fsURI = URI.create( "git://uf-playground" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( "username", userName );
            put( "password", password );
            put( "origin", gitURL );
        }};

        try {
            ioService.newFileSystem( fsURI, env, BOOTSTRAP_INSTANCE );
        } catch ( FileSystemAlreadyExistsException ex ) {
        }

        final Path root = newPath( "uf-playground", "default://uf-playground" );
        activeFileSystems.addBootstrapFileSystem( new FileSystemImpl( asList( root ) ) );
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named("fs")
    public ActiveFileSystems fileSystems() {
        return activeFileSystems;
    }

}
