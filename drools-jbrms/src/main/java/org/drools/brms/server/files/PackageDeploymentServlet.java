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
 * This servlet deals with providing packages in binary form.
 *   
 * @author Michael Neale
 */
public class PackageDeploymentServlet extends RepositoryServlet {

    private static final long serialVersionUID = 3909768997932550498L;
    

    /**
     * post makes no sense in this context.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {

        response.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED, "This servlet only provides packages, they can " +
                "not be updated via a POST." );
    }

    /**
     * Get the binary package.
     * This will get the compiled package stuff from either the latest package, 
     * or a snapshot.
     * 
     * The end of the URI is of the form:
     * /<packageName>/(<snapshotVersionName> | LATEST)
     * 
     * if you pass in "LATEST" it will get the latest (not a snapshot) if it exists.
     * Normally that will only be used when downloading on demand, otherwise you should ONLY
     * use a snapshot as they are always "up to date".
     */
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse response) throws ServletException,
                                                 IOException {
        PackageDeploymentURIHelper helper = new PackageDeploymentURIHelper(req.getRequestURI());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String fileName = uploadHelper.loadBinaryPackage( helper.getPackageName(), 
                                        helper.getVersion(), helper.isLatest(), out, getRepository() );
        response.setContentType( "application/x-download" );
        response.setHeader( "Content-Disposition",
                       "attachment; filename=" + fileName + ";");
        response.setContentLength( out.size() );
        response.getOutputStream().write( out.toByteArray() );
        response.getOutputStream().flush();        
        
    }


    
}
