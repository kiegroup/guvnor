package org.drools.brms.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.brms.client.admin.BackupManager;
import org.drools.brms.client.packages.ModelAttachmentFileWidget;
import org.drools.brms.server.util.FileUploadHelper;
import org.drools.brms.server.util.FormData;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
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
    final FileUploadHelper uploadHelper = new FileUploadHelper();
    

    /**
     * The post accepts files for attachment to an asset.
     */

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {
        response.setContentType( "text/plain" );
        FormData uploadItem = new FileUploadHelper().getFileItem( request );

        if ( uploadItem.getFile().getFieldName().equals( "uploadFormElement" ) ) {
            processUploadRepository( uploadItem.getFile().getInputStream() );
            return;
        } else if ( uploadItem.getFile() != null && uploadItem.getUuid() != null ) {
            response.getWriter().write( processUploadFileToAsset(uploadItem) );
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

        String uuid = (String) req.getParameter( ModelAttachmentFileWidget.FORM_FIELD_UUID );
        String repo = (String) req.getParameter( BackupManager.FORM_FIELD_REPOSITORY );

        if ( uuid != null ) {
            processFileDownload( uuid, res );
        } else if ( repo != null ) {
            try {
                processXmlFileDownload(res);
            } catch ( Exception e ) {
                e.printStackTrace( new PrintWriter( res.getOutputStream() ) );
            }
        } else {
            res.sendError( HttpServletResponse.SC_BAD_REQUEST );
            return;
        }
    }
    
    private void processXmlFileDownload(HttpServletResponse res) throws PathNotFoundException, IOException, RepositoryException {
        res.setContentType( "application/zip" );
        res.setHeader( "Content-Disposition",
                       "inline; filename=repository_export.zip;" );

        res.getOutputStream().write( getRepository().exportRulesRepository() );
        res.getOutputStream().flush();
    }

    private void processFileDownload(String uuid, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String filename = uploadHelper.getFilebyUUID( uuid, output, getRepository() );

        
        response.setContentType( "application/x-download" );
        response.setHeader( "Content-Disposition",
                       "attachment; filename=" + filename + ");");
        response.setContentLength( output.size() );
        response.getOutputStream().write( output.toByteArray() );
        response.getOutputStream().flush();
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
 
    
    private String processUploadFileToAsset(FormData uploadItem) throws IOException {
        RulesRepository repo = getRepository();

        uploadHelper.attachFile( uploadItem, repo );
        uploadItem.getFile().getInputStream().close();
        
        return "OK";
    }


    private void processUploadRepository(InputStream file) throws IOException {
        byte[] byteArray = new byte[file.available()];
        RulesRepository repo = getRepository();

        file.read( byteArray );
        repo.importRulesRepository( byteArray );
    }
    
}
