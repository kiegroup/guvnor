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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.guvnor.client.rpc.DetailedSerializableException;
import org.drools.guvnor.server.util.FormData;

/**
 * This is for dealing with assets that have an attachment (ie assets that are really an attachment).
 *
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class AssetFileServlet extends RepositoryServlet {

    private static final long serialVersionUID = 400L;

    /**
     * Posting accepts content of various types -
     * may be an attachement for an asset, or perhaps a repository import to process.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {

        response.setContentType( "text/html" );
        FormData uploadItem = FileManagerUtils.getFormData( request );

        if ( uploadItem.getFile() != null && uploadItem.getUuid() != null ) {
            //attaching to an asset.
            response.getWriter().write( processAttachFileToAsset( uploadItem ) );

            return;
        }
        response.getWriter().write( "NO-SCRIPT-DATA" );

    }

    /**
     * doGet acting like a dispatcher.
     */
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res) throws ServletException,
                                                 IOException {

        String uuid = req.getParameter( HTMLFileManagerFields.FORM_FIELD_UUID );

        if ( uuid != null ) {
            processAttachmentDownload( uuid,
                                       res );
        } else {
            res.sendError( HttpServletResponse.SC_BAD_REQUEST );
        }
    }

    protected void processAttachmentDownload(String uuid,
                                           HttpServletResponse response) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String filename = getFileManager().loadFileAttachmentByUUID( uuid,
                                                                     output );

        response.setContentType( "application/x-download" );
        response.setHeader( "Content-Disposition",
                            "attachment; filename=" + filename + ";" );
        response.setContentLength( output.size() );
        response.getOutputStream().write( output.toByteArray() );
        response.getOutputStream().flush();
    }

    private String processAttachFileToAsset(FormData uploadItem) throws IOException {

        FileManagerUtils manager = getFileManager();

        // If the file it doesn't exist.
        if ( "".equals( uploadItem.getFile().getName() ) ) {
            throw new IOException( "No file selected.");
        }

        manager.attachFile( uploadItem );
        uploadItem.getFile().getInputStream().close();

        return "OK";
    }

}