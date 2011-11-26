/*
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.inject.Alternative;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

@Alternative
public class MockHTTPResponse implements HttpServletResponse {

    private ByteArrayOutputStream byteArrayOut;
    private ServletOutputStream servletOut;
    private PrintWriter writer;
    private String contentType;
    Map<String, String> headers = new HashMap<String, String>();
    int errorCode;
    int status;


    public MockHTTPResponse() {
        byteArrayOut = new ByteArrayOutputStream();
        servletOut = new MockStream(byteArrayOut);
        writer = new PrintWriter(new OutputStreamWriter(servletOut));
    }

    public String extractContent() {
        return extractContent("UTF-8");
    }

    public String extractContent(String encoding) {
        try {
            return new String(extractContentBytes(), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported encoding (" + encoding + ").", e);
        }
    }

    public byte[] extractContentBytes() {
        IOUtils.closeQuietly(writer);
        return byteArrayOut.toByteArray();
    }

    public void addCookie(Cookie arg0) {
    }

    public void addDateHeader(String arg0, long arg1) {
    }

    public void addHeader(String arg0, String arg1) {
        headers.put(arg0, arg1);
    }

    public void addIntHeader(String arg0, int arg1) {
    }

    public boolean containsHeader(String a) {
        return this.headers.containsKey(a);
    }

    public String encodeRedirectURL(String arg0) {
        return null;
    }

    public String encodeRedirectUrl(String arg0) {
        return null;
    }

    public String encodeURL(String arg0) {
        return null;
    }

    public String encodeUrl(String arg0) {
        return null;
    }

    public void sendError(int i) throws IOException {
        this.errorCode = i;
    }

    public void sendError(int arg0, String arg1) throws IOException {
    }

    public void sendRedirect(String arg0) throws IOException {
    }

    public void setDateHeader(String arg0, long arg1) {
    }

    public void setHeader(String k, String v) {
        this.headers.put(k, v);
    }

    public void setIntHeader(String arg0, int arg1) {
    }

    public void setStatus(int arg0) {
        status = arg0;
    }

    public void setStatus(int arg0, String arg1) {
    }

    public void flushBuffer() throws IOException {
    }

    public int getBufferSize() {
        return 0;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public Locale getLocale() {
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return servletOut;
    }

    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    public void setCharacterEncoding(String s) {
    }

    public boolean isCommitted() {
        return false;
    }

    public void reset() {
    }

    public void resetBuffer() {
    }

    public void setBufferSize(int arg0) {
    }

    public void setContentLength(int arg0) {
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String s) {
        this.contentType = s;
    }

    public void setLocale(Locale arg0) {
    }

    static class MockStream extends ServletOutputStream {

        private OutputStream out;

        public MockStream(OutputStream out) {
            this.out = out;
        }

        public void write(int a) throws IOException {
            out.write(a);
        }

        public void flush() throws IOException {
            out.flush();
        }

        public void close() throws IOException {
            out.close();
        }

    }

}
