/**
* Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.files;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
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

    final private URI uri;
    private Map<String, String> headers;
    ServletInputStream stream;
    String queryString;
    Map<String, String> parameters;
    public String method;

    public StringBuffer url = new StringBuffer("http://foo.com");


    public MockHTTPRequest(String uri, Map<String, String> headers) {
        try {
            this.uri = new URI (uri);
            this.headers = headers;
        } catch (URISyntaxException e) {
            throw new RuntimeException (e);
        }
    }

    public MockHTTPRequest(String uri, Map<String, String> headers, Map<String, String> parameters) {
        try {
            this.uri = new URI (uri);
            this.headers = headers;
            this.parameters = parameters;
        } catch (URISyntaxException e) {
            throw new RuntimeException (e);
        }
    }

    public MockHTTPRequest(String uri, Map<String, String> headers, InputStream in) {
        try {
            this.uri = new URI (uri);
            this.headers = headers;
            this.stream = new MockInput(in);
        } catch (URISyntaxException e) {
            throw new RuntimeException (e);
        }
    }

    public MockHTTPRequest(String uri, Map<String, String> headers,
            Map<String,String> parameters, InputStream in)
    {
        try {
            this.uri = new URI (uri);
            this.headers = headers;
            this.parameters = parameters;
            this.stream = new MockInput(in);
        } catch (URISyntaxException e) {
            throw new RuntimeException (e);
        }
    }

    public String getAuthType() {
        return null;
    }

    public String getContextPath() {
        return uri.getPath();
    }

    public Cookie[] getCookies() {
        return null;
    }

    public long getDateHeader(String arg0) {
        return 0;
    }

    public String getHeader(String n) {
        return headers.get(n);
    }

    public Enumeration getHeaderNames() {
        return null;
    }

    public Enumeration getHeaders(String arg0) {
        return null;
    }

    public int getIntHeader(String arg0) {
        return 0;
    }

    public String getMethod() {
        return method;
    }

    public String getPathInfo() {
        return null;
    }

    public String getPathTranslated() {
        return uri.getPath();
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRemoteUser() {
        return null;
    }

    public String getRequestURI() {
        return uri.toString();
    }

    public StringBuffer getRequestURL() {
        return url;
    }

    public String getRequestedSessionId() {
        return null;
    }

    public String getServletPath() {
        return null;
    }

    public HttpSession getSession() {
        return null;
    }

    public HttpSession getSession(boolean arg0) {
        return null;
    }

    public Principal getUserPrincipal() {
        return null;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    public boolean isRequestedSessionIdValid() {
        return false;
    }

    public boolean isUserInRole(String arg0) {
        return false;
    }

    public Object getAttribute(String arg0) {
        return null;
    }

    public Enumeration getAttributeNames() {
        return null;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public int getContentLength() {
        return 0;
    }

    public String getContentType() {
        return null;
    }

    public ServletInputStream getInputStream() throws IOException {
        return stream ;
    }

    public Locale getLocale() {
        return null;
    }

    public Enumeration getLocales() {
        return null;
    }

    public String getParameter(String arg0) {
        return parameters.get(arg0);
    }

    public Map getParameterMap() {
        return parameters;
    }

    public Enumeration getParameterNames() {
        return null;
    }

    public String[] getParameterValues(String arg0) {
        return null;
    }

    public String getProtocol() {
        return null;
    }

    public BufferedReader getReader() throws IOException {
        return null;
    }

    public String getRealPath(String arg0) {
        return null;
    }

    public int getRemotePort() {
        return 0;
    }

    public String getLocalName() {
        return null;
    }

    public String getLocalAddr() {
        return null;
    }

    public int getLocalPort() {
        return 0;
    }

    public String getRemoteAddr() {
        return null;
    }

    public String getRemoteHost() {
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {
        return null;
    }

    public String getScheme() {
        return null;
    }

    public String getServerName() {
        return null;
    }

    public int getServerPort() {
        return 0;
    }

    public boolean isSecure() {
        return false;
    }

    public void removeAttribute(String arg0) {
    }

    public void setAttribute(String arg0, Object arg1) {
    }

    public void setCharacterEncoding(String arg0)
            throws UnsupportedEncodingException {
    }

    static class MockInput extends ServletInputStream {

        private InputStream stream;

        MockInput(InputStream in) {
            this.stream = in;
        }

        public int read() throws IOException {
            return stream.read();
        }

    }
    }
