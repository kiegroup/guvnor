package org.drools.brms.server.files;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.brms.client.common.HTMLFileManagerFields;
import org.drools.brms.server.util.FileManagerUtils;
import org.drools.brms.server.util.FormData;
import org.drools.repository.RulesRepository;

/**
 * This is for dealing with assets that have an attachment (ie assets that are really an attachment).
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class AssetFileServlet extends RepositoryServlet {

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

        if ( uploadItem.getFile() != null && uploadItem.getUuid() != null ) {
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
        
        String uuid = (String) req.getParameter( HTMLFileManagerFields.FORM_FIELD_UUID );
        
        if ( uuid != null ) {
            processAttachmentDownload( uuid, res );
        } else {
            res.sendError( HttpServletResponse.SC_BAD_REQUEST );
            return;
        }
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

    
}
