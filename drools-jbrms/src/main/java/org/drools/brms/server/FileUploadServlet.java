package org.drools.brms.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.drools.brms.client.packages.ModelAttachmentFileWidget;
import org.drools.brms.server.util.RepositoryManager;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;

/**
 * Files can be uploaded as part of the repo (eg model classes, spreadsheets).
 * This servlet supports the uploading and downloading of files 
 * in assets in the repository.
 * 
 * @author Michael Neale
 */
public class FileUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 3909768997932550498L;

    /**
     * The post accepts files for attachment to an asset.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {
        response.setContentType( "text/plain" );

        FormData uploadItem = getFileItem( request );
        if ( uploadItem.file == null || uploadItem.uuid == null) {            
            response.getWriter().write( "NO-SCRIPT-DATA" );
            return;
        }
        
        
        RulesRepository repo = getRepository( request );
        attachFile( uploadItem, repo );
        
        uploadItem.file.getInputStream().close();
        
        response.getWriter().write( "OK" );
    }
    
    /** 
     * The get returns files based on UUID.
     * you can do a request like /fileManager?uuid=..... 
     * (need to know the UUID) and it will return it as a file.
     */
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res) throws ServletException,
                                                  IOException {
        
        String uuid = (String) req.getParameter( ModelAttachmentFileWidget.FORM_FIELD_UUID );
        if (uuid == null) {
            res.sendError( HttpServletResponse.SC_BAD_REQUEST );
            return;
        }
        AssetItem item = getRepository( req ).loadAssetByUUID( uuid );
        
        res.setContentType("application/x-download");
        res.setHeader("Content-Disposition", "attachment; filename=" + item.getBinaryContentAttachmentFileName());
        
        byte[] data = item.getBinaryContentAsBytes();
        res.setContentLength( data.length );
        
        OutputStream out = res.getOutputStream();
        out.write( data );        
        out.flush();
        
    }    

    private RulesRepository getRepository(HttpServletRequest request) {
        RepositoryManager repoMan = new RepositoryManager();
        RulesRepository repo = repoMan.getRepositoryFrom( request.getSession() );
        return repo;
    }

    void attachFile(FormData uploadItem,
                            RulesRepository repo) throws IOException {
        String uuid = uploadItem.uuid;
        InputStream fileData = uploadItem.file.getInputStream();
        String fileName = uploadItem.file.getName();
        
        attachFileToAsset( repo,
                           uuid,
                           fileData,
                           fileName );
    }

    /**
     * This utility method attaches a file to an asset.
     */
    public static void attachFileToAsset(RulesRepository repo,
                                   String uuid,
                                   InputStream fileData,
                                   String fileName) {
        AssetItem item = repo.loadAssetByUUID( uuid );
        item.updateBinaryContentAttachment( fileData );
        item.updateBinaryContentAttachmentFileName( fileName );
        item.checkin( "Attached file: " + fileName );
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
                if (item.isFormField() && item.getFieldName().equals( ModelAttachmentFileWidget.FORM_FIELD_UUID )) {
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
