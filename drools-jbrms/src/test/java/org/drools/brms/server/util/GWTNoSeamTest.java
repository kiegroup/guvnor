package org.drools.brms.server.util;

import junit.framework.TestCase;

import org.drools.brms.server.ServiceImplementation;
import org.jboss.seam.remoting.gwt.GWTToSeamAdapter;

public class GWTNoSeamTest extends TestCase {

    /**
     * Check that my hacked Seam adapter is working.
     * This is needed for hosted mode.
     */
    public void testTestSession() {
        TestAdapter ad = new TestAdapter();
        Object obj = ad.getComponent( "foobar" );
        assertTrue(obj instanceof ServiceImplementation);
    }    
    
    static class TestAdapter extends GWTToSeamAdapter {
        public Object getComponent(String x) {
            return super.getServiceComponent( x );
        }
    }
    
}
