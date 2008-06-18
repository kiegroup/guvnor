/*
 * Copyright 1999,2004 The Apache Software Foundation.
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
package net.sf.webdav.methods;

import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.ObjectAlreadyExistsException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.exceptions.ObjectNotFoundException;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.ResourceLocks;
import net.sf.webdav.WebdavStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Hashtable;
import java.io.IOException;

public class DoDelete extends ReportingMethod {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory
	    .getLogger("net.sf.webdav.methods");

    private WebdavStore store;
    private ResourceLocks resourceLocks;
    private boolean readOnly;

    public DoDelete(WebdavStore store, ResourceLocks resourceLocks,
	    boolean readOnly) {
	this.store = store;
	this.resourceLocks = resourceLocks;
	this.readOnly = readOnly;
    }

    public void execute(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	log.trace("-- " + this.getClass().getName());

	if (!readOnly) {
	    String path = getRelativePath(req);
	    String lockOwner = "doDelete" + System.currentTimeMillis()
		    + req.toString();
	    if (resourceLocks.lock(path, lockOwner, true, -1)) {
		try {
		    Hashtable errorList = new Hashtable();
		    deleteResource(path, errorList, req, resp);
		    if (!errorList.isEmpty()) {
			sendReport(req, resp, errorList);
		    }
		} catch (AccessDeniedException e) {
		    resp.sendError(WebdavStatus.SC_FORBIDDEN);
		} catch (ObjectAlreadyExistsException e) {
		    resp.sendError(WebdavStatus.SC_NOT_FOUND, req
			    .getRequestURI());
		} catch (WebdavException e) {
		    resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
		} finally {
		    resourceLocks.unlock(path, lockOwner);
		}
	    } else {
		resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
	    }
	} else {
	    resp.sendError(WebdavStatus.SC_FORBIDDEN);
	}

    }

    /**
     * deletes the recources at "path"
     * 
     * @param path
     *                the folder to be deleted
     * @param errorList
     *                all errors that ocurred
     * @param req
     *                HttpServletRequest
     * @param resp
     *                HttpServletResponse
     * @throws WebdavException
     *                 if an error in the underlying store occurs
     * @throws IOException
     *                 when an error occurs while sending the response
     */
    public void deleteResource(String path, Hashtable errorList,
	    HttpServletRequest req, HttpServletResponse resp)
	    throws IOException, WebdavException {

	resp.setStatus(WebdavStatus.SC_NO_CONTENT);
	if (!readOnly) {

	    if (store.isResource(path)) {
		store.removeObject(path);
	    } else {
		if (store.isFolder(path)) {

		    deleteFolder(path, errorList, req, resp);
		    store.removeObject(path);
		} else {
		    resp.sendError(WebdavStatus.SC_NOT_FOUND);
		}
	    }

	} else {
	    resp.sendError(WebdavStatus.SC_FORBIDDEN);
	}
    }

    /**
     * 
     * helper method of deleteResource() deletes the folder and all of its
     * contents
     * 
     * @param path
     *                the folder to be deleted
     * @param errorList
     *                all errors that ocurred
     * @param req
     *                HttpServletRequest
     * @param resp
     *                HttpServletResponse
     * @throws WebdavException
     *                 if an error in the underlying store occurs
     */
    private void deleteFolder(String path, Hashtable errorList,
	    HttpServletRequest req, HttpServletResponse resp)
	    throws WebdavException {

	String[] children = store.getChildrenNames(path);
	for (int i = children.length - 1; i >= 0; i--) {
	    children[i] = "/" + children[i];
	    try {
		if (store.isResource(path + children[i])) {
		    store.removeObject(path + children[i]);

		} else {
		    deleteFolder(path + children[i], errorList, req, resp);

		    store.removeObject(path + children[i]);

		}
	    } catch (AccessDeniedException e) {
		errorList.put(path + children[i], new Integer(
			WebdavStatus.SC_FORBIDDEN));
	    } catch (ObjectNotFoundException e) {
		errorList.put(path + children[i], new Integer(
			WebdavStatus.SC_NOT_FOUND));
	    } catch (WebdavException e) {
		errorList.put(path + children[i], new Integer(
			WebdavStatus.SC_INTERNAL_SERVER_ERROR));
	    }
	}

    }

}
