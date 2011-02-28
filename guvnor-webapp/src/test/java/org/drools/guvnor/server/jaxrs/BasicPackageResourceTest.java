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

import java.io.*;
import java.net.*;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.junit.*;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import static org.junit.Assert.*;
import static org.jboss.resteasy.test.TestPortProvider.*;


public class BasicPackageResourceTest extends RestTestingBase {

    @Before @Override
    public void setUp() throws Exception {
        super.setUp();
        dispatcher.getRegistry().addPerRequestResource(PackageResource.class);
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test
    public void testGetPackagesForJSON() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();        
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());        
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        logger.log (LogLevel, GetContent(connection));
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test
    public void testGetPackagesForXML() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        logger.log (LogLevel, GetContent(connection));
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test
    public void testGetPackagesForAtom() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test
    public void testGetPackageForJSON() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        logger.log (LogLevel, GetContent(connection));
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test
    public void testGetPackageForXML() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test
    public void testGetPackageForAtom() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    /* Package Creation */
    @Test
    public void testCreatePackageFromJAXB() throws Exception {
        Package p = createTestPackage("TestCreatePackageFromJAXB");
        JAXBContext context = JAXBContext.newInstance(p.getClass());
        Marshaller marshaller = context.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(p, sw);
        String xml = sw.toString();
        URL url = new URL(generateBaseUrl() + "/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_XML);
        connection.setRequestProperty("Content-Length", Integer.toString(xml.getBytes().length));
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //Send request
        DataOutputStream wr = new DataOutputStream (
              connection.getOutputStream ());
        wr.writeBytes (xml);
        wr.flush ();
        wr.close ();

        assertEquals (204, connection.getResponseCode());
    }

    /* Package Creation */
    @Test
    public void testCreatePackageFromDRLAsEntry() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.setDoOutput(true);

        //Send request
        BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("simple_rules.drl")));
        DataOutputStream dos = new DataOutputStream (
              connection.getOutputStream ());
        while (br.ready())
            dos.writeBytes (br.readLine());
        dos.flush();
        dos.close();

        /* Retry with a -1 from the connection */
        if (connection.getResponseCode() == -1) {
            url = new URL(generateBaseUrl() + "/packages");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
            connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
            connection.setDoOutput(true);

            //Send request
            br = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream("simple_rules.drl")));
            dos = new DataOutputStream (
                  connection.getOutputStream ());
            while (br.ready())
                dos.writeBytes (br.readLine());
            dos.flush();
            dos.close();
        }

        assertEquals (200, connection.getResponseCode());
        assertEquals (MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testCreatePackageFromDRLAsJson() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.setDoOutput(true);

        //Send request
        BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("simple_rules2.drl")));
        DataOutputStream dos = new DataOutputStream (
              connection.getOutputStream ());
        while (br.ready())
            dos.writeBytes (br.readLine());
        dos.flush();
        dos.close();

        assertEquals (200, connection.getResponseCode());
        assertEquals (MediaType.APPLICATION_JSON, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testCreatePackageFromDRLAsJaxB() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.setDoOutput(true);

        //Send request
        BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("simple_rules3.drl")));
        DataOutputStream dos = new DataOutputStream (
              connection.getOutputStream ());
        while (br.ready())
            dos.writeBytes (br.readLine());
        dos.flush();
        dos.close();

        assertEquals (200, connection.getResponseCode());
        assertEquals (MediaType.APPLICATION_XML, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testCreatePackageFromAtom() throws Exception {
        Package p = createTestPackage("TestCreatePackageFromAtom");
        Entry e = toPackageEntry(p);
        e.setTitle("TestAtomPackageCreation");

        URL url = new URL(generateBaseUrl() + "/packages");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", MediaType.APPLICATION_ATOM_XML);
        conn.setRequestProperty("Content-Length", Integer.toString(e.toString().getBytes().length));
        conn.setDoOutput(true);
        e.writeTo(conn.getOutputStream());
        assertEquals(204, conn.getResponseCode());
        conn.disconnect();
    }

    private Entry toPackageEntry (Package p) throws Exception {
        Abdera a = new Abdera();
        Entry e = a.newEntry();
        e.setTitle(p.getTitle());
        e.setUpdated(p.getMetadata().getLastModified());
        e.setPublished(p.getMetadata().getCreated());
        e.addLink("self", generateBaseUrl() + "/packages/" + p.getTitle());
        e.setSummary(p.getDescription());
        return e;
    }

    @Ignore @Test
    public void testCreatePackageFromJson() {
        //TODO: implement test
    }

    @Test
    public void testGetPackageSource() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/source");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.WILDCARD);
        connection.connect();

        /* Try again with a -1 response */
        if (connection.getResponseCode() == -1) {
            url = new URL(generateBaseUrl() + "/packages/mortgages/source");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", MediaType.WILDCARD);
            connection.connect();
        }

        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testGetPackageBinary () throws Exception {
        /* Tests package compilation in addition to byte retrieval */
        URL url = new URL(generateBaseUrl() + "/packages/mortgages/binary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();

        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testUpdatePackageFromJAXB() throws Exception {
        org.drools.guvnor.server.jaxrs.jaxb.Package p = createTestPackage("TestCreatePackageFromJAXB");
        JAXBContext context = JAXBContext.newInstance(p.getClass());
        Marshaller marshaller = context.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(p, sw);
        String xml = sw.toString();
        URL url = new URL(generateBaseUrl() + "/packages/TestCreatePackageFromJAXB");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_XML);
        connection.setRequestProperty("Content-Length", Integer.toString(xml.getBytes().length));
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //Send request
        DataOutputStream wr = new DataOutputStream (
              connection.getOutputStream ());
        wr.writeBytes (xml);
        wr.flush ();
        wr.close ();

        assertEquals (204, connection.getResponseCode());

    }

    @Ignore @Test
    public void testUpdatePackageFromAtom() throws Exception {
        Package p = createTestPackage("TestCreatePackageFromAtom");
        Entry e = toPackageEntry(p);
        e.setTitle("TestAtomPackageCreation");
        e.addAuthor("Test McTesty");

        URL url = new URL(generateBaseUrl() + "/packages/TestCreatePackageFromAtom");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", MediaType.APPLICATION_ATOM_XML);
        conn.setRequestProperty("Content-Length", Integer.toString(e.toString().getBytes().length));
        conn.setDoOutput(true);
        e.writeTo(conn.getOutputStream());

        assertEquals(204, conn.getResponseCode());
        conn.disconnect();
    }

    @Ignore @Test
    public void testUpdatePackageFromJson() {
        //TODO:  implement test
    }

    @Ignore @Test
    public void testArchivePackage() throws Exception {
        //TODO: Not sure how to get package archiving working, currently breaking as a package is not an asset */
        URL url = new URL(generateBaseUrl() + "/packages/TestCreatePackageFromJAXB");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.connect();
        assertEquals (200, connection.getResponseCode());
    }
}
