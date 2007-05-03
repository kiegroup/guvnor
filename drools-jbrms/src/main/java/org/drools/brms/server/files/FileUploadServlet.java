package org.drools.brms.server.files;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.brms.client.common.HTMLFileManagerFields;
import org.drools.brms.server.util.FileManagerUtils;
import org.drools.brms.server.util.FormData;
import org.drools.repository.RulesRepository;

/**
 * Files can be uploaded as part of the repo (eg model classes, spreadsheets).
 * This servlet supports the uploading and downloading of files 
 * in assets in the repository.
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class FileUploadServlet extends RepositoryServlet {

    private static final long serialVersionUID = 3909768997932550498L;
    

    /**
     * Posting accepts content of various types - 
     * may be an attachement for an asset, or perhaps a repository import to process.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {
        response.setContentType( "text/plain" );
        FormData uploadItem = new FileManagerUtils().getFormData( request );

        if ( uploadItem.getFile().getFieldName().equals( HTMLFileManagerFields.FILE_UPLOAD_FIELD_NAME_IMPORT ) ) {
            //importing a while repo
            response.getWriter().write(processImportRepository( uploadItem.getFile().getInputStream() ));
            return;
        } else if ( uploadItem.getFile() != null && uploadItem.getUuid() != null ) {
            //attaching to an asset.
            response.getWriter().write( processAttachFileToAsset(uploadItem) );
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

        System.err.println("path info:" + req.getPathInfo());
        System.err.println("servlet path:" + req.getServletPath());
        System.err.println("URI:" + req.getRequestURI());
        System.err.println("URL:" + req.getRequestURL());
        System.err.println("path translated:" + req.getPathTranslated());
        
        String uuid = (String) req.getParameter( HTMLFileManagerFields.FORM_FIELD_UUID );
        String repo = (String) req.getParameter( HTMLFileManagerFields.FORM_FIELD_REPOSITORY );
        
        if ( uuid != null ) {
            processAttachmentDownload( uuid, res );
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

    private void processAttachmentDownload(String uuid, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String filename = uploadHelper.loadFileAttachmentByUUID( uuid, output, getRepository() );

        
        response.setContentType( "application/x-download" );
        response.setHeader( "Content-Disposition",
                       "attachment; filename=" + filename + ";");
        response.setContentLength( output.size() );
        response.getOutputStream().write( output.toByteArray() );
        response.getOutputStream().flush();
    }

    
    private String processAttachFileToAsset(FormData uploadItem) throws IOException {
        RulesRepository repo = getRepository();

        uploadHelper.attachFile( uploadItem, repo );
        uploadItem.getFile().getInputStream().close();
        
        return "OK";
    }


    private String processImportRepository(InputStream file) throws IOException {
        byte[] byteArray = new byte[file.available()];
        RulesRepository repo = getRepository();

        file.read( byteArray );
        repo.importRulesRepository( byteArray );
        return "OK";
    }
    
}
