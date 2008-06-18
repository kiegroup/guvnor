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

import net.sf.webdav.WebdavStatus;
import net.sf.webdav.WebdavStore;
import net.sf.webdav.ResourceLocks;
import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.ObjectAlreadyExistsException;
import net.sf.webdav.exceptions.WebdavException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Hashtable;
import java.io.IOException;

public class DoMove extends ReportingMethod {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory
	    .getLogger("net.sf.webdav.methods");

    private ResourceLocks resourceLocks;
    private DoDelete doDelete;
    private DoCopy doCopy;
    private boolean readOnly;

    public DoMove(ResourceLocks resourceLocks, DoDelete doDelete,
	    DoCopy doCopy, boolean readOnly) {
	this.resourceLocks = resourceLocks;
	this.doDelete = doDelete;
	this.doCopy = doCopy;
	this.readOnly = readOnly;
    }

    public void execute(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	if (!readOnly) {

	    log.trace("-- " + this.getClass().getName());

	    String path = getRelativePath(req);
	    String lockOwner = "doMove" + System.currentTimeMillis()
		    + req.toString();
	    if (resourceLocks.lock(path, lockOwner, false, -1)) {
		try {
		    if (doCopy.copyResource(req, resp)) {

			Hashtable errorList = new Hashtable();
			doDelete.deleteResource(path, errorList, req, resp);
			if (!errorList.isEmpty()) {
			    sendReport(req, resp, errorList);
			}

		    } else {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
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
}
