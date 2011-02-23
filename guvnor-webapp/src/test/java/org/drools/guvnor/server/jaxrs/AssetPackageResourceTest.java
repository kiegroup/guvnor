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

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.drools.guvnor.server.jaxrs.jaxb.Asset;
import org.junit.*;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.junit.Assert.assertEquals;

public class AssetPackageResourceTest extends RestTestingBase {

    @Override @Before
    public void setUp() throws Exception {
        super.setUp();
        dispatcher.getRegistry().addPerRequestResource(PackageResource.class);
    }

    @Test
    public void testGetAssetsAsAtom() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/assets");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testGetAssetsAsJaxB() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/assets");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testGetAssetsAsJson() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/assets");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testGetAssetAsAtom() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/asset/MortgageModel");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));

    }

    @Test
    public void testGetAssetAsJaxB() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/asset/MortgageModel");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testGetAssetAsJson() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/asset/MortgageModel");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testGetAssetSource() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/asset/MortgageModel/source");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testGetAssetBinary() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/asset/MortgageModel/binary");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testUpdateAssetFromAtom() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/asset/MortgageModel");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());

        Abdera abdera = new Abdera();
        Parser parser = abdera.getParser();
        Document<Entry> document = parser.parse(connection.getInputStream());
        connection.disconnect();

        Entry e = document.getRoot();
        e.addAuthor("Tester X McTestness");

        url = new URL(generateBaseUrl() + "/packages/mortgages/asset/MortgageModel");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-type", MediaType.APPLICATION_ATOM_XML);
        conn.setDoOutput(true);
        e.writeTo(conn.getOutputStream());

        assertEquals(204, conn.getResponseCode());
        conn.disconnect();
    }

    @Ignore @Test
    public void testUpdateAssetFromJaxB() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/asset/MortgageModel");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        JAXBContext context = JAXBContext.newInstance(Asset.class);
        Unmarshaller un = context.createUnmarshaller();
        Asset a = (Asset) un.unmarshal(br);
        a.setDescription("An updated description.");
        a.getMetadata().setLastModified(new Date(System.currentTimeMillis()));
        connection.disconnect();

        HttpURLConnection conn2 = (HttpURLConnection)url.openConnection();
        Marshaller ma = context.createMarshaller();
        conn2.setRequestMethod("PUT");
        conn2.setRequestProperty("Content-Type", MediaType.APPLICATION_XML);
        conn2.setRequestProperty("Content-Length", Integer.toString(a.toString().getBytes().length));
        conn2.setUseCaches (false);
        conn2.setDoInput(true);
        conn2.setDoOutput(true);
        ma.marshal(a, conn2.getOutputStream());
        assertEquals (200, connection.getResponseCode());
        conn2.disconnect();
    }

    @Ignore @Test
    public void testUpdateAssetFromJson() throws Exception {
        //TODO: implement test
    }
}
