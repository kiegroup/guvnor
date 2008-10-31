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
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.webdav.MimeTyper;
import net.sf.webdav.ResourceLocks;
import net.sf.webdav.WebdavStore;

public class DoGet extends DoHead {

    public DoGet(WebdavStore store, String dftIndexFile, String insteadOf404,
	    ResourceLocks resourceLocks, MimeTyper mimeTyper,
	    int contentLengthHeader) {
	super(store, dftIndexFile, insteadOf404, resourceLocks, mimeTyper,
		contentLengthHeader);

    }

    protected void doBody(HttpServletResponse resp, String path)
	    throws IOException {
	OutputStream out = resp.getOutputStream();
	InputStream in = store.getResourceContent(path);
	try {
	    int read = -1;
	    byte[] copyBuffer = new byte[BUF_SIZE];

	    while ((read = in.read(copyBuffer, 0, copyBuffer.length)) != -1) {
		out.write(copyBuffer, 0, read);
	    }
	} finally {
	    in.close();
	    out.flush();
	    out.close();
	}
    }

    protected void folderBody(String path, HttpServletResponse resp,
	    HttpServletRequest req) throws IOException {
	if (store.isFolder(path)) {
	    // TODO some folder response (for browsers, DAV tools
	    // use propfind) in html?
	    OutputStream out = resp.getOutputStream();
	    String[] children = store.getChildrenNames(path);
	    StringBuffer childrenTemp = new StringBuffer();
	    childrenTemp.append("Contents of this Folder:\n");
	    for (int i = 0; i < children.length; i++) {
		childrenTemp.append(children[i]);
		childrenTemp.append("\n");
	    }
	    out.write(childrenTemp.toString().getBytes());
	} else {
	    if (!store.objectExists(path)) {
		resp.sendError(HttpServletResponse.SC_NOT_FOUND, req
			.getRequestURI());
	    }

	}
    }

}
