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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.jaxrs.jaxb.Category;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.guvnor.server.jaxrs.jaxb.PackageMetadata;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.util.codec.Base64;
import org.junit.*;
import org.mvel2.util.StringAppender;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;



public class BasicPackageResourceTest extends AbstractBusClientServerTestBase {
    private Abdera abdera = new Abdera();
    private static RestTestingBase restTestingBase;

    @BeforeClass
    public static void startServers() throws Exception {
       	restTestingBase = new RestTestingBase();
       	restTestingBase.setup();       	

        assertTrue("server did not launch correctly",
                   launchServer(CXFJAXRSServer.class, true));

        
        ServiceImplementation impl = restTestingBase.getServiceImplementation();
        //Package version 1(Initial version)
        PackageItem pkg = impl.getRulesRepository().createPackage( "restPackage1",
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
    }
    
    @AfterClass
    public static void tearDown() {
    	restTestingBase.tearDownGuvnorTestBase();
    }
    
    @Test
    public void testBasicAuthentication() throws MalformedURLException, IOException {
        //Test with invalid user name and pwd
        URL url = new URL(generateBaseUrl() + "/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        String userpassword = "test" + ":" + "invalidPwd";
        byte[] authEncBytes = Base64.encodeBase64(userpassword
                .getBytes());
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        assertEquals (401, connection.getResponseCode());
        //assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        
        //Test with valid user name and pwd
        url = new URL(generateBaseUrl() + "/packages");
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        userpassword = "test" + ":" + "password";
        authEncBytes = Base64.encodeBase64(userpassword
                .getBytes());
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(GetContent(connection));        
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
        //System.out.println(GetContent(connection));
        //TODO: verify
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
        //System.out.println(GetContent(connection));
        //TODO: verify
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
        String userpassword = "jliu" + ":" + "pwd";
        byte[] authEncBytes = Base64.encodeBase64(userpassword
                .getBytes());
        connection.setRequestProperty("Authorization", "Basic "
                + new String(authEncBytes));
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(GetContent(connection));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
		Document<Feed> doc = abdera.getParser().parse(in);
		Feed feed = doc.getRoot();
		assertEquals("/packages", feed.getBaseUri().getPath());
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
				assertEquals("/packages/restPackage1", links.get(0).getHref().getPath());
			}
		}
		assertTrue(foundPackageEntry);
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test 
    public void testGetPackageForJSON() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log (LogLevel, GetContent(connection));
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test 
    public void testGetPackageForXML() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));
    }

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     */
    @Test 
    public void testGetPackageForAtom() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(GetContent(connection));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
		Document<Entry> doc = abdera.getParser().parse(in);
		Entry entry = doc.getRoot();
		assertEquals("/packages/restPackage1", entry.getBaseUri().getPath());		
		assertEquals("restPackage1", entry.getTitle());
		assertNotNull(entry.getPublished());
		assertNotNull(entry.getAuthor().getName());		
		assertEquals("this is package restPackage1", entry.getSummary());
		//assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE.getType(), entry.getContentMimeType().getPrimaryType());
		assertEquals("/packages/restPackage1/binary", entry.getContentSrc().getPath());
		
		List<Link> links = entry.getLinks();
		assertEquals(6, links.size());
		Map<String, Link> linksMap = new HashMap<String, Link>();
		for(Link link : links){
			linksMap.put(link.getTitle(), link);
		}
		
		assertEquals("/packages/restPackage1/assets/drools", linksMap.get("drools").getHref().getPath());		
		assertEquals("/packages/restPackage1/assets/func", linksMap.get("func").getHref().getPath());		
		assertEquals("/packages/restPackage1/assets/myDSL", linksMap.get("myDSL").getHref().getPath());		
		assertEquals("/packages/restPackage1/assets/rule1", linksMap.get("rule1").getHref().getPath());		
		assertEquals("/packages/restPackage1/assets/rule2", linksMap.get("rule2").getHref().getPath());		
		assertEquals("/packages/restPackage1/assets/model1", linksMap.get("model1").getHref().getPath());
		
		ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA); 
        ExtensibleElement archivedExtension = metadataExtension.getExtension(Translator.ARCHIVED);     
		assertEquals("false", archivedExtension.getSimpleExtension(Translator.VALUE)); 
        ExtensibleElement uuidExtension = metadataExtension.getExtension(Translator.UUID);     
		assertNotNull(uuidExtension.getSimpleExtension(Translator.VALUE)); 
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
    @Test  @Ignore
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
        //logger.log(LogLevel, GetContent(connection));
    }

    @Test @Ignore
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
        //logger.log(LogLevel, GetContent(connection));
    }

    @Test @Ignore
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
        //logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testCreateAndUpdateAndDeletePackageFromAtom() throws Exception {
    	//Test create
    	Abdera abdera = new Abdera();
    	AbderaClient client = new AbderaClient(abdera);
    	Entry entry = abdera.newEntry();		
    	entry.setTitle("testCreatePackageFromAtom");
    	entry.setSummary("desc for testCreatePackageFromAtom");
    	
    	ClientResponse resp = client.post(generateBaseUrl() + "/packages", entry);
        //System.out.println(GetContent(resp.getInputStream()));

		assertEquals(ResponseType.SUCCESS, resp.getType());

		Document<Entry> doc = resp.getDocument();
		Entry returnedEntry = doc.getRoot();
		assertEquals("/packages/testCreatePackageFromAtom", returnedEntry.getBaseUri().getPath());
		assertEquals("testCreatePackageFromAtom", returnedEntry.getTitle());
		assertEquals("desc for testCreatePackageFromAtom", returnedEntry.getSummary());
		
		//Test update package
        Entry e = abdera.newEntry();
        e.setTitle("testUpdatePackageFromAtom");
        org.apache.abdera.model.Link l = abdera.getNewFactory().newLink();
        l.setHref(generateBaseUrl() + "/packages/" + "testCreatePackageFromAtom");
        l.setRel("self");
        e.addLink(l);
        e.setSummary("updated desc for testCreatePackageFromAtom");
        e.addAuthor("Test McTesty");		
        resp = client.put(generateBaseUrl() + "/packages/testCreatePackageFromAtom", e);
        assertEquals(ResponseType.SUCCESS, resp.getType());

        //NOTE: could not figure out why the code below always returns -1 as the ResponseCode.
/*        URL url = new URL(generateBaseUrl() + "/packages/testCreatePackageFromAtom");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-type", MediaType.APPLICATION_ATOM_XML);
        conn.setRequestProperty("Content-Length", Integer.toString(e.toString().getBytes().length));
        conn.setDoOutput(true);
        e.writeTo(conn.getOutputStream());
        assertEquals(204, conn.getResponseCode());
        conn.disconnect(); */
 
        URL url1 = new URL(generateBaseUrl() + "/packages/testCreatePackageFromAtom");
        HttpURLConnection conn1 = (HttpURLConnection)url1.openConnection();
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
		assertEquals("/packages/testCreatePackageFromAtom", entry.getBaseUri().getPath());		
		assertEquals("testCreatePackageFromAtom", entry.getTitle());
		assertTrue(entry.getPublished() != null);
		assertEquals("updated desc for testCreatePackageFromAtom", entry.getSummary());

        
		//Roll back changes. 
		resp = client.delete(generateBaseUrl() + "/packages/testCreatePackageFromAtom");
		assertEquals(ResponseType.SUCCESS, resp.getType());

		//Verify the package is indeed deleted
		URL url2 = new URL(generateBaseUrl() + "/packages/testCreatePackageFromAtom");
		HttpURLConnection conn2 = (HttpURLConnection)url2.openConnection();
        conn2.setRequestMethod("GET");
        conn2.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn2.connect();
        //System.out.println(GetContent(connection));
        assertEquals (500, conn2.getResponseCode());
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
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/source");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.WILDCARD);
        connection.connect();

        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        String result = GetContent(connection);        
  
        assertEquals("attachment; filename=restPackage1", connection.getHeaderField("Content-Disposition"));
        assertTrue( result.indexOf( "package restPackage1" ) >= 0 );
        assertTrue( result.indexOf( "import com.billasurf.Board" ) >= 0 );
        assertTrue( result.indexOf( "global com.billasurf.Person customer2" ) >= 0 );
        assertTrue( result.indexOf( "function void foo() { System.out.println(version 2); }" ) >= 0 );
        assertTrue( result.indexOf( "declare Album2" ) >= 0 );
    }

    @Test
    @Ignore
    public void testGetPackageBinary () throws Exception {
        /* Tests package compilation in addition to byte retrieval */
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/binary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();

        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        System.out.println(GetContent(connection));
    }

    @Test @Ignore
    public void testUpdatePackageFromJAXB() throws Exception {
        Package p = createTestPackage("TestCreatePackageFromJAXB");
        p.setDescription("Updated description.");
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
    public void testUpdatePackageFromJson() {
        //TODO:  implement test
    }

    @Test
    public void testGetPackageVersionsForAtom() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/versions");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(GetContent(connection));
        
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
		
		assertEquals("/packages/restPackage1/versions/1", entriesMap.get("1").getLinks().get(0).getHref().getPath());		
		assertTrue(entriesMap.get("1").getUpdated() != null);		
		assertEquals("/packages/restPackage1/versions/2", entriesMap.get("2").getLinks().get(0).getHref().getPath());		
		assertTrue(entriesMap.get("2").getUpdated() != null);		
		assertEquals("/packages/restPackage1/versions/3", entriesMap.get("3").getLinks().get(0).getHref().getPath());		
		assertTrue(entriesMap.get("3").getUpdated() != null);		
    }
    
    @Test
    public void testGetHistoricalPackageForAtom() throws MalformedURLException, IOException {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/versions/2");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(GetContent(connection));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
		Document<Entry> doc = abdera.getParser().parse(in);
		Entry entry = doc.getRoot();
		assertEquals("/packages/restPackage1/versions/2", entry.getBaseUri().getPath());
		assertEquals("restPackage1", entry.getTitle());
		assertEquals("this is package restPackage1", entry.getSummary());
		//assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE.getType(), entry.getContentMimeType().getPrimaryType());
		assertEquals("/packages/restPackage1/versions/2/binary", entry.getContentSrc().getPath());
		
		List<Link> links = entry.getLinks();
		assertEquals(6, links.size());
		Map<String, Link> linksMap = new HashMap<String, Link>();
		for(Link link : links){
			linksMap.put(link.getTitle(), link);
		}
		
		assertEquals("/packages/restPackage1/versions/2/assets/drools", linksMap.get("drools").getHref().getPath());		
		assertEquals("/packages/restPackage1/versions/2/assets/func", linksMap.get("func").getHref().getPath());		
		assertEquals("/packages/restPackage1/versions/2/assets/myDSL", linksMap.get("myDSL").getHref().getPath());		
		assertEquals("/packages/restPackage1/versions/2/assets/rule1", linksMap.get("rule1").getHref().getPath());		
		assertEquals("/packages/restPackage1/versions/2/assets/rule2", linksMap.get("rule2").getHref().getPath());		
		assertEquals("/packages/restPackage1/versions/2/assets/model1", linksMap.get("model1").getHref().getPath());   
	}    

    @Test
    public void testGetHistoricalPackageSource() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/versions/2/source");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.WILDCARD);
        connection.connect();

        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        String result = GetContent(connection);
        System.out.println(result);
       
        assertTrue( result.indexOf( "package restPackage1" ) >= 0 );
        assertTrue( result.indexOf( "import com.billasurf.Board" ) >= 0 );
        assertTrue( result.indexOf( "global com.billasurf.Person customer1" ) >= 0 );
        assertTrue( result.indexOf( "function void foo() { System.out.println(version 1); }" ) >= 0 );
        assertTrue( result.indexOf( "declare Album1" ) >= 0 );
    }
    
    @Test 
    public void testGetHistoricalPackageBinary () throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/versions/2/binary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();

        assertEquals (500, connection.getResponseCode());
        //String result = GetContent(connection);
        //System.out.println(result);
    }
    
    public String generateBaseUrl() {
    	return "http://localhost:9080";
    }
    public static String GetContent (InputStream is) throws IOException {
        StringAppender ret = new StringAppender();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            ret.append(line + "\n");
        }

        return ret.toString();
    }
    
    public static String GetContent (HttpURLConnection connection) throws IOException {
        StringAppender ret = new StringAppender();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            ret.append(line + "\n");
        }

        return ret.toString();
    }    

    protected Package createTestPackage(String title) {
        Category c = new Category();
        c.setName("test");

        Package p = new Package();
        PackageMetadata metadata = new PackageMetadata();
        metadata.setCreated(new Date(System.currentTimeMillis()));
        metadata.setUuid(UUID.randomUUID().toString());
        metadata.setLastContributor("awaterma");
        metadata.setLastModified(new Date(System.currentTimeMillis()));

        p.setMetadata(metadata);
        p.setCategory(c);
        p.setCheckInComment("Check in comment for test package.");
        p.setTitle(title);
        p.setDescription("A simple test package with 0 assets.");
        return p;
    }
    
}
