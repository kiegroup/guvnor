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

package org.kie.guvnor.dtablexls.backend.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
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
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystemNotFoundException;
import org.kie.guvnor.dtablexls.service.DecisionTableXLSService;
import org.kie.guvnor.dtablexls.service.HTMLFileManagerFields;
import org.kie.guvnor.project.model.GAV;
import org.uberfire.backend.server.util.Paths;

/**
 * This is for dealing with assets that have an attachment (ie assets that are really an attachment).
 */
//TODO: Basic authentication
public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = 510l;
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    
    @Inject
    private Paths paths;
    
    @Inject
    private DecisionTableXLSService decisionTableXLSService;
    /**
     * Posting accepts content of various types -
     * may be an attachement for an asset, or perhaps a repository import to process.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {

        response.setContentType( "text/html" );
        FormData uploadItem = getFormData(request);

        
        if ( uploadItem.getFile() != null) {
            response.getWriter().write( processUpload( uploadItem ) );
            return;
        }
        
        response.getWriter().write( "NO-SCRIPT-DATA" );
    }

    private String processUpload(FormData uploadItem) throws IOException {

        // If the file it doesn't exist.
        if ("".equals(uploadItem.getFile().getName())) {
            throw new IOException("No file selected.");
        }

        String processResult = uploadFile(uploadItem);
        uploadItem.getFile().getInputStream().close();
        
        return processResult;
    }
    
    /**
     * Get the form data from the inbound request.
     */
    @SuppressWarnings("rawtypes")
    public static FormData getFormData(HttpServletRequest request) throws IOException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );
        upload.setHeaderEncoding( "UTF-8" );

        FormData data = new FormData();
        try {
            List items = upload.parseRequest( request );
            Iterator it = items.iterator();
            while ( it.hasNext() ) {
                FileItem item = (FileItem) it.next();
                if ( !item.isFormField() ) {
                    data.setFile( item );
                }
                if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.FORM_FIELD_PATH ) ) {
                    System.out.println("path:" + item.getString());
                    data.setPath(item.getString());
                } 
            }

            return data;
        } catch ( FileUploadException e ) {
            //TODO
            //throw new RulesRepositoryException( e );
        }
        
        return null;
    }
    
    public String uploadFile(FormData uploadItem) throws IOException {
        InputStream fileData = uploadItem.getFile().getInputStream();
        String fileName = uploadItem.getFile().getName();
        OutputStream os = null;
        try {            
            org.kie.commons.java.nio.file.Path path = ioService.get( new URI(uploadItem.getPath()));
            os = decisionTableXLSService.save(paths.convert(path.resolve( fileName ), false));
            IOUtils.copy(fileData, os);
            return "OK";
        } catch (IOException ioe) {
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            uploadItem.getFile().getInputStream().close();
            if(os != null) {
                os.close();
            }
        }

        return "INTERNAL ERROR";
    }    
  
    /**
     * doGet acting like a dispatcher.
     */
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res) throws ServletException,
                                                 IOException {

        String path = req.getParameter( HTMLFileManagerFields.FORM_FIELD_PATH );

        if ( path != null ) {
            processAttachmentDownload( path,
                                       res );
        } else {
            res.sendError( HttpServletResponse.SC_BAD_REQUEST );
        }
    }

    protected void processAttachmentDownload(String path,
                                             HttpServletResponse response) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOUtils.copy(decisionTableXLSService.load(null), output);
        
        //String fileName = m2RepoService.getJarName(path);
        String fileName = "";
        
        response.setContentType( "application/x-download" );
        response.setHeader( "Content-Disposition",
                            "attachment; filename=" + fileName + ";" );
        response.setContentLength( output.size() );
        response.getOutputStream().write( output.toByteArray() );
        response.getOutputStream().flush();
    }

}
