package org.drools.guvnor.server.files;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class MockHTTPRequest implements HttpServletRequest {

	final private String uri;
	private Map<String, String> headers;
	ServletInputStream stream;
	String queryString;

	public MockHTTPRequest(String uri, Map<String, String> headers) {
		this.uri = uri;
		this.headers = headers;
	}

	public MockHTTPRequest(String uri, Map<String, String> headers, InputStream in) {
		this.uri = uri;
		this.headers = headers;
		this.stream = new MockInput(in);
	}

	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getDateHeader(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getHeader(String n) {
		return headers.get(n);
	}

	public Enumeration getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getIntHeader(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQueryString() {
		return queryString;
	}

	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRequestURI() {
		return uri;
	}

	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServletInputStream getInputStream() throws IOException {

		return stream ;
	}

	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getParameterValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

    public int getRemotePort() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getLocalName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getLocalAddr() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getLocalPort() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getServerPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub

	}


	static class MockInput extends ServletInputStream {

		private InputStream stream;

		MockInput(InputStream in) {
			this.stream = in;
		}

		public int read() throws IOException {
			// TODO Auto-generated method stub
			return stream.read();
		}

	}
}
