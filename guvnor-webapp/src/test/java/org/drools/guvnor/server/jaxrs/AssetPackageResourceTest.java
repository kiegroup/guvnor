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
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.parser.Parser;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.jaxrs.jaxb.Asset;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.utils.IOUtils;
import org.drools.util.codec.Base64;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
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

import static org.junit.Assert.*;

public class AssetPackageResourceTest extends GuvnorTestBase {

    private static int totalAssets = 7;

    private Abdera abdera = new Abdera();

    public AssetPackageResourceTest() {
        autoLoginAsAdmin = false;
    }

//    @BeforeClass
    // Unreliable HACK
    // Fixable after this is fixed: https://issues.jboss.org/browse/ARQ-540
    @Test
    public void startServers() throws Exception {
        CategoryItem cat = rulesRepository.loadCategory("/");
        cat.addCategory("AssetPackageResourceTestCategory",
                "yeah");
        cat.addCategory("AssetPackageResourceTestCategory2",
        "yeah");        
        
        rulesRepository.createState( "Dev" );
        
        //Package version 1(Initial version)
        PackageItem pkg = rulesRepository.createPackage("restPackage1",
                "this is package restPackage1");

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
        int assetsCount = 0;
        while (assets.hasNext()) {
            assetsCount++;
            assets.next();
        }
        assertEquals(totalAssets, assetsCount);
    }

    @Test @RunAsClient
    public void testGetAssetsAsAtom(@ArquillianResource URL baseURL) throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        ClientResponse resp = client.get(new URL(baseURL, "rest/packages/restPackage1/assets").toExternalForm(), options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve assets-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        //Get the entry element
        Document<Feed> document = resp.getDocument();
        
        assertEquals(totalAssets, document.getRoot().getEntries().size());
    }

    @Test @RunAsClient
    public void testGetAssetsAsJaxB(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //logger.log(LogLevel, getContent(connection));
    }

    @Test @RunAsClient
     public void testGetAssetsAsJson(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log(LogLevel, getContent(connection));
    }
    
    @Test @RunAsClient
    public void testGetDRLAssetsAsAtom(@ArquillianResource URL baseURL) throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);
        
        ClientResponse resp = client.get(new URL(baseURL, "rest/packages/restPackage1/assets?format=drl").toExternalForm(), options);
        
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
    
    @Test @RunAsClient
    public void testGetDRLAssetsAsJSON(@ArquillianResource URL baseURL) throws Exception {
        
        //Use abdera for connection only
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_JSON);
        
        ClientResponse resp = client.get(new URL(baseURL, "rest/packages/restPackage1/assets?format=drl").toExternalForm(), options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve DRL assets-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        BufferedReader reader = new BufferedReader(resp.getReader());
        reader.close();
        //TODO: check response content!
        
    }
    
    
    @Test @RunAsClient
    public void testGetDRLAndDSLAssetsAsAtom(@ArquillianResource URL baseURL) throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);
        
        ClientResponse resp = client.get(new URL(baseURL, "rest/packages/restPackage1/assets?format=drl&format=dsl").toExternalForm(), options);
        
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
    
    @Test @RunAsClient
    public void testGetDRLAndDSLAssetsAsJSON(@ArquillianResource URL baseURL) throws Exception {
        
        //Use abdera for connection only
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_JSON);
        
        ClientResponse resp = client.get(new URL(baseURL, "rest/packages/restPackage1/assets?format=drl&format=dsl").toExternalForm(),options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve DRL and DSL assets-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        BufferedReader reader = new BufferedReader(resp.getReader());
        reader.close();
        //TODO: check response content!
        
    }

    @Test @RunAsClient
    public void testGetAssetAsAtom(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //System.out.println(getContent(connection));
        
        InputStream in = connection.getInputStream();
        assertNotNull(in);
		Document<Entry> doc = abdera.getParser().parse(in);
		Entry entry = doc.getRoot();
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1", entry.getBaseUri().getPath());
		assertEquals("model1", entry.getTitle());
		assertNotNull(entry.getPublished());
		assertNotNull(entry.getAuthor().getName());
		assertEquals("desc for model1", entry.getSummary());
		//assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE.getType(), entry.getContentMimeType().getPrimaryType());
		assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1/binary", entry.getContentSrc().getPath());
		
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
        assertEquals("AssetPackageResourceTestCategory", categoryExtension.getSimpleExtension(Translator.VALUE));		
    }
    
    @Test @RunAsClient
    public void testUpdateAssetFromAtom(@ArquillianResource URL baseURL) throws Exception {     
        URL url = new URL(baseURL + "rest/packages/restPackage1/assets/model1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
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
        assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1", entry.getBaseUri().getPath());
        assertEquals("model1", entry.getTitle());
        assertNotNull(entry.getPublished());
        assertNotNull(entry.getAuthor().getName());
        assertEquals("desc for model1", entry.getSummary());
        //assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE.getType(), entry.getContentMimeType().getPrimaryType());
        assertEquals(baseURL.getPath() + "rest/packages/restPackage1/assets/model1/binary", entry.getContentSrc().getPath());
        
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
        assertEquals("AssetPackageResourceTestCategory", categoryExtension.getSimpleExtension(Translator.VALUE));   
        connection.disconnect();
        
        //Update category. Add a new category tag
        categoryExtension.addSimpleExtension(Translator.VALUE, "AssetPackageResourceTestCategory2");
        //Update state
        stateExtension.getExtension(Translator.VALUE).setText("Dev");
        //Update format
        formatExtension.getExtension(Translator.VALUE).setText("anotherformat");
        
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-type", MediaType.APPLICATION_ATOM_XML);
        connection.setDoOutput(true);
        entry.writeTo(connection.getOutputStream());
        assertEquals(204, connection.getResponseCode());
        connection.disconnect();
        
        //Verify again
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        //System.out.println(GetContent(connection));

        in = connection.getInputStream();
        assertNotNull(in);
        doc = abdera.getParser().parse(in);
        entry = doc.getRoot();
        
        metadataExtension  = entry.getExtension(Translator.METADATA); 
        archivedExtension = metadataExtension.getExtension(Translator.ARCHIVED);     
        assertEquals("false", archivedExtension.getSimpleExtension(Translator.VALUE));      
        stateExtension = metadataExtension.getExtension(Translator.STATE);     
        assertEquals("Dev", stateExtension.getSimpleExtension(Translator.VALUE)); 
        formatExtension = metadataExtension.getExtension(Translator.FORMAT);     
        assertEquals("anotherformat", formatExtension.getSimpleExtension(Translator.VALUE)); 
        categoryExtension = metadataExtension.getExtension(Translator.CATEGORIES);     
        List<Element> categoryValues = categoryExtension.getExtensions(Translator.VALUE);
        assertTrue(categoryValues.size() == 2);
        boolean foundCategory1 = false;
        boolean foundCategory2 = false;
        for (Element cat : categoryValues) {
            String catgoryValue = cat.getText();
            if ("AssetPackageResourceTestCategory".equals(catgoryValue)) {
                foundCategory1 = true;
            }
            if ("AssetPackageResourceTestCategory2".equals(catgoryValue)) {
                foundCategory2 = true;
            }
        }
        assertTrue(foundCategory1);
        assertTrue(foundCategory2);
    }
    
    @Test @RunAsClient
    public void testUpdateAssetFromAtomWithStateNotExist(@ArquillianResource URL baseURL) throws Exception {     
        URL url = new URL(baseURL + "rest/packages/restPackage1/assets/model1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        InputStream in = connection.getInputStream();
        assertNotNull(in);
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();       
        
        //Update state
        ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA); 
        ExtensibleElement stateExtension = metadataExtension.getExtension(Translator.STATE);   
        stateExtension.getExtension(Translator.VALUE).setText("NonExistState");
        
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-type", MediaType.APPLICATION_ATOM_XML);
        connection.setDoOutput(true);
        entry.writeTo(connection.getOutputStream());
        assertEquals(500, connection.getResponseCode());
        connection.disconnect();                
    }

    
    @Test @RunAsClient
    public void testGetAssetAsJaxB(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        System.out.println(IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient
    public void testGetAssetAsJson(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log(LogLevel, getContent(connection));
    }

    @Test @RunAsClient
    public void testGetAssetSource(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1/source");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.TEXT_PLAIN, connection.getContentType());
        String result = IOUtils.toString(connection.getInputStream());
        assertTrue(result.indexOf("declare Album2")>=0);
        assertTrue(result.indexOf("genre2: String")>=0);
    }

    @Test @RunAsClient
    public void testGetAssetBinary(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1/binary");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_OCTET_STREAM);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, connection.getContentType());
        //logger.log(LogLevel, getContent(connection));
    }

    @Test @RunAsClient
    public void testCreateAssetFromAtom(@ArquillianResource URL baseURL) throws Exception {
        
        //Check there is no model1-New asset
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        ClientResponse resp = client.get(new URL(baseURL, "rest/packages/restPackage1/assets/model1-New").toExternalForm());
        
        //If the asset doesn't exist, an HTTP 500 Error is expected. :S
        if (resp.getType() != ResponseType.SERVER_ERROR){
            fail("I was expecting an HTTP 500 Error because 'model1-New' shouldn't exist. "
                    + "Instead of that I got-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        
        //--------------------------------------------------------------
        
        
        //Get asset 'model1' from Guvnor
        client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        
        options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        resp = client.get(new URL(baseURL, "rest/packages/restPackage1/assets/model1").toExternalForm());
        
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
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        
        options = client.getDefaultRequestOptions();
        options.setContentType(MediaType.APPLICATION_ATOM_XML);

        resp = client.post(new URL(baseURL, "rest/packages/restPackage1/assets").toExternalForm(), entry, options);
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't store 'model1-New' asset-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        
        //--------------------------------------------------------------
        
        
        //Check that the new asset is in the repository
        client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        resp = client.get(new URL(baseURL, "rest/packages/restPackage1/assets/model1-New").toExternalForm());
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Couldn't retrieve 'model1-New' asset-> "+resp.getStatus()+": "+resp.getStatusText());
        }
        
        //Get the entry element
        doc = resp.getDocument();
        entry = doc.getRoot();
        
        //Compare the title :P
        assertEquals(entry.getTitle(),"model1-New");
    }

    @Test @RunAsClient
    @Ignore
    public void testUpdateAssetFromJaxB(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/packages/restPackage1/assets/model1");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
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
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
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

    @Test @RunAsClient
    @Ignore
    public void testUpdateAssetFromJson(@ArquillianResource URL baseURL) throws Exception {
        //TODO: implement test
    }

}
