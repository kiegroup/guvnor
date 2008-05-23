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
import net.sf.webdav.exceptions.ObjectNotFoundException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.WebdavStore;
import net.sf.webdav.ResourceLocks;
import net.sf.webdav.fromcatalina.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Hashtable;

public class DoCopy extends ReportingMethod {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory
	    .getLogger("net.sf.webdav.methods");

    private WebdavStore store;
    private ResourceLocks resourceLocks;
    private DoDelete doDelete;
    private boolean readOnly;

    public DoCopy(WebdavStore store, ResourceLocks resourceLocks,
	    DoDelete doDelete, boolean readOnly) {
	this.store = store;
	this.resourceLocks = resourceLocks;
	this.doDelete = doDelete;
	this.readOnly = readOnly;
    }

    public void execute(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	log.trace("-- " + this.getClass().getName());

	String path = getRelativePath(req);
	if (!readOnly) {
	    String lockOwner = "doCopy" + System.currentTimeMillis()
		    + req.toString();
	    if (resourceLocks.lock(path, lockOwner, false, -1)) {
		try {
		    copyResource(req, resp);
		} catch (AccessDeniedException e) {
		    resp.sendError(WebdavStatus.SC_FORBIDDEN);
		} catch (ObjectAlreadyExistsException e) {
		    resp.sendError(WebdavStatus.SC_CONFLICT, req
			    .getRequestURI());
		} catch (ObjectNotFoundException e) {
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
     * Copy a resource.
     * 
     * @param req
     *                Servlet request
     * @param resp
     *                Servlet response
     * @return true if the copy is successful
     * @throws WebdavException
     *                 if an error in the underlying store occurs
     * @throws IOException
     *                 when an error occurs while sending the response
     */
    public boolean copyResource(HttpServletRequest req, HttpServletResponse resp)
	    throws WebdavException, IOException {

	// Parsing destination header

	String destinationPath = req.getHeader("Destination");

	if (destinationPath == null) {
	    resp.sendError(WebdavStatus.SC_BAD_REQUEST);
	    return false;
	}

	// Remove url encoding from destination
	destinationPath = RequestUtil.URLDecode(destinationPath, "UTF8");

	int protocolIndex = destinationPath.indexOf("://");
	if (protocolIndex >= 0) {
	    // if the Destination URL contains the protocol, we can safely
	    // trim everything upto the first "/" character after "://"
	    int firstSeparator = destinationPath
		    .indexOf("/", protocolIndex + 4);
	    if (firstSeparator < 0) {
		destinationPath = "/";
	    } else {
		destinationPath = destinationPath.substring(firstSeparator);
	    }
	} else {
	    String hostName = req.getServerName();
	    if ((hostName != null) && (destinationPath.startsWith(hostName))) {
		destinationPath = destinationPath.substring(hostName.length());
	    }

	    int portIndex = destinationPath.indexOf(":");
	    if (portIndex >= 0) {
		destinationPath = destinationPath.substring(portIndex);
	    }

	    if (destinationPath.startsWith(":")) {
		int firstSeparator = destinationPath.indexOf("/");
		if (firstSeparator < 0) {
		    destinationPath = "/";
		} else {
		    destinationPath = destinationPath.substring(firstSeparator);
		}
	    }
	}

	// Normalise destination path (remove '.' and '..')
	destinationPath = normalize(destinationPath);

	String contextPath = req.getContextPath();
	if ((contextPath != null) && (destinationPath.startsWith(contextPath))) {
	    destinationPath = destinationPath.substring(contextPath.length());
	}

	String pathInfo = req.getPathInfo();
	if (pathInfo != null) {
	    String servletPath = req.getServletPath();
	    if ((servletPath != null)
		    && (destinationPath.startsWith(servletPath))) {
		destinationPath = destinationPath.substring(servletPath
			.length());
	    }
	}

	String path = getRelativePath(req);

	// if source = destination
	if (path.equals(destinationPath)) {
	    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	// Parsing overwrite header

	boolean overwrite = true;
	String overwriteHeader = req.getHeader("Overwrite");

	if (overwriteHeader != null) {
	    overwrite = overwriteHeader.equalsIgnoreCase("T");
	}

	// Overwriting the destination
	String lockOwner = "copyResource" + System.currentTimeMillis()
		+ req.toString();
	if (resourceLocks.lock(destinationPath, lockOwner, true, -1)) {
	    try {

		// Retrieve the resources
		if (!store.objectExists(path)) {
		    resp
			    .sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    return false;
		}

		boolean exists = store.objectExists(destinationPath);
		Hashtable errorList = new Hashtable();

		if (overwrite) {

		    // Delete destination resource, if it exists
		    if (exists) {
			doDelete.deleteResource(destinationPath, errorList,
				req, resp);

		    } else {
			resp.setStatus(WebdavStatus.SC_CREATED);
		    }
		} else {

		    // If the destination exists, then it's a conflict
		    if (exists) {
			resp.sendError(WebdavStatus.SC_PRECONDITION_FAILED);
			return false;
		    } else {
			resp.setStatus(WebdavStatus.SC_CREATED);
		    }

		}
		copy(path, destinationPath, errorList, req, resp);
		if (!errorList.isEmpty()) {
		    sendReport(req, resp, errorList);
		}

	    } finally {
		resourceLocks.unlock(destinationPath, lockOwner);
	    }
	} else {
	    resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
	    return false;
	}
	return true;

    }

    /**
     * copies the specified resource(s) to the specified destination.
     * preconditions must be handled by the caller. Standard status codes must
     * be handled by the caller. a multi status report in case of errors is
     * created here.
     * 
     * @param sourcePath
     *                path from where to read
     * @param destinationPath
     *                path where to write
     * @param req
     *                HttpServletRequest
     * @param resp
     *                HttpServletResponse
     * @throws WebdavException
     *                 if an error in the underlying store occurs
     * @throws IOException
     */
    private void copy(String sourcePath, String destinationPath,
	    Hashtable errorList, HttpServletRequest req,
	    HttpServletResponse resp) throws WebdavException, IOException {

	if (store.isResource(sourcePath)) {
	    store.createResource(destinationPath);
	    store.setResourceContent(destinationPath, store
		    .getResourceContent(sourcePath), null, null);
	} else {

	    if (store.isFolder(sourcePath)) {
		copyFolder(sourcePath, destinationPath, errorList, req, resp);
	    } else {
		resp.sendError(WebdavStatus.SC_NOT_FOUND);
	    }
	}
    }

    /**
     * helper method of copy() recursively copies the FOLDER at source path to
     * destination path
     * 
     * @param sourcePath
     *                where to read
     * @param destinationPath
     *                where to write
     * @param errorList
     *                all errors that ocurred
     * @param req
     *                HttpServletRequest
     * @param resp
     *                HttpServletResponse
     * @throws WebdavException
     *                 if an error in the underlying store occurs
     */
    private void copyFolder(String sourcePath, String destinationPath,
	    Hashtable errorList, HttpServletRequest req,
	    HttpServletResponse resp) throws WebdavException {

	store.createFolder(destinationPath);
	boolean infiniteDepth = true;
	if (req.getHeader("depth") != null) {
	    if (req.getHeader("depth").equals("0")) {
		infiniteDepth = false;
	    }
	}
	if (infiniteDepth) {
	    String[] children = store.getChildrenNames(sourcePath);

	    for (int i = children.length - 1; i >= 0; i--) {
		children[i] = "/" + children[i];
		try {
		    if (store.isResource(sourcePath + children[i])) {
			store.createResource(destinationPath + children[i]);
			store.setResourceContent(destinationPath + children[i],
				store.getResourceContent(sourcePath
					+ children[i]), null, null);

		    } else {
			copyFolder(sourcePath + children[i], destinationPath
				+ children[i], errorList, req, resp);
		    }
		} catch (AccessDeniedException e) {
		    errorList.put(destinationPath + children[i], new Integer(
			    WebdavStatus.SC_FORBIDDEN));
		} catch (ObjectNotFoundException e) {
		    errorList.put(destinationPath + children[i], new Integer(
			    WebdavStatus.SC_NOT_FOUND));
		} catch (ObjectAlreadyExistsException e) {
		    errorList.put(destinationPath + children[i], new Integer(
			    WebdavStatus.SC_CONFLICT));
		} catch (WebdavException e) {
		    errorList.put(destinationPath + children[i], new Integer(
			    WebdavStatus.SC_INTERNAL_SERVER_ERROR));
		}
	    }
	}
    }

    /**
     * Return a context-relative path, beginning with a "/", that represents the
     * canonical version of the specified path after ".." and "." elements are
     * resolved out. If the specified path attempts to go outside the boundaries
     * of the current context (i.e. too many ".." path elements are present),
     * return <code>null</code> instead.
     * 
     * @param path
     *                Path to be normalized
     */
    protected String normalize(String path) {

	if (path == null)
	    return null;

	// Create a place for the normalized path
	String normalized = path;

	if (normalized.equals("/."))
	    return "/";

	// Normalize the slashes and add leading slash if necessary
	if (normalized.indexOf('\\') >= 0)
	    normalized = normalized.replace('\\', '/');
	if (!normalized.startsWith("/"))
	    normalized = "/" + normalized;

	// Resolve occurrences of "//" in the normalized path
	while (true) {
	    int index = normalized.indexOf("//");
	    if (index < 0)
		break;
	    normalized = normalized.substring(0, index)
		    + normalized.substring(index + 1);
	}

	// Resolve occurrences of "/./" in the normalized path
	while (true) {
	    int index = normalized.indexOf("/./");
	    if (index < 0)
		break;
	    normalized = normalized.substring(0, index)
		    + normalized.substring(index + 2);
	}

	// Resolve occurrences of "/../" in the normalized path
	while (true) {
	    int index = normalized.indexOf("/../");
	    if (index < 0)
		break;
	    if (index == 0)
		return (null); // Trying to go outside our context
	    int index2 = normalized.lastIndexOf('/', index - 1);
	    normalized = normalized.substring(0, index2)
		    + normalized.substring(index + 3);
	}

	// Return the normalized path that we have completed
	return (normalized);

    }

}
