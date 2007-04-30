package org.drools.brms.server.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.brms.server.util.FileManagerUtils;
import org.drools.brms.server.util.FormData;
import org.drools.repository.RulesRepository;

/**
 * 
 * This servlet deals with import and export of the repository to XML/zip files.
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */
public class RepositoryBackupServlet extends RepositoryServlet {

    private static final long serialVersionUID = 3909768997932550498L;
    final FileManagerUtils uploadHelper = new FileManagerUtils();
    

    /**
     * This accepts a repository, and will apply it.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {
        response.setContentType( "text/plain" );
        FormData uploadItem = new FileManagerUtils().getFormData( request );
        response.getWriter().write(processImportRepository( uploadItem.getFile().getInputStream() ));
    }

    /**
     * Explore the repo, provide a download
     */
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res) throws ServletException,
                                                 IOException {
            try {
                processExportRepositoryDownload(res);
            } catch ( Exception e ) {
                e.printStackTrace( new PrintWriter( res.getOutputStream() ) );
            }
    }
    
    private void processExportRepositoryDownload(HttpServletResponse res) throws PathNotFoundException, IOException, RepositoryException {
        res.setContentType( "application/zip" );
        res.setHeader( "Content-Disposition",
                       "inline; filename=repository_export.zip;" );

        res.getOutputStream().write( getRepository().exportRulesRepository() );
        res.getOutputStream().flush();
    }


    private String processImportRepository(InputStream file) throws IOException {
        byte[] byteArray = new byte[file.available()];
        RulesRepository repo = getRepository();

        file.read( byteArray );
        repo.importRulesRepository( byteArray );
        return "OK";
    }
    
}
