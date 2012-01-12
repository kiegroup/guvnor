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
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.guvnor.server.jaxrs.jaxb.PackageMetadata;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.utils.IOUtils;
import org.drools.util.codec.Base64;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mvel2.util.StringAppender;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

public class BasicPackageResourceTest extends GuvnorTestBase {
    
    private Abdera abdera = new Abdera();

    public BasicPackageResourceTest() {
        autoLoginAsAdmin = false;
    }

//    @BeforeClass
    // Unreliable HACK
    // Fixable after this is fixed: https://issues.jboss.org/browse/ARQ-540
    @Test
    public void startServers() throws Exception {
        loginAs("admin");
        //Package version 1(Initial version)
        ModuleItem pkg = rulesRepository.createModule( "restPackage1",
                                                                   "this is package restPackage1" );

        //Package version 2	
        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer1",
                                         pkg );

        AssetItem func = pkg.addAsset( "func",
                                       "" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void foo() { System.out.println(version 1); }" );
        func.checkin( "version 1" );

        AssetItem dsl = pkg.addAsset( "myDSL",
                                      "" );
        dsl.updateFormat( AssetFormats.DSL );
        dsl.updateContent( "[then]call a func=foo();\n[when]foo=FooBarBaz1()" );
        dsl.checkin( "version 1" );

        AssetItem rule = pkg.addAsset( "rule1",
                                       "" );
        rule.updateFormat( AssetFormats.DRL );
        rule.updateContent( "rule 'foo' when Goo1() then end" );
        rule.checkin( "version 1" );

        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        rule2.updateContent( "when \n foo \n then \n call a func" );
        rule2.checkin( "version 1" );

        AssetItem rule3 = pkg.addAsset( "model1",
                                        "" );
        rule3.updateFormat( AssetFormats.DRL_MODEL );
        rule3.updateContent( "declare Album1\n genre1: String \n end" );
        rule3.checkin( "version 1" );

        AssetItem rule4 = pkg.addAsset( "rule4",
                                        "" );
        rule4.updateFormat( AssetFormats.DRL );
        rule4.updateContent( "rule 'nheron' when Goo1() then end" );
        rule4.checkin( "version 1" );
        pkg.checkin( "version2" );

        //Package version 3
        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer2",
                                         pkg );
        func.updateContent( "function void foo() { System.out.println(version 2); }" );
        func.checkin( "version 2" );
        dsl.updateContent( "[then]call a func=foo();\n[when]foo=FooBarBaz2()" );
        dsl.checkin( "version 2" );
        rule.updateContent( "rule 'foo' when Goo2() then end" );
        rule.checkin( "version 2" );
        rule2.updateContent( "when \n foo \n then \n call a func" );
        rule2.checkin( "version 2" );
        rule3.updateContent( "declare Album2\n genre2: String \n end" );
        rule3.checkin( "version 2" );
        //impl.buildPackage(pkg.getUUID(), true);
        pkg.checkin( "version3" );
        logoutAs("admin");
    }
    
    @Test @RunAsClient
    public void testBasicAuthenticationInvalidPassword(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        //Test with invalid user name and pwd
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        byte[] authEncBytes = Base64.encodeBase64("admin:invalidPassword"
                .getBytes());
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        assertEquals (401, connection.getResponseCode());
        //assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
    }

    @Test @RunAsClient
    public void testBasicAuthentication(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        //Test with valid user name and pwd
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        byte[] authEncBytes = Base64.encodeBase64("admin:admin".getBytes());
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test @RunAsClient 
    public void testGetPackagesForJSON(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());        
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        //TODO: verify
     }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test @RunAsClient
    public void testGetPackagesForXML(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        //TODO: verify
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test @RunAsClient
    public void testGetPackagesForAtom(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
		Document<Feed> doc = abdera.getParser().parse(in);
		Feed feed = doc.getRoot();
		assertEquals(baseURL.getPath() + "rest/packages", feed.getBaseUri().getPath());
		assertEquals("Packages", feed.getTitle());
		
		List<Entry> entries = feed.getEntries();
		assertEquals(2, entries.size());
		Iterator<Entry> it = entries.iterator();	
		boolean foundPackageEntry = false;
		while (it.hasNext()) {
			Entry entry = it.next();
			if("restPackage1".equals(entry.getTitle())) {
				foundPackageEntry = true;
				List<Link> links = entry.getLinks();
				assertEquals(1, links.size());
				assertEquals(baseURL.getPath() + "rest/packages/restPackage1", links.get(0).getHref().getPath());
			}
		}
		assertTrue(foundPackageEntry);
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test @RunAsClient 
    public void testGetPackageForJSON(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        URL url = new URL(baseURL, "rest/packages/restPackage1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log (LogLevel, IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient 
    public void testGetPackageForXML(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        Package p = unmarshalPackageXML(connection.getInputStream());
        assertEquals("restPackage1", p.getTitle());
        assertEquals("this is package restPackage1", p.getDescription());
        assertEquals("version3", p.getCheckInComment());
        assertEquals(3, p.getVersion());
        assertEquals(new URL(baseURL, "rest/packages/restPackage1/source").toExternalForm(), p.getSourceLink().toString());
        assertEquals(new URL(baseURL, "rest/packages/restPackage1/binary").toExternalForm(), p.getBinaryLink().toString());
        PackageMetadata pm = p.getMetadata();
        assertEquals("admin", pm.getLastContributor());
        assertNotNull(pm.getCreated());
        assertNotNull(pm.getUuid());
        assertNotNull(pm.getLastModified());
        Set<URI> assetsURI = p.getAssets();
        
        assertEquals(7, assetsURI.size());
    	assertTrue(assetsURI.contains(new URL(baseURL, "rest/packages/restPackage1/assets/drools").toURI()));
    	assertTrue(assetsURI.contains(new URL(baseURL, "rest/packages/restPackage1/assets/func").toURI()));
    	assertTrue(assetsURI.contains(new URL(baseURL, "rest/packages/restPackage1/assets/myDSL").toURI()));
    	assertTrue(assetsURI.contains(new URL(baseURL, "rest/packages/restPackage1/assets/rule1").toURI()));
    	assertTrue(assetsURI.contains(new URL(baseURL, "rest/packages/restPackage1/assets/rule2").toURI()));
    	assertTrue(assetsURI.contains(new URL(baseURL, "rest/packages/restPackage1/assets/rule4").toURI()));
    	assertTrue(assetsURI.contains(new URL(baseURL, "rest/packages/restPackage1/assets/model1").toURI()));
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test @RunAsClient 
    public void testGetPackageForAtom(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        URL url = new URL(baseURL, "rest/packages/restPackage1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
		Document<Entry> doc = abdera.getParser().parse(in);
		Entry entry = doc.getRoot();
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1", entry.getBaseUri().getPath());
		assertEquals("restPackage1", entry.getTitle());
		assertNotNull(entry.getPublished());
		assertNotNull(entry.getAuthor().getName());		
		assertEquals("this is package restPackage1", entry.getSummary());
		//assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE.getType(), entry.getContentMimeType().getPrimaryType());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/binary", entry.getContentSrc().getPath());
		
		List<Link> links = entry.getLinks();
		assertEquals(7, links.size());
		Map<String, Link> linksMap = new HashMap<String, Link>();
		for(Link link : links){
			linksMap.put(link.getTitle(), link);
		}
		
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/drools", linksMap.get("drools").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/func", linksMap.get("func").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/myDSL", linksMap.get("myDSL").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/rule1", linksMap.get("rule1").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/rule2", linksMap.get("rule2").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/rule4", linksMap.get("rule4").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1", linksMap.get("model1").getHref().getPath());
		
		ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA); 
        ExtensibleElement archivedExtension = metadataExtension.getExtension(Translator.ARCHIVED);     
		assertEquals("false", archivedExtension.getSimpleExtension(Translator.VALUE)); 
        ExtensibleElement uuidExtension = metadataExtension.getExtension(Translator.UUID);     
		assertNotNull(uuidExtension.getSimpleExtension(Translator.VALUE)); 
    }

    /* Package Creation */
    @Test @RunAsClient
    public void testCreatePackageFromJAXB(@ArquillianResource URL baseURL) throws Exception {
        Package p = createTestPackage("TestCreatePackageFromJAXB");
        JAXBContext context = JAXBContext.newInstance(p.getClass());
        Marshaller marshaller = context.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(p, sw);
        String xml = sw.toString();
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
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

        assertEquals (200, connection.getResponseCode());
    }

    /* Package Creation */
    @Test @RunAsClient  @Ignore
    public void testCreatePackageFromDRLAsEntry(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
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
            url = new URL(baseURL, "rest/packages");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Authorization",
                    "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
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
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient @Ignore
    public void testCreatePackageFromDRLAsJson(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
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
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient @Ignore
    public void testCreatePackageFromDRLAsJaxB(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
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
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient
    public void testCreateAndUpdateAndDeletePackageFromAtom(@ArquillianResource URL baseURL) throws Exception {
    	//Test create
    	Abdera abdera = new Abdera();
    	AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
    	Entry entry = abdera.newEntry();		
    	entry.setTitle("testCreatePackageFromAtom");
    	entry.setSummary("desc for testCreatePackageFromAtom");
    	
    	ClientResponse resp = client.post(new URL(baseURL, "rest/packages").toExternalForm(), entry);
        //System.out.println(GetContent(resp.getInputStream()));

		assertEquals(ResponseType.SUCCESS, resp.getType());

		Document<Entry> doc = resp.getDocument();
		Entry returnedEntry = doc.getRoot();
		assertEquals(baseURL.getPath() + "rest/packages/testCreatePackageFromAtom", returnedEntry.getBaseUri().getPath());
		assertEquals("testCreatePackageFromAtom", returnedEntry.getTitle());
		assertEquals("desc for testCreatePackageFromAtom", returnedEntry.getSummary());
		
		//Test update package
        Entry e = abdera.newEntry();
        e.setTitle("testUpdatePackageFromAtom");
        org.apache.abdera.model.Link l = Abdera.getNewFactory().newLink();
        l.setHref(new URL(baseURL, "rest/packages/testCreatePackageFromAtom").toExternalForm());
        l.setRel("self");
        e.addLink(l);
        e.setSummary("updated desc for testCreatePackageFromAtom");
        e.addAuthor("Test McTesty");		
        resp = client.put(new URL(baseURL, "rest/packages/testCreatePackageFromAtom").toExternalForm(), e);
        assertEquals(ResponseType.SUCCESS, resp.getType());
        assertEquals(204, resp.getStatus());

        //NOTE: could not figure out why the code below always returns -1 as the ResponseCode.
/*        URL url = new URL(baseURL, "rest/packages/testCreatePackageFromAtom");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-type", MediaType.APPLICATION_ATOM_XML);
        conn.setRequestProperty("Content-Length", Integer.toString(e.toString().getBytes().length));
        conn.setDoOutput(true);
        e.writeTo(conn.getOutputStream());
        assertEquals(204, conn.getResponseCode());
        conn.disconnect(); */
 
        URL url1 = new URL(baseURL, "rest/packages/testCreatePackageFromAtom");
        HttpURLConnection conn1 = (HttpURLConnection)url1.openConnection();
        conn1.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn1.setRequestMethod("GET");
        conn1.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn1.connect();
        //System.out.println(GetContent(conn));
        assertEquals (200, conn1.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, conn1.getContentType());
        
        InputStream in = conn1.getInputStream();
        assertNotNull(in);
		doc = abdera.getParser().parse(in);
		entry = doc.getRoot();
		assertEquals(baseURL.getPath() + "rest/packages/testCreatePackageFromAtom", entry.getBaseUri().getPath());
		assertEquals("testCreatePackageFromAtom", entry.getTitle());
		assertTrue(entry.getPublished() != null);
		assertEquals("updated desc for testCreatePackageFromAtom", entry.getSummary());
        
		//Roll back changes. 
		resp = client.delete(new URL(baseURL, "rest/packages/testCreatePackageFromAtom").toExternalForm());
		assertEquals(ResponseType.SUCCESS, resp.getType());

		//Verify the package is indeed deleted
		URL url2 = new URL(baseURL, "rest/packages/testCreatePackageFromAtom");
		HttpURLConnection conn2 = (HttpURLConnection)url2.openConnection();
        conn2.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn2.setRequestMethod("GET");
        conn2.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn2.connect();
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals (500, conn2.getResponseCode());
    }
    
    //https://bugzilla.redhat.com/show_bug.cgi?id=756683
    @Test @RunAsClient @Ignore("verify this test indeed works once we get Arquillian working")
    public void testRenamePackageFromAtom(@ArquillianResource URL baseURL) throws Exception {
        //create a package for testing
        Abdera abdera = new Abdera();
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        Entry entry = abdera.newEntry();        
        entry.setTitle("testRenamePackageFromAtom");
        entry.setSummary("desc for testRenamePackageFromAtom");
        
        ClientResponse resp = client.post(new URL(baseURL, "rest/packages").toExternalForm(), entry);
        //System.out.println(GetContent(resp.getInputStream()));
        assertEquals(ResponseType.SUCCESS, resp.getType());

        Document<Entry> doc = resp.getDocument();
        Entry returnedEntry = doc.getRoot();
        assertEquals(baseURL.getPath() + "rest/packages/testRenamePackageFromAtom", returnedEntry.getBaseUri().getPath());
        assertEquals("testCreatePackageFromAtom", returnedEntry.getTitle());
        assertEquals("desc for testCreatePackageFromAtom", returnedEntry.getSummary());
        
        //Test rename package
        Entry e = abdera.newEntry();
        e.setTitle("testRenamePackageFromAtomNew");
        org.apache.abdera.model.Link l = Abdera.getNewFactory().newLink();
        l.setHref(new URL(baseURL, "rest/packages/testRenamePackageFromAtomNew").toExternalForm());
        l.setRel("self");
        e.addLink(l);
        e.setSummary("renamed package testCreatePackageFromAtom");
        e.addAuthor("Test McTesty");        
        resp = client.put(new URL(baseURL, "rest/packages/testCreatePackageFromAtom").toExternalForm(), e);
        assertEquals(ResponseType.SUCCESS, resp.getType());
        assertEquals(204, resp.getStatus());

        //Verify everything still works after renaming
        URL url1 = new URL(baseURL, "rest/packages/testRenamePackageFromAtomNew");
        HttpURLConnection conn1 = (HttpURLConnection)url1.openConnection();
        conn1.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn1.setRequestMethod("GET");
        conn1.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn1.connect();
        //System.out.println(GetContent(conn));
        assertEquals (200, conn1.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, conn1.getContentType());
        
        InputStream in = conn1.getInputStream();
        assertNotNull(in);
        doc = abdera.getParser().parse(in);
        entry = doc.getRoot();
        assertEquals(baseURL.getPath() + "rest/packages/testRenamePackageFromAtomNew", entry.getBaseUri().getPath());
        assertEquals("testRenamePackageFromAtomNew", entry.getTitle());
        assertTrue(entry.getPublished() != null);
        assertEquals("renamed package testCreatePackageFromAtom", entry.getSummary());
        
        //Roll back changes. 
        resp = client.delete(new URL(baseURL, "rest/packages/testRenamePackageFromAtomNew").toExternalForm());
        assertEquals(ResponseType.SUCCESS, resp.getType());

        //Verify the package is indeed deleted
        URL url2 = new URL(baseURL, "rest/packages/testRenamePackageFromAtomNew");
        HttpURLConnection conn2 = (HttpURLConnection)url2.openConnection();
        conn2.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn2.setRequestMethod("GET");
        conn2.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn2.connect();
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals (500, conn2.getResponseCode());
    }

    @Ignore @Test @RunAsClient
    public void testCreatePackageFromJson(@ArquillianResource URL baseURL) {
        //TODO: implement test
    }

    @Test @RunAsClient
    public void testGetPackageSource(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/source");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.WILDCARD);
        connection.connect();

        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        String result = IOUtils.toString(connection.getInputStream());
  
        assertEquals("attachment; filename=restPackage1", connection.getHeaderField("Content-Disposition"));
        assertTrue( result.indexOf( "package restPackage1" ) >= 0 );
        assertTrue( result.indexOf( "import com.billasurf.Board" ) >= 0 );
        assertTrue( result.indexOf( "global com.billasurf.Person customer2" ) >= 0 );
        assertTrue( result.indexOf( "function void foo() { System.out.println(version 2); }" ) >= 0 );
        assertTrue( result.indexOf( "declare Album2" ) >= 0 );
    }

    @Test @RunAsClient
    @Ignore
    public void testGetPackageBinary (@ArquillianResource URL baseURL) throws Exception {
        /* Tests package compilation in addition to byte retrieval */
        URL url = new URL(baseURL, "rest/packages/restPackage1/binary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        System.out.println(IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient @Ignore
    public void testUpdatePackageFromJAXB(@ArquillianResource URL baseURL) throws Exception {
        Package p = createTestPackage("TestCreatePackageFromJAXB");
        p.setDescription("Updated description.");
        JAXBContext context = JAXBContext.newInstance(p.getClass());
        Marshaller marshaller = context.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(p, sw);
        String xml = sw.toString();
        URL url = new URL(baseURL, "rest/packages/TestCreatePackageFromJAXB");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_XML);
        connection.setRequestProperty("Content-Length", Integer.toString(xml.getBytes().length));
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //Send request
        DataOutputStream wr = new DataOutputStream(
              connection.getOutputStream());
        wr.writeBytes(xml);
        wr.flush();
        wr.close();

        assertEquals(204, connection.getResponseCode());
    }

    @Ignore @Test @RunAsClient
    public void testUpdatePackageFromJson(@ArquillianResource URL baseURL) {
        //TODO:  implement test
    }

    @Test @RunAsClient
    public void testGetPackageVersionsForAtom(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        URL url = new URL(baseURL, "rest/packages/restPackage1/versions");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
		Document<Feed> doc = abdera.getParser().parse(in);
		Feed feed = doc.getRoot();
		assertEquals("Version history of restPackage1", feed.getTitle());
		
		List<Entry> entries = feed.getEntries();
		assertEquals(3, entries.size());

		Map<String, Entry> entriesMap = new HashMap<String, Entry>();
		for(Entry entry : entries){
			entriesMap.put(entry.getTitle(), entry);
		}
		
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/1", entriesMap.get("1").getLinks().get(0).getHref().getPath());
		assertTrue(entriesMap.get("1").getUpdated() != null);		
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/2", entriesMap.get("2").getLinks().get(0).getHref().getPath());
		assertTrue(entriesMap.get("2").getUpdated() != null);		
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/3", entriesMap.get("3").getLinks().get(0).getHref().getPath());
		assertTrue(entriesMap.get("3").getUpdated() != null);		
    }
    
    @Test @RunAsClient
    public void testGetHistoricalPackageForAtom(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        URL url = new URL(baseURL, "rest/packages/restPackage1/versions/2");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals(200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
		Document<Entry> doc = abdera.getParser().parse(in);
		Entry entry = doc.getRoot();
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/2", entry.getBaseUri().getPath());
		assertEquals("restPackage1", entry.getTitle());
		assertEquals("this is package restPackage1", entry.getSummary());
		//assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE.getType(), entry.getContentMimeType().getPrimaryType());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/2/binary", entry.getContentSrc().getPath());
		
		List<Link> links = entry.getLinks();
		assertEquals(7, links.size());
		Map<String, Link> linksMap = new HashMap<String, Link>();
		for(Link link : links){
			linksMap.put(link.getTitle(), link);
		}
		
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/2/assets/drools", linksMap.get("drools").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/2/assets/func", linksMap.get("func").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/2/assets/myDSL", linksMap.get("myDSL").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/2/assets/rule1", linksMap.get("rule1").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/2/assets/rule2", linksMap.get("rule2").getHref().getPath());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/versions/2/assets/model1", linksMap.get("model1").getHref().getPath());
	}    

    @Test @RunAsClient
    public void testGetHistoricalPackageSource(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/versions/2/source");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.WILDCARD);
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        String result = IOUtils.toString(connection.getInputStream());
        System.out.println(result);
       
        assertTrue(result.indexOf( "package restPackage1" ) >= 0 );
        assertTrue(result.indexOf( "import com.billasurf.Board" ) >= 0 );
        assertTrue(result.indexOf( "global com.billasurf.Person customer1" ) >= 0 );
        assertTrue(result.indexOf( "function void foo() { System.out.println(version 1); }" ) >= 0 );
        assertTrue(result.indexOf( "declare Album1" ) >= 0 );
    }
    
    @Test @RunAsClient 
    public void testGetHistoricalPackageBinary(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/versions/2/binary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();

        //TODO:
        //assertEquals (500, connection.getResponseCode());
        //String result = IOUtils.toString(connection.getInputStream());
        //System.out.println(result);
    }

    @Test @RunAsClient
    public void testUpdateAndGetAssetSource(@ArquillianResource URL baseURL) throws Exception {
        /*
         *  Get the content of rule4
         */
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/rule4/source");
        HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();
        connection1.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection1.setRequestMethod("GET");
        connection1.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
        connection1.connect();
        assertEquals(200, connection1.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection1.getContentType());
        String newContent = "rule 'nheron' when Goo1() then end";
          /*
           * update the content
           */
        URL url2 = new URL(baseURL, "rest/packages/restPackage1/assets/rule4/source");
        HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
        connection2.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection2.setDoOutput(true);
        connection2.setRequestMethod("PUT");
        connection2.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        OutputStreamWriter out = new OutputStreamWriter(connection2.getOutputStream());
        out.write(newContent);
        out.close();
        connection2.getInputStream();
        assertEquals(204, connection2.getResponseCode());
        /*
         * get the content again and verify it was modified
         */
        URL url3 = new URL(baseURL, "rest/packages/restPackage1/assets/rule4/source");
        HttpURLConnection connection3 = (HttpURLConnection) url3.openConnection();
        connection3.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection3.setRequestMethod("GET");
        connection3.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
        connection3.connect();

        assertEquals(200, connection3.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection3.getContentType());
        String result = IOUtils.toString(connection3.getInputStream());
        assertEquals(result, newContent);
    }
    
    @Test @RunAsClient
    public void testCreateAndUpdateAndGetBinaryAsset(@ArquillianResource URL baseURL) throws Exception {
        //Query if the asset exist
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        byte[] authEncBytes = Base64.encodeBase64("admin:admin".getBytes());
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        //The asset should not exist
        assertEquals(500, connection.getResponseCode());

        //Create the asset from binary
        url = new URL(baseURL, "rest/packages/restPackage1/assets");
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.setRequestProperty("Slug", "Error-image.gif");
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.setDoOutput(true);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[1000];
        int count = 0;
        InputStream is = this.getClass().getResourceAsStream("Error-image.gif");
        while((count = is.read(data,0,1000)) != -1) {
            out.write(data, 0, count);
        }
        connection.getOutputStream ().write(out.toByteArray());
        out.close();
        assertEquals(200, connection.getResponseCode());
        
        //Get the asset meta data and verify
        url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        InputStream in = connection.getInputStream();
        assertNotNull(in);
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();
        assertEquals("Error-image", entry.getTitle());
        ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA); 
        ExtensibleElement formatExtension = metadataExtension.getExtension(Translator.FORMAT);     
        assertEquals("gif", formatExtension.getSimpleExtension(Translator.VALUE)); 

        assertTrue(entry.getPublished() != null);
        
        //Get the asset binary and verify
        url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image/binary");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals(200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        in = connection.getInputStream();
        assertNotNull(in);
                
        //Update asset binary
        url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image/binary");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        byte[] data2 = new byte[1000];
        int count2 = 0;
        InputStream is2 = this.getClass().getResourceAsStream("Error-image-new.gif");
        while((count2 = is2.read(data2,0,1000)) != -1) {
            out2.write(data2, 0, count2);
        }
        connection.getOutputStream ().write(out2.toByteArray());
        out2.close();
        assertEquals(204, connection.getResponseCode());
        
        //Roll back changes. 
        url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image");
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals(204, connection.getResponseCode());

        //Verify the package is indeed deleted
        url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        assertEquals(500, connection.getResponseCode());
    }
    
    @Test @RunAsClient
    public void testGetSourceContentFromBinaryAsset(@ArquillianResource URL baseURL) throws Exception {
        //Query if the asset exist
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image-new");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        //The asset should not exist
        assertEquals(500, connection.getResponseCode());

        //Create the asset from binary
        url = new URL(baseURL, "rest/packages/restPackage1/assets");
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.setRequestProperty("Slug", "Error-image-new");
        connection.setDoOutput(true);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[1000];
        int count = 0;
        InputStream is = this.getClass().getResourceAsStream("Error-image.gif");
        while((count = is.read(data,0,1000)) != -1) {
            out.write(data, 0, count);
        }
        connection.getOutputStream ().write(out.toByteArray());
        out.close();
        assertEquals(200, connection.getResponseCode());
        
        //Get the asset source. this will return the binary data as a byte array.
        url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image-new/source");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        String result = IOUtils.toString(connection.getInputStream());
        assertNotNull(result);
         
        //Roll back changes. 
        url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image-new");
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("DELETE");
        connection.connect();
        System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals(204, connection.getResponseCode());

        //Verify the package is indeed deleted
        url = new URL(baseURL, "rest/packages/restPackage1/assets/Error-image-new");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals(500, connection.getResponseCode());
    }
    
    @Test @RunAsClient
    public void testGetBinaryContentFromNonBinaryAsset(@ArquillianResource URL baseURL) throws Exception {
        //Get the asset binary. If this asset has no binary content, this will return its 
        //source content instead
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1/binary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        String result = IOUtils.toString(connection.getInputStream());
        assertTrue(result.indexOf("declare Album2") > -1);
    }
    
    @Test @RunAsClient
    public void testGetAssetVersionsForAtom(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1/versions");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
        Document<Feed> doc = abdera.getParser().parse(in);
        Feed feed = doc.getRoot();
        assertEquals("Version history of model1", feed.getTitle());
        
        List<Entry> entries = feed.getEntries();
        assertEquals(2, entries.size());

        Map<String, Entry> entriesMap = new HashMap<String, Entry>();
        for(Entry entry : entries){
            entriesMap.put(entry.getTitle(), entry);
        }
        
        assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1/versions/1", entriesMap.get("1").getLinks().get(0).getHref().getPath());
        assertTrue(entriesMap.get("1").getUpdated() != null);       
        assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1/versions/2", entriesMap.get("2").getLinks().get(0).getHref().getPath());
        assertTrue(entriesMap.get("2").getUpdated() != null);     
    }
    
    @Test @RunAsClient @Ignore("Verify this test once we get Arquillian working")
    public void testGetAssetVersionsAfterUpdatingSource(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {        
        /*
         * check version feed
         */        
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/rule4/versions");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
        Document<Feed> doc = abdera.getParser().parse(in);
        Feed feed = doc.getRoot();
        assertEquals("Version history of model1", feed.getTitle());        
        List<Entry> entries = feed.getEntries();
        int versionNumbers = entries.size(); 
        connection.disconnect();
        
        /*
         * update the content rule4
         */
        URL url2 = new URL(baseURL, "rest/packages/restPackage1/assets/rule4/source");
        HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
        connection2.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection2.setDoOutput(true);
        connection2.setRequestMethod("PUT");
        connection2.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        OutputStreamWriter out = new OutputStreamWriter(connection2.getOutputStream());
        String newContent = "rule 'nheron' when Goo1() then end";
        out.write(newContent);
        out.close();
        connection2.getInputStream();
        assertEquals(204, connection2.getResponseCode());
                
        /*
         * check version feed
         */        
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        
        in = connection.getInputStream();
        assertNotNull(in);
        doc = abdera.getParser().parse(in);
        feed = doc.getRoot();
        assertEquals("Version history of model1", feed.getTitle());        
        entries = feed.getEntries();
        assertEquals(versionNumbers +1, entries.size());     
    }
    
    @Test @RunAsClient
    public void testGetHistoricalAssetForAtom(@ArquillianResource URL baseURL) throws MalformedURLException, IOException {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1/versions/1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        InputStream in = connection.getInputStream();
        
        assertNotNull(in);
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();
        assertEquals("model1", entry.getTitle());
        assertTrue(entry.getPublished() != null);        
        assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1/versions/1", entry.getId().getPath());
        assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1/versions/1/binary", entry.getContentSrc().getPath());
        ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA); 
        ExtensibleElement formatExtension = metadataExtension.getExtension(Translator.FORMAT);     
        assertEquals("model.drl", formatExtension.getSimpleExtension(Translator.VALUE)); 
        ExtensibleElement stateExtension = metadataExtension.getExtension(Translator.STATE);   
        assertEquals("Draft", stateExtension.getSimpleExtension(Translator.VALUE)); 
        ExtensibleElement archivedExtension = metadataExtension.getExtension(Translator.ARCHIVED);   
        assertEquals("false", archivedExtension.getSimpleExtension(Translator.VALUE)); 
        
        
        url = new URL(baseURL, "rest/packages/restPackage1/assets/model1/versions/2");
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        in = connection.getInputStream();
        
        assertNotNull(in);
        doc = abdera.getParser().parse(in);
        entry = doc.getRoot();
        assertEquals("model1", entry.getTitle());
        assertTrue(entry.getPublished() != null);        
        assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1/versions/2", entry.getId().getPath());
        assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1/versions/2/binary", entry.getContentSrc().getPath());
        metadataExtension  = entry.getExtension(Translator.METADATA); 
        formatExtension = metadataExtension.getExtension(Translator.FORMAT);     
        assertEquals("model.drl", formatExtension.getSimpleExtension(Translator.VALUE)); 
        stateExtension = metadataExtension.getExtension(Translator.STATE);   
        assertEquals("Draft", stateExtension.getSimpleExtension(Translator.VALUE)); 
        archivedExtension = metadataExtension.getExtension(Translator.ARCHIVED);   
        assertEquals("false", archivedExtension.getSimpleExtension(Translator.VALUE)); 
  
    }
    
    @Test @RunAsClient
    public void testGetHistoricalAssetSource(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1/versions/1/source");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.WILDCARD);
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        String result = IOUtils.toString(connection.getInputStream());
        System.out.println(result);

        assertTrue(result.indexOf( "declare Album1" ) >= 0 );
        assertTrue(result.indexOf( "genre1: String" ) >= 0 );
        
        url = new URL(baseURL, "rest/packages/restPackage1/assets/model1/versions/2/source");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.WILDCARD);
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        result = IOUtils.toString(connection.getInputStream());
        System.out.println(result);

        assertTrue(result.indexOf( "declare Album2" ) >= 0 );
        assertTrue(result.indexOf( "genre2: String" ) >= 0 );
    }  
    
    @Test @RunAsClient
    public void testGetHistoricalAssetBinary(@ArquillianResource URL baseURL) throws Exception {
        //Query if the asset exist
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/testGetHistoricalAssetBinary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        byte[] authEncBytes = Base64.encodeBase64("admin:admin".getBytes());
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        //The asset should not exist
        assertEquals(500, connection.getResponseCode());

        //Create the asset from binary
        url = new URL(baseURL, "rest/packages/restPackage1/assets");
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.setRequestProperty("Slug", "testGetHistoricalAssetBinary.gif");
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.setDoOutput(true);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[1000];
        int count = 0;
        InputStream is = this.getClass().getResourceAsStream("Error-image.gif");
        while((count = is.read(data,0,1000)) != -1) {
            out.write(data, 0, count);
        }
        connection.getOutputStream ().write(out.toByteArray());
        out.close();
        assertEquals(200, connection.getResponseCode());
        
        //Update asset binary
        url = new URL(baseURL, "rest/packages/restPackage1/assets/testGetHistoricalAssetBinary/binary");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        byte[] data2 = new byte[1000];
        int count2 = 0;
        InputStream is2 = this.getClass().getResourceAsStream("Error-image-new.gif");
        while((count2 = is2.read(data2,0,1000)) != -1) {
            out2.write(data2, 0, count2);
        }
        connection.getOutputStream ().write(out2.toByteArray());
        out2.close();
        assertEquals(204, connection.getResponseCode());
                
        //Get the asset binary version 1 and verify
        url = new URL(baseURL, "rest/packages/restPackage1/assets/testGetHistoricalAssetBinary/versions/1/binary");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        assertEquals(200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        InputStream in = connection.getInputStream();
        assertNotNull(in);
        
        //Get the asset binary version 2 and verify
        url = new URL(baseURL, "rest/packages/restPackage1/assets/testGetHistoricalAssetBinary/versions/2/binary");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        assertEquals(200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        in = connection.getInputStream();
        assertNotNull(in);
        
        //Roll back changes. 
        url = new URL(baseURL, "rest/packages/restPackage1/assets/testGetHistoricalAssetBinary");
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals(204, connection.getResponseCode());

        //Verify the package is indeed deleted
        url = new URL(baseURL, "rest/packages/restPackage1/assets/testGetHistoricalAssetBinary");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        assertEquals(500, connection.getResponseCode());
    }

    protected Package createTestPackage(String title) {
        Package p = new Package();
        PackageMetadata metadata = new PackageMetadata();
        metadata.setCreated(new Date(System.currentTimeMillis()));
        metadata.setUuid(UUID.randomUUID().toString());
        metadata.setLastContributor("awaterma");
        metadata.setLastModified(new Date(System.currentTimeMillis()));

        p.setMetadata(metadata);
        p.setCheckInComment("Check in comment for test package.");
        p.setTitle(title);
        p.setDescription("A simple test package with 0 assets.");
        return p;
    }
        
    private Package unmarshalPackageXML(InputStream is) throws Exception {
        JAXBContext c = JAXBContext.newInstance(new Class[]{Package.class});
        Unmarshaller u = c.createUnmarshaller();
        return (Package)u.unmarshal(is);
    }
    
}
