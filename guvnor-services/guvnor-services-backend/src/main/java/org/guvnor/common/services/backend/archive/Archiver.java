package org.guvnor.common.services.backend.archive;

import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archiver {

    private Path originalPath;
    private ZipOutputStream outputStream;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;


    public void archive(ByteArrayOutputStream outputStream,
                        String uri) throws IOException, URISyntaxException {

        init(outputStream, uri);
        zip();
    }

    private void zip() throws IOException {
        if (Files.isDirectory(originalPath)) {
            addPath(Files.newDirectoryStream(originalPath));
        } else {
            addFile(originalPath);
        }
        outputStream.close();
    }

    private void init(ByteArrayOutputStream outputStream, String uri) throws URISyntaxException {
        this.originalPath = ioService.get(new URI(uri));
        this.outputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));
    }

    private void addPath(DirectoryStream<Path> directoryStream) throws IOException {
        for (Path subPath : directoryStream) {
            if (Files.isDirectory(subPath)) {
                addPath(Files.newDirectoryStream(subPath));
            } else {
                addFile(subPath);
            }
        }
    }

    private void addFile(Path subPath) throws IOException {
        final int BUFFER = 2048;
        byte data[] = new byte[BUFFER];

        BufferedInputStream origin = new BufferedInputStream(ioService.newInputStream(subPath), BUFFER);

        outputStream.putNextEntry(getZipEntry(subPath));
        int count;
        while ((count = origin.read(data, 0, BUFFER)) != -1) {
            outputStream.write(data, 0, count);
        }

        outputStream.flush();
        origin.close();
    }

    private ZipEntry getZipEntry(Path subPath) {
        String fileName = subPath.toUri().getPath().substring(originalPath.toUri().getPath().length() + 1);
        return new ZipEntry(fileName);
    }
}
