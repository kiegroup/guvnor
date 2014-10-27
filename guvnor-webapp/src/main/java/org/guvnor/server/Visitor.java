package org.guvnor.server;

import org.guvnor.common.services.project.model.Project;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Visitor {

    private Project project;
    private IOService ioService;

    public Visitor(Project project, IOService ioService) {
        this.project = project;
        this.ioService = ioService;
    }

    public void visitPaths(final DirectoryStream<Path> directoryStream) throws IOException {
        for (final org.uberfire.java.nio.file.Path path : directoryStream) {
            if (Files.isDirectory(path)) {
                visitPaths(Files.newDirectoryStream(path));

            } else {
                //Add new resource
                final InputStream is = ioService.newInputStream(path);
                final BufferedInputStream bis = new BufferedInputStream(is);


            }
        }
    }
}
