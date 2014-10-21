package org.guvnor.common.services.backend.archive;

import org.guvnor.common.services.shared.file.upload.FileManagerFields;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class ArchiveServlet
        extends HttpServlet {


    @Inject
    private Archiver archiver;

    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        final String uri = request.getParameter(FileManagerFields.FORM_FIELD_PATH);

        try {
            if (uri != null) {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                archiver.archive(outputStream, uri);

                response.setContentType("application/zip");
                response.setHeader("Content-Disposition",
                        "attachment; filename=download.zip");

                response.setContentLength(outputStream.size());
                response.getOutputStream().write(outputStream.toByteArray());
                response.getOutputStream().flush();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (URISyntaxException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
