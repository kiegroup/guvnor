/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.jaxrs;

import org.junit.*;

import javax.ws.rs.core.MediaType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

//import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.junit.Assert.assertEquals;

public class CategoryResourceTest extends RestTestingBase {

    private String category = "Home Mortgage";

    @Before @Override
    public void setUpGuvnorTestBase() {
        super.setUpGuvnorTestBase();
        //dispatcher.getRegistry().addPerRequestResource(CategoryResource.class);
    }

    @Test @Ignore
    public void testGetAssetsByCategoryAsAtom() throws Exception {
        URL url = new URL(generateBaseUrl() + "/categories/" + URLEncoder.encode(category, "UTF-8"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));
    }

    @Test @Ignore
    public void testGetAssetsByCategoryAsJson() throws Exception {
        URL url = new URL(generateBaseUrl() + "/categories/" + URLEncoder.encode(category, "UTF-8"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));

    }

    @Test @Ignore
    public void testGetAssetsByCategoryAsJaxb() throws Exception {
        URL url = new URL(generateBaseUrl() + "/categories/" + URLEncoder.encode(category, "UTF-8"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));
    }

    @Test @Ignore
    public void testGetAssetsByCategoryAndPageAsAtom() throws Exception {
        URL url = new URL(generateBaseUrl() + "/categories/" + URLEncoder.encode(category, "UTF-8") + "/page/0");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));
    }

    @Test @Ignore
    public void testGetAssetsByCategoryAndPageAsJson() throws Exception {
        URL url = new URL(generateBaseUrl() + "/categories/" + URLEncoder.encode(category, "UTF-8") + "/page/0");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));

    }

    @Test @Ignore
    public void testGetAssetsByCategoryAndPageAsJaxb() throws Exception {
        URL url = new URL(generateBaseUrl() + "/categories/" + URLEncoder.encode(category, "UTF-8") + "/page/0");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));
    }
}
