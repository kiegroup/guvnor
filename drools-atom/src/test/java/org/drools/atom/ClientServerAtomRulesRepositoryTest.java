
package org.drools.atom;

import java.io.InputStream;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.testutil.common.AbstractClientServerTestBase;
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
            assertTrue(response.indexOf("testPackage1") > 0);
            assertTrue(response.indexOf("description=desc1, archived=false") > 0);
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
}
