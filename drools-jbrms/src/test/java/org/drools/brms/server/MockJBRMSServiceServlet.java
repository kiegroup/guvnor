package org.drools.brms.server;

import org.drools.repository.RulesRepository;

/**
 * This isn't really a mock, it just stubs out enough so I can test it from the servlet
 * down - at least the non servlet specific stuff.
 * 
 * @author michael neale.
 *
 */
public class MockJBRMSServiceServlet extends JBRMSServiceServlet {

    RulesRepository repo;

    public MockJBRMSServiceServlet() throws Exception  {
        repo = new RulesRepository(SessionHelper.getSession());        
    }
    
    RulesRepository getRulesRepository() {        
        return repo;
    }

}
