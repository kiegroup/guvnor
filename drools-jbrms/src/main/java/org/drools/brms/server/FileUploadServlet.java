package org.drools.brms.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.drools.brms.client.packages.ModelArchiveFileWidget;
import org.drools.repository.RulesRepositoryException;

/**
 * Files can be uploaded as part of the repo (eg model classes, spreadsheets).
 * 
 * @author Michael Neale
 */
public class FileUploadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {
        response.setContentType( "text/plain" );

        FormData uploadItem = getFileItem( request );
        if ( uploadItem.file == null || uploadItem.uuid == null) {            
            response.getWriter().write( "NO-SCRIPT-DATA" );
            return;
        }

        //TODO: this is where we attach the file to the asset.
        
        response.getWriter().write( "OK" );
    }

    /**
     * This will return the file and the Asset UUID that it is to be attached to.
     */
    private FormData getFileItem(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );
        
        FormData data = new FormData();
        try {
            List items = upload.parseRequest( request );
            Iterator it = items.iterator();
            while ( it.hasNext() ) {
                FileItem item = (FileItem) it.next();
                if (item.isFormField() && item.getFieldName().equals( ModelArchiveFileWidget.FORM_FIELD_UUID )) {
                    data.uuid = item.getString();
                } else if ( !item.isFormField() ) {
                    data.file = item;
                }
            }
            return data;
        } catch ( FileUploadException e ) {
            throw new RulesRepositoryException( e );
        }
        
    }
    
    static class FormData {
        FileItem file;
        String uuid;
    }

}
