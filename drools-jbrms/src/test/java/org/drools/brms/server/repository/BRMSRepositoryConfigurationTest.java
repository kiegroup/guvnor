package org.drools.brms.server.repository;




import javax.jcr.Session;

import junit.framework.TestCase;

public class BRMSRepositoryConfigurationTest extends TestCase {

    public void testConfiguration() throws Exception {
        
        BRMSRepositoryConfiguration config = new BRMSRepositoryConfiguration();
        config.setHomeDirectory( "qed" );
        assertEquals("qed", config.repositoryHomeDirectory);
        config.setRepositoryConfigurator( MockRepositoryConfigurator.class.getName() );
        
        assertTrue(config.configurator instanceof MockRepositoryConfigurator);
        config.repository = new MockRepo();

        assertNotNull(config.newSession("foo"));
        assertNotSame(config.newSession("foo"), config.newSession("foo"));
        
    }
    
    public void testCreate() throws Exception {
        BRMSRepositoryConfiguration config = new BRMSRepositoryConfiguration();
        config.create();
        Session s = config.newSession( "admin" );
        config.create( s );
        assertFalse( s.isLive() );
    }

    
}
