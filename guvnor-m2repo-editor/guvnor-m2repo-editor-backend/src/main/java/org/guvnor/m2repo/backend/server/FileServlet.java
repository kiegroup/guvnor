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

package org.guvnor.m2repo.backend.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.drools.compiler.kproject.xml.PomModel;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.model.HTMLFileManagerFields;

/**
 * This is for dealing with assets that have an attachment (ie assets that are really an attachment).
 */
//TODO: Basic authentication
public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = 510l;

    @Inject
    private ExtendedM2RepoService m2RepoService;

    @Inject
    private GuvnorM2Repository repository;
    private final static String NO_VALID_POM = "NO VALID POM";

    /**
     * Posting accepts content of various types -
     * may be an attachement for an asset, or perhaps a repository import to process.
     */
    protected void doPost( HttpServletRequest request,
                           HttpServletResponse response ) throws ServletException,
            IOException {

        response.setContentType( "text/html" );
        FormData uploadItem = getFormData( request );

        if ( uploadItem.getFile() != null ) {
            response.getWriter().write( processUpload( uploadItem ) );
            return;
        }

        response.getWriter().write("NO-SCRIPT-DATA");
    }

    private String processUpload( FormData uploadItem ) throws IOException {

        // If the file it doesn't exist.
        if ( "".equals( uploadItem.getFile().getName() ) ) {
            throw new IOException( "No file selected." );
        }

        String processResult = uploadFile( uploadItem );
        uploadItem.getFile().getInputStream().close();

        return processResult;
    }

    /**
     * Get the form data from the inbound request.
     */
    @SuppressWarnings("rawtypes")
    public static FormData getFormData( HttpServletRequest request ) throws IOException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );
        upload.setHeaderEncoding( "UTF-8" );

        FormData data = new FormData();
        GAV emptyGAV = new GAV();
        try {
            List items = upload.parseRequest( request );
            Iterator it = items.iterator();
            while ( it.hasNext() ) {
                FileItem item = (FileItem) it.next();
                if ( !item.isFormField() ) {
                    data.setFile( item );
                }

                if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.GROUP_ID ) ) {
                    emptyGAV.setGroupId( item.getString() );
                } else if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.ARTIFACT_ID ) ) {
                    emptyGAV.setArtifactId( item.getString() );
                } else if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.VERSION_ID ) ) {
                    emptyGAV.setVersion( item.getString() );
                }
            }

            if ( emptyGAV.getArtifactId() == null
                    || "".equals( emptyGAV.getArtifactId() )
                    || emptyGAV.getArtifactId() == null
                    || "".equals( emptyGAV.getArtifactId() )
                    || emptyGAV.getVersion() == null
                    || "".equals( emptyGAV.getVersion() ) ) {
                data.setGav( null );
            } else {
                data.setGav( emptyGAV );
            }

            return data;
        } catch ( FileUploadException e ) {
            //TODO
            //throw new RulesRepositoryException( e );
        }

        return null;
    }

    public String uploadFile( FormData uploadItem ) throws IOException {
        InputStream fileData = uploadItem.getFile().getInputStream();
        GAV gav = uploadItem.getGav();

        try {
            if ( gav == null ) {
                if ( !fileData.markSupported() ) {
                    fileData = new BufferedInputStream( fileData );
                }

                fileData.mark( fileData.available() ); // is available() safe?

                String pomContent = GuvnorM2Repository.loadPOMFromJar(fileData);
                PomModel pomModel = null;
                if ( pomContent != null ) pomModel = PomModel.Parser.parse("pom.xml", new StringInputStream(pomContent));

                if ( pomModel != null ) {

                    String groupId = pomModel.getReleaseId().getGroupId();
                    String artifactId = pomModel.getReleaseId().getArtifactId();
                    String version = pomModel.getReleaseId().getVersion();

                    if (isNullOrEmpty(groupId) || isNullOrEmpty(artifactId) || isNullOrEmpty(version)) {
                        return NO_VALID_POM;
                    } else {
                        gav = new GAV(groupId, artifactId, version);
                    }

                } else {
                    return NO_VALID_POM;
                }
            }

            fileData.reset();
            m2RepoService.deployJar( fileData, gav );
            uploadItem.getFile().getInputStream().close();

            return "OK";
        } catch ( IOException ioe ) {
            throw ExceptionUtilities.handleException( ioe );
        }
    }

    private boolean isNullOrEmpty(String groupId) {
        return groupId == null || groupId.isEmpty();
    }

    /**
     * doGet acting like a dispatcher.
     */
    protected void doGet( HttpServletRequest req,
                          HttpServletResponse res ) throws ServletException,
            IOException {

        String path = req.getParameter( HTMLFileManagerFields.FORM_FIELD_PATH );

        if ( path != null ) {
            processAttachmentDownload( path,
                                       res );
        } else {
            res.sendError( HttpServletResponse.SC_BAD_REQUEST );
        }
    }

    protected void processAttachmentDownload( String path,
                                              HttpServletResponse response ) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOUtils.copy( m2RepoService.loadJar( path ), output );

        String fileName = m2RepoService.getJarName( path );

        response.setContentType( "application/x-download" );
        response.setHeader( "Content-Disposition",
                            "attachment; filename=" + fileName + ";" );
        response.setContentLength( output.size() );
        response.getOutputStream().write( output.toByteArray() );
        response.getOutputStream().flush();
    }

}
