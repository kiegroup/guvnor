
package org.drools.atom;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.AtomEntryProvider;
import org.apache.cxf.jaxrs.provider.AtomFeedProvider;
import org.apache.cxf.testutil.common.AbstractTestServerBase;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepository;

public class AtomRulesRepositoryServer extends AbstractTestServerBase{

    protected void run() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(AtomRulesRepository.class);
        List providers = new ArrayList();
        providers.add(new AtomFeedProvider());
        providers.add(new AtomEntryProvider());        
        sf.setProviders(providers);

        AtomRulesRepository atomRepo = new AtomRulesRepository();
        RulesRepository repo = RepositorySessionUtil.getRepository();
        repo.createPackage("testPackage1", "desc1");
        atomRepo.setRulesRepository(repo);
        // default lifecycle is per-request, change it to singleton
        sf.setResourceProvider(AtomRulesRepository.class,
				new SingletonResourceProvider(atomRepo));
        sf.setAddress("http://localhost:9080/");

        sf.create();
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
