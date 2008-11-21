package org.drools.repository;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import junit.framework.TestCase;

public class ArtifactManagerTest extends TestCase {

	public void testCreateArtifact() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        ArtifactManager am = new ArtifactManager(repo);
        
		Map<String, Object> metadata = new HashMap<String, Object>() {{
			put("archived", "false");
			put("format", "drl");
			put("multi-value-property", new String[]{"value1","value2"});
			put("DefaultLifeCycle", "created");

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
       
        Map<String, Object> result = artifactResult.getMetadata();
        // assertEquals(3, result.size());

        String archived = (String)result.get("archived");
        assertEquals("false", archived);       
        
        String format = (String)result.get("format");
        assertEquals("drl", format);    
        
        String[] multiValueProperty = (String[])result.get("multi-value-property");
        assertEquals("value1", multiValueProperty[0]);     
        assertEquals("value2", multiValueProperty[1]);   
        
        String lifeCycle = (String)result.get("DefaultLifeCycle");
        assertEquals("created", lifeCycle);    
        
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
        
		Map<String, Object> metadata = new HashMap<String, Object>() {{
			put("archived", "false");
			put("format", "drl");
			put("multi-value-property",  new String[]{"value1","value2"});
			put("DefaultLifeCycle", "created");

		}};

		Artifact artifact = new Artifact();
		artifact.setMetadata(metadata);
		artifact.setName("testArtifact1");
		artifact.setDescription("desc1");
		artifact.setContent("the string content of testArtifact1");
		am.createArtifact(artifact);

		//Register a new metadata type named "new-multi-value-property".
		MetaData md = new MetaData();
        md.setName("new-multi-value-property");
        md.setMetaDataType(ArtifactManager.METADATA_TYPE_MULTI_VALUE_STRING);
        am.createMetadataType(md);
        
		// update
		Map<String, Object> newMetadata = new HashMap<String, Object>() {{
			put("archived", "true");
			put("format", "otherformat");
			put("multi-value-property",  new String[]{"value3","value4"});
			put("new-multi-value-property",  new String[]{"value1","value2"});
			put("DefaultLifeCycle", "developed");
			put("checkinComment", "move to developed phase");
		}};


		Artifact newArtifact = new Artifact();
		// TODO: how to remove a meta-data?
		newArtifact.setMetadata(newMetadata);
		newArtifact.setName("testArtifact1");
		newArtifact.setDescription("desc2");
		newArtifact.setContent("new content");
		am.updateArtifact(newArtifact);

		// verify
		Artifact artifactResult = am.getArtifact("testArtifact1");
		assertEquals("testArtifact1", artifactResult.getName());
		assertEquals("desc2", artifactResult.getDescription());
		assertEquals("new content", artifactResult.getContent());
		assertNotNull(artifactResult.getLastModified());

		Map<String, Object> result = artifactResult.getMetadata();

		String archived = (String)result.get("archived");
		assertEquals("true",archived);

		String format = (String)result.get("format");
		assertEquals("otherformat", format);

		String[] multiValueProperty = (String[])result.get("multi-value-property");
		assertEquals("value3", multiValueProperty[0]);
		assertEquals("value4", multiValueProperty[1]);

		String[] newMultiValueProperty = (String[])result
				.get("new-multi-value-property");
		assertEquals("value1", newMultiValueProperty[0]);
		assertEquals("value2", newMultiValueProperty[1]);

		String lifeCycle = (String)result.get("DefaultLifeCycle");
		assertEquals("developed", lifeCycle);
		
		System.out.println(artifactResult.toString());
	}

	public void testMultiValuedEntry() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		ArtifactManager am = new ArtifactManager(repo);

		// add a single entry value first
		Map<String, Object> metadata = new HashMap<String, Object>() {{
			put("archived", "true");
		}};

		Artifact artifact = new Artifact();
		artifact.setMetadata(metadata);
		artifact.setName("testArtifact1");
		am.createArtifact(artifact);

		// update the single value entry with multi valued entry
		Map<String, Object> newMetadata = new HashMap<String, Object>() {{
			put("archived", new String[]{"value1", "value2"});
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

		// update
		Map<String, Object> newMetadata = new HashMap<String, Object>() {{
			put("serviceCategory", "helloWorld");
		}};

		Artifact newArtifact = new Artifact();
		newArtifact.setMetadata(newMetadata);
		newArtifact.setName("testArtifact1");
		newArtifact.setDescription("desc2");
		newArtifact.setSrcLink("/esbs/HelloWorld.esb");
		am.updateArtifact(newArtifact);

		// verify
		Artifact artifactResult = am.getArtifact("testArtifact1");
		assertEquals("testArtifact1", artifactResult.getName());
		assertEquals("desc2", artifactResult.getDescription());
		assertNull(artifactResult.getContent());
		assertEquals("/esbs/HelloWorld.esb", artifactResult.getSrcLink());
		assertTrue(artifactResult.isBinary());
		;
		assertNotNull(artifactResult.getLastModified());

		Map<String, Object> result = artifactResult.getMetadata();
		String serviceCategory = (String)result.get("serviceCategory");
		assertEquals("helloWorld", serviceCategory);

		System.out.println(artifactResult.toString());
	}

	public void testCreateAndUpdateESBJar() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		ArtifactManager am = new ArtifactManager(repo);

		// create
		InputStream is = getClass().getResourceAsStream(
				"resources/Quickstart_helloworld.esb");
		am.createEBSJar("Quickstart_helloworld.esb", is);

		Artifact artifactResult = am.getArtifact("Quickstart_helloworld.esb");
		assertEquals("Quickstart_helloworld.esb", artifactResult.getName());
		assertNull(artifactResult.getDescription());
		assertNull(artifactResult.getContent());
		assertEquals("/repository/esbs/Quickstart_helloworld.esb",
				artifactResult.getSrcLink());
		assertTrue(artifactResult.isBinary());
		;
		assertNotNull(artifactResult.getLastModified());

		// update
		Artifact artifact = new Artifact();
		artifact.setName("Quickstart_helloworld.esb");
		artifact.setDescription("desc1");
		am.updateArtifact(artifact);

		// verify
		Artifact artifactResultNew = am
				.getArtifact("Quickstart_helloworld.esb");
		assertEquals("Quickstart_helloworld.esb", artifactResultNew.getName());
		assertEquals("desc1", artifactResultNew.getDescription());
		assertNull(artifactResultNew.getContent());
		assertEquals("/repository/esbs/Quickstart_helloworld.esb",
				artifactResultNew.getSrcLink());
		assertTrue(artifactResultNew.isBinary());
		;
		assertNotNull(artifactResultNew.getLastModified());

		System.out.println(artifactResult.toString());
	}

	public void testGetMetadataTypes() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		ArtifactManager am = new ArtifactManager(repo);

		Map<String, MetaData> metadataTypes = am.getMetadataTypes();
		assertEquals(ArtifactManager.METADATA_TYPE_STRING, metadataTypes.get(
				"archived").getMetaDataType());
		assertEquals("is archived or not", metadataTypes.get("archived")
				.getDescription());
		assertEquals(ArtifactManager.METADATA_TYPE_STRING, metadataTypes.get(
				"format").getMetaDataType());
		assertEquals(ArtifactManager.METADATA_TYPE_MULTI_VALUE_STRING,
				metadataTypes.get("multi-value-property").getMetaDataType());
	}

	public void testCreateLifeCycle() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		ArtifactManager am = new ArtifactManager(repo);

		LifeCycle lc = new LifeCycle();
		lc.setDescription("This is my LifeCycle");
		lc.setName("MyLifeCycle");

		Phase phase = new Phase();
		phase.setName("phase1");
		phase.setDescription("This is the initial phase");
		phase.setInitialPhase(true);
		phase.setNextPhase("phase2");
		lc.addPhase(phase);

		phase = new Phase();
		phase.setName("phase2");
		phase.setDescription("This is the phase2");
		phase.setInitialPhase(false);
		phase.setNextPhase("phase3");
		lc.addPhase(phase);

		phase = new Phase();
		phase.setName("phase3");
		phase.setInitialPhase(false);
		lc.addPhase(phase);

		am.createLifeCycle(lc);

		Map<String, LifeCycle> expectedlifeCycles = am.getLifeCycles();
		assertEquals(2, expectedlifeCycles.size());

		List<Phase> phases = expectedlifeCycles.get("MyLifeCycle").getPhases();
		assertEquals(3, phases.size());

		int expectedPhasesFound = 0;
		int totalPhasesFound = 0;
		for (Phase expectedPhase : phases) {
			totalPhasesFound++;
			if (expectedPhase.getName().equals("phase1")) {
				assertEquals("This is the initial phase", expectedPhase
						.getDescription());
				assertEquals(true, expectedPhase.isInitialPhase());
				assertEquals("phase2", expectedPhase.getNextPhase());
				expectedPhasesFound++;
			} else if (expectedPhase.getName().equals("phase2")) {
				assertEquals("This is the phase2", expectedPhase
						.getDescription());
				assertEquals(false, expectedPhase.isInitialPhase());
				assertEquals("phase3", expectedPhase.getNextPhase());
				expectedPhasesFound++;
			} else if (expectedPhase.getName().equals("phase3")) {
				assertEquals("", expectedPhase.getDescription());
				assertEquals(false, expectedPhase.isInitialPhase());
				assertEquals(null, expectedPhase.getNextPhase());
				expectedPhasesFound++;
			}
		}

		assertEquals(3, expectedPhasesFound);
		assertEquals(3, totalPhasesFound);
	}
}
