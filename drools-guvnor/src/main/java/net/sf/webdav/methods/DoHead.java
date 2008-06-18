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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.webdav.methods;

import net.sf.webdav.WebdavStore;
import net.sf.webdav.ResourceLocks;
import net.sf.webdav.MimeTyper;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.ObjectAlreadyExistsException;
import net.sf.webdav.exceptions.WebdavException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DoHead extends AbstractMethod {

    protected String dftIndexFile;
    protected WebdavStore store;
    protected String insteadOf404;
    protected ResourceLocks resLocks;
    protected MimeTyper mimeTyper;
    protected int contLength;

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory
	    .getLogger("net.sf.webdav.methods");

    public DoHead(WebdavStore store, String dftIndexFile, String insteadOf404,
	    ResourceLocks resourceLocks, MimeTyper mimeTyper,
	    int contentLengthHeader) {
	this.store = store;
	this.dftIndexFile = dftIndexFile;
	this.insteadOf404 = insteadOf404;
	this.resLocks = resourceLocks;
	this.mimeTyper = mimeTyper;
	this.contLength = contentLengthHeader;
    }

    public void execute(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {

	// determines if the uri exists.

	boolean bUriExists = false;

	String path = getRelativePath(req);
	log.trace("-- " + this.getClass().getName());

	log.debug("-- do " + req.getMethod());

	if (store.isFolder(path)) {
	    bUriExists = true;
	    if (dftIndexFile != null && !dftIndexFile.trim().equals("")) {

		resp.sendRedirect(resp.encodeRedirectURL(req.getRequestURI()
			+ this.dftIndexFile));
		return;
	    }
	}
	if (!store.objectExists(path)) {

	    if (this.insteadOf404 != null && !insteadOf404.trim().equals("")) {
		path = this.insteadOf404;
	    }

	} else
	    bUriExists = true;

	String lockOwner = "doGet" + System.currentTimeMillis()
		+ req.toString();

	if (resLocks.lock(path, lockOwner, false, 0)) {
	    try {

		if (store.isResource(path)) {
		    // path points to a file but ends with / or \
		    if (path.endsWith("/") || (path.endsWith("\\"))) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, req
				.getRequestURI());
		    } else {

			// setting headers
			long lastModified = store.getLastModified(path)
				.getTime();
			resp.setDateHeader("last-modified", lastModified);

			long resourceLength = store.getResourceLength(path);

			if (contLength == 1) {
			    if (resourceLength > 0) {
				if (resourceLength <= Integer.MAX_VALUE) {
				    resp.setContentLength((int) resourceLength);
				} else {
				    resp.setHeader("content-length", ""
					    + resourceLength);
				    // is "content-length" the right header?
				    // is
				    // long
				    // a valid format?
				}
			    }
			}

			String mimeType = mimeTyper.getMimeType(path);
			if (mimeType != null) {
			    resp.setContentType(mimeType);
			} else {
			    int lastSlash = path.replace('\\', '/')
				    .lastIndexOf('/');
			    int lastDot = path.indexOf(".", lastSlash);
			    if (lastDot == -1) {
				resp.setContentType("text/html");
			    }
			}

			doBody(resp, path);
		    }
		} else {
		    folderBody(path, resp, req);
		}
	    } catch (AccessDeniedException e) {
		resp.sendError(WebdavStatus.SC_FORBIDDEN);
	    } catch (ObjectAlreadyExistsException e) {
		resp.sendError(WebdavStatus.SC_NOT_FOUND, req.getRequestURI());
	    } catch (WebdavException e) {
		resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
	    } finally {
		resLocks.unlock(path, lockOwner);
	    }
	} else {
	    resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
	}
	if (!bUriExists)
	    resp.setStatus(WebdavStatus.SC_NOT_FOUND);
    }

    protected void folderBody(String path, HttpServletResponse resp,
	    HttpServletRequest req) throws IOException {
	// no body for HEAD
    }

    protected void doBody(HttpServletResponse resp, String path)
	    throws IOException {
	// no body for HEAD
    }
}
