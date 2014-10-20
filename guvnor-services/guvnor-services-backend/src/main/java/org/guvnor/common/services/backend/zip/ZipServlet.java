package org.guvnor.common.services.backend.zip;

import org.guvnor.common.services.shared.file.upload.FileManagerFields;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipServlet
        extends HttpServlet {


    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        final String uri = request.getParameter(FileManagerFields.FORM_FIELD_PATH);

        org.uberfire.java.nio.file.Path path = null;
        try {
            path = ioService.get(new URI(uri));
        } catch (URISyntaxException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (path != null) {
            ByteArrayOutputStream dest = new ByteArrayOutputStream();
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            if (Files.isDirectory(path)) {
                addPath(out, Files.newDirectoryStream(path));
            } else {
                addFile(out, path);
            }

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition",
                    "attachment; filename=download.zip");

            response.setContentLength(dest.size());
            response.getOutputStream().write(dest.toByteArray());
            response.getOutputStream().flush();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void addPath(ZipOutputStream out, DirectoryStream<Path> directoryStream) throws IOException {
        for (Path subPath : directoryStream) {
            if (Files.isDirectory(subPath)) {
                addPath(out, Files.newDirectoryStream(subPath));
            } else {
                addFile(out, subPath);
            }
        }
    }

    private void addFile(ZipOutputStream out, Path subPath) throws IOException {
        final int BUFFER = 2048;
        byte data[] = new byte[BUFFER];

        BufferedInputStream origin = new BufferedInputStream(ioService.newInputStream(subPath), BUFFER);

        ZipEntry entry = new ZipEntry(Paths.convert(subPath).getFileName());
        out.putNextEntry(entry);
        int count;
        while ((count = origin.read(data, 0,
                BUFFER)) != -1) {
            out.write(data, 0, count);
        }

//        IOUtils.copy(origin,out);
        origin.close();
    }

}
