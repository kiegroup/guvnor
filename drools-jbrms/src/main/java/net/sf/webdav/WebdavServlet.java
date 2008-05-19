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

package net.sf.webdav;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.catalina.util.MD5Encoder;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.util.XMLWriter;
import org.drools.brms.server.files.RestAPIServlet;
import org.drools.brms.server.files.WebDAVImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Servlet which provides support for WebDAV level 2.
 *
 * the original class is org.apache.catalina.servlets.WebdavServlet
 */

public class WebdavServlet extends HttpServlet {

	// -------------------------------------------------------------- Constants

	private static final String METHOD_HEAD = "HEAD";

	private static final String METHOD_PROPFIND = "PROPFIND";

	private static final String METHOD_PROPPATCH = "PROPPATCH";

	private static final String METHOD_MKCOL = "MKCOL";

	private static final String METHOD_COPY = "COPY";

	private static final String METHOD_MOVE = "MOVE";

	private static final String METHOD_PUT = "PUT";

	private static final String METHOD_GET = "GET";

	private static final String METHOD_OPTIONS = "OPTIONS";

	private static final String METHOD_DELETE = "DELETE";

	// header names

	/**
	 * MD5 message digest provider.
	 */
	protected static MessageDigest md5Helper;

	/**
	 * The MD5 helper object for this class.
	 */
	protected static final MD5Encoder md5Encoder = new MD5Encoder();

	/**
	 * Default depth is infite.
	 */
	private static final int INFINITY = 3; // To limit tree browsing a bit

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

	/**
	 * size of the io-buffer
	 */
	private static int BUF_SIZE = 50000;

	/**
	 * Default namespace.
	 */
	protected static final String DEFAULT_NAMESPACE = "DAV:";

	/**
	 * Simple date format for the creation date ISO representation (partial).
	 */
	protected static final SimpleDateFormat creationDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	/**
	 * indicates that the store is readonly ?
	 */
	private static final boolean readOnly = false;

	static {
		creationDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Array containing the safe characters set.
	 */
	protected static URLEncoder urlEncoder;
	/**
	 * GMT timezone - all HTTP dates are on GMT
	 */
	static {
		urlEncoder = new URLEncoder();
		urlEncoder.addSafeCharacter('-');
		urlEncoder.addSafeCharacter('_');
		urlEncoder.addSafeCharacter('.');
		urlEncoder.addSafeCharacter('*');
		urlEncoder.addSafeCharacter('/');
	}

	// ----------------------------------------------------- Instance Variables

	private ResourceLocks fResLocks = null;

	private IWebdavStorage fStore = null;

	private static final String DEBUG_PARAMETER = "servletDebug";

	private static int fdebug = -1;

	private WebdavStoreFactory fFactory;

	private Hashtable fParameter;

	/**
	 * Initialize this servlet.
	 */
	public void init() throws ServletException {
		try {
			md5Helper = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}

		// Parameters from web.xml
//		String clazz = getServletConfig().getInitParameter(
//				"ResourceHandlerImplementation");
		try {
			fFactory = new WebdavStoreFactory(WebDAVImpl.class);
			// parameter
			fParameter = new Hashtable();
			Enumeration initParameterNames = getServletConfig()
					.getInitParameterNames();
			while (initParameterNames.hasMoreElements()) {
				String key = (String) initParameterNames.nextElement();
				fParameter.put(key, getServletConfig().getInitParameter(key));
			}


				fStore = fFactory.getStore();
				fResLocks = new ResourceLocks();
				String debugString = (String) fParameter.get(DEBUG_PARAMETER);
				if (debugString == null) {
					fdebug = 0;
				} else {
					fdebug = Integer.parseInt(debugString);
				}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}


	/**
	 * Will actually check if they are authed.
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String auth = req.getHeader("Authorization");
        if (!RestAPIServlet.allowUser(auth)) {
          resp.setHeader("WWW-Authenticate", "BASIC realm=\"users\"");
          resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
        	serviceInternal(req, resp);
        }
	}

	/**
	 * Handles the special WebDAV methods.
	 */
	private void serviceInternal(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String method = req.getMethod();

		if (fdebug == 1) {
			System.out.println("-----------");
			System.out.println("WebdavServlet\n request: method = " + method);
			System.out.println("Zeit: " + System.currentTimeMillis());
			System.out.println("path: " + getRelativePath(req));
			System.out.println("-----------");
			Enumeration e = req.getHeaderNames();
			while (e.hasMoreElements()) {
				String s = (String) e.nextElement();
				System.out.println("header: " + s + " " + req.getHeader(s));
			}
			e = req.getAttributeNames();
			while (e.hasMoreElements()) {
				String s = (String) e.nextElement();
				System.out.println("attribute: " + s + " "
						+ req.getAttribute(s));
			}
			e = req.getParameterNames();
			while (e.hasMoreElements()) {
				String s = (String) e.nextElement();
				System.out.println("parameter: " + s + " "
						+ req.getParameter(s));
			}
		}

		try {
			fStore.begin(req.getUserPrincipal(), fParameter);
			fStore.checkAuthentication();
			resp.setStatus(WebdavStatus.SC_OK);

			try {
				if (method.equals(METHOD_PROPFIND)) {
					doPropfind(req, resp);
				} else if (method.equals(METHOD_PROPPATCH)) {
					doProppatch(req, resp);
				} else if (method.equals(METHOD_MKCOL)) {
					doMkcol(req, resp);
				} else if (method.equals(METHOD_COPY)) {
					doCopy(req, resp);
				} else if (method.equals(METHOD_MOVE)) {
					doMove(req, resp);
				} else if (method.equals(METHOD_PUT)) {
					doPut(req, resp);
				} else if (method.equals(METHOD_GET)) {
					doGet(req, resp, true);
				} else if (method.equals(METHOD_OPTIONS)) {
					doOptions(req, resp);
				} else if (method.equals(METHOD_HEAD)) {
					doHead(req, resp);
				} else if (method.equals(METHOD_DELETE)) {
					doDelete(req, resp);
				} else {
					super.service(req, resp);
				}

				fStore.commit();
			} catch (IOException e) {
				e.printStackTrace();
				resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
				fStore.rollback();
				throw new ServletException(e);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	// protected long getLastModified

	/**
	 * goes recursive through all folders. used by propfind
	 *
	 */
	private void recursiveParseProperties(String currentPath,
			HttpServletRequest req,
			XMLWriter generatedXML, int propertyFindType, Vector properties,
			int depth) throws IOException {

		parseProperties(req, generatedXML, currentPath,
				propertyFindType, properties);
		String[] names = fStore.getChildrenNames(currentPath);
		if ((names!=null) && (depth > 0)) {

			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				String newPath = currentPath;
				if (!(newPath.endsWith("/"))) {
					newPath += "/";
				}
				newPath += name;
				recursiveParseProperties(newPath, req, generatedXML,
						propertyFindType, properties, depth - 1);
			}
		}
	}

	/**
	 * overwrites propNode and type, parsed from xml input stream
	 *
	 * @param propNode
	 * @param type
	 * @param req
	 * @throws ServletException
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

	private String getParentPath(String path) {
		int slash = path.lastIndexOf('/');
		if (slash != -1) {
			return path.substring(0, slash);
		}
		return null;
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

	/**
	 * Return the relative path associated with this servlet.
	 *
	 * @param request
	 *            The servlet request we are processing
	 */
	protected String getRelativePath(HttpServletRequest request) {

		// Are we being processed by a RequestDispatcher.include()?
		if (request.getAttribute("javax.servlet.include.request_uri") != null) {
			String result = (String) request
					.getAttribute("javax.servlet.include.path_info");
			if (result == null)
				result = (String) request
						.getAttribute("javax.servlet.include.servlet_path");
			if ((result == null) || (result.equals("")))
				result = "/";
			return (result);
		}

		// No, extract the desired path directly from the request
		String result = request.getPathInfo();
		if (result == null) {
			result = request.getServletPath();
		}
		if ((result == null) || (result.equals(""))) {
			result = "/";
		}
		return (result);

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
	 * reads the depth header from the request and returns it as a int
	 *
	 * @param req
	 * @return
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
	 * @return the path without trailing /
	 */
	private String getCleanPath(String path) {

		if (path.endsWith("/") && path.length() > 1)
			path = path.substring(0, path.length() - 1);
		return path;
	}

	/**
	 * OPTIONS Method.</br>
	 *
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp
			) throws ServletException, IOException {

		String lockOwner = "doOptions" + System.currentTimeMillis()
				+ req.toString();
		String path = getRelativePath(req);
		if (fResLocks.lock(path, lockOwner, false, 0)) {
			try {
				resp.addHeader("DAV", "1, 2");

				String methodsAllowed = determineMethodsAllowed(path,fStore.objectExists(path),fStore.isFolder(path));
				resp.addHeader("Allow", methodsAllowed);
				resp.addHeader("MS-Author-Via", "DAV");
			} finally {
				fResLocks.unlock(path, lockOwner);
			}
		} else {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * PROPFIND Method.
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doPropfind(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// Retrieve the resources
		String lockOwner = "doPropfind" + System.currentTimeMillis()
				+ req.toString();
		String path = getRelativePath(req);
		int depth = getDepth(req);
		if (fResLocks.lock(path, lockOwner, false, depth)) {
			try {
				if (!fStore.objectExists(path)) {
					resp
							.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
					// we do not to continue since there is no root
					// resource
				}

				Vector properties = null;
				path = getCleanPath(getRelativePath(req));


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
				generatedXML.writeElement(null, "multistatus"
						+ generateNamespaceDeclarations(), XMLWriter.OPENING);
				if (depth == 0) {
					parseProperties(req, generatedXML, path,
							propertyFindType, properties);
				} else {
					recursiveParseProperties(path, req, generatedXML,
							propertyFindType, properties, depth);
				}
				generatedXML.writeElement(null, "multistatus",
						XMLWriter.CLOSING);
				generatedXML.sendData();
			} finally {
				fResLocks.unlock(path, lockOwner);
			}
		} else {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * PROPPATCH Method.
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doProppatch(HttpServletRequest req,
			HttpServletResponse resp)
			throws ServletException, IOException {

		if (readOnly) {
			resp.sendError(WebdavStatus.SC_FORBIDDEN);

		} else

			resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
		// TODO implement proppatch
	}

	/**
	 * GET Method
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			boolean includeBody) throws ServletException,
			IOException {

		String lockOwner = "doGet" + System.currentTimeMillis()
				+ req.toString();
		String path = getRelativePath(req);
		if (fResLocks.lock(path, lockOwner, false, 0)) {
			try {

				if (fStore.isResource(path)) {
					// path points to a file but ends with / or \
					if (path.endsWith("/") || (path.endsWith("\\"))) {
						resp.sendError(HttpServletResponse.SC_NOT_FOUND, req
								.getRequestURI());
					} else {

						// setting headers
						long lastModified = fStore.getLastModified(path)
								.getTime();
						resp.setDateHeader("last-modified", lastModified);

						long resourceLength = fStore.getResourceLength(path);
						if (resourceLength > 0) {
							if (resourceLength <= Integer.MAX_VALUE) {
								resp.setContentLength((int) resourceLength);
							} else {
								resp.setHeader("content-length", ""
										+ resourceLength);
								// is "content-length" the right header? is long
								// a valid format?
							}

						}


						String mimeType = getServletContext().getMimeType(path);
						if (mimeType != null) {
							resp.setContentType(mimeType);
						}

						//resp.setHeader("ETag", getETag(path));
						// resp.setHeader
						// ("Content-Language", "en-us");
						// resp.setHeader("name","nameFromHeader");
						// resp.setHeader("parentname",getParentPath(path));
						// resp.setHeader("href","hrefFromHeader");
						// resp.setHeader("ishidden","f");
						// resp.setHeader("iscollection","f");
						// resp.setHeader("isreadonly","f");
						// resp.setHeader("getcontenttype","contenttypeFromHeader");
						// resp.setHeader("getcontentlanguage", "en-us");
						// resp.setHeader("contentclass","contentclassFromHeader");
						// resp.setDateHeader("creationdate",
						// store.getLastModified(
						// path).getTime());
						// resp.setDateHeader("getlastmodified",
						// store.getLastModified(
						// path).getTime());
						// resp.setDateHeader("lastaccessed",
						// store.getLastModified(
						// path).getTime());
						// resp.setHeader("getcontentlength",""+store.getResourceLength(path));
						// resp.setHeader("resourcetype","resourcetypeFromHeader");
						// resp.setHeader("isstructureddocument","f");
						// resp.setHeader("defaultdocument","f");
						// resp.setHeader("displayname","displaynameFromHeader");
						// resp.setHeader("isroot","f");

						if (includeBody) {
							OutputStream out = resp.getOutputStream();
							InputStream in = fStore.getResourceContent(path);
							try {
								int read = -1;
								byte[] copyBuffer = new byte[BUF_SIZE];

								while ((read = in.read(copyBuffer, 0,
										copyBuffer.length)) != -1) {
									out.write(copyBuffer, 0, read);
								}

							} finally {

								in.close();
								out.flush();
								out.close();
							}
						}
					}
				} else {
					if (includeBody && fStore.isFolder(path)) {
						// TODO some folder response (for browsers, DAV tools
						// use propfind) in html?
						OutputStream out = resp.getOutputStream();
						String[] children = fStore.getChildrenNames(path);
						StringBuffer childrenTemp = new StringBuffer();
						childrenTemp.append("Contents of this Folder:\n");
						for (int i = 0; i < children.length; i++) {
							childrenTemp.append(children[i]);
							childrenTemp.append("\n");
						}
						out.write(childrenTemp.toString().getBytes());
					} else {
						if (!fStore.objectExists(path)) {
							resp.sendError(HttpServletResponse.SC_NOT_FOUND,
									req.getRequestURI());
						}

					}
				}
			} finally {
				fResLocks.unlock(path, lockOwner);
			}
		} else {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * HEAD Method.
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp, false);
	}

	/**
	 * MKCOL Method.
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doMkcol(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (req.getContentLength() != 0) {
			resp.sendError(WebdavStatus.SC_NOT_IMPLEMENTED);
		} else {

			if (!readOnly) {
				// not readonly
				String path = getRelativePath(req);
				String parentPath = getParentPath(path);
				String lockOwner = "doMkcol" + System.currentTimeMillis()
						+ req.toString();
				if (fResLocks.lock(path, lockOwner, true, 0)) {
					try {

						if (parentPath != null && fStore.isFolder(parentPath)) {
							if (!fStore.objectExists(path)) {

								fStore.createFolder(path);

							} else {
								// object already exists
								String methodsAllowed = determineMethodsAllowed(path,true,fStore.isFolder(path));
								resp.addHeader("Allow", methodsAllowed);
								resp
										.sendError(WebdavStatus.SC_METHOD_NOT_ALLOWED);
							}
						} else {
							resp.sendError(WebdavStatus.SC_CONFLICT);
						}
					} finally {
						fResLocks.unlock(path, lockOwner);
					}
				} else {
					resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
				}
			} else {
				resp.sendError(WebdavStatus.SC_FORBIDDEN);
			}
		}

	}

	/**
	 * DELETE Method.
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (!readOnly) {
			String path = getRelativePath(req);
			String lockOwner = "doDelete" + System.currentTimeMillis()
					+ req.toString();
			if (fResLocks.lock(path, lockOwner, true, -1)) {
				try {
					Hashtable errorList = new Hashtable();
					deleteResource(path, errorList, req, resp);
					if (!errorList.isEmpty()) {
						sendReport(req, resp, errorList);
					}
				} finally {
					fResLocks.unlock(path, lockOwner);
				}
			} else {
				resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			resp.sendError(WebdavStatus.SC_FORBIDDEN);
		}

	}

	/**
	 * Process a POST request for the specified resource.
	 *
	 * @param req
	 *            The servlet request we are processing
	 * @param resp
	 *            The servlet response we are creating
	 *
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet-specified error occurs
	 */
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!readOnly) {
			String path = getRelativePath(req);
			String parentPath = getParentPath(path);
			String lockOwner = "doPut" + System.currentTimeMillis()
					+ req.toString();
			if (fResLocks.lock(path, lockOwner, true, -1)) {
				try {
					if (parentPath != null && fStore.isFolder(parentPath)
							&& !fStore.isFolder(path)) {
						if (!fStore.objectExists(path)) {
							fStore.createResource(path);
							resp.setStatus(HttpServletResponse.SC_CREATED);
						} else {
							resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
						}
						fStore.setResourceContent(path, req.getInputStream(),
								null, null);
						resp.setContentLength((int) fStore
								.getResourceLength(path));
					} else {
						resp.sendError(WebdavStatus.SC_CONFLICT);
					}

				} finally {
					fResLocks.unlock(path, lockOwner);
				}
			} else {
				resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			resp.sendError(WebdavStatus.SC_FORBIDDEN);
		}

	}

	/**
	 * COPY Method.
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doCopy(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String path = getRelativePath(req);
		if (!readOnly) {
			String lockOwner = "doCopy" + System.currentTimeMillis()
					+ req.toString();
			if (fResLocks.lock(path, lockOwner, false, -1)) {
				try {
					copyResource(req, resp);
				} finally {
					fResLocks.unlock(path, lockOwner);
				}
			} else {
				resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			}

		} else {
			resp.sendError(WebdavStatus.SC_FORBIDDEN);
		}

	}

	/**
	 * MOVE Method.
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doMove(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!readOnly) {

			String path = getRelativePath(req);
			String lockOwner = "doMove" + System.currentTimeMillis()
					+ req.toString();
			if (fResLocks.lock(path, lockOwner, false, -1)) {
				try {
					if (copyResource(req, resp)) {

						Hashtable errorList = new Hashtable();
						deleteResource(path, errorList, req, resp);
						if (!errorList.isEmpty()) {
							sendReport(req, resp, errorList);
						}

					} else {
						resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
					}
				} finally {
					fResLocks.unlock(path, lockOwner);
				}
			} else {
				resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			resp.sendError(WebdavStatus.SC_FORBIDDEN);

		}
	}

	// -------------------------------------------------------- Private Methods

	/**
	 * Generate the namespace declarations.
	 */
	private String generateNamespaceDeclarations() {
		return " xmlns=\"" + DEFAULT_NAMESPACE + "\"";
	}

	/**
	 * Copy a resource.
	 *
	 * @param req
	 *            Servlet request
	 * @param resp
	 *            Servlet response
	 * @return boolean true if the copy is successful
	 */
	private boolean copyResource(HttpServletRequest req,
			HttpServletResponse resp)
			throws ServletException, IOException {

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
		if (fResLocks.lock(destinationPath, lockOwner, true, -1)) {
			try {

				// Retrieve the resources
				if (!fStore.objectExists(path)) {
					resp
							.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return false;
				}

				boolean exists = fStore.objectExists(destinationPath);
				Hashtable errorList = new Hashtable();

				if (overwrite) {

					// Delete destination resource, if it exists
					if (exists) {
						deleteResource(destinationPath, errorList, req, resp);

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
				fResLocks.unlock(destinationPath, lockOwner);
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
	 *            path from where to read
	 * @param destinationPath
	 *            path where to write
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @throws IOException
	 *             if an error in the underlying store occurs
	 * @throws ServletException
	 */
	private void copy(String sourcePath, String destinationPath,
			Hashtable errorList, HttpServletRequest req,
			HttpServletResponse resp) throws IOException,
			ServletException {

		if (fStore.isResource(sourcePath)) {
			fStore.createResource(destinationPath);
			fStore.setResourceContent(destinationPath, fStore
					.getResourceContent(sourcePath), null, null);
		} else {

			if (fStore.isFolder(sourcePath)) {
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
	 * @param destinationPath
	 * @param errorList
	 * @param req
	 * @param resp
	 * @param store
	 * @throws IOException
	 * @throws ServletException
	 */
	private void copyFolder(String sourcePath, String destinationPath,
			Hashtable errorList, HttpServletRequest req,
			HttpServletResponse resp) throws IOException,
			ServletException {

		fStore.createFolder(destinationPath);
		boolean infiniteDepth = true;
		if (req.getHeader("depth") != null) {
			if (req.getHeader("depth").equals("0")) {
				infiniteDepth = false;
			}
		}
		if (infiniteDepth) {
			String[] children = fStore.getChildrenNames(sourcePath);

			for (int i = children.length - 1; i >= 0; i--) {
				children[i] = "/" + children[i];
				try {
					if (fStore.isResource(sourcePath + children[i])) {
						fStore.createResource(destinationPath + children[i]);
						fStore.setResourceContent(destinationPath + children[i],
								fStore.getResourceContent(sourcePath
										+ children[i]), null, null);

					} else {
						copyFolder(sourcePath + children[i], destinationPath
								+ children[i], errorList, req, resp);
					}
				} catch (IOException e) {
					errorList.put(destinationPath + children[i], new Integer(
							WebdavStatus.SC_INTERNAL_SERVER_ERROR));
				}
			}
		}
	}

	/**
	 * deletes the recources at "path"
	 *
	 * @param path
	 * @param errorList
	 * @param req
	 * @param resp
	 * @param store
	 * @throws IOException
	 * @throws ServletException
	 */
	private void deleteResource(String path, Hashtable errorList,
			HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setStatus(WebdavStatus.SC_NO_CONTENT);
		if (!readOnly) {

			if (fStore.isResource(path)) {
				fStore.removeObject(path);
			} else {
				if (fStore.isFolder(path)) {

					deleteFolder(path, errorList, req, resp);
					fStore.removeObject(path);
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
	 *            the folder to be deleted
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param store
	 *            class that handles the actual storing
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	private void deleteFolder(String path, Hashtable errorList,
			HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		String[] children = fStore.getChildrenNames(path);
		for (int i = children.length - 1; i >= 0; i--) {
			children[i] = "/" + children[i];
			try {
				if (fStore.isResource(path + children[i])) {
					fStore.removeObject(path + children[i]);

				} else {
					deleteFolder(path + children[i], errorList, req, resp);

					fStore.removeObject(path + children[i]);

				}
			} catch (IOException e) {
				errorList.put(path + children[i], new Integer(
						WebdavStatus.SC_INTERNAL_SERVER_ERROR));
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
	 *            Path to be normalized
	 */
	protected String normalize(String path) {

		if (path == null)
			return null;

		// Create a place for the normalized path
		String normalized = path;

		// if (normalized == null)
		// return (null);

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

	/**
	 * Send a multistatus element containing a complete error report to the
	 * client.
	 *
	 * @param req
	 *            Servlet request
	 * @param resp
	 *            Servlet response
	 * @param errorList
	 *            List of error to be displayed
	 */
	private void sendReport(HttpServletRequest req, HttpServletResponse resp,
			Hashtable errorList) throws ServletException, IOException {

		resp.setStatus(WebdavStatus.SC_MULTI_STATUS);

		String absoluteUri = req.getRequestURI();
		String relativePath = getRelativePath(req);

		XMLWriter generatedXML = new XMLWriter();
		generatedXML.writeXMLHeader();

		generatedXML.writeElement(null, "multistatus"
				+ generateNamespaceDeclarations(), XMLWriter.OPENING);

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

	/**
	 * Propfind helper method.
	 *
	 * @param req
	 *            The servlet request
	 * @param resources
	 *            Resources object associated with this context
	 * @param generatedXML
	 *            XML response to the Propfind request
	 * @param path
	 *            Path of the current resource
	 * @param type
	 *            Propfind type
	 * @param propertiesVector
	 *            If the propfind type is find properties by name, then this
	 *            Vector contains those properties
	 */
	private void parseProperties(HttpServletRequest req,
			XMLWriter generatedXML, String path, int type,
			Vector propertiesVector) throws IOException {

		String creationdate = getISOCreationDate(fStore.getCreationDate(path)
				.getTime());
		boolean isFolder = fStore.isFolder(path);
		String lastModified = fStore.getLastModified(path).toString();
		String resourceLength = String.valueOf(fStore.getResourceLength(path));

		// ResourceInfo resourceInfo = new ResourceInfo(path, resources);

		generatedXML.writeElement(null, "response", XMLWriter.OPENING);
		String status = new String("HTTP/1.1 " + WebdavStatus.SC_OK + " "
				+ WebdavStatus.getStatusText(WebdavStatus.SC_OK));

		// Generating href element
		generatedXML.writeElement(null, "href", XMLWriter.OPENING);

		String href = req.getContextPath();
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
				String contentType = getServletContext().getMimeType(path);
				if (contentType != null) {
					generatedXML.writeProperty(null, "getcontenttype",
							contentType);
				}
				generatedXML.writeProperty(null, "getetag",
						getETag(path,resourceLength,lastModified));
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

			// String supportedLocks = "<lockentry>"
			// + "<lockscope><exclusive/></lockscope>"
			// + "<locktype><write/></locktype>" + "</lockentry>"
			// + "<lockentry>" + "<lockscope><shared/></lockscope>"
			// + "<locktype><write/></locktype>" + "</lockentry>";
			// generatedXML.writeElement(null, "supportedlock",
			// XMLWriter.OPENING);
			// generatedXML.writeText(supportedLocks);
			// generatedXML.writeElement(null, "supportedlock",
			// XMLWriter.CLOSING);
			//
			// generateLockDiscovery(path, generatedXML);

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
								getServletContext().getMimeType(path));
					}
				} else if (property.equals("getetag")) {
					if (isFolder) {
						propertiesNotFound.addElement(property);
					} else {
						generatedXML.writeProperty(null, "getetag", getETag(
								path,resourceLength,lastModified));
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
					// } else if (property.equals("supportedlock")) {
					// supportedLocks = "<lockentry>"
					// + "<lockscope><exclusive/></lockscope>"
					// + "<locktype><write/></locktype>" + "</lockentry>"
					// + "<lockentry>"
					// + "<lockscope><shared/></lockscope>"
					// + "<locktype><write/></locktype>" + "</lockentry>";
					// generatedXML.writeElement(null, "supportedlock",
					// XMLWriter.OPENING);
					// generatedXML.writeText(supportedLocks);
					// generatedXML.writeElement(null, "supportedlock",
					// XMLWriter.CLOSING);
					// } else if (property.equals("lockdiscovery")) {
					// if (!generateLockDiscovery(path, generatedXML))
					// propertiesNotFound.addElement(property);
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
	 * Get the ETag associated with a file.
	 *
	 * @param resourceInfo
	 *            File object
	 */
	protected String getETag(String path, String resourceLength, String lastModified)
			throws IOException {
		// if (resourceInfo.strongETag != null) {
		// return resourceInfo.strongETag;
		// } else if (resourceInfo.weakETag != null) {
		// return resourceInfo.weakETag;
		// } else {
		return "W/\"" + resourceLength + "-"
				+ lastModified + "\"";
		// }
	}

	/**
	 * URL rewriter.
	 *
	 * @param path
	 *            Path which has to be rewiten
	 */
	protected String rewriteUrl(String path) {
		return urlEncoder.encode(path);
	}

	/**
	 * Get creation date in ISO format.
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
	 * Determines the methods normally allowed for the resource.
	 *
	 */
	private String determineMethodsAllowed(String uri, boolean exists, boolean isFolder) {
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

};
