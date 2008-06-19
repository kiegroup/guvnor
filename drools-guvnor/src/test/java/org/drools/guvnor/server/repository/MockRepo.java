/**
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.repository;

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