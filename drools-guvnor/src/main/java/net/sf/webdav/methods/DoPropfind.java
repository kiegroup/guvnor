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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.webdav.MimeTyper;
import net.sf.webdav.ResourceLocks;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.WebdavStore;
import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.fromcatalina.URLEncoder;
import net.sf.webdav.fromcatalina.XMLWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DoPropfind extends AbstractMethod {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory
	    .getLogger("net.sf.webdav.methods");

    /**
     * Array containing the safe characters set.
     */
    protected static URLEncoder urlEncoder;

    /**
     * Simple date format for the creation date ISO representation (partial).
     */
    protected static final SimpleDateFormat creationDateFormat = new SimpleDateFormat(
	    "yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * Default depth is infite.
     */
    private static final int INFINITY = 3;

    /**
     * PROPFIND - Specify a property mask.
     */
    private static final int FIND_BY_PROPERTY = 0;

    /**
     * PROPFIND - Display all properties.
     */
    private static final int FIND_ALL_PROP = 1;

    /**
     * PROPFIND - Return property names.
     */
    private static final int FIND_PROPERTY_NAMES = 2;

    private WebdavStore store;
    private ResourceLocks resLocks;
    private boolean readOnly;
    private MimeTyper mimeTyper;

    static {
	creationDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	/**
	 * GMT timezone - all HTTP dates are on GMT
	 */
	urlEncoder = new URLEncoder();
	urlEncoder.addSafeCharacter('-');
	urlEncoder.addSafeCharacter('_');
	urlEncoder.addSafeCharacter('.');
	urlEncoder.addSafeCharacter('*');
	urlEncoder.addSafeCharacter('/');
    }

    public DoPropfind(WebdavStore store, ResourceLocks resLocks,
	    boolean readOnly, MimeTyper mimeTyper) {
	this.store = store;
	this.resLocks = resLocks;
	this.readOnly = readOnly;
	this.mimeTyper = mimeTyper;
    }

    public void execute(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	log.trace("-- " + this.getClass().getName());

	// Retrieve the resources
	String lockOwner = "doPropfind" + System.currentTimeMillis()
		+ req.toString();
	String path = getCleanPath(getRelativePath(req));
	int depth = getDepth(req);
	if (resLocks.lock(path, lockOwner, false, depth)) {
	    try {
		if (!store.objectExists(path)) {
		    resp.sendError(HttpServletResponse.SC_NOT_FOUND, req
			    .getRequestURI());
		    return;
		}

		Vector properties = null;

		int propertyFindType = FIND_ALL_PROP;
		Node propNode = null;
		getPropertyNodeAndType(propNode, propertyFindType, req);

		if (propertyFindType == FIND_BY_PROPERTY) {
		    properties = getPropertiesFromXML(propNode);
		}

		resp.setStatus(WebdavStatus.SC_MULTI_STATUS);
		resp.setContentType("text/xml; charset=UTF-8");

		// Create multistatus object
		XMLWriter generatedXML = new XMLWriter(resp.getWriter());
		generatedXML.writeXMLHeader();
		generatedXML.writeElement(null, "multistatus xmlns=\"DAV:\"",
			XMLWriter.OPENING);
		if (depth == 0) {
		    parseProperties(req, generatedXML, path, propertyFindType,
			    properties, mimeTyper.getMimeType(path));
		} else {
		    recursiveParseProperties(path, req, generatedXML,
			    propertyFindType, properties, depth, mimeTyper
				    .getMimeType(path));
		}
		generatedXML.writeElement(null, "multistatus",
			XMLWriter.CLOSING);
		generatedXML.sendData();
	    } catch (AccessDeniedException e) {
		resp.sendError(WebdavStatus.SC_FORBIDDEN);
	    } catch (WebdavException e) {
		log.warn("Sending internal error!");
		resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
	    } catch (ServletException e) {
		e.printStackTrace(); // To change body of catch statement use
		// File | Settings | File Templates.
	    } finally {
		resLocks.unlock(path, lockOwner);
	    }
	} else {
	    log.warn("Sending internal error!");
	    resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
	}
    }

    /**
     * reads the depth header from the request and returns it as a int
     * 
     * @param req
     * @return the depth from the depth header
     */
    private int getDepth(HttpServletRequest req) {
	int depth = INFINITY;
	String depthStr = req.getHeader("Depth");
	if (depthStr != null) {
	    if (depthStr.equals("0")) {
		depth = 0;
	    } else if (depthStr.equals("1")) {
		depth = 1;
	    } else if (depthStr.equals("infinity")) {
		depth = INFINITY;
	    }
	}
	return depth;
    }

    /**
     * removes a / at the end of the path string, if present
     * 
     * @param path
     *                the path
     * @return the path without trailing /
     */
    private String getCleanPath(String path) {

	if (path.endsWith("/") && path.length() > 1)
	    path = path.substring(0, path.length() - 1);
	return path;
    }

    /**
     * overwrites propNode and type, parsed from xml input stream
     * 
     * @param propNode
     * @param type
     * @param req
     *                HttpServletRequest
     * @throws javax.servlet.ServletException
     */
    private void getPropertyNodeAndType(Node propNode, int type,
	    ServletRequest req) throws ServletException {
	if (req.getContentLength() != 0) {
	    DocumentBuilder documentBuilder = getDocumentBuilder();
	    try {
		Document document = documentBuilder.parse(new InputSource(req
			.getInputStream()));
		// Get the root element of the document
		Element rootElement = document.getDocumentElement();
		NodeList childList = rootElement.getChildNodes();

		for (int i = 0; i < childList.getLength(); i++) {
		    Node currentNode = childList.item(i);
		    switch (currentNode.getNodeType()) {
		    case Node.TEXT_NODE:
			break;
		    case Node.ELEMENT_NODE:
			if (currentNode.getNodeName().endsWith("prop")) {
			    type = FIND_BY_PROPERTY;
			    propNode = currentNode;
			}
			if (currentNode.getNodeName().endsWith("propname")) {
			    type = FIND_PROPERTY_NAMES;
			}
			if (currentNode.getNodeName().endsWith("allprop")) {
			    type = FIND_ALL_PROP;
			}
			break;
		    }
		}
	    } catch (Exception e) {

	    }
	} else {
	    // no content, which means it is a allprop request
	    type = FIND_ALL_PROP;
	}
    }

    /**
     * Return JAXP document builder instance.
     */
    private DocumentBuilder getDocumentBuilder() throws ServletException {
	DocumentBuilder documentBuilder = null;
	DocumentBuilderFactory documentBuilderFactory = null;
	try {
	    documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    documentBuilderFactory.setNamespaceAware(true);
	    documentBuilder = documentBuilderFactory.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    throw new ServletException("jaxp failed");
	}
	return documentBuilder;
    }

    private Vector getPropertiesFromXML(Node propNode) {
	Vector properties;
	properties = new Vector();
	NodeList childList = propNode.getChildNodes();

	for (int i = 0; i < childList.getLength(); i++) {
	    Node currentNode = childList.item(i);
	    switch (currentNode.getNodeType()) {
	    case Node.TEXT_NODE:
		break;
	    case Node.ELEMENT_NODE:
		String nodeName = currentNode.getNodeName();
		String propertyName = null;
		if (nodeName.indexOf(':') != -1) {
		    propertyName = nodeName
			    .substring(nodeName.indexOf(':') + 1);
		} else {
		    propertyName = nodeName;
		}
		// href is a live property which is handled differently
		properties.addElement(propertyName);
		break;
	    }
	}
	return properties;
    }

    /**
     * goes recursive through all folders. used by propfind
     * 
     * @param currentPath
     *                the current path
     * @param req
     *                HttpServletRequest
     * @param generatedXML
     * @param propertyFindType
     * @param properties
     * @param depth
     *                depth of the propfind
     * @throws IOException
     *                 if an error in the underlying store occurs
     */
    private void recursiveParseProperties(String currentPath,
	    HttpServletRequest req, XMLWriter generatedXML,
	    int propertyFindType, Vector properties, int depth, String mimeType)
	    throws WebdavException {

	parseProperties(req, generatedXML, currentPath, propertyFindType,
		properties, mimeType);
	String[] names = store.getChildrenNames(currentPath);
	if ((names != null) && (depth > 0)) {

	    for (int i = 0; i < names.length; i++) {
		String name = names[i];
		String newPath = currentPath;
		if (!(newPath.endsWith("/"))) {
		    newPath += "/";
		}
		newPath += name;
		recursiveParseProperties(newPath, req, generatedXML,
			propertyFindType, properties, depth - 1, mimeType);
	    }
	}
    }

    /**
     * Propfind helper method.
     * 
     * @param req
     *                The servlet request
     * @param generatedXML
     *                XML response to the Propfind request
     * @param path
     *                Path of the current resource
     * @param type
     *                Propfind type
     * @param propertiesVector
     *                If the propfind type is find properties by name, then this
     *                Vector contains those properties
     */
    private void parseProperties(HttpServletRequest req,
	    XMLWriter generatedXML, String path, int type,
	    Vector propertiesVector, String mimeType) throws WebdavException {

	String creationdate = getISOCreationDate(store.getCreationDate(path)
		.getTime());
	boolean isFolder = store.isFolder(path);
	SimpleDateFormat formatter = new SimpleDateFormat(
		"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
	formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	String lastModified = formatter.format(store.getLastModified(path));
	String resourceLength = String.valueOf(store.getResourceLength(path));

	// ResourceInfo resourceInfo = new ResourceInfo(path, resources);

	generatedXML.writeElement(null, "response", XMLWriter.OPENING);
	String status = new String("HTTP/1.1 " + WebdavStatus.SC_OK + " "
		+ WebdavStatus.getStatusText(WebdavStatus.SC_OK));

	// Generating href element
	generatedXML.writeElement(null, "href", XMLWriter.OPENING);

	String href = req.getContextPath();
	String servletPath = req.getServletPath();
	if (servletPath != null) {
	    if ((href.endsWith("/")) && (servletPath.startsWith("/")))
		href += servletPath.substring(1);
	    else
		href += servletPath;
	}
	if ((href.endsWith("/")) && (path.startsWith("/")))
	    href += path.substring(1);
	else
	    href += path;
	if ((isFolder) && (!href.endsWith("/")))
	    href += "/";

	generatedXML.writeText(rewriteUrl(href));

	generatedXML.writeElement(null, "href", XMLWriter.CLOSING);

	String resourceName = path;
	int lastSlash = path.lastIndexOf('/');
	if (lastSlash != -1)
	    resourceName = resourceName.substring(lastSlash + 1);

	switch (type) {

	case FIND_ALL_PROP:

	    generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
	    generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

	    generatedXML.writeProperty(null, "creationdate", creationdate);
	    generatedXML.writeElement(null, "displayname", XMLWriter.OPENING);
	    generatedXML.writeData(resourceName);
	    generatedXML.writeElement(null, "displayname", XMLWriter.CLOSING);
	    if (!isFolder) {
		generatedXML.writeProperty(null, "getlastmodified",
			lastModified);
		generatedXML.writeProperty(null, "getcontentlength",
			resourceLength);
		String contentType = mimeType;
		if (contentType != null) {
		    generatedXML.writeProperty(null, "getcontenttype",
			    contentType);
		}
		generatedXML.writeProperty(null, "getetag", getETag(path,
			resourceLength, lastModified));
		generatedXML.writeElement(null, "resourcetype",
			XMLWriter.NO_CONTENT);
	    } else {
		generatedXML.writeElement(null, "resourcetype",
			XMLWriter.OPENING);
		generatedXML.writeElement(null, "collection",
			XMLWriter.NO_CONTENT);
		generatedXML.writeElement(null, "resourcetype",
			XMLWriter.CLOSING);
	    }

	    generatedXML.writeProperty(null, "source", "");
	    generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
	    generatedXML.writeElement(null, "status", XMLWriter.OPENING);
	    generatedXML.writeText(status);
	    generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
	    generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

	    break;

	case FIND_PROPERTY_NAMES:

	    generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
	    generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

	    generatedXML.writeElement(null, "creationdate",
		    XMLWriter.NO_CONTENT);
	    generatedXML
		    .writeElement(null, "displayname", XMLWriter.NO_CONTENT);
	    if (!isFolder) {
		generatedXML.writeElement(null, "getcontentlanguage",
			XMLWriter.NO_CONTENT);
		generatedXML.writeElement(null, "getcontentlength",
			XMLWriter.NO_CONTENT);
		generatedXML.writeElement(null, "getcontenttype",
			XMLWriter.NO_CONTENT);
		generatedXML
			.writeElement(null, "getetag", XMLWriter.NO_CONTENT);
		generatedXML.writeElement(null, "getlastmodified",
			XMLWriter.NO_CONTENT);
	    }
	    generatedXML.writeElement(null, "resourcetype",
		    XMLWriter.NO_CONTENT);
	    generatedXML.writeElement(null, "source", XMLWriter.NO_CONTENT);
	    generatedXML.writeElement(null, "lockdiscovery",
		    XMLWriter.NO_CONTENT);

	    generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
	    generatedXML.writeElement(null, "status", XMLWriter.OPENING);
	    generatedXML.writeText(status);
	    generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
	    generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

	    break;

	case FIND_BY_PROPERTY:

	    Vector propertiesNotFound = new Vector();

	    // Parse the list of properties

	    generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
	    generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

	    Enumeration properties = propertiesVector.elements();

	    while (properties.hasMoreElements()) {

		String property = (String) properties.nextElement();

		if (property.equals("creationdate")) {
		    generatedXML.writeProperty(null, "creationdate",
			    creationdate);
		} else if (property.equals("displayname")) {
		    generatedXML.writeElement(null, "displayname",
			    XMLWriter.OPENING);
		    generatedXML.writeData(resourceName);
		    generatedXML.writeElement(null, "displayname",
			    XMLWriter.CLOSING);
		} else if (property.equals("getcontentlanguage")) {
		    if (isFolder) {
			propertiesNotFound.addElement(property);
		    } else {
			generatedXML.writeElement(null, "getcontentlanguage",
				XMLWriter.NO_CONTENT);
		    }
		} else if (property.equals("getcontentlength")) {
		    if (isFolder) {
			propertiesNotFound.addElement(property);
		    } else {
			generatedXML.writeProperty(null, "getcontentlength",
				resourceLength);
		    }
		} else if (property.equals("getcontenttype")) {
		    if (isFolder) {
			propertiesNotFound.addElement(property);
		    } else {
			generatedXML.writeProperty(null, "getcontenttype",
				mimeType);
		    }
		} else if (property.equals("getetag")) {
		    if (isFolder) {
			propertiesNotFound.addElement(property);
		    } else {
			generatedXML.writeProperty(null, "getetag", getETag(
				path, resourceLength, lastModified));
		    }
		} else if (property.equals("getlastmodified")) {
		    if (isFolder) {
			propertiesNotFound.addElement(property);
		    } else {
			generatedXML.writeProperty(null, "getlastmodified",
				lastModified);
		    }
		} else if (property.equals("resourcetype")) {
		    if (isFolder) {
			generatedXML.writeElement(null, "resourcetype",
				XMLWriter.OPENING);
			generatedXML.writeElement(null, "collection",
				XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "resourcetype",
				XMLWriter.CLOSING);
		    } else {
			generatedXML.writeElement(null, "resourcetype",
				XMLWriter.NO_CONTENT);
		    }
		} else if (property.equals("source")) {
		    generatedXML.writeProperty(null, "source", "");
		} else {
		    propertiesNotFound.addElement(property);
		}

	    }

	    generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
	    generatedXML.writeElement(null, "status", XMLWriter.OPENING);
	    generatedXML.writeText(status);
	    generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
	    generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

	    Enumeration propertiesNotFoundList = propertiesNotFound.elements();

	    if (propertiesNotFoundList.hasMoreElements()) {

		status = new String("HTTP/1.1 " + WebdavStatus.SC_NOT_FOUND
			+ " "
			+ WebdavStatus.getStatusText(WebdavStatus.SC_NOT_FOUND));

		generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
		generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

		while (propertiesNotFoundList.hasMoreElements()) {
		    generatedXML.writeElement(null,
			    (String) propertiesNotFoundList.nextElement(),
			    XMLWriter.NO_CONTENT);
		}

		generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
		generatedXML.writeElement(null, "status", XMLWriter.OPENING);
		generatedXML.writeText(status);
		generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
		generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

	    }

	    break;

	}

	generatedXML.writeElement(null, "response", XMLWriter.CLOSING);

    }

    /**
     * Get creation date in ISO format.
     * 
     * @param creationDate
     *                the date in milliseconds
     * @return the Date in ISO format
     */
    private String getISOCreationDate(long creationDate) {
	StringBuffer creationDateValue = new StringBuffer(creationDateFormat
		.format(new Date(creationDate)));
	/*
	 * int offset = Calendar.getInstance().getTimeZone().getRawOffset() /
	 * 3600000; // FIXME ? if (offset < 0) { creationDateValue.append("-");
	 * offset = -offset; } else if (offset > 0) {
	 * creationDateValue.append("+"); } if (offset != 0) { if (offset < 10)
	 * creationDateValue.append("0"); creationDateValue.append(offset +
	 * ":00"); } else { creationDateValue.append("Z"); }
	 */
	return creationDateValue.toString();
    }

    /**
     * Get the ETag associated with a file.
     * 
     * @param path
     *                path to the resource
     * @param resourceLength
     *                filesize
     * @param lastModified
     *                last-modified date
     * @return the ETag
     */
    protected String getETag(String path, String resourceLength,
	    String lastModified) {
	// TODO create a real (?) ETag
	// parameter "path" is not used at the monent
	return "W/\"" + resourceLength + "-" + lastModified + "\"";

    }

    /**
     * URL rewriter.
     * 
     * @param path
     *                Path which has to be rewiten
     * @return the rewritten path
     */
    protected String rewriteUrl(String path) {
	return urlEncoder.encode(path);
    }

}
