package org.drools.brms.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.drools.brms.client.admin.BackupManager;
import org.drools.brms.client.packages.ModelAttachmentFileWidget;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

/**
 * Files can be uploaded as part of the repo (eg model classes, spreadsheets).
 * This servlet supports the uploading and downloading of files 
 * in assets in the repository.
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class FileUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 3909768997932550498L;

    /**
     * The post accepts files for attachment to an asset.
     */

    //TODO: must extract bunisess logic form servlet action
    
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {
        response.setContentType( "text/plain" );

        FormData uploadItem = getFileItem( request );
        RulesRepository repo = getRepository();

        if ( uploadItem.file.getFieldName().equals( "uploadFormElement" ) ) {
            processUploadRepository( uploadItem.file.getInputStream() );
            return;
        } else if ( uploadItem.file != null && uploadItem.uuid != null ) {
            attachFile( uploadItem,
                        repo );
            uploadItem.file.getInputStream().close();
            response.getWriter().write( "OK" );
            return;
        }
        response.getWriter().write( "NO-SCRIPT-DATA" );
    }

    protected void processUploadRepository(InputStream file) throws IOException {
        byte[] byteArray = new byte[file.available()];
        RulesRepository repo = getRepository();

        file.read( byteArray );
        repo.importRulesRepository( byteArray );
    }

    /**
     * doGet acting like a dispatcher.
     */

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res) throws ServletException,
                                                 IOException {

        String uuid = (String) req.getParameter( ModelAttachmentFileWidget.FORM_FIELD_UUID );
        String repo = (String) req.getParameter( BackupManager.FORM_FIELD_REPOSITORY );

        if ( uuid != null ) {
            getFilebyUUID( uuid,
                           req,
                           res );
        } else if ( repo != null ) {
            ServletOutputStream out = res.getOutputStream();
            try {

                res.setContentType( "application/zip" );
                res.setHeader( "Content-Disposition",
                               "inline; filename=repository_export.zip;" );

                out.write( getRepository().exportRulesRepository() );
                out.flush();
            } catch ( Exception e ) {
                e.printStackTrace( new PrintWriter( out ) );
            }
        } else {
            res.sendError( HttpServletResponse.SC_BAD_REQUEST );
            return;
        }
    }

    private RulesRepository getRepository() {
        if ( Contexts.isApplicationContextActive() ) {
            return (RulesRepository) Component.getInstance( "repository" );
        } else {
            //MN: NOTE THIS IS MY HACKERY TO GET IT WORKING IN GWT HOSTED MODE.
            //THIS IS ALL THAT IS NEEDED.
            System.out.println( "WARNING: RUNNING IN NON SEAM MODE SINGLE USER MODE - ONLY FOR TESTING AND DEBUGGING !!!!!" );
            ServiceImplementation impl = new ServiceImplementation();

            try {
                return new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) );
            } catch ( Exception e ) {
                throw new IllegalStateException( "Unable to launch debug mode..." );
            }
        }
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
                if ( item.isFormField() && item.getFieldName().equals( ModelAttachmentFileWidget.FORM_FIELD_UUID ) ) {
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
        String   uuid;
    }

    /** 
     * The get returns files based on UUID.
     * you can do a request like /fileManager?uuid=..... 
     * (need to know the UUID) and it will return it as a file.
     */

    private void getFilebyUUID(String uuid,
                               HttpServletRequest req,
                               HttpServletResponse res) throws IOException {

        AssetItem item = getRepository().loadAssetByUUID( uuid );

        res.setContentType( "application/x-download" );
        res.setHeader( "Content-Disposition",
                       "attachment; filename=" + item.getBinaryContentAttachmentFileName() );

        byte[] data = item.getBinaryContentAsBytes();
        res.setContentLength( data.length );

        OutputStream out = res.getOutputStream();
        out.write( data );
        out.flush();
    }
}
