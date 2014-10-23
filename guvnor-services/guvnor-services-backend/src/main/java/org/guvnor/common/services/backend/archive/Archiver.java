/*
 * Copyright 2014 JBoss Inc
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


    private IOService ioService;

    public Archiver() {
    }

    @Inject
    public Archiver(@Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

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
        return new ZipEntry(FileNameResolver.resolve(subPath.toUri().getPath(), originalPath.toUri().getPath()));
    }

    static class FileNameResolver {
        static protected String resolve(String subPath, String originalPath) {
            if ("/".equals(originalPath)) {
                return subPath.substring(originalPath.length());
            } else {
                return getBaseFolder(originalPath) + subPath.substring(originalPath.length() + 1);
            }
        }

        private static String getBaseFolder(String originalPath) {
            if (originalPath.contains("/")) {
                return originalPath.substring(originalPath.lastIndexOf("/") + 1) + "/";
            } else {
                return originalPath + "/";
            }
        }
    }
}
