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

import javax.xml.namespace.QName;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.Abdera;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.guvnor.server.ServiceImplementation;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.drools.util.codec.Base64;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.*;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

//import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.drools.guvnor.server.jaxrs.jaxb.Category;

import static org.junit.Assert.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CategoryResourceTest extends GuvnorTestBase {

    private Abdera abdera = new Abdera();
    
    public CategoryResourceTest() {
        autoLoginAsAdmin = false;
    }
    
//    @BeforeClass
    // Unreliable HACK
    // Fixable after this is fixed: https://issues.jboss.org/browse/ARQ-540
    @Test
    public void startServers() throws Exception {
        
        //Create 2 categories
        repositoryCategoryService.createCategory(null, "Category 1", "Category 1");
        repositoryCategoryService.createCategory(null, "Category 2", "Category 2");
        repositoryCategoryService.createCategory("Category 1", "Category 1.1", "Category 1.1");
        repositoryCategoryService.createCategory("Category 1", "Category 1.2", "Category 1.2");
        repositoryCategoryService.createCategory("Category 1/Category 1.1", "Category 1.1.1", "Category 1.1.1");
        
        
        //create a new package
        ModuleItem pkg = rulesRepository.createModule( "categoriesPackage1",
                                                                   "this is package categoriesPackage1" );
        //Create rule1 with 'category 1'
        AssetItem rule = pkg.addAsset( "rule1",
                                       "" );
        rule.updateFormat( AssetFormats.DRL );
        rule.updateContent( "rule 'foo' when Goo1() then end" );
        rule.updateCategoryList(new String[]{"Category 1"});
        rule.checkin( "version 1" );
        
        //Create rule2 with 'category 2'
        rule = pkg.addAsset( "rule2",
                                       "" );
        rule.updateFormat( AssetFormats.DRL );
        rule.updateContent( "rule 'foo' when Goo1() then end" );
        rule.updateCategoryList(new String[]{"Category 2"});
        rule.checkin( "version 1" );
        
        //Create rule3 with 'category 1' and 'category 2'
        rule = pkg.addAsset( "rule3",
                                       "" );
        rule.updateFormat( AssetFormats.DRL );
        rule.updateContent( "rule 'foo' when Goo1() then end" );
        rule.updateCategoryList(new String[]{"Category 1", "Category 2"});
        rule.checkin( "version 1" );
        
    }

    @Test @RunAsClient
    public void getCategoriesAsJAXB(@ArquillianResource URL baseURL) throws Exception {
        //list all categories
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        ClientResponse resp = client.get(new URL(baseURL, "rest/categories").toExternalForm());
        
        assertEquals (ResponseType.SUCCESS, resp.getType());
        assertEquals(MediaType.APPLICATION_XML, resp.getContentType().toString());
        
        Document<Feed> document = resp.getDocument();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.writeTo(outputStream);
        outputStream.close();
        
        Map<String, Category> categories = this.fromXMLToCategoriesMap(outputStream.toString());
        
        assertEquals(5, categories.size());
        assertTrue(categories.containsKey("Category 1"));
        assertTrue(categories.containsKey("Category 2"));
        assertTrue(categories.containsKey("Category 1/Category 1.1"));
        assertTrue(categories.containsKey("Category 1/Category 1.2"));
        assertTrue(categories.containsKey("Category 1/Category 1.1/Category 1.1.1"));
        
    }
    
    @Test @RunAsClient
    public void getCategoryAsJAXB(@ArquillianResource URL baseURL) throws Exception {
        //get 'Category 1'
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        ClientResponse resp = client.get(new URL(baseURL, "rest/categories/Category%201").toExternalForm());
        
        assertEquals (ResponseType.SUCCESS, resp.getType());
        assertEquals(MediaType.APPLICATION_XML, resp.getContentType().toString());
        
        Document<Feed> document = resp.getDocument();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.writeTo(outputStream);
        outputStream.close();
        
        Map<String, Category> categories = this.fromXMLToCategoriesMap(outputStream.toString());
        
        assertEquals(1, categories.size());
        
        Category category = categories.values().iterator().next();
        assertEquals("Category 1", category.getPath());
        
        //get 'Category 1/Category 1.1/Category 1.1.1'
        client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));

        resp = client.get(new URL(baseURL, "rest/categories/Category%201/Category%201.1/Category%201.1.1").toExternalForm());
        
        assertEquals (ResponseType.SUCCESS, resp.getType());
        assertEquals(MediaType.APPLICATION_XML, resp.getContentType().toString());
        
        document = resp.getDocument();

        outputStream = new ByteArrayOutputStream();
        document.writeTo(outputStream);
        outputStream.close();
        
        categories = this.fromXMLToCategoriesMap(outputStream.toString());
        
        assertEquals(1, categories.size());
        category = categories.values().iterator().next();
        
        assertEquals("Category 1/Category 1.1/Category 1.1.1", category.getPath());
        
    }
    
    @Test @RunAsClient
    public void getCategoryChildrenAsJAXB(@ArquillianResource URL baseURL) throws Exception {
        //get children of 'Category 1'
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));

        ClientResponse resp = client.get(new URL(baseURL, "rest/categories/Category%201/children").toExternalForm());
        
        assertEquals (ResponseType.SUCCESS, resp.getType());
        assertEquals(MediaType.APPLICATION_XML, resp.getContentType().toString());
        
        Document<Feed> document = resp.getDocument();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.writeTo(outputStream);
        outputStream.close();
        
        Map<String, Category> categories = this.fromXMLToCategoriesMap(outputStream.toString());
        
        assertEquals(2, categories.size());
        assertTrue(categories.containsKey("Category 1/Category 1.1"));
        assertTrue(categories.containsKey("Category 1/Category 1.2"));
        
        //get children of 'Category 1.1'
        client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));

        resp = client.get(new URL(baseURL, "rest/categories/Category%201/Category%201.1/children").toExternalForm());
        
        assertEquals (ResponseType.SUCCESS, resp.getType());
        assertEquals(MediaType.APPLICATION_XML, resp.getContentType().toString());
        
        document = resp.getDocument();

        outputStream = new ByteArrayOutputStream();
        document.writeTo(outputStream);
        outputStream.close();
        
        categories = this.fromXMLToCategoriesMap(outputStream.toString());
        
        assertEquals(1, categories.size());
        assertTrue(categories.containsKey("Category 1/Category 1.1/Category 1.1.1"));
        
        //get children of 'Category 2'
        client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));

        resp = client.get(new URL(baseURL, "rest/categories/Category%202/children").toExternalForm());
        
        assertEquals (ResponseType.SUCCESS, resp.getType());
        assertEquals(MediaType.APPLICATION_XML, resp.getContentType().toString());
        
        document = resp.getDocument();

        outputStream = new ByteArrayOutputStream();
        document.writeTo(outputStream);
        outputStream.close();
        
        categories = this.fromXMLToCategoriesMap(outputStream.toString());
        
        assertTrue(categories.isEmpty());
        
    }

    @Test @RunAsClient
    public void createCategory(@ArquillianResource URL baseURL) throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));

        ClientResponse resp = client.put(new URL(baseURL, "rest/categories/createCategory").toExternalForm(),
                new ByteArrayInputStream(new byte[]{}));
        assertEquals(ResponseType.SUCCESS, resp.getType());
    }

    @Test @RunAsClient
    public void deleteCategory(@ArquillianResource URL baseURL) throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));

        ClientResponse resp = client.put(new URL(baseURL, "rest/categories/deleteCategory").toExternalForm(),
                new ByteArrayInputStream(new byte[]{}));
        assertEquals(ResponseType.SUCCESS, resp.getType());

        resp = client.delete(new URL(baseURL, "rest/categories/deleteCategory").toExternalForm());
        assertEquals(ResponseType.SUCCESS, resp.getType());
    }

    @Test @RunAsClient
    public void testGetAssetsByCategoryAsAtom(@ArquillianResource URL baseURL) throws Exception {
        //Get assets from category 1
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        ClientResponse resp = client.get(new URL(baseURL, "rest/categories/Category%201/assets").toExternalForm());
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Error getting assets from 'Category 1'");
        }
        
        Document<Feed> document = resp.getDocument();
        
        //Must be 2 assets in the response
        assertEquals(2, document.getRoot().getEntries().size());
        
        List<String> assetNames = new ArrayList<String>();
        for (Entry entry : document.getRoot().getEntries()) {
            assetNames.add(entry.getTitle());
        }
        
        //rule1 and rule3 should be in the response
        assertTrue(assetNames.contains("rule1"));
        assertTrue(assetNames.contains("rule3"));
        
        //-----------------------------
        
        
        //Get assets from category 2
        client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        resp = client.get(new URL(baseURL, "rest/categories/Category%202/assets").toExternalForm());
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Error getting assets from 'Category 1'");
        }
        
        document = resp.getDocument();
        
        //Must be 2 assets in the response
        assertEquals(2, document.getRoot().getEntries().size());
        
        assetNames = new ArrayList<String>();
        for (Entry entry : document.getRoot().getEntries()) {
            assetNames.add(entry.getTitle());
        }
        
        //rule2 and rule3 should be in the response
        assertTrue(assetNames.contains("rule2"));
        assertTrue(assetNames.contains("rule3"));
    }
    
    /**
     * Similar to testGetAssetsByCategoryAsAtom but here the rules are created
     * using REST API. This test demonstrate that the category of assets created 
     * in this way is correctly stored in the repository. See GUVNOR-1599
     * @throws Exception 
     */
    @Test @RunAsClient
    public void testGetAssetsCreatedByAtomByCategoryAsAtom(@ArquillianResource URL baseURL) throws Exception {
        //Create 2 new assets, one in each category
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
            
        Entry processEntry = this.createProcessEntry("Process1", "Some test process", new ArrayList<String>(){{this.add("Category 1");
        }});
        
        //invoke Guvnor REST API to store the process
        RequestOptions options = client.getDefaultRequestOptions();
        options.setContentType("application/atom+xml");

        ClientResponse resp = client.post(new URL(baseURL, "rest/packages/categoriesPackage1/assets").toExternalForm(), processEntry, options);

        if (resp.getType() != ResponseType.SUCCESS){
            fail("Error creating process asset: "+resp.getStatusText());
        }
        
        client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
            
        processEntry = this.createProcessEntry("Process2", "Some other test process", new ArrayList<String>(){{this.add("Category 2");
        }});
        
        //invoke Guvnor REST API to store the process
        options = client.getDefaultRequestOptions();
        options.setContentType("application/atom+xml");

        resp = client.post(new URL(baseURL, "rest/packages/categoriesPackage1/assets").toExternalForm(), processEntry, options);

        if (resp.getType() != ResponseType.SUCCESS){
            fail("Error creating process asset: "+resp.getStatusText());
        }
        
        
        //---------------------------------------------
        //Enough configuration... Let's the tests begin
        
        //Get assets from category 1
        client = new AbderaClient(abdera);
        client.addCredentials(baseURL.toExternalForm(), null, null,
                new org.apache.commons.httpclient.UsernamePasswordCredentials("admin", "admin"));
        options = client.getDefaultRequestOptions();
        options.setAccept(MediaType.APPLICATION_ATOM_XML);

        resp = client.get(new URL(baseURL, "rest/categories/Category%201/assets").toExternalForm());
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Error getting assets from 'Category 1'");
        }
        
        Document<Feed> document = resp.getDocument();
        
        //Must be 3 assets in the response
        assertEquals(3, document.getRoot().getEntries().size());
        
        List<String> assetNames = new ArrayList<String>();
        for (Entry entry : document.getRoot().getEntries()) {
            assetNames.add(entry.getTitle());
        }
        
        //rule1, rule 3 and Process 1 should be in the response
        assertTrue(assetNames.contains("rule1"));
        assertTrue(assetNames.contains("rule3"));
        assertTrue(assetNames.contains("Process1"));
        
        
        //------------------------------------------------------------------
                
        //clean up
        resp = client.delete(new URL(baseURL, "rest/packages/categoriesPackage1/assets/Process1").toExternalForm());
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Error deleting 'Process1'");
        }

        resp = client.delete(new URL(baseURL, "rest/packages/categoriesPackage1/assets/Process2").toExternalForm());
        
        if (resp.getType() != ResponseType.SUCCESS){
            fail("Error deleting 'Process2'");
        }
        
    }

    @Test @RunAsClient @Ignore
    public void testGetAssetsByCategoryAsJson(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/categories/Home%20Mortgage/assets");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));

    }

    @Test @RunAsClient @Ignore
    public void testGetAssetsByCategoryAsJaxb(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/categories/Home%20Mortgage/assets");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient @Ignore
    public void testGetAssetsByCategoryAndPageAsAtom(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/categories/Home%20Mortgage/assets//page/0");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_ATOM_XML, connection.getContentType());
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));
    }

    @Test @RunAsClient @Ignore
    public void testGetAssetsByCategoryAndPageAsJson(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/categories/Home%20Mortgage/assets//page/0");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_JSON, connection.getContentType());
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));

    }

    @Test @RunAsClient @Ignore
    public void testGetAssetsByCategoryAndPageAsJaxb(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "rest/categories/Home%20Mortgage/assets//page/0");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
        connection.connect();
        assertEquals (200, connection.getResponseCode());
        assertEquals(MediaType.APPLICATION_XML, connection.getContentType());
        //logger.log(LogLevel, IOUtils.toString(connection.getInputStream()));
    }
    
    private Entry createProcessEntry(String title, String summary, List<String> categories){
        
        Entry processEntry = abdera.newEntry();
        
        processEntry.setTitle(title);
        processEntry.setSummary(summary);
        
        //create metadata element
        ExtensibleElement metadataExtension = processEntry.addExtension(new QName("", "metadata"));
        
        //add format element to metadata
        ExtensibleElement formatExtension = metadataExtension.addExtension(new QName("", "format"));
        formatExtension.addSimpleExtension(new QName("", "value"), "bpmn2");
        
        //add categories element to metadata
        ExtensibleElement categoriesExtension = metadataExtension.addExtension(new QName("", "categories"));

        categoriesExtension.addSimpleExtension(new QName("", "value"), categories.get(0));
        
        return processEntry;
    }
    
    public Map<String, Category> fromXMLToCategoriesMap(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            
            final List<String> errors = new ArrayList<String>();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            builder.setErrorHandler(new ErrorHandler() {

                public void warning(SAXParseException exception) throws SAXException {
                    java.util.logging.Logger.getLogger(Translator.class.getName()).log(Level.WARNING, "Warning parsing categories from Guvnor", exception);
                }

                public void error(SAXParseException exception) throws SAXException {
                    java.util.logging.Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, "Error parsing categories from Guvnor", exception);
                    errors.add(exception.getMessage());
                }

                public void fatalError(SAXParseException exception) throws SAXException {
                    java.util.logging.Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, "Error parsing categories from Guvnor", exception);
                    errors.add(exception.getMessage());
                }
            });
            
            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
            
            if (!errors.isEmpty()){
                throw new IllegalStateException("Error parsing categories from guvnor. Check the log for errors' details.");
            }

            Map<String, Category> categories = new HashMap<String, Category>();
            
            //convert all catergories and add them to the list
            NodeList categoriesList = doc.getElementsByTagName("category");
            for (int i = 0; i < categoriesList.getLength(); i++) {
                Element element = (Element) categoriesList.item(i);
                
                NodeList paths = element.getElementsByTagName("path");
                if (paths.getLength() != 1){
                    throw new IllegalStateException("Malfromed category. Expected 1 <path> tag, but found "+paths.getLength());
                }
                
                String path = paths.item(0).getTextContent();
                
                Category category = new Category();
                category.setPath(path);
                
                categories.put(path, category);
            }
            
            return categories;
        } catch (SAXException ex) {
            throw new RuntimeException("Error parsing categories' xml",ex);
        } catch (IOException ex) {
            throw new RuntimeException("Error parsing categories' xml",ex);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Error parsing categories' xml",ex);
        }
    }

}
