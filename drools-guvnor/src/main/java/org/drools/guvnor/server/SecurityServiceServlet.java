package org.drools.guvnor.server;

import org.drools.guvnor.client.rpc.SecurityService;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.server.security.SecurityServiceImpl;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Wrapper for GWT RPC.
 * @author michaelneale
 *
 */
public class SecurityServiceServlet extends RemoteServiceServlet implements
		SecurityService {

	SecurityService service = new SecurityServiceImpl();

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
