
package org.drools.atom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.AtomEntryProvider;
import org.apache.cxf.jaxrs.provider.AtomFeedProvider;
import org.apache.cxf.testutil.common.AbstractTestServerBase;
import org.drools.repository.Artifact;
import org.drools.repository.ArtifactManager;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryAdministrator;

public class AtomRulesRepositoryServer extends AbstractTestServerBase{
	private RulesRepository repo;
    protected void run() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(AtomRulesRepository.class);
        List providers = new ArrayList();
        providers.add(new AtomFeedProvider());
        providers.add(new AtomEntryProvider());        
        sf.setProviders(providers);

        AtomRulesRepository atomRepo = new AtomRulesRepository();
        repo = RepositorySessionUtil.getRepository();
        PackageItem pkg = repo.createPackage("testPackage1", "desc1");
        repo.loadCategory( "/" ).addCategory( "AtomRulesRepositoryTestCat", "X" );
        AssetItem testAsset1 = pkg.addAsset( "testAsset1", "testAsset1Desc1", "/AtomRulesRepositoryTestCat", "drl");
        testAsset1.updateContent("a new test rule for testAsset1");
        AssetItem testAsset2 = pkg.addAsset( "testAsset2", "testAsset2Desc1", "/AtomRulesRepositoryTestCat", "drl");
        testAsset2.updateContent("a new test rule for testAsset2");

        ArtifactManager artifactManager = new ArtifactManager(repo);
        Artifact artifact = new Artifact();
		Map<String, List<String>> metadata = new HashMap<String, List<String>>() {{
			put("archived", new ArrayList<String>() {{add("true");}});
			put("format", new ArrayList<String>() {{add("drl");}});
			put("multi-value-property", new ArrayList<String>() {{add("value1"); add("value2");}});
		}};
		artifact.setMetadata(metadata);
		artifact.setName("testArtifact1");
		artifact.setDescription("desc1");
		artifact.setContent("the string content of testArtifact1");		
		artifactManager.createArtifact(artifact);
        
        repo.save();

        atomRepo.setRulesRepository(repo);
        // default lifecycle is per-request, change it to singleton
        sf.setResourceProvider(AtomRulesRepository.class,
				new SingletonResourceProvider(atomRepo));
        sf.setAddress("http://localhost:9080/");

        sf.create();
	}
    
    public void tearDown() throws Exception {
        super.tearDown();

		RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(
				repo.getSession());
		admin.clearRulesRepository();
    }  
    
	public static void main(String[] args) {
        try {
        	AtomRulesRepositoryServer s = new AtomRulesRepositoryServer();
        	
			s.start();
	    } catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
	    } finally {
			System.out.println("done!");
	    }
    }

}
