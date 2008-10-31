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
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.webdav.WebdavStatus;
import net.sf.webdav.fromcatalina.XMLWriter;

public abstract class ReportingMethod extends AbstractMethod {

    /**
     * Send a multistatus element containing a complete error report to the
     * client.
     * 
     * @param req
     *                Servlet request
     * @param resp
     *                Servlet response
     * @param errorList
     *                List of error to be displayed
     */
    protected void sendReport(HttpServletRequest req, HttpServletResponse resp,
	    Hashtable errorList) throws IOException {

	resp.setStatus(WebdavStatus.SC_MULTI_STATUS);

	String absoluteUri = req.getRequestURI();
	String relativePath = getRelativePath(req);

	XMLWriter generatedXML = new XMLWriter();
	generatedXML.writeXMLHeader();

	generatedXML.writeElement(null, "multistatus xmlns=\"DAV:\"",
		XMLWriter.OPENING);

	Enumeration pathList = errorList.keys();
	while (pathList.hasMoreElements()) {

	    String errorPath = (String) pathList.nextElement();
	    int errorCode = ((Integer) errorList.get(errorPath)).intValue();

	    generatedXML.writeElement(null, "response", XMLWriter.OPENING);

	    generatedXML.writeElement(null, "href", XMLWriter.OPENING);
	    String toAppend = errorPath.substring(relativePath.length());
	    if (!toAppend.startsWith("/"))
		toAppend = "/" + toAppend;
	    generatedXML.writeText(absoluteUri + toAppend);
	    generatedXML.writeElement(null, "href", XMLWriter.CLOSING);
	    generatedXML.writeElement(null, "status", XMLWriter.OPENING);
	    generatedXML.writeText("HTTP/1.1 " + errorCode + " "
		    + WebdavStatus.getStatusText(errorCode));
	    generatedXML.writeElement(null, "status", XMLWriter.CLOSING);

	    generatedXML.writeElement(null, "response", XMLWriter.CLOSING);

	}

	generatedXML.writeElement(null, "multistatus", XMLWriter.CLOSING);

	Writer writer = resp.getWriter();
	writer.write(generatedXML.toString());
	writer.close();

    }

}
