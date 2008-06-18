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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.webdav.ResourceLocks;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.WebdavStore;
import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.WebdavException;

public class DoPut extends AbstractMethod {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory
	    .getLogger("net.sf.webdav.methods");

    private WebdavStore store;
    private ResourceLocks resLocks;
    private boolean readOnly;
    private boolean lazyFolderCreationOnPut;

    public DoPut(WebdavStore store, ResourceLocks resLocks, boolean readOnly,
	    boolean lazyFolderCreationOnPut) {
	this.store = store;
	this.resLocks = resLocks;
	this.readOnly = readOnly;
	this.lazyFolderCreationOnPut = lazyFolderCreationOnPut;
    }

    public void execute(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	log.trace("-- " + this.getClass().getName());

	if (!readOnly) {
	    String path = getRelativePath(req);
	    String parentPath = getParentPath(path);
	    String lockOwner = "doPut" + System.currentTimeMillis()
		    + req.toString();
	    if (resLocks.lock(path, lockOwner, true, -1)) {
		try {
		    if (parentPath != null && !store.isFolder(parentPath)
			    && lazyFolderCreationOnPut) {
			store.createFolder(parentPath);
		    }
		    if (!store.isFolder(path)) {
			if (!store.objectExists(path)) {
			    store.createResource(path);
			    resp.setStatus(HttpServletResponse.SC_CREATED);
			} else {
			    String userAgent = req.getHeader("User-Agent");
			    if (userAgent.contains("WebDAVFS/1.5")) {
				log
					.trace("DoPut.execute() : do workaround for user agent '"
						+ userAgent + "'");
				resp.setStatus(HttpServletResponse.SC_CREATED);
			    } else {
				resp
					.setStatus(HttpServletResponse.SC_NO_CONTENT);
			    }
			}
			store.setResourceContent(path, req.getInputStream(),
				null, null);
			resp.setContentLength((int) store
				.getResourceLength(path));
		    }
		} catch (AccessDeniedException e) {
		    resp.sendError(WebdavStatus.SC_FORBIDDEN);
		} catch (WebdavException e) {
		    resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
		} finally {
		    resLocks.unlock(path, lockOwner);
		}
	    } else {
		resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
	    }
	} else {
	    resp.sendError(WebdavStatus.SC_FORBIDDEN);
	}

    }
}
