package org.guvnor.server;

import org.guvnor.common.services.project.model.Project;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Visitor {

    private Project project;
    private IOService ioService;
    private File rootFolder;

    private static String tempDir = System.getProperty("java.io.tmpdir");

    public Visitor(Project project, IOService ioService) {
        this.project = project;
        this.ioService = ioService;
    }

    public void visit() throws IOException {
        Path path = Paths.convert(project.getRootPath());
        rootFolder = makeTempDirectory(path);
        visitPaths(ioService.newDirectoryStream(path));
    }

    public File getRootFolder() {
        return rootFolder;
    }

    private void visitPaths(final DirectoryStream<Path> directoryStream) throws IOException {
        for (final org.uberfire.java.nio.file.Path path : directoryStream) {
            if (Files.isDirectory(path)) {
                makeTempDirectory(path);
                visitPaths(Files.newDirectoryStream(path));
            } else {
                makeTempFile(path);
            }
        }
    }

    private File makeTempDirectory(Path path) {
        String filePath = getFilePath(path);
        File tempDirectory = new File(filePath);
        tempDirectory.mkdir();
        return tempDirectory;
    }

    private void makeTempFile(Path path) throws IOException {


        final int BUFFER = 2048;
        byte data[] = new byte[BUFFER];

        BufferedInputStream origin = new BufferedInputStream(ioService.newInputStream(path), BUFFER);

        String filePath = getFilePath(path);
        File tempFile = new File(filePath);
        tempFile.createNewFile();

        FileOutputStream output = new FileOutputStream(tempFile);

        int count;
        while ((count = origin.read(data, 0, BUFFER)) != -1) {
            output.write(data, 0, count);
        }

        origin.close();
        output.close();
    }

    private String getFilePath(Path path) {
        String rawPath = path.toUri().getRawPath();
        return tempDir + rawPath;
    }
}
