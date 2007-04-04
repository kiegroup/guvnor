/**
 * 
 */
package org.drools.brms.server.repository;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


class MockRepo implements Repository {

    
    public MockSession session;
    
    public String getDescriptor(String arg0) {
        return null;
    }

    public String[] getDescriptorKeys() {
        return null;
    }

    public Session login() throws LoginException,
                          RepositoryException {
        return null;
    }

    public Session login(Credentials arg0) throws LoginException,
                                          RepositoryException {
        session = new MockSession();
        return session;
    }

    public Session login(String arg0) throws LoginException,
                                     NoSuchWorkspaceException,
                                     RepositoryException {
        return null;
    }

    public Session login(Credentials arg0, String arg1) throws LoginException,
                                                       NoSuchWorkspaceException,
                                                       RepositoryException {
        return null;
    }
    
}