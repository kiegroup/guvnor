/*
 * $Header: /home/ak/dev/webdav-servlet/webdav-servlet/src/main/java/net/sf/webdav/WebdavStore.java,v 1.2 2007-01-07 00:02:22 paul-h Exp $
 * $Revision: 1.2 $
 * $Date: 2007-01-07 00:02:22 $
 *
 * ====================================================================
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.sf.webdav;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;

import net.sf.webdav.exceptions.WebdavException;

/**
 * Interface for simple implementation of any store for the WebdavServlet
 * <p>
 * based on the BasicWebdavStore from Oliver Zeigermann, that was part of the
 * Webdav Construcktion Kit from slide
 * 
 */
public interface WebdavStore {

    /**
     * Indicates that a new request or transaction with this store involved has
     * been started. The request will be terminated by either {@link #commit()}
     * or {@link #rollback()}. If only non-read methods have been called, the
     * request will be terminated by a {@link #commit()}. This method will be
     * called by (@link WebdavStoreAdapter} at the beginning of each request.
     * 
     * 
     * @param principal
     *                the principal that started this request or
     *                <code>null</code> if there is non available
     * 
     * @throws WebdavException
     */
    void begin(Principal principal);

    /**
     * Checks if authentication information passed in is valid. If not throws an
     * exception.
     * 
     */
    void checkAuthentication();

    /**
     * Indicates that all changes done inside this request shall be made
     * permanent and any transactions, connections and other temporary resources
     * shall be terminated.
     * 
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    void commit();

    /**
     * Indicates that all changes done inside this request shall be undone and
     * any transactions, connections and other temporary resources shall be
     * terminated.
     * 
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    void rollback();

    /**
     * Checks if there is an object at the position specified by
     * <code>uri</code>.
     * 
     * @param uri
     *                URI of the object to check
     * @return <code>true</code> if the object at <code>uri</code> exists
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    boolean objectExists(String uri);

    /**
     * Checks if there is an object at the position specified by
     * <code>uri</code> and if so if it is a folder.
     * 
     * @param uri
     *                URI of the object to check
     * @return <code>true</code> if the object at <code>uri</code> exists
     *         and is a folder
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    boolean isFolder(String uri);

    /**
     * Checks if there is an object at the position specified by
     * <code>uri</code> and if so if it is a content resource.
     * 
     * @param uri
     *                URI of the object to check
     * @return <code>true</code> if the object at <code>uri</code> exists
     *         and is a content resource
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    boolean isResource(String uri);

    /**
     * Creates a folder at the position specified by <code>folderUri</code>.
     * 
     * @param folderUri
     *                URI of the folder
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    void createFolder(String folderUri);

    /**
     * Creates a content resource at the position specified by
     * <code>resourceUri</code>.
     * 
     * @param resourceUri
     *                URI of the content resource
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    void createResource(String resourceUri);

    /**
     * Sets / stores the content of the resource specified by
     * <code>resourceUri</code>.
     * 
     * @param resourceUri
     *                URI of the resource where the content will be stored
     * @param content
     *                input stream from which the content will be read from
     * @param contentType
     *                content type of the resource or <code>null</code> if
     *                unknown
     * @param characterEncoding
     *                character encoding of the resource or <code>null</code>
     *                if unknown or not applicable
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    void setResourceContent(String resourceUri, InputStream content,
	    String contentType, String characterEncoding);

    /**
     * Gets the date of the last modiciation of the object specified by
     * <code>uri</code>.
     * 
     * @param uri
     *                URI of the object, i.e. content resource or folder
     * @return date of last modification, <code>null</code> declares this
     *         value as invalid and asks the adapter to try to set it from the
     *         properties if possible
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    Date getLastModified(String uri);

    /**
     * Gets the date of the creation of the object specified by <code>uri</code>.
     * 
     * @param uri
     *                URI of the object, i.e. content resource or folder
     * @return date of creation, <code>null</code> declares this value as
     *         invalid and asks the adapter to try to set it from the properties
     *         if possible
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    Date getCreationDate(String uri);

    /**
     * Gets the names of the children of the folder specified by
     * <code>folderUri</code>.
     * 
     * @param folderUri
     *                URI of the folder
     * @return array containing names of the children or null if it is no folder
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    String[] getChildrenNames(String folderUri);

    /**
     * Gets the content of the resource specified by <code>resourceUri</code>.
     * 
     * @param resourceUri
     *                URI of the content resource
     * @return input stream you can read the content of the resource from
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    InputStream getResourceContent(String resourceUri);

    /**
     * Gets the length of the content resource specified by
     * <code>resourceUri</code>.
     * 
     * @param resourceUri
     *                URI of the content resource
     * @return length of the resource in bytes, <code>-1</code> declares this
     *         value as invalid and asks the adapter to try to set it from the
     *         properties if possible
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    long getResourceLength(String resourceUri);

    /**
     * Removes the object specified by <code>uri</code>.
     * 
     * @param uri
     *                URI of the object, i.e. content resource or folder
     * @throws WebdavException
     *                 if something goes wrong on the store level
     */
    void removeObject(String uri);

}