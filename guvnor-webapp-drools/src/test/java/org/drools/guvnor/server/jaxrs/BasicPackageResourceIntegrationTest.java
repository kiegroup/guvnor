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
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.guvnor.server.jaxrs.jaxb.PackageMetadata;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.utils.IOUtils;
import org.drools.util.codec.Base64;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Ignore;
import org.junit.Test;

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

public class BasicPackageResourceIntegrationTest extends GuvnorIntegrationTest {
    
    private Abdera abdera = new Abdera();

    public BasicPackageResourceIntegrationTest() {
        autoLoginAsAdmin = false;
    }

    //    @BeforeClass
    // HACK - Fixable after this is fixed: https://issues.jboss.org/browse/ARQ-540
    @Test @InSequence(-1)
    public void startServers() throws Exception {
        loginAs("admin");
        //Package version 1(Initial version)
        ModuleItem pkg = rulesRepository.createModule( "restPackage1",
                                                                   "this is package restPackage1" );

        //Package version 2	
        DroolsHeader.updateDroolsHeader( "import org.drools.Cheese\n global org.drools.Person customer1",
                                         pkg );

        AssetItem func = pkg.addAsset( "func",
                                       "" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void foo() { System.out.println(\"version 1\"); }" );
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
        rule4.updateContent( "rule 'nheron' when Cheese() then end" );
        rule4.checkin( "version 1" );
        pkg.checkin( "version2" );

        //Package version 3
        DroolsHeader.updateDroolsHeader( "import org.drools.Cheese\n global org.drools.Person customer2",
                                         pkg );
        func.updateContent( "function void foo() { System.out.println(\"version 2\"); }" );
        func.checkin( "version 2" );
        dsl.updateContent( "[then]call a func=foo();\n[when]foo=Cheese()" );
        dsl.checkin( "version 2" );
        rule.updateContent( "rule 'foo' when Cheese() then end" );
        rule.checkin( "version 2" );
        rule2.updateContent( "when \n foo \n then \n call a func" );
        rule2.checkin( "version 2" );
        rule3.updateContent( "declare Album2\n genre2: String \n end" );
        rule3.checkin( "version 2" );
        //impl.buildPackage(pkg.getUUID(), true);
        pkg.checkin( "version3" );
        
        ModuleItem pkg2 = rulesRepository.createModule( "restPackage2",
                "this is package restPackage2" );   
        pkg2.checkout();
        repositoryPackageService.buildPackage(pkg2.getUUID(), true);        
        pkg2.checkin("version2");
        pkg2.checkout();
        repositoryPackageService.buildPackage(pkg2.getUUID(), true);       
        pkg2.checkin("version3");

        ModuleItem pkg3 = rulesRepository.createModule( "restPackageCompilationFailure",
                "this is package restPackageCompilationFailure" );

        DroolsHeader.updateDroolsHeader( "import org.drools.NonExistingClass",
                pkg3 );

        AssetItem brokenRule = pkg3.addAsset( "ruleCompilationFailure",
                "" );
        brokenRule.updateFormat(AssetFormats.DRL);
        brokenRule.updateContent("rule 'compilationFailure' when NonExistingClass() then end");
        brokenRule.checkin("version 1");
        pkg3.checkin("version2");
        
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
		assertEquals(4, entries.size());
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

    /**
     * Test of getPackagesAsFeed method, of class PackageService.
     * An example of expected result:
     * 
     *   <?xml version="1.0" encoding="UTF-8" standalone="yes" ?> 
     *   <package>
     *     <binaryLink>http://127.0.0.1:8080/da8f3038-194b-4627-ae55-94b1f21d8e24/rest/packages/restPackage1/binary</binaryLink> 
     *     <description>this is package restPackage1</description> 
     *     <sourceLink>http://127.0.0.1:8080/da8f3038-194b-4627-ae55-94b1f21d8e24/rest/packages/restPackage1/source</sourceLink> 
     *     <title>restPackage1</title> 
     *     <author>guest</author>
     *     <published>2012-01-31T19:15:56.933+08:00</published>
     *     <metadata>
     *       <archived>false</archived>
     *       <created>2012-01-30T16:48:37.081+08:00</created> 
     *       <state /> 
     *       <uuid>690e386a-89df-4018-8688-cb322b492f53</uuid> 
     *       <versionNumber>3</versionNumber>
     *       <checkInComment>version3</checkInComment>
     *     </metadata>
     *     
     *     <assets>http://127.0.0.1:8080/da8f3038-194b-4627-ae55-94b1f21d8e24/rest/packages/restPackage1/assets/rule4</assets> 
     *     <assets>http://127.0.0.1:8080/da8f3038-194b-4627-ae55-94b1f21d8e24/rest/packages/restPackage1/assets/drools</assets> 
     *     <assets>http://127.0.0.1:8080/da8f3038-194b-4627-ae55-94b1f21d8e24/rest/packages/restPackage1/assets/rule1</assets> 
     *     <assets>http://127.0.0.1:8080/da8f3038-194b-4627-ae55-94b1f21d8e24/rest/packages/restPackage1/assets/func</assets> 
     *     <assets>http://127.0.0.1:8080/da8f3038-194b-4627-ae55-94b1f21d8e24/rest/packages/restPackage1/assets/myDSL</assets> 
     *     <assets>http://127.0.0.1:8080/da8f3038-194b-4627-ae55-94b1f21d8e24/rest/packages/restPackage1/assets/rule2</assets> 
     *     <assets>http://127.0.0.1:8080/da8f3038-194b-4627-ae55-94b1f21d8e24/rest/packages/restPackage1/assets/model1</assets> 
     *   </package>     
     */
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
        //System.out.println("------------------------");
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        Package p = unmarshalPackageXML(connection.getInputStream());
        assertEquals("restPackage1", p.getTitle());
        assertEquals("this is package restPackage1", p.getDescription());
        assertNotNull(p.getAuthor());       
        assertNotNull(p.getPublished());
        assertEquals(new URL(baseURL, "rest/packages/restPackage1/source").toExternalForm(), p.getSourceLink().toString());
        assertEquals(new URL(baseURL, "rest/packages/restPackage1/binary").toExternalForm(), p.getBinaryLink().toString());
        PackageMetadata pm = p.getMetadata();
        assertFalse(pm.isArchived());
        assertNotNull(pm.getCreated());
        assertNotNull(pm.getUuid());
        assertEquals("version3", pm.getCheckinComment());
        assertEquals(3, pm.getVersionNumber());
        
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
     * Test of getPackageAsEntry method, of class PackageResource.
     * 
     * An example of expected result:
     * 
     * <entry xmlns="http://www.w3.org/2005/Atom" xml:base="http://127.0.0.1:8080/1c71582d-3f64-4027-bc5a-d442ab4816e6/rest/packages/restPackage1"> 
     *   <title type="text">restPackage1</title> 
     *   <summary type="text">this is package restPackage1</summary>
     *   <published>2012-01-30T08:17:29.321Z</published> 
     *   <author>
     *     <name>guest</name> 
     *   </author>   
     *   <id>http://127.0.0.1:8080/1c71582d-3f64-4027-bc5a-d442ab4816e6/rest/packages/restPackage1</id> 
     *   
     *   <link href= "http://127.0.0.1:8080/1c71582d-3f64-4027-bc5a-d442ab4816e6/rest/packages/restPackage1/assets/drools" title="drools" rel="asset" /> 
     *   <link href= "http://127.0.0.1:8080/1c71582d-3f64-4027-bc5a-d442ab4816e6/rest/packages/ restPackage1/assets/func" title="func" rel="asset" /> 
     *   <link href= "http://127.0.0.1:8080/1c71582d-3f64-4027-bc5a-d442ab4816e6/rest/packages/restPackage1/assets/myDSL" title="myDSL" rel="asset" /> 
     *   <link href= "http://127.0.0.1:8080/1c71582d-3f64-4027-bc5a-d442ab4816e6/rest/packages/restPackage1/assets/rule1" title="rule1" rel="asset" /> 
     *   <link href= "http://127.0.0.1:8080/1c71582d-3f64-4027-bc5a-d442ab4816e6/rest/packages/restPackage1/assets/rule2" title="rule2" rel="asset" /> 
     *   <link href= "http://127.0.0.1:8080/1c71582d-3f64-4027-bc5a-d442ab4816e6/rest/packages/restPackage1/assets/model1" title="model1" rel="asset" />
     *   <link href= "http://127.0.0.1:8 080/1c71582d-3f64-4027-bc5a-d442ab4816e6/rest/packages/restPackage1/assets/rule4" title="rule4" rel="asset" /> 
     *   
     *   <metadata xmlns=""> 
     *     <archived>
     *       <value>false</value> 
     *     </archived>
     *     <uuid>
     *       <value>a2cabf64-bf84-4a42-83f0-1ce24702fc63</value> 
     *     </uuid> 
     *     <state>
     *       <value /> 
     *     </state> 
     *     <versionNumber>
     *       <value>3</value>
     *     </versionNumber>
     *     <checkinComment>
     *       <value>version3</value>
     *     </checkinComment>
     *   </metadata> 
     *   
     *   <content src= "http://127.0.0.1:8080/1c71582d-3 f64-4027-bc5a-d442ab4816e6/rest/packages/restPackage1/binary"/> 
     * </entry>
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
        //System.out.println("------------------------");
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
        ExtensibleElement checkinCommentExtension = metadataExtension.getExtension(Translator.CHECKIN_COMMENT);  
        assertEquals("version3", checkinCommentExtension.getSimpleExtension(Translator.VALUE));
        ExtensibleElement versionNumberExtension = metadataExtension.getExtension(Translator.VERSION_NUMBER);  
        assertEquals("3", versionNumberExtension.getSimpleExtension(Translator.VALUE));
    }

    /* Package Creation */
    @Test @RunAsClient
    public void testCreatePackageFromJAXB(@ArquillianResource URL baseURL) throws Exception {
        Package p = createTestPackage("TestCreatePackageFromJAXB");
        p.setDescription("desc for testCreatePackageFromJAXB");
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
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
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
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        Package result = unmarshalPackageXML(connection.getInputStream());
        assertEquals("TestCreatePackageFromJAXB", result.getTitle());
        assertEquals("desc for testCreatePackageFromJAXB", result.getDescription());
        assertNotNull(result.getPublished());
        assertEquals(new URL(baseURL, "rest/packages/TestCreatePackageFromJAXB/source").toExternalForm(), result.getSourceLink().toString());
        assertEquals(new URL(baseURL, "rest/packages/TestCreatePackageFromJAXB/binary").toExternalForm(), result.getBinaryLink().toString());
        PackageMetadata pm = result.getMetadata();
        assertFalse(pm.isArchived());
        assertNotNull(pm.getCreated());
        assertNotNull(pm.getUuid());
    }

    /* Package Creation */
    @Test @RunAsClient
    public void testCreatePackageFromDRLAsEntry(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.setDoOutput(true);

        //Send request
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getClass().getResourceAsStream("simple_rules.drl");
            out = connection.getOutputStream();
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        assertEquals (200, connection.getResponseCode());
        assertEquals (MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient
    public void testCreatePackageFromDRLAsJson(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.setDoOutput(true);

        //Send request
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getClass().getResourceAsStream("simple_rules2.drl");
            out = connection.getOutputStream();
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        assertEquals (200, connection.getResponseCode());
        assertEquals (MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient
    public void testCreatePackageFromDRLAsJaxB(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.setDoOutput(true);

        //Send request
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getClass().getResourceAsStream("simple_rules3.drl");
            out = connection.getOutputStream();
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        assertEquals(200, connection.getResponseCode());
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
        ExtensibleElement extension = entry.addExtension(Translator.METADATA);
        ExtensibleElement childExtension = extension.addExtension(Translator.CHECKIN_COMMENT);
        childExtension.addSimpleExtension(Translator.VALUE, "checkin comment:initial desc for testCreatePackageFromAtom");
      	
    	ClientResponse resp = client.post(new URL(baseURL, "rest/packages").toExternalForm(), entry);
        //System.out.println(GetContent(resp.getInputStream()));

		assertEquals(ResponseType.SUCCESS, resp.getType());

		Document<Entry> doc = resp.getDocument();
		Entry returnedEntry = doc.getRoot();
		assertEquals(baseURL.getPath() + "rest/packages/testCreatePackageFromAtom", returnedEntry.getBaseUri().getPath());
		assertEquals("testCreatePackageFromAtom", returnedEntry.getTitle());
		assertEquals("desc for testCreatePackageFromAtom", returnedEntry.getSummary());
        ExtensibleElement metadataExtension = entry.getExtension(Translator.METADATA);
        ExtensibleElement checkinCommentExtension = metadataExtension.getExtension(Translator.CHECKIN_COMMENT);
        assertEquals("checkin comment:initial desc for testCreatePackageFromAtom", checkinCommentExtension.getSimpleExtension(Translator.VALUE));
        

		//Test update package
        Entry e = abdera.newEntry();
        e.setTitle("testCreatePackageFromAtom");
        org.apache.abdera.model.Link l = Abdera.getNewFactory().newLink();
        l.setHref(new URL(baseURL, "rest/packages/testCreatePackageFromAtom").toExternalForm());
        l.setRel("self");
        e.addLink(l);
        e.setSummary("updated desc for testCreatePackageFromAtom");
        e.addAuthor("Test McTesty");		
        extension = e.addExtension(Translator.METADATA);
        childExtension = extension.addExtension(Translator.CHECKIN_COMMENT);
        childExtension.addSimpleExtension(Translator.VALUE, "checkin comment:updated desc for testCreatePackageFromAtom");  
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
		assertNotNull(entry.getPublished());
	    assertNotNull(entry.getAuthor().getName());     
		assertEquals("updated desc for testCreatePackageFromAtom", entry.getSummary());
        metadataExtension  = entry.getExtension(Translator.METADATA); 
        checkinCommentExtension = metadataExtension.getExtension(Translator.CHECKIN_COMMENT);  
        assertEquals("checkin comment:updated desc for testCreatePackageFromAtom", checkinCommentExtension.getSimpleExtension(Translator.VALUE));
        
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
        assertEquals (404, conn2.getResponseCode());
    }
    
    @Test @RunAsClient 
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
        assertEquals("testRenamePackageFromAtom", returnedEntry.getTitle());
        assertEquals("desc for testRenamePackageFromAtom", returnedEntry.getSummary());
        
        
        //Test rename package
        Entry e = abdera.newEntry();
        e.setTitle("testRenamePackageFromAtomNew");
        org.apache.abdera.model.Link l = Abdera.getNewFactory().newLink();
        l.setHref(new URL(baseURL, "rest/packages/testRenamePackageFromAtomNew").toExternalForm());
        l.setRel("self");
        e.addLink(l);
        e.setSummary("renamed package testRenamePackageFromAtom");
        e.addAuthor("Test McTesty");        
        resp = client.put(new URL(baseURL, "rest/packages/testRenamePackageFromAtom").toExternalForm(), e);
        assertEquals(ResponseType.SUCCESS, resp.getType());
        assertEquals(204, resp.getStatus());

        
        //Verify new package is available after renaming
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
        assertEquals("renamed package testRenamePackageFromAtom", entry.getSummary());
        
        
        //Verify the old package does not exist after renaming
        URL url2 = new URL(baseURL, "rest/packages/testRenamePackageFromAtom");
        HttpURLConnection conn2 = (HttpURLConnection)url2.openConnection();
        conn2.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn2.setRequestMethod("GET");
        conn2.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn2.connect();
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals (404, conn2.getResponseCode());
        
        
        //Roll back changes. 
        resp = client.delete(new URL(baseURL, "rest/packages/testRenamePackageFromAtomNew").toExternalForm());
        assertEquals(ResponseType.SUCCESS, resp.getType());

        
        //Verify the package is indeed deleted
        URL url3 = new URL(baseURL, "rest/packages/testRenamePackageFromAtomNew");
        HttpURLConnection conn3 = (HttpURLConnection)url3.openConnection();
        conn3.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn3.setRequestMethod("GET");
        conn3.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn3.connect();
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals (404, conn3.getResponseCode());
    }
    
    @Test @RunAsClient 
    public void testRenamePackageFromXML(@ArquillianResource URL baseURL) throws Exception {
        //create a package for testing
        Package p = createTestPackage("testRenamePackageFromXML");
        p.setDescription("desc for testRenamePackageFromXML");
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
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        
        DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
        wr.writeBytes (xml);
        wr.flush ();
        wr.close ();

        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        Package result = unmarshalPackageXML(connection.getInputStream());
        assertEquals("testRenamePackageFromXML", result.getTitle());
        assertEquals("desc for testRenamePackageFromXML", result.getDescription());
        assertNotNull(result.getPublished());
        assertEquals(new URL(baseURL, "rest/packages/testRenamePackageFromXML/source").toExternalForm(), result.getSourceLink().toString());
        assertEquals(new URL(baseURL, "rest/packages/testRenamePackageFromXML/binary").toExternalForm(), result.getBinaryLink().toString());
        PackageMetadata pm = result.getMetadata();
        assertFalse(pm.isArchived());
        assertNotNull(pm.getCreated());
        assertNotNull(pm.getUuid());
         
        
        //Test rename package      
        p.setDescription("renamed package testRenamePackageFromXML");
        p.setTitle("testRenamePackageFromXMLNew");
        JAXBContext context2 = JAXBContext.newInstance(p.getClass());
        Marshaller marshaller2 = context2.createMarshaller();
        StringWriter sw2 = new StringWriter();
        marshaller2.marshal(p, sw2);
        String xml2 = sw2.toString();
        URL url2 = new URL(baseURL, "rest/packages/testRenamePackageFromXML");
        HttpURLConnection connection2 = (HttpURLConnection)url2.openConnection();
        connection2.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection2.setRequestMethod("PUT");
        connection2.setRequestProperty("Content-Type", MediaType.APPLICATION_XML);
        connection2.setRequestProperty("Content-Length", Integer.toString(xml2.getBytes().length));
        connection2.setUseCaches (false);
        //connection2.setDoInput(true);
        connection2.setDoOutput(true);

        OutputStreamWriter out = new OutputStreamWriter(connection2.getOutputStream());
        out.write(xml2);
        out.close();
        connection2.getInputStream();
        //assertEquals (200, connection2.getResponseCode());
        
        //Verify the new package is available after renaming
        URL url3 = new URL(baseURL, "rest/packages/testRenamePackageFromXMLNew");
        HttpURLConnection conn3 = (HttpURLConnection)url3.openConnection();
        conn3.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn3.setRequestMethod("GET");
        conn3.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn3.connect();
        //System.out.println(GetContent(conn));
        assertEquals (200, conn3.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, conn3.getContentType());
        
        InputStream in = conn3.getInputStream();
        assertNotNull(in);
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();
        assertEquals(baseURL.getPath() + "rest/packages/testRenamePackageFromXMLNew", entry.getBaseUri().getPath());
        assertEquals("testRenamePackageFromXMLNew", entry.getTitle());
        assertTrue(entry.getPublished() != null);
        assertEquals("renamed package testRenamePackageFromXML", entry.getSummary());
        
        
        //Verify the old package does not exist after renaming
        URL url4 = new URL(baseURL, "rest/packages/testRenamePackageFromXML");
        HttpURLConnection conn4 = (HttpURLConnection)url4.openConnection();
        conn4.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn4.setRequestMethod("GET");
        conn4.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn4.connect();
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals (404, conn4.getResponseCode());
        
        
        //Roll back changes. 
        Abdera abdera = new Abdera();
        AbderaClient client = new AbderaClient(abdera);     
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        ClientResponse resp = client.delete(new URL(baseURL, "rest/packages/testRenamePackageFromXMLNew").toExternalForm());
        assertEquals(ResponseType.SUCCESS, resp.getType());

        
        //Verify the package is indeed deleted
        URL url5 = new URL(baseURL, "rest/packages/testRenamePackageFromXMLNew");
        HttpURLConnection conn5 = (HttpURLConnection)url5.openConnection();
        conn5.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        conn5.setRequestMethod("GET");
        conn5.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        conn5.connect();
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        assertEquals (404, conn5.getResponseCode());
    }
    
    @Ignore @Test @RunAsClient
    public void testCreatePackageFromJson(@ArquillianResource URL baseURL) {
        //TODO: implement test
    }
    
    @Test @RunAsClient
    public void testCreatePackageSnapshot(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/snapshot/testsnapshot");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("POST");
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        assertEquals (204, connection.getResponseCode());
    }

    @Test @RunAsClient
    public void testPackageNotExists(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restNotExistingPackage");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals(404, connection.getResponseCode());
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
        assertTrue( result.indexOf( "import org.drools.Cheese" ) >= 0 );
        assertTrue( result.indexOf( "global org.drools.Person customer2" ) >= 0 );
        assertTrue( result.indexOf( "function void foo() { System.out.println(\"version 2\"); }" ) >= 0 );
        assertTrue( result.indexOf( "declare Album2" ) >= 0 );
    }

    /* Tests package compilation in addition to byte retrieval */
    @Test @RunAsClient
    public void testGetPackageBinary (@ArquillianResource URL baseURL) throws Exception {
        // restPackageCompilationFailure build fails due to: ClassNotFoundException
        URL url = new URL(baseURL, "rest/packages/restPackageCompilationFailure/binary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();

        assertEquals(500, connection.getResponseCode());
        
        //restPackage2 should build ok. 
        URL url2 = new URL(baseURL, "rest/packages/restPackage2/binary");
        HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
        connection2.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection2.setRequestMethod("GET");
        connection2.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection2.connect();

        assertEquals(200, connection2.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection2.getContentType());
        //System.out.println(IOUtils.toString(connection2.getInputStream()));
    }

    @Test @RunAsClient
    public void testUpdatePackageFromJAXB(@ArquillianResource URL baseURL) throws Exception {
        //create a package for fixtures
        Package p = createTestPackage("testUpdatePackageFromJAXB");
        p.setDescription("desc for testUpdatePackageFromJAXB");
        p.getMetadata().setCheckinComment("checkincomment for testUpdatePackageFromJAXB");
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
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        
        DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
        wr.writeBytes (xml);
        wr.flush ();
        wr.close ();

        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        Package result = unmarshalPackageXML(connection.getInputStream());
        assertEquals("testUpdatePackageFromJAXB", result.getTitle());
        assertEquals("desc for testUpdatePackageFromJAXB", result.getDescription());
        assertNotNull(result.getPublished());
        assertEquals(new URL(baseURL, "rest/packages/testUpdatePackageFromJAXB/source").toExternalForm(), result.getSourceLink().toString());
        assertEquals(new URL(baseURL, "rest/packages/testUpdatePackageFromJAXB/binary").toExternalForm(), result.getBinaryLink().toString());
        PackageMetadata pm = result.getMetadata();
        assertFalse(pm.isArchived());
        assertNotNull(pm.getCreated());
        assertNotNull(pm.getUuid());
        assertEquals("checkincomment for testUpdatePackageFromJAXB", pm.getCheckinComment());
        
        //Test update package      
        Package p2 = createTestPackage("testUpdatePackageFromJAXB");
        p2.setDescription("update package testUpdatePackageFromJAXB");
        PackageMetadata meta = new PackageMetadata();
        meta.setCheckinComment("checkInComment: update package testUpdatePackageFromJAXB");
        p2.setMetadata(meta);
        JAXBContext context2 = JAXBContext.newInstance(p2.getClass());
        Marshaller marshaller2 = context2.createMarshaller();
        StringWriter sw2 = new StringWriter();
        marshaller2.marshal(p2, sw2);
        String xml2 = sw2.toString();
        URL url2 = new URL(baseURL, "rest/packages/testUpdatePackageFromJAXB");
        HttpURLConnection connection2 = (HttpURLConnection)url2.openConnection();
        connection2.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection2.setRequestMethod("PUT");
        connection2.setRequestProperty("Content-Type", MediaType.APPLICATION_XML);
        connection2.setRequestProperty("Content-Length", Integer.toString(xml2.getBytes().length));
        connection2.setUseCaches (false);
        //connection2.setDoInput(true);
        connection2.setDoOutput(true);

        OutputStreamWriter out = new OutputStreamWriter(connection2.getOutputStream());
        out.write(xml2);
        out.close();
        connection2.getInputStream();
       
        
        //Verify
        URL url3 = new URL(baseURL, "rest/packages/testUpdatePackageFromJAXB");
        HttpURLConnection connection3 = (HttpURLConnection)url3.openConnection();
        connection3.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection3.setRequestMethod("GET");
        connection3.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection3.connect();
        assertEquals (200, connection3.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection3.getContentType());
        //System.out.println("------------------------");
        //System.out.println(IOUtils.toString(connection.getInputStream()));
        Package p3 = unmarshalPackageXML(connection3.getInputStream());
        assertEquals("testUpdatePackageFromJAXB", p3.getTitle());
        assertEquals("update package testUpdatePackageFromJAXB", p3.getDescription());
        assertNotNull(p3.getPublished());
        assertEquals(new URL(baseURL, "rest/packages/testUpdatePackageFromJAXB/source").toExternalForm(), p3.getSourceLink().toString());
        assertEquals(new URL(baseURL, "rest/packages/testUpdatePackageFromJAXB/binary").toExternalForm(), p3.getBinaryLink().toString());
        PackageMetadata pm3 = p3.getMetadata();
        assertFalse(pm3.isArchived());
        assertNotNull(pm3.getCreated());
        assertNotNull(pm3.getUuid());
        assertEquals("checkInComment: update package testUpdatePackageFromJAXB", pm3.getCheckinComment());
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
		
        ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA); 
        ExtensibleElement archivedExtension = metadataExtension.getExtension(Translator.ARCHIVED);     
        assertEquals("false", archivedExtension.getSimpleExtension(Translator.VALUE)); 
        ExtensibleElement uuidExtension = metadataExtension.getExtension(Translator.UUID);     
        assertNotNull(uuidExtension.getSimpleExtension(Translator.VALUE)); 
        ExtensibleElement checkinCommentExtension = metadataExtension.getExtension(Translator.CHECKIN_COMMENT);  
        assertEquals("version2", checkinCommentExtension.getSimpleExtension(Translator.VALUE));
        ExtensibleElement versionNumberExtension = metadataExtension.getExtension(Translator.VERSION_NUMBER);  
        assertEquals("2", versionNumberExtension.getSimpleExtension(Translator.VALUE));		
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
        assertTrue(result.indexOf( "import org.drools.Cheese" ) >= 0 );
        assertTrue(result.indexOf( "global org.drools.Person customer1" ) >= 0 );
        assertTrue(result.indexOf( "function void foo() { System.out.println(\"version 1\"); }" ) >= 0 );
        assertTrue(result.indexOf( "declare Album1" ) >= 0 );
    }
    
    //REVISIT: https://issues.jboss.org/browse/GUVNOR-1232: Force a pacakge rebuild before every package check in operation. 
    @Test @RunAsClient 
    public void testGetHistoricalPackageBinary(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage2/versions/2/binary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();

        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
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
        connection2.setRequestProperty("Content-Type", MediaType.TEXT_PLAIN);
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
        assertEquals(404, connection.getResponseCode());

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
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
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
        assertEquals(404, connection.getResponseCode());
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
        assertEquals(404, connection.getResponseCode());

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
        assertEquals(404, connection.getResponseCode());
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
        connection2.setRequestProperty("Content-Type", MediaType.APPLICATION_XML);
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
        assertEquals(404, connection.getResponseCode());

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
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
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
        assertEquals(404, connection.getResponseCode());
    }

    protected Package createTestPackage(String title) {
        Package p = new Package();
        PackageMetadata metadata = new PackageMetadata();
        metadata.setCreated(new Date(System.currentTimeMillis()));
        metadata.setUuid(UUID.randomUUID().toString());
        metadata.setCheckinComment("Check in comment for test package.");

        p.setMetadata(metadata);
        p.setAuthor("awaterma");
        p.setPublished(new Date(System.currentTimeMillis()));
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
