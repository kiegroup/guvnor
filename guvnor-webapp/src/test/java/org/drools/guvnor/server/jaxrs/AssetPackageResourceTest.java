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

import java.util.Iterator;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.parser.Parser;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.jaxrs.jaxb.Asset;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.junit.*;
import org.mvel2.util.StringAppender;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;


public class AssetPackageResourceTest extends AbstractBusClientServerTestBase {
    private static int totalAssets;
    private Abdera abdera = new Abdera();
    private static RestTestingBase restTestingBase;

    @BeforeClass
    public static void startServers() throws Exception {
       	restTestingBase = new RestTestingBase();
       	restTestingBase.setup();       	

        assertTrue("server did not launch correctly",
                   launchServer(CXFJAXRSServer.class, true));

        ServiceImplementation impl = restTestingBase.getServiceImplementation();
        

        CategoryItem cat = impl.getRulesRepository().loadCategory( "/" );
        cat.addCategory( "AssetPackageResourceTestCategory",
                         "yeah" );
        
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
        
        AssetItem rule4 = pkg.addAsset( "rule4",
                                       "" );
        rule4.updateFormat( AssetFormats.DRL );
        rule4.updateContent( "rule 'foo 2' when Goo1() then end" );
        rule4.checkin( "version 1" );

        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        rule2.updateContent( "when \n foo \n then \n call a func" );
        rule2.checkin( "version 1" );

        AssetItem rule3 = pkg.addAsset( "model1",
                                        "desc for model1",
                                        "AssetPackageResourceTestCategory",
                                        AssetFormats.DRL_MODEL
                                        );
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
        
        //Count all the assets
        Iterator<AssetItem> assets = pkg.getAssets();
        while (assets.hasNext()) {
            totalAssets++;
            assets.next();
        }
    }
    
    @AfterClass
    public static void tearDown() {
    	restTestingBase.tearDownGuvnorTestBase();
    }

    @Test
    public void testGetAssetsAsAtom() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);
        
        ClientResponse resp = client.get(generateBaseUrl() + "/packages/restPackage1/assets",options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve assets-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        //Get the entry element
        Document<Feed> document = resp.getDocument();
        
        assertEquals(totalAssets, document.getRoot().getEntries().size());
        
    }

    @Test
    public void testGetAssetsAsJaxB() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/assets");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));
    }

    @Test
     public void testGetAssetsAsJson() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/assets");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));
    }
    
    @Test
    public void testGetDRLAssetsAsAtom() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);
        
        ClientResponse resp = client.get(generateBaseUrl() + "/packages/restPackage1/assets?format=drl", options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve DRL assets-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        //Get the entry element
        Document<Feed> document = resp.getDocument();
        
        //Check number of results
        assertEquals(2, document.getRoot().getEntries().size());
        
        //Check assets names
        List<String> assetNames = new ArrayList<String>();
        for (Entry entry : document.getRoot().getEntries()) {
            assetNames.add(entry.getTitle());
        }
        
        assertTrue(assetNames.contains("rule1"));
        assertTrue(assetNames.contains("rule4"));
    }
    
    @Test
    public void testGetDRLAssetsAsJSON() throws Exception {
        
        //Use abdera for connection only
        AbderaClient client = new AbderaClient(abdera);
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_JSON);
        
        ClientResponse resp = client.get(generateBaseUrl() + "/packages/restPackage1/assets?format=drl",options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve DRL assets-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        BufferedReader reader = new BufferedReader(resp.getReader());
        reader.close();
        //TODO: check response content!
        
    }
    
    
    @Test
    public void testGetDRLAndDSLAssetsAsAtom() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);
        
        ClientResponse resp = client.get(generateBaseUrl() + "/packages/restPackage1/assets?format=drl&format=dsl", options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve DRL and DSL assets-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        //Get the entry element
        Document<Feed> document = resp.getDocument();
        
        //Check number of results
        assertEquals(3, document.getRoot().getEntries().size());
        
        //Check assets names
        List<String> assetNames = new ArrayList<String>();
        for (Entry entry : document.getRoot().getEntries()) {
            assetNames.add(entry.getTitle());
        }
        
        assertTrue(assetNames.contains("rule1"));
        assertTrue(assetNames.contains("rule4"));
        assertTrue(assetNames.contains("myDSL"));
    }
    
    @Test
    public void testGetDRLAndDSLAssetsAsJSON() throws Exception {
        
        //Use abdera for connection only
        AbderaClient client = new AbderaClient(abdera);
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_JSON);
        
        ClientResponse resp = client.get(generateBaseUrl() + "/packages/restPackage1/assets?format=drl&format=dsl",options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve DRL and DSL assets-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        BufferedReader reader = new BufferedReader(resp.getReader());
        reader.close();
        //TODO: check response content!
        
    }

    @Test
    public void testGetAssetAsAtom() throws Exception {   	
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/assets/model1");
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
		assertEquals("/packages/restPackage1/assets/model1", entry.getBaseUri().getPath());		
		assertEquals("model1", entry.getTitle());
		assertNotNull(entry.getPublished());
		assertNotNull(entry.getAuthor().getName());
		assertEquals("desc for model1", entry.getSummary());
		//assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE.getType(), entry.getContentMimeType().getPrimaryType());
		assertEquals("/packages/restPackage1/assets/model1/binary", entry.getContentSrc().getPath());
		
		ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA); 
        ExtensibleElement archivedExtension = metadataExtension.getExtension(Translator.ARCHIVED);     
		assertEquals("false", archivedExtension.getSimpleExtension(Translator.VALUE)); 		
        ExtensibleElement stateExtension = metadataExtension.getExtension(Translator.STATE);     
		assertEquals("Draft", stateExtension.getSimpleExtension(Translator.VALUE)); 
        ExtensibleElement formatExtension = metadataExtension.getExtension(Translator.FORMAT);     
		assertEquals("model.drl", formatExtension.getSimpleExtension(Translator.VALUE)); 
        ExtensibleElement uuidExtension = metadataExtension.getExtension(Translator.UUID);     
		assertNotNull(uuidExtension.getSimpleExtension(Translator.VALUE));         
        ExtensibleElement categoryExtension = metadataExtension.getExtension(Translator.CATEGORIES);     
        assertNotNull(categoryExtension.getSimpleExtension(Translator.VALUE));		
    }

    @Test
    public void testGetAssetAsJaxB() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/assets/model1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        System.out.println(GetContent(connection));
    }

    @Test
    public void testGetAssetAsJson() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/assets/model1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testGetAssetSource() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/assets/model1/source");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        String result = GetContent(connection);
        assertTrue(result.indexOf("declare Album2")>=0);
        assertTrue(result.indexOf("genre2: String")>=0);
    }

    @Test
    public void testGetAssetBinary() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/assets/model1/binary");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        //logger.log(LogLevel, GetContent(connection));
    }

    @Test
    public void testCreateAssetFromAtom() throws Exception {
        
        //Check there is no model1-New asset
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        ClientResponse resp = client.get(generateBaseUrl() + "/packages/restPackage1/assets/model1-New");
        
        //If the asset doesn't exist, an HTTP 500 Error is expected. :S
        if (resp.getType() != ResponseType.SERVER_ERROR){
            fail("I was expecting an HTTP 500 Error because 'model1-New' shouldn't exist. "
                    + "Instead of that I got-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        
        //--------------------------------------------------------------
        
        
        //Get asset 'model1' from Guvnor
        client = new AbderaClient(abdera);
        
        options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        resp = client.get(generateBaseUrl() + "/packages/restPackage1/assets/model1");
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve 'model1' asset-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        //Get the entry element
        Document<Entry> doc = resp.getDocument();
        Entry entry = doc.getRoot();
        
        
        //--------------------------------------------------------------
        
        
        //Change the title of the asset
        entry.setTitle(entry.getTitle()+"-New");
        
        //Save it as a new Asset
        client = new AbderaClient(abdera);
        
        options = client.getDefaultRequestOptions();
        options.setContentType(MediaType.APPLICATION_ATOM_XML);

        resp = client.post(generateBaseUrl() + "/packages/restPackage1/assets", entry, options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't store 'model1-New' asset-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        
        //--------------------------------------------------------------
        
        
        //Check that the new asset is in the repository
        client = new AbderaClient(abdera);
        options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        resp = client.get(generateBaseUrl() + "/packages/restPackage1/assets/model1-New");
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve 'model1-New' asset-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        //Get the entry element
        doc = resp.getDocument();
        entry = doc.getRoot();
        
        //Compare the title :P
        assertEquals(entry.getTitle(),"model1-New");
    }
    
    @Test
    public void testUpdateAssetFromAtom() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/assets/model1");
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

        url = new URL(generateBaseUrl() + "/packages/restPackage1/assets/model1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-type", MediaType.APPLICATION_ATOM_XML);
        conn.setDoOutput(true);
        e.writeTo(conn.getOutputStream());

        assertEquals(204, conn.getResponseCode());
        conn.disconnect();
    }

    @Test
    @Ignore
    public void testUpdateAssetFromJaxB() throws Exception {
        URL url = new URL(generateBaseUrl() + "/packages/restPackage1/assets/model1");
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

    @Test
    @Ignore
    public void testUpdateAssetFromJson() throws Exception {
        //TODO: implement test
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

}
