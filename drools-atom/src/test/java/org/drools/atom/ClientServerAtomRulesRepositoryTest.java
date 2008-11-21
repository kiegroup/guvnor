
package org.drools.atom;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.testutil.common.AbstractClientServerTestBase;
import org.drools.repository.ArtifactManager;
import org.drools.repository.PackageItem;
import org.drools.repository.Phase;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientServerAtomRulesRepositoryTest extends AbstractClientServerTestBase {

    private Abdera abdera = new Abdera();
    
    @BeforeClass
    public static void startServers() throws Exception {
        assertTrue("server did not launch correctly",
                   launchServer(AtomRulesRepositoryServer.class));
    }
    
    @Test
    public void testGetPackages() throws Exception {
        String endpointAddress =
            "http://localhost:9080/repository/packages"; 
        GetMethod get = new GetMethod(endpointAddress);
        get.setRequestHeader("Content-Type", "*/*");
        //get.setRequestHeader("Accept", type);
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.executeMethod(get);           
            String response = getStringFromInputStream(get.getResponseBodyAsStream());
            String expected = getStringFromInputStream(
                  getClass().getResourceAsStream("resources/expected_get_packages.txt"));            

            assertEquals(response, expected);
        } finally {
            get.releaseConnection();
        }
    }        
    
    @Test
    public void testGetAssets() throws Exception {
        String endpointAddress =
            "http://localhost:9080/repository/packages/testPackage1/assets"; 
        GetMethod get = new GetMethod(endpointAddress);
        get.setRequestHeader("Content-Type", "*/*");
        //get.setRequestHeader("Accept", type);
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.executeMethod(get);           
            String response = getStringFromInputStream(get.getResponseBodyAsStream());
            String expected = getStringFromInputStream(
                  getClass().getResourceAsStream("resources/expected_get_assets.txt"));            

            assertEquals(response, expected);
        } finally {
            get.releaseConnection();
        }
    } 

    @Test
    public void testGetPackagesUsingAbdera() throws Exception {        
        String endpointAddress =
            "http://localhost:9080/repository/packages"; 
    	GetMethod get = new GetMethod(endpointAddress);
        get.setRequestHeader("Content-Type", "*/*");

        HttpClient httpClient = new HttpClient();
        try {
			httpClient.executeMethod(get);
			Document<Feed> doc = abdera.getParser().parse(
					get.getResponseBodyAsStream());
			Feed feed = doc.getRoot();

			assertEquals("http://localhost:9080/repository/packages", feed
					.getBaseUri().toString());
			assertEquals("Packages", feed.getTitle());
			List<Entry> entries = feed.getEntries();
			assertEquals(entries.size(), 2);			
		} finally {
			get.releaseConnection();
		}
    }    
    
    @Test
    public void testGetPackage() throws Exception {
        String endpointAddress =
            "http://localhost:9080/repository/packages/testPackage1"; 
        GetMethod get = new GetMethod(endpointAddress);
        get.setRequestHeader("Content-Type", "*/*");
        //get.setRequestHeader("Accept", type);
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.executeMethod(get);           
            String response = getStringFromInputStream(get.getResponseBodyAsStream());
            //System.out.print(response);
            assertTrue(response.indexOf("testPackage1</title>") > 0);
            assertTrue(response.indexOf("desc1</summary>") > 0);
        } finally {
            get.releaseConnection();
        }
    }  
    
    @Test
    public void testGetAsset() throws Exception {
        String endpointAddress =
            "http://localhost:9080/repository/packages/testPackage1/assets/testAsset1"; 
        GetMethod get = new GetMethod(endpointAddress);
        get.setRequestHeader("Content-Type", "*/*");
        //get.setRequestHeader("Accept", type);
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.executeMethod(get);           
            String response = getStringFromInputStream(get.getResponseBodyAsStream());
            //System.out.print(response);
            assertTrue(response.indexOf("testAsset1</title>") > 0);
            assertTrue(response.indexOf("testAsset1Desc1</summary>") > 0);
            assertTrue(response.indexOf("a new test rule for testAsset1</content>") > 0);
            assertTrue(response.indexOf("</metadata>") > 0);

        } finally {
            get.releaseConnection();
        }
    }  
    
    @Test
    public void testAddAndDeletePackage() throws Exception {
    	//Create a new package called testPackage2
        String endpointAddress =
            "http://localhost:9080/repository/packages";         
        Entry e = createPackageItemEntry("testPackage2");
        StringWriter w = new StringWriter();
        e.writeTo(w);
        
        PostMethod post = new PostMethod(endpointAddress);
        post.setRequestEntity(
             new StringRequestEntity(w.toString(), "application/atom+xml", null));
        HttpClient httpclient = new HttpClient();
        
        String location = null;
        try {
            int result = httpclient.executeMethod(post);
            assertEquals(201, result);
            location = post.getResponseHeader("Location").getValue();
            Document<Entry> entryDoc = abdera.getParser().parse(post.getResponseBodyAsStream());
            assertEquals(entryDoc.getRoot().toString(), e.toString());
        } finally {
            post.releaseConnection();
        } 
        
        //Verify the testPackage2 has been created:
        String endpointAddress2 =
            "http://localhost:9080/repository/packages/testPackage2"; 
        GetMethod get2 = new GetMethod(endpointAddress2);
        get2.setRequestHeader("Content-Type", "*/*");
        HttpClient httpClient2 = new HttpClient();
        try {
            httpClient2.executeMethod(get2);           
            String response = getStringFromInputStream(get2.getResponseBodyAsStream());
            //System.out.print(response);
            assertTrue(response.indexOf("testPackage2</title>") > 0);
        } finally {
            get2.releaseConnection();
        }
        
        //Delete the testPackage2
        DeleteMethod delete = new DeleteMethod(endpointAddress2);
        delete.setRequestHeader("Content-Type", "*/*");
        HttpClient httpClient3 = new HttpClient();
        try {
            httpClient3.executeMethod(delete);           
        } finally {
        	delete.releaseConnection();
        }
        
        //Verify the testPackage2 has been deleted:
        GetMethod get4 = new GetMethod(endpointAddress2);
        get4.setRequestHeader("Content-Type", "*/*");
        HttpClient httpClient4 = new HttpClient();
        try {
            httpClient4.executeMethod(get4);           
            String response = getStringFromInputStream(get4.getResponseBodyAsStream());
            //System.out.print(response);
            assertTrue(response.indexOf("org.drools.atom.ResourceNotFoundFault") > 0);
        } finally {
            get4.releaseConnection();
        }
    }  
    
    @Test
    public void testUpdatePakcage() throws Exception {
    	//Update the testPackage1, set its description text from "desc1" to "desc2"
        String endpointAddress = "http://localhost:9080/repository/packages";
        File input = new File(getClass().getResource("resources/update_testPackage1.txt").toURI());
        PutMethod put = new PutMethod(endpointAddress);
        RequestEntity entity = new FileRequestEntity(input, "application/atom+xml; charset=ISO-8859-1");
        put.setRequestEntity(entity);
        HttpClient httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(put);
            assertEquals(200, result);
        } finally {
            // Release current connection to the connection pool once you are
            // done
            put.releaseConnection();
        }

        // Verify result
        String endpointAddress1 = "http://localhost:9080/repository/packages/testPackage1";
        URL url = new URL(endpointAddress1);
        URLConnection connect = url.openConnection();
        connect.addRequestProperty("Accept", "application/atom+xml");
        InputStream in = connect.getInputStream();
        assertNotNull(in);
        String response = getStringFromInputStream(in);
        assertTrue(response.indexOf("desc2</summary>") > 0);

         // Roll back changes:
        File input1 = new File(getClass().getResource("resources/expected_get_testPackage1.txt").toURI());
        PutMethod put1 = new PutMethod(endpointAddress);
        RequestEntity entity1 = new FileRequestEntity(input1, "application/atom+xml; charset=ISO-8859-1");
        put1.setRequestEntity(entity1);
        HttpClient httpclient1 = new HttpClient();

        try {
            int result = httpclient1.executeMethod(put1);
            assertEquals(200, result);
        } finally {
            // Release current connection to the connection pool once you are
            // done
            put1.releaseConnection();
        }
    }  
    
    @Test
    public void testAddAndUpdateArtifact() throws Exception {
        String endpointAddress =
            "http://localhost:9080/repository/artifacts"; 
        
    	
        Factory factory = Abdera.getNewFactory();        
        Entry entry = factory.getAbdera().newEntry();
        entry.setTitle("testArtifact1");
        entry.setContent("the content of testArtifact1 in String format");
       
        PostMethod post = new PostMethod(endpointAddress);
        post.setRequestEntity(new StringRequestEntity(entry.toString(), "application/atom+xml", null));
        HttpClient httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(post);
            assertEquals(201, result);
            //String response = getStringFromInputStream(post.getResponseBodyAsStream());
            //System.out.print(response);
			Document<Entry> doc = abdera.getParser().parse(post.getResponseBodyAsStream());
			entry = doc.getRoot();

			assertEquals("testArtifact1", entry.getTitle());
			assertEquals("the content of testArtifact1 in String format", entry.getContent());
			assertEquals("TEXT", entry.getContentType().name());
        } finally {
            post.releaseConnection();
        } 
        
    	//Update artifact testArtifact1
        entry.setSummary("desc for testArtifact1");
        String NS = "http://overlord.jboss.org/drools/1.0";
        QName METADATA = new QName(NS, "metadata");
        QName PROPERTY = new QName(NS, "property");
        QName VALUE = new QName(NS, "value");
        QName LIFECYCLE = new QName(NS, "lifecycle");
        ExtensibleElement metadataExtension  = entry.getExtension(METADATA);
        if(metadataExtension == null) {
        	metadataExtension = entry.addExtension(METADATA);
        }
        ExtensibleElement childExtension = metadataExtension.addExtension(PROPERTY);

        childExtension.setAttributeValue("name", "archived");
        childExtension.addSimpleExtension(VALUE, "false");
        
		String[] values = new String[]{"value1", "value2"};
		childExtension = metadataExtension.addExtension(PROPERTY);
		childExtension.setAttributeValue("name", "multi-value-property");
		for(String value : values) {
            childExtension.addSimpleExtension(VALUE, value);					
		}		
        
        childExtension = metadataExtension.addExtension(LIFECYCLE);
		childExtension.setAttributeValue("name", "DefaultLifeCycle");
		childExtension.setAttributeValue("phase", "created");
		
        String endpointAddress1 = "http://localhost:9080/repository/artifacts";
        PutMethod put = new PutMethod(endpointAddress1);
        put.setRequestEntity(new StringRequestEntity(entry.toString(), "application/atom+xml", null));
        HttpClient httpclient1 = new HttpClient();

        try {
            int result = httpclient1.executeMethod(put);
            assertEquals(200, result);
        } finally {
            // Release current connection to the connection pool once you are
            // done
            put.releaseConnection();
        }
        
        // Verify result
        String endpointAddress2 = "http://localhost:9080/repository/artifacts/testArtifact1";
        URL url = new URL(endpointAddress2);
        URLConnection connect = url.openConnection();
        connect.addRequestProperty("Accept", "application/atom+xml");
        InputStream in = connect.getInputStream();
        assertNotNull(in);
		Document<Entry> doc = abdera.getParser().parse(in);
		entry = doc.getRoot();

		assertEquals("testArtifact1", entry.getTitle());
		assertEquals("the content of testArtifact1 in String format", entry.getContent());
		assertEquals("TEXT", entry.getContentType().name());
		assertEquals("desc for testArtifact1", entry.getSummary());

        metadataExtension  = entry.getExtension(METADATA); 
        ExtensibleElement lifeCycleExtension = metadataExtension.getExtension(LIFECYCLE);
		assertEquals("DefaultLifeCycle", lifeCycleExtension.getAttributeValue("name"));       
		assertEquals("created", lifeCycleExtension.getAttributeValue("phase")); 
		
        List<ExtensibleElement> propertyExtensions = metadataExtension.getExtensions(PROPERTY);
		assertEquals(3, propertyExtensions.size());  		

		int expectedExtensionFound = 0;
		int totalPhasesFound = 0;
		for (ExtensibleElement expectedExtension : propertyExtensions) {
			totalPhasesFound++;
			if (expectedExtension.getAttributeValue("name").equals("archived")) {
				assertEquals("false", expectedExtension.getSimpleExtension(VALUE));
				expectedExtensionFound++;
			} else if (expectedExtension.getAttributeValue("name").equals("multi-value-property")) {
				expectedExtensionFound++;
			} 
		}		

		assertEquals(2, expectedExtensionFound);
		assertEquals(3, totalPhasesFound);
		//a Abdera bug?
/*	       List<Element> propertyExtensions1 = metadataExtension.getExtensions();
	       assertEquals(4, propertyExtensions1.size());  */
    }  
    
    @Test
    public void testAddAndUpdateESBJar() throws Exception {
        String endpointAddress =
            "http://localhost:9080/repository/esbs"; 
        
        PostMethod post = new PostMethod(endpointAddress);
        File testESBJar = new File(getClass().getResource("resources/Quickstart_helloworld.esb").toURI());
        post.setRequestEntity(
             new FileRequestEntity(testESBJar, "application/esb"));
        HttpClient httpclient = new HttpClient();
        post.addRequestHeader("Slug", "Quickstart_helloworld.esb");
        Entry entry = null;
        
        try {
            int result = httpclient.executeMethod(post);
            assertEquals(201, result);
            //String response = getStringFromInputStream(post.getResponseBodyAsStream());
            //System.out.print(response);
			Document<Entry> doc = abdera.getParser().parse(post.getResponseBodyAsStream());
			entry = doc.getRoot();

			assertEquals("Quickstart_helloworld.esb", entry.getTitle());
			assertEquals("/repository/esbs/Quickstart_helloworld.esb", entry.getContentSrc().getPath());
			assertEquals("MEDIA", entry.getContentType().name());

        } finally {
            post.releaseConnection();
        } 
        
    	//Update artifact Quickstart_helloworld.esb
        entry.setSummary("desc for esb");
        String NS = "http://overlord.jboss.org/drools/1.0";
        QName METADATA = new QName(NS, "metadata");
        QName LIFECYCLE = new QName(NS, "lifecycle");
        ExtensibleElement metadataExtension  = entry.getExtension(METADATA);

        ExtensibleElement childExtension = metadataExtension.addExtension(LIFECYCLE);
		childExtension.setAttributeValue("name", "DefaultLifeCycle");
		childExtension.setAttributeValue("phase", "created");
				
        String endpointAddress1 = "http://localhost:9080/repository/artifacts";
        PutMethod put = new PutMethod(endpointAddress1);
        System.out.println("***********" + entry.toString());
        put.setRequestEntity(new StringRequestEntity(entry.toString(), "application/atom+xml", null));
        HttpClient httpclient1 = new HttpClient();

        try {
            int result = httpclient1.executeMethod(put);
            assertEquals(200, result);
        } finally {
            // Release current connection to the connection pool once you are
            // done
            put.releaseConnection();
        }
        
        // Verify result
        String endpointAddress2 = "http://localhost:9080/repository/artifacts/Quickstart_helloworld.esb";
        URL url = new URL(endpointAddress2);
        URLConnection connect = url.openConnection();
        connect.addRequestProperty("Accept", "application/atom+xml");
        InputStream in = connect.getInputStream();
        assertNotNull(in);
		Document<Entry> doc = abdera.getParser().parse(in);
		entry = doc.getRoot();

		assertEquals("Quickstart_helloworld.esb", entry.getTitle());
		assertEquals("/repository/esbs/Quickstart_helloworld.esb", entry.getContentSrc().getPath());
		assertEquals("MEDIA", entry.getContentType().name());
		assertEquals("desc for esb", entry.getSummary());
        metadataExtension  = entry.getExtension(METADATA); 
        ExtensibleElement lifeCycleExtension = metadataExtension.getExtension(LIFECYCLE);
		assertEquals("DefaultLifeCycle", lifeCycleExtension.getAttributeValue("name"));       
		assertEquals("created", lifeCycleExtension.getAttributeValue("phase"));            
        
		
		//Get back the binary content of ESB Jar
        String endpointAddress3 = "http://localhost:9080/repository/esbs/Quickstart_helloworld.esb";
        URL url3 = new URL(endpointAddress3);
        URLConnection connect3 = url3.openConnection();
        connect3.addRequestProperty("Accept", "application/esb");
        InputStream in3 = connect3.getInputStream();
        assertNotNull(in);
		
         // Roll back changes:
/*        File input1 = new File(getClass().getResource("resources/expected_get_testPackage1.txt").toURI());
        PutMethod put1 = new PutMethod(endpointAddress);
        RequestEntity entity1 = new FileRequestEntity(input1, "application/atom+xml; charset=ISO-8859-1");
        put1.setRequestEntity(entity1);
        HttpClient httpclient1 = new HttpClient();

        try {
            int result = httpclient1.executeMethod(put1);
            assertEquals(200, result);
        } finally {
            // Release current connection to the connection pool once you are
            // done
            put1.releaseConnection();
        }*/
    }      
    
    @Test
    public void testGetMetaDataTypes() throws Exception {
        String endpointAddress =
            "http://localhost:9080/repository/metadatatypes"; 
        GetMethod get = new GetMethod(endpointAddress);
        get.setRequestHeader("Content-Type", "*/*");
        //get.setRequestHeader("Accept", type);
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.executeMethod(get);           
            //String response = getStringFromInputStream(get.getResponseBodyAsStream());
			Document<Feed> doc = abdera.getParser().parse(
					get.getResponseBodyAsStream());
			Feed feed = doc.getRoot();

			assertEquals("http://localhost:9080/repository/metadatatypes", feed
					.getBaseUri().toString());
			assertEquals("Metadata types", feed.getTitle());
			List<Entry> entries = feed.getEntries();
			assertEquals(entries.size(), 4);	
			
        } finally {
            get.releaseConnection();
        }
    }
    
    @Test
    public void testAddAndDeleteMetaDataTypes() throws Exception {       
        //Add a new metadata type called ServiceCategory
        String endpointAddress =
            "http://localhost:9080/repository/metadatatypes"; 
        PostMethod post = new PostMethod(endpointAddress);
        File newMetaData = new File(getClass().getResource("resources/add_metadata_servicecategory.txt").toURI());
        post.setRequestEntity(
             new FileRequestEntity(newMetaData, "application/atom+xml"));
        HttpClient httpclient = new HttpClient();
        
        try {
            int result = httpclient.executeMethod(post);
            assertEquals(201, result);
        } finally {
            post.releaseConnection();
        }       
                
        // Verify result
        endpointAddress =
            "http://localhost:9080/repository/metadatatypes"; 
        GetMethod get = new GetMethod(endpointAddress);
        get.setRequestHeader("Content-Type", "*/*");
        //get.setRequestHeader("Accept", type);
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.executeMethod(get);           
            //String response = getStringFromInputStream(get.getResponseBodyAsStream());
			Document<Feed> doc = abdera.getParser().parse(
					get.getResponseBodyAsStream());
			Feed feed = doc.getRoot();

			assertEquals("http://localhost:9080/repository/metadatatypes", feed
					.getBaseUri().toString());
			assertEquals("Metadata types", feed.getTitle());
			List<Entry> entries = feed.getEntries();
			assertEquals(entries.size(), 5);	
			
        } finally {
            get.releaseConnection();
        }		
        
        //Delete
        endpointAddress =
            "http://localhost:9080/repository/metadatatypes/serviceCategory"; 
        DeleteMethod delete = new DeleteMethod(endpointAddress);
        delete.setRequestHeader("Content-Type", "*/*");
        httpClient = new HttpClient();
        try {
        	httpClient.executeMethod(delete);           
        } finally {
        	delete.releaseConnection();
        }    
        
        // Verify it has been deleted
        endpointAddress =
            "http://localhost:9080/repository/metadatatypes"; 
        get = new GetMethod(endpointAddress);
        get.setRequestHeader("Content-Type", "*/*");
        httpClient = new HttpClient();
        try {
            httpClient.executeMethod(get);           
            //String response = getStringFromInputStream(get.getResponseBodyAsStream());
			Document<Feed> doc = abdera.getParser().parse(
					get.getResponseBodyAsStream());
			Feed feed = doc.getRoot();
			List<Entry> entries = feed.getEntries();
			assertEquals(entries.size(), 4);				
        } finally {
            get.releaseConnection();
        }	
    } 
    
    private String getStringFromInputStream(InputStream in) throws Exception {        
        CachedOutputStream bos = new CachedOutputStream();
        IOUtils.copy(in, bos);
        in.close();
        bos.close();
        return bos.getOut().toString();        
    }
    
    private Entry createPackageItemEntry(String packageName) {
        Factory factory = Abdera.getNewFactory();        
        Entry e = factory.getAbdera().newEntry();

        e.setTitle(packageName);

        return e;
    }

}
