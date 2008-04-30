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



import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.brms.server.util.FormData;
import org.drools.compiler.DroolsParserException;
import org.drools.repository.RulesRepositoryException;

/**
 * This servlet deals with providing packages in binary form.
 *
 * @author Michael Neale
 */
public class PackageDeploymentServlet extends RepositoryServlet {

    private static final long serialVersionUID = 400L;


    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                            IOException {
        if (request.getMethod().equals( "HEAD" )) {
            PackageDeploymentURIHelper helper = new PackageDeploymentURIHelper(request.getRequestURI());
            FileManagerUtils fm = getFileManager();
            long mod = fm.getLastModified( helper.getPackageName(), helper.getVersion() );
            response.addHeader( "lastModified", "" + mod);

        } else {
            super.doHead(request, response);
        }
    }

    /**
     * This is used for importing legacy DRL.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {
    	response.setContentType( "text/html" );
        FormData data = FileManagerUtils.getFormData( request );
        //System.err.println("Filename: " + data.getFile().getName());

        try {
            getFileManager().importClassicDRL( data.getFile().getInputStream() );
            response.getWriter().write( "OK" );
        } catch ( DroolsParserException e ) {
            response.getWriter().write( "Unable to process import: " + e.getMessage() );
        } catch ( RulesRepositoryException e) {
            response.getWriter().write( "Unable to process import: " + e.getMessage() );
        }


    }

    /**
     * Get the binary package.
     * This will get the compiled package stuff from either the latest package,
     * or a snapshot.
     *
     * The end of the URI is of the form:
     * /<packageName>/(<snapshotVersionName> | LATEST)
     *
     * if you pass in "LATEST" it will get the latest (not a snapshot) if it exists.
     * Normally that will only be used when downloading on demand, otherwise you should ONLY
     * use a snapshot as they are always "up to date".
     */
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse response) throws ServletException,
                                                 IOException {
        PackageDeploymentURIHelper helper = new PackageDeploymentURIHelper(req.getRequestURI());

        System.out.println( "PackageName: " + helper.getPackageName() );
        System.out.println( "PackageVersion: " + helper.getVersion() );
        System.out.println( "PackageIsLatest: " + helper.isLatest() );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileManagerUtils fm = getFileManager();
        String fileName = (helper.isSource()) ? fm.loadBinaryPackage( helper.getPackageName(),
                                        helper.getVersion(), helper.isLatest(), out )
                                        :
                                        fm.loadSourcePackage(helper.getPackageName(),
                                        helper.getVersion(), helper.isLatest(), out );
        response.setContentType( "application/x-download" );
        response.setHeader( "Content-Disposition",
                       "attachment; filename=" + fileName + ";");
        response.setContentLength( out.size() );
        response.getOutputStream().write( out.toByteArray() );
        response.getOutputStream().flush();

    }



}