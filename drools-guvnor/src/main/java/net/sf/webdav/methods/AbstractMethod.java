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

import net.sf.webdav.MethodExecutor;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractMethod implements MethodExecutor {

    /**
     * size of the io-buffer
     */
    protected static int BUF_SIZE = 50000;

    /**
     * Return the relative path associated with this servlet.
     * 
     * @param request
     *                The servlet request we are processing
     */
    protected String getRelativePath(HttpServletRequest request) {

	// Are we being processed by a RequestDispatcher.include()?
	if (request.getAttribute("javax.servlet.include.request_uri") != null) {
	    String result = (String) request
		    .getAttribute("javax.servlet.include.path_info");
	    // if (result == null)
	    // result = (String) request
	    // .getAttribute("javax.servlet.include.servlet_path");
	    if ((result == null) || (result.equals("")))
		result = "/";
	    return (result);
	}

	// No, extract the desired path directly from the request
	String result = request.getPathInfo();
	// if (result == null) {
	// result = request.getServletPath();
	// }
	if ((result == null) || (result.equals(""))) {
	    result = "/";
	}
	return (result);

    }

    /**
     * creates the parent path from the given path by removing the last '/' and
     * everything after that
     * 
     * @param path
     *                the path
     * @return parent path
     */
    public String getParentPath(String path) {
	int slash = path.lastIndexOf('/');
	if (slash != -1) {
	    return path.substring(0, slash);
	}
	return null;
    }

}
