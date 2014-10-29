/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.m2repo.backend.server.helpers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.model.HTMLFileManagerFields;
import org.kie.api.builder.ReleaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpPostHelper {

    private static final Logger log = LoggerFactory.getLogger( HttpPostHelper.class );

    private final static String NO_VALID_POM = "NO VALID POM";

    private final static String NO_SCRIPT_DATA = "NO-SCRIPT-DATA";

    private final static String OK = "OK";

    @Inject
    private ExtendedM2RepoService m2RepoService;

    /**
     * Posting accepts content of various types -
     * may be an attachment for an asset, or perhaps a repository import to process.
     */
    public void handle( final HttpServletRequest request,
                        final HttpServletResponse response ) throws ServletException, IOException {

        response.setContentType( "text/html" );
        FormData uploadItem = getFormData( request );

        if ( uploadItem.getFile() != null ) {
            response.getWriter().write( processUpload( uploadItem ) );
            return;
        }

        response.getWriter().write( NO_SCRIPT_DATA );
    }

    private String processUpload( final FormData uploadItem ) throws IOException {

        // If the file it doesn't exist.
        if ( "".equals( uploadItem.getFile().getName() ) ) {
            throw new IOException( "No file selected." );
        }

        String processResult = uploadFile( uploadItem );
        uploadItem.getFile().getInputStream().close();

        return processResult;
    }

    @SuppressWarnings("rawtypes")
    private FormData getFormData( final HttpServletRequest request ) throws IOException {
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
            log.error( e.getMessage(),
                       e );
        }

        return null;
    }

    private String uploadFile( final FormData uploadItem ) throws IOException {
        InputStream fileData = uploadItem.getFile().getInputStream();
        GAV gav = uploadItem.getGav();

        try {
            if ( gav == null ) {
                if ( !fileData.markSupported() ) {
                    fileData = new BufferedInputStream( fileData );
                }

                // is available() safe?
                fileData.mark( fileData.available() );

                PomModel pomModel = PomModelResolver.resolve(fileData);

                //If we were able to get a POM model we can get the GAV
                if ( pomModel != null ) {
                    String groupId = pomModel.getReleaseId().getGroupId();
                    String artifactId = pomModel.getReleaseId().getArtifactId();
                    String version = pomModel.getReleaseId().getVersion();

                    if ( isNullOrEmpty( groupId ) || isNullOrEmpty( artifactId ) || isNullOrEmpty( version ) ) {
                        return NO_VALID_POM;
                    } else {
                        gav = new GAV( groupId,
                                       artifactId,
                                       version );
                    }

                } else {
                    return NO_VALID_POM;
                }
                fileData.reset();
            }

            m2RepoService.deployJar( fileData, gav );
            uploadItem.getFile().getInputStream().close();

            return OK;

        } catch ( IOException ioe ) {
            log.error( ioe.getMessage(),
                       ioe );
            throw ExceptionUtilities.handleException( ioe );
        }
    }

    private boolean isNullOrEmpty( String groupId ) {
        return groupId == null || groupId.isEmpty();
    }

}
