/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.files;

import org.apache.commons.fileupload.FileItem;
import org.drools.RuntimeDroolsException;
import org.drools.guvnor.server.util.FormData;
import org.drools.guvnor.server.util.LoggingHelper;

import javax.inject.Inject;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * This servlet deals with import and export of the repository to XML/zip files.
 */
public class RepositoryBackupServlet extends RepositoryServlet {

    private static final LoggingHelper log = LoggingHelper.getLogger(RepositoryBackupServlet.class);
    private static final long serialVersionUID = 510l;

    private static final List<String> zipMimeTypes = new ArrayList<String>();

    @Inject
    private FileManagerService fileManagerService;

    static {
        zipMimeTypes.add("application/zip");
        zipMimeTypes.add("application/x-compress");
        zipMimeTypes.add("application/x-compressed");
        zipMimeTypes.add("application/x-zip");
        zipMimeTypes.add("application/x-zip-compressed");
        zipMimeTypes.add("application/zip-compressed");
        zipMimeTypes.add("application/x-7zip-compressed");

        //Firefox maps .zip file extensions to this in /mimeTypes.rdf
        zipMimeTypes.add("application/x-sdlc");
    }

    /**
     * This accepts a repository, and will apply it.
     */
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException,
            IOException {

        doAuthorizedAction(request,
                response,
                new Command() {
                    public void execute() throws Exception {

                        String repoConfig = request.getParameter("repoConfig");

                        if (repoConfig != null) {
                            processExportRepoConfig(response,
                                    repoConfig);
                        } else {
                            response.setContentType("text/html");
                            FormData uploadItem = FileManagerService.getFormData(request);

                            String packageImport = request.getParameter("packageImport");

                            InputStream is = uploadItem.getFile().getInputStream();
                            if (isFileZipped(uploadItem.getFile())) {
                                ZipInputStream zipInputStream = new ZipInputStream(is);
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                if (zipEntry != null) {
                                    is = zipInputStream;
                                } else {
                                    new RuntimeDroolsException("Invalid compressed reporitory");
                                }
                            }

                            if ("true".equals(packageImport)) {
                                boolean importAsNew = "true".equals(request.getParameter("importAsNew"));
                                response.getWriter().write(processImportPackage(is,
                                        importAsNew));
                            } else {
                                response.getWriter().write(processImportRepository(is));
                            }
                            is.close();

                        }
                    }
                });
    }

    private boolean isFileZipped(FileItem file) throws IOException {
        String mimeType = file.getContentType().toLowerCase();
        return zipMimeTypes.contains(mimeType);
    }

    /**
     * Explore the repo, provide a download
     */
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse res)
            throws ServletException,
            IOException {

        doAuthorizedAction(req,
                res,
                new Command() {
                    public void execute() throws Exception {

                        try {
                            String packageName = req.getParameter("packageName");

                            if (packageName == null) {
                                processExportRepositoryDownload(res);
                            } else {
                                if(fileManagerService.isPackageExist(packageName)) {
                                    processExportPackageFromRepositoryDownload(res,
                                        packageName);
                                } else {
                                    res.setContentType("text/plain");
                                    res.setStatus(500);
                                    res.getWriter().write("Package [" + packageName + "] does not exist");
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace(new PrintWriter(res.getOutputStream()));
                        }
                    }
                });
    }

    private void processExportRepoConfig(HttpServletResponse res,
                                         String repoConfig)
            throws IOException {
        log.debug("Exporting Repository Config...");
        res.setContentType("application/x-download");
        res.setHeader("Content-Disposition",
                "attachment; filename=repository.xml;");
        log.debug("Starting to process repository configuration");
        res.getOutputStream().write(repoConfig.getBytes());
        res.getOutputStream().flush();
        log.debug("Done exporting repository config!");
    }

    private void processExportRepositoryDownload(HttpServletResponse res)
            throws PathNotFoundException,
            IOException,
            RepositoryException {
        log.debug("Exporting...");
        res.setContentType("application/zip");
        res.setHeader("Content-Disposition",
                "attachment; filename=repository_export.zip;");

        log.debug("Starting to process export");
        ZipOutputStream zout = new ZipOutputStream(res.getOutputStream());
        zout.putNextEntry(new ZipEntry("repository_export.xml"));
        fileManagerService.exportRulesRepository(zout);
        zout.closeEntry();
        zout.finish();
        res.getOutputStream().flush();
        log.debug("Done exporting!");
    }

    private void processExportPackageFromRepositoryDownload(
            HttpServletResponse res,
            String packageName)
            throws PathNotFoundException,
            IOException,
            RepositoryException {
        res.setContentType("application/zip");
        res.setHeader("Content-Disposition",
                "inline; filename=" + packageName
                        + ".zip;");

        res.getOutputStream().write(
                fileManagerService.exportPackageFromRepository(packageName));
        res.getOutputStream().flush();
    }

    private String processImportRepository(InputStream file) throws IOException {
        fileManagerService.importRulesRepository(file);
        return "OK";
    }

    private String processImportPackage(InputStream file,
                                        boolean importAsNew)
            throws IOException {
        byte[] byteArray = new byte[file.available()];
        file.read(byteArray);
        fileManagerService.importPackageToRepository(byteArray,
                importAsNew);
        return "OK";
    }

}
