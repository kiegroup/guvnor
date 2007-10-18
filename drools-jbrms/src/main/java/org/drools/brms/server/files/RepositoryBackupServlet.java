package org.drools.brms.server.files;
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

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.brms.server.util.FormData;
import org.drools.repository.RulesRepository;

/**
 *
 * This servlet deals with import and export of the repository to XML/zip files.
 *
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class RepositoryBackupServlet extends RepositoryServlet {

    private static final long serialVersionUID = 400L;
    //final FileManagerUtils uploadHelper = new FileManagerUtils();


    /**
     * This accepts a repository, and will apply it.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {
        response.setContentType( "text/html" );
        FormData uploadItem = FileManagerUtils.getFormData( request );
        response.getWriter().write(processImportRepository( uploadItem.getFile().getInputStream() ));
    }

    /**
     * Explore the repo, provide a download
     */
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res) throws ServletException,
                                                 IOException {
            try {
                processExportRepositoryDownload(res);
            } catch ( Exception e ) {
                e.printStackTrace( new PrintWriter( res.getOutputStream() ) );
            }
    }

    private void processExportRepositoryDownload(HttpServletResponse res) throws PathNotFoundException, IOException, RepositoryException {
        res.setContentType( "application/zip" );
        res.setHeader( "Content-Disposition",
                       "inline; filename=repository_export.zip;" );

        res.getOutputStream().write( getFileManager().exportRulesRepository() );
        res.getOutputStream().flush();
    }


    private String processImportRepository(InputStream file) throws IOException {
        byte[] byteArray = new byte[file.available()];
        file.read( byteArray );
        getFileManager().importRulesRepository( byteArray );
        return "OK";
    }

}