package org.drools.guvnor.server.files;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.server.security.AdminType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.FormData;
import org.drools.guvnor.server.util.LoggingHelper;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.apache.log4j.Logger;

/**
 * 
 * This servlet deals with import and export of the repository to XML/zip files.
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class RepositoryBackupServlet extends RepositoryServlet {

    private static final Logger log                               = LoggingHelper.getLogger(RepositoryBackupServlet.class);
	private static final long serialVersionUID = 400L;

	// final FileManagerUtils uploadHelper = new FileManagerUtils();

	/**
	 * This accepts a repository, and will apply it.
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (Contexts.isSessionContextActive()) {
			Identity.instance().checkPermission(new AdminType(),
					RoleTypes.ADMIN);
		}

		response.setContentType("text/html");
		FormData uploadItem = FileManagerUtils.getFormData(request);

		String packageImport = request.getParameter("packageImport");

		if ("true".equals(packageImport)) {
			boolean importAsNew = "true".equals(request
					.getParameter("importAsNew"));

			response.getWriter().write(
					processImportPackage(uploadItem.getFile().getInputStream(),
							importAsNew));
		} else {
			response.getWriter().write(
					processImportRepository(uploadItem.getFile()
							.getInputStream()));
		}
	}

	/**
	 * Explore the repo, provide a download
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		if (Contexts.isSessionContextActive()) {
			Identity.instance().checkPermission(new AdminType(),
					RoleTypes.ADMIN);
		}

		try {
			String packageName = req.getParameter("packageName");

			if (packageName == null) {
				processExportRepositoryDownload(res);
			} else {
				processExportPackageFromRepositoryDownload(res, packageName);
			}

		} catch (Exception e) {
			e.printStackTrace(new PrintWriter(res.getOutputStream()));
		}
	}

	private void processExportRepositoryDownload(HttpServletResponse res)
			throws PathNotFoundException, IOException, RepositoryException {
        log.debug("Exporting...");
		res.setContentType("application/zip");
		res.setHeader("Content-Disposition",
				"attachment; filename=repository_export.zip;");

        log.debug("Starting to process export");
        ZipOutputStream zout = new ZipOutputStream(res.getOutputStream());
        zout.putNextEntry(new ZipEntry("repository_export.xml"));
        getFileManager().exportRulesRepository(zout);
        zout.closeEntry();
        zout.finish();
		res.getOutputStream().flush();
        log.debug("Done exporting!");
	}

	private void processExportPackageFromRepositoryDownload(
			HttpServletResponse res, String packageName)
			throws PathNotFoundException, IOException, RepositoryException {
		res.setContentType("application/zip");
		res.setHeader("Content-Disposition", "inline; filename=" + packageName
				+ ".zip;");

		res.getOutputStream().write(
				getFileManager().exportPackageFromRepository(packageName));
		res.getOutputStream().flush();
	}

	private String processImportRepository(InputStream file) throws IOException {
		getFileManager().importRulesRepository(file);
		return "OK";
	}

	private String processImportPackage(InputStream file, boolean importAsNew)
			throws IOException {
		byte[] byteArray = new byte[file.available()];
		file.read(byteArray);
		getFileManager().importPackageToRepository(byteArray, importAsNew);
		return "OK";
	}

}