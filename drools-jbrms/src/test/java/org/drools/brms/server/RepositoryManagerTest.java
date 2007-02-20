package org.drools.brms.server;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.brms.server.util.RepositoryManager;
import org.drools.repository.RulesRepository;

/**
 * This tests the basic http stuff for the servlet.
 */
public class RepositoryManagerTest extends TestCase {

    public void testInit() throws Exception {
        RepositoryManager serv = new RepositoryManager();
        MockHttpSession session = new MockHttpSession();
        RulesRepository repo = serv.getRepositoryFrom( session );
        assertTrue(session.sessionData.containsKey( "drools.repository" ));
        assertEquals(repo, session.getAttribute( "drools.repository" ));
        
        RulesRepository repo2 = serv.getRepositoryFrom( session );
        assertSame(repo, repo2);
        
        
    }
    
    public void testGetRepository() throws Exception {
        RepositoryManager serv = new RepositoryManager();
        RulesRepository repo = serv.createRuleRepositoryInstance();
        assertNotNull(repo);
    }
    


    
    
    static class MockHttpSession implements HttpSession {

        public Map sessionData = new HashMap();
        
        public Object getAttribute(String arg0) {
            
            return sessionData.get( arg0 );
        }

        public Enumeration getAttributeNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public long getCreationTime() {
            return 0;
        }

        public String getId() {
            return null;
        }

        public long getLastAccessedTime() {
            return 0;
        }

        public int getMaxInactiveInterval() {
            return 0;
        }

        public ServletContext getServletContext() {
            return null;
        }

        public HttpSessionContext getSessionContext() {
            return null;
        }

        public Object getValue(String arg0) {
            return null;
        }

        public String[] getValueNames() {
            return null;
        }

        public void invalidate() {
        }

        public boolean isNew() {
            return false;
        }

        public void putValue(String arg0,
                             Object arg1) {
            Assert.fail("DO NOT USE THIS METHOD !");
            
        }

        public void removeAttribute(String arg0) {
            this.sessionData.remove( arg0 );
        }

        public void removeValue(String arg0) {
        }

        public void setAttribute(String arg0,
                                 Object arg1) {
            sessionData.put( arg0, arg1 );
        }

        public void setMaxInactiveInterval(int arg0) {
            
            
        }
        
    }
}
