package org.drools.guvnor.server;

import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;
import org.uberfire.backend.vfs.impl.FileSystemImpl;
import org.uberfire.backend.vfs.impl.PathImpl;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystems;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

@Singleton
public class AppSetup {

    private ActiveFileSystems fileSystems = new ActiveFileSystemsImpl();

    @PostConstruct
    public void onStartup() {
        final String gitURL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final String userName = "guvnorngtestuser1";
        final String password = "test1234";
        final URI fsURI = URI.create("jgit:///guvnorng-playground");

        final Map<String, Object> env = new HashMap<String, Object>();
        env.put("username", userName);
        env.put("password", password);
        env.put("giturl", gitURL);

        final FileSystem fs = FileSystems.newFileSystem(fsURI, env);

        final Path root = new PathImpl("guvnorng-playground", "default:///guvnorng-playground");

        fileSystems.addBootstrapFileSystem(new FileSystemImpl(asList(root)));
    }

    @Produces @Named("fs")
    public ActiveFileSystems fileSystems() {
        return fileSystems;
    }

}
