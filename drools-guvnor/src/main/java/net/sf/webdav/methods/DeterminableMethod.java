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

public abstract class DeterminableMethod extends AbstractMethod {

    /**
     * Determines the methods normally allowed for the resource.
     * 
     * @param exists
     *                does the resource exist?
     * @param isFolder
     *                is the resource a folder?
     * @return all allowed methods, separated by commas
     */
    protected String determineMethodsAllowed(boolean exists, boolean isFolder) {
	StringBuffer methodsAllowed = new StringBuffer();
	try {
	    if (exists) {
		methodsAllowed
			.append("OPTIONS, GET, HEAD, POST, DELETE, TRACE");
		methodsAllowed
			.append(", PROPPATCH, COPY, MOVE, LOCK, UNLOCK, PROPFIND");
		if (isFolder) {
		    methodsAllowed.append(", PUT");
		}
		return methodsAllowed.toString();
	    }
	} catch (Exception e) {
	    // we do nothing, just return less allowed methods

	}
	methodsAllowed.append("OPTIONS, MKCOL, PUT, LOCK");
	return methodsAllowed.toString();

    }

}
