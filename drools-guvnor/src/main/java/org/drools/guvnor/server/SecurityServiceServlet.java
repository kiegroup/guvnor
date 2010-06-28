package org.drools.guvnor.server;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.client.rpc.SecurityService;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.server.security.SecurityServiceImpl;
import org.drools.guvnor.server.util.LoggingHelper;
import org.jboss.seam.security.AuthorizationException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Wrapper for GWT RPC.
 * @author michaelneale
 *
 */
public class SecurityServiceServlet extends RemoteServiceServlet implements
		SecurityService {

    private static final LoggingHelper log              = LoggingHelper.getLogger(SecurityServiceServlet.class);
	SecurityService service = new SecurityServiceImpl();

    @Override
    protected void doUnexpectedFailure(Throwable e) {
        if (e.getCause() instanceof AuthorizationException) {
            log.info(e.getMessage(), e);
            HttpServletResponse response = getThreadLocalResponse();
            try {
              response.setContentType("text/plain");
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.getWriter().write(e.getCause().getMessage());
            } catch (IOException ex) {
              getServletContext().log(
                  "respondWithUnexpectedFailure failed while sending the previous failure to the client",
                  ex);
            }
        } else {
            log.error(e.getMessage(), e.getCause());
            super.doUnexpectedFailure(e);
        }
    }


	public UserSecurityContext getCurrentUser() {
		return service.getCurrentUser();
	}

	public Capabilities getUserCapabilities() {
		return service.getUserCapabilities();
	}

	public boolean login(String userName, String password) {
		return service.login(userName, password);
	}

}
