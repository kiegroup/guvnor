package org.drools.repository;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;


import junit.framework.TestCase;

public class ArtifactManagerTest extends TestCase {

    public void testCreateArtifact() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        ArtifactManager am = new ArtifactManager(repo);
        
		Map<String, List<String>> metadata = new HashMap<String, List<String>>() {{
			put("archived", new ArrayList<String>() {{add("true");}});
			put("format", new ArrayList<String>() {{add("drl");}});
			put("multi-value-property", new ArrayList<String>() {{add("value1"); add("value2");}});
		}};
        	
		
		Artifact artifact = new Artifact();
		artifact.setMetadata(metadata);
		artifact.setName("testArtifact1");
		artifact.setDescription("desc1");
		artifact.setContent("the string content of testArtifact1");		
        am.createArtifact(artifact);
        
        Artifact artifactResult = am.getArtifact("testArtifact1");
        assertEquals("testArtifact1", artifactResult.getName());       
        assertEquals("desc1", artifactResult.getDescription());    
        assertEquals("the string content of testArtifact1", artifactResult.getContent());       
        assertFalse(artifactResult.isBinary());       
        assertNotNull(artifactResult.getLastModified());       
       
        Map<String, List<String>> result = artifactResult.getMetadata();
        //assertEquals(3, result.size());       

        List<String> archived = result.get("archived");
        assertEquals("true", archived.get(0));       
        
        List<String> format = result.get("format");
        assertEquals("drl", format.get(0));    
        
        List<String> multiValueProperty = result.get("multi-value-property");
        assertEquals("value1", multiValueProperty.get(0));     
        assertEquals("value2", multiValueProperty.get(1));   
        
        System.out.println(artifactResult.toString());
    }    

    public void testCreateArtifactWithBinaryContent() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        ArtifactManager am = new ArtifactManager(repo);
		
		Artifact artifact = new Artifact();
		artifact.setName("testArtifact1");
		artifact.setSrcLink("/esbs/HelloWorld.esb");		
        am.createArtifact(artifact);
        
        Artifact artifactResult = am.getArtifact("testArtifact1");
        assertEquals("testArtifact1", artifactResult.getName());       
        assertNull(artifactResult.getDescription());    
        assertNull(artifactResult.getContent());       
        assertEquals("/esbs/HelloWorld.esb", artifactResult.getSrcLink());  
        assertTrue(artifactResult.isBinary());     
        assertNotNull(artifactResult.getLastModified());       
       
        System.out.println(artifactResult.toString());
    }
    
    public void testUpdateArtifact() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        ArtifactManager am = new ArtifactManager(repo);
        
		Map<String, List<String>> metadata = new HashMap<String, List<String>>() {{
			put("archived", new ArrayList<String>() {{add("true");}});
			put("format", new ArrayList<String>() {{add("drl");}});
			put("multi-value-property", new ArrayList<String>() {{add("value1"); add("value2");}});
		}};        	
		
		Artifact artifact = new Artifact();
		artifact.setMetadata(metadata);
		artifact.setName("testArtifact1");
		artifact.setDescription("desc1");
		artifact.setContent("the string content of testArtifact1");		
        am.createArtifact(artifact);
                
        
        //update
		Map<String, List<String>> newMetadata = new HashMap<String, List<String>>() {{
			put("archived", new ArrayList<String>() {{add("false");}});
			put("format", new ArrayList<String>() {{add("otherformat");}});
			put("multi-value-property", new ArrayList<String>() {{add("value3"); add("value4");}});
			put("new-multi-value-property", new ArrayList<String>() {{add("value1"); add("value2");}});
			put("checkinComment", new ArrayList<String>() {{add("initial");}});
		}};        	
		
		Artifact newArtifact = new Artifact();
		//TODO: how to remove a meta-data?
		newArtifact.setMetadata(newMetadata);
		newArtifact.setName("testArtifact1");
		newArtifact.setDescription("desc2");
		newArtifact.setContent("new content");		
        am.updateArtifact(newArtifact);
        
        //verify 
        Artifact artifactResult = am.getArtifact("testArtifact1");
        assertEquals("testArtifact1", artifactResult.getName());       
        assertEquals("desc2", artifactResult.getDescription());    
        assertEquals("new content", artifactResult.getContent());       
        assertNotNull(artifactResult.getLastModified());       
       
        Map<String, List<String>> result = artifactResult.getMetadata();
 
        List<String> archived = result.get("archived");
        assertEquals("false", archived.get(0));       
        
        List<String> format = result.get("format");
        assertEquals("otherformat", format.get(0));    
        
        List<String> multiValueProperty = result.get("multi-value-property");
        assertEquals("value3", multiValueProperty.get(0));     
        assertEquals("value4", multiValueProperty.get(1));   
        
        List<String> newMultiValueProperty = result.get("new-multi-value-property");
        assertEquals("value1", newMultiValueProperty.get(0));     
        assertEquals("value2", newMultiValueProperty.get(1));
        
        System.out.println(artifactResult.toString());        
    }
    
    public void testMultiValuedEntry() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        ArtifactManager am = new ArtifactManager(repo);
        
        //add a single entry value first
		Map<String, List<String>> metadata = new HashMap<String, List<String>>() {{
			put("archived", new ArrayList<String>(1) {{add("true");}});
		}};        	
		
		Artifact artifact = new Artifact();
		artifact.setMetadata(metadata);
		artifact.setName("testArtifact1");
        am.createArtifact(artifact);                
        
        //update the single value entry with multi valued entry
		Map<String, List<String>> newMetadata = new HashMap<String, List<String>>() {{
			put("archived", new ArrayList<String>() {{add("value1"); add("value2");}});
		}};        	
		
		Artifact newArtifact = new Artifact();
		newArtifact.setMetadata(newMetadata);
		newArtifact.setName("testArtifact1");
		try {
			am.updateArtifact(newArtifact);
			fail("did not catch expected exception");
		} catch (RulesRepositoryException e) {

		}
    }
    
    public void testUpdateArtifactWithBinaryContent() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        ArtifactManager am = new ArtifactManager(repo);
		
		Artifact artifact = new Artifact();
		artifact.setName("testArtifact1");
        am.createArtifact(artifact);                
        
        //update
		Map<String, List<String>> newMetadata = new HashMap<String, List<String>>() {{
			put("serviceCategory", new ArrayList<String>() {{add("helloWorld");}});
		}};        	
		
		Artifact newArtifact = new Artifact();
		newArtifact.setMetadata(newMetadata);
		newArtifact.setName("testArtifact1");
		newArtifact.setDescription("desc2");
		newArtifact.setSrcLink("/esbs/HelloWorld.esb");
        am.updateArtifact(newArtifact);
        
        //verify 
        Artifact artifactResult = am.getArtifact("testArtifact1");
        assertEquals("testArtifact1", artifactResult.getName());       
        assertEquals("desc2", artifactResult.getDescription());    
        assertNull(artifactResult.getContent());       
        assertEquals("/esbs/HelloWorld.esb", artifactResult.getSrcLink());  
        assertTrue(artifactResult.isBinary());  ;       
        assertNotNull(artifactResult.getLastModified());       
       
        Map<String, List<String>> result = artifactResult.getMetadata(); 
        List<String> serviceCategory = result.get("serviceCategory");
        assertEquals("helloWorld", serviceCategory.get(0));       
        
        System.out.println(artifactResult.toString());        
    }
    
    public void testCreateAndUpdateESBJar() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        ArtifactManager am = new ArtifactManager(repo);
        
        //create
        InputStream is = getClass().getResourceAsStream("resources/Quickstart_helloworld.esb");
        am.createEBSJar("Quickstart_helloworld.esb", is);
        
        Artifact artifactResult = am.getArtifact("Quickstart_helloworld.esb");
        assertEquals("Quickstart_helloworld.esb", artifactResult.getName());       
        assertNull(artifactResult.getDescription());    
        assertNull(artifactResult.getContent());       
        assertEquals("/repository/esbs/Quickstart_helloworld.esb", artifactResult.getSrcLink());  
        assertTrue(artifactResult.isBinary());  ;       
        assertNotNull(artifactResult.getLastModified());       
		
        //update
		Artifact artifact = new Artifact();
		artifact.setName("Quickstart_helloworld.esb");
		artifact.setDescription("desc1");
        am.updateArtifact(artifact);        
        
        //verify
        Artifact artifactResultNew = am.getArtifact("Quickstart_helloworld.esb");
        assertEquals("Quickstart_helloworld.esb", artifactResultNew.getName());       
        assertEquals("desc1", artifactResultNew.getDescription());       
        assertNull(artifactResultNew.getContent());       
        assertEquals("/repository/esbs/Quickstart_helloworld.esb", artifactResultNew.getSrcLink());  
        assertTrue(artifactResultNew.isBinary());  ;       
        assertNotNull(artifactResultNew.getLastModified());      
     
        
        System.out.println(artifactResult.toString());
    } 
    
    public void testGetMetadataTypes() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        ArtifactManager am = new ArtifactManager(repo);
        
		Map<String, MetaData> metadataTypes = am.getMetadataTypes();
        assertEquals(ArtifactManager.METADATA_TYPE_STRING, metadataTypes.get("archived").getMetaDataType());
        assertEquals("is archived or not", metadataTypes.get("archived").getDescription());
        assertEquals(ArtifactManager.METADATA_TYPE_STRING, metadataTypes.get("format").getMetaDataType());
        assertEquals(ArtifactManager.METADATA_TYPE_MULTI_VALUE_STRING, metadataTypes.get("multi-value-property").getMetaDataType());
    }


}
