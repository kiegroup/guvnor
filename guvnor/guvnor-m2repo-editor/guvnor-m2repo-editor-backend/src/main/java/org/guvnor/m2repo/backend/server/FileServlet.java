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
import java.io.StringReader;
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
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.m2repo.model.HTMLFileManagerFields;
import org.kie.guvnor.project.model.GAV;

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

        response.getWriter().write( "NO-SCRIPT-DATA" );
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
                    System.out.println( "GROUP_ID:" + item.getString() );
                    emptyGAV.setGroupId( item.getString() );
                } else if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.ARTIFACT_ID ) ) {
                    System.out.println( "ARTIFACT_ID:" + item.getString() );
                    emptyGAV.setArtifactId( item.getString() );
                } else if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.VERSION_ID ) ) {
                    System.out.println( "VERSION_ID:" + item.getString() );
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
        String fileName = uploadItem.getFile().getName();
        GAV gav = uploadItem.getGav();

        try {
            if ( gav == null ) {
                if ( !fileData.markSupported() ) {
                    fileData = new BufferedInputStream( fileData );
                }

                fileData.mark( fileData.available() ); // is available() safe?
                String pom = GuvnorM2Repository.loadPOMFromJar( fileData );
                fileData.reset();

                if ( pom != null ) {
                    Model model = new MavenXpp3Reader().read( new StringReader( pom ) );

                    String groupId = model.getGroupId();
                    String artifactId = model.getArtifactId();
                    String version = model.getVersion();

                    if ( groupId == null ) {
                        groupId = model.getParent().getGroupId();
                    }
                    if ( version == null ) {
                        version = model.getParent().getVersion();
                    }

                    gav = new GAV( groupId, artifactId, version );
                } else {
                    return "NO VALID POM";
                }
            }

            m2RepoService.deployJar( fileData, gav );
            uploadItem.getFile().getInputStream().close();

            return "OK";
        } catch ( XmlPullParserException e ) {
        } catch ( IOException ioe ) {
        }

        return "INTERNAL ERROR";
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
