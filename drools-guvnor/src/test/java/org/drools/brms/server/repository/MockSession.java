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

package org.drools.brms.server.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;

import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

class MockSession implements Session {

    public boolean loggedout;

    public void addLockToken(String arg0) {
         
        
    }

    public void checkPermission(String arg0, String arg1) throws AccessControlException,
                                                         RepositoryException {
         
        
    }

    public void exportDocumentView(String arg0, ContentHandler arg1, boolean arg2, boolean arg3) throws PathNotFoundException,
                                                                                                SAXException,
                                                                                                RepositoryException {
         
        
    }

    public void exportDocumentView(String arg0, OutputStream arg1, boolean arg2, boolean arg3) throws IOException,
                                                                                              PathNotFoundException,
                                                                                              RepositoryException {
         
        
    }

    public void exportSystemView(String arg0, ContentHandler arg1, boolean arg2, boolean arg3) throws PathNotFoundException,
                                                                                              SAXException,
                                                                                              RepositoryException {
         
        
    }

    public void exportSystemView(String arg0, OutputStream arg1, boolean arg2, boolean arg3) throws IOException,
                                                                                            PathNotFoundException,
                                                                                            RepositoryException {
         
        
    }

    public Object getAttribute(String arg0) {
         
        return null;
    }

    public String[] getAttributeNames() {
         
        return null;
    }

    public ContentHandler getImportContentHandler(String arg0, int arg1) throws PathNotFoundException,
                                                                        ConstraintViolationException,
                                                                        VersionException,
                                                                        LockException,
                                                                        RepositoryException {
         
        return null;
    }

    public Item getItem(String arg0) throws PathNotFoundException,
                                    RepositoryException {
         
        return null;
    }

    public String[] getLockTokens() {
         
        return null;
    }

    public String getNamespacePrefix(String arg0) throws NamespaceException,
                                                 RepositoryException {
         
        return null;
    }

    public String[] getNamespacePrefixes() throws RepositoryException {
         
        return null;
    }

    public String getNamespaceURI(String arg0) throws NamespaceException,
                                              RepositoryException {
         
        return null;
    }

    public Node getNodeByUUID(String arg0) throws ItemNotFoundException,
                                          RepositoryException {
         
        return null;
    }

    public Repository getRepository() {
         
        return null;
    }

    public Node getRootNode() throws RepositoryException {
         
        return null;
    }

    public String getUserID() {
         
        return null;
    }

    public ValueFactory getValueFactory() throws UnsupportedRepositoryOperationException,
                                         RepositoryException {
         
        return null;
    }

    public Workspace getWorkspace() {
         
        return null;
    }

    public boolean hasPendingChanges() throws RepositoryException {
         
        return false;
    }

    public Session impersonate(Credentials arg0) throws LoginException,
                                                RepositoryException {
         
        return null;
    }

    public void importXML(String arg0, InputStream arg1, int arg2) throws IOException,
                                                                  PathNotFoundException,
                                                                  ItemExistsException,
                                                                  ConstraintViolationException,
                                                                  VersionException,
                                                                  InvalidSerializedDataException,
                                                                  LockException,
                                                                  RepositoryException {
         
        
    }

    public boolean isLive() {
         
        return false;
    }

    public boolean itemExists(String arg0) throws RepositoryException {
         
        return false;
    }

    public void logout() {
         this.loggedout = true;
        
    }

    public void move(String arg0, String arg1) throws ItemExistsException,
                                              PathNotFoundException,
                                              VersionException,
                                              ConstraintViolationException,
                                              LockException,
                                              RepositoryException {
         
        
    }

    public void refresh(boolean arg0) throws RepositoryException {
         
        
    }

    public void removeLockToken(String arg0) {
         
        
    }

    public void save() throws AccessDeniedException,
                      ItemExistsException,
                      ConstraintViolationException,
                      InvalidItemStateException,
                      VersionException,
                      LockException,
                      NoSuchNodeTypeException,
                      RepositoryException {
         
        
    }

    public void setNamespacePrefix(String arg0, String arg1) throws NamespaceException,
                                                            RepositoryException {
         
        
    }
    
}