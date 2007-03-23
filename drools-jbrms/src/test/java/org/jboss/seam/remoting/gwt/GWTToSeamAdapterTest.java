package org.jboss.seam.remoting.gwt;

import junit.framework.TestCase;

import org.jboss.seam.remoting.gwt.GWTToSeamAdapter.ReturnedObject;

public class GWTToSeamAdapterTest extends TestCase {

    public void testAdapter() throws Exception {
        MyServiceThingie service = new SubServiceThingie();
        StubbedAdapter adapter = new StubbedAdapter(service);
        
        int startSize = StubbedAdapter.METHOD_CACHE.size();
        
        ReturnedObject obj = adapter.callWebRemoteMethod( "x", "doSomething", new Class[] {String.class}, new String[] {"yeah"} );
        assertEquals(startSize + 1, StubbedAdapter.METHOD_CACHE.size());
        assertEquals("yeah", service.something);
        assertNull(obj.returnedObject);
        assertEquals("x", adapter.calledService);
        
        //check its still the same size, ie the cache is working.
        obj = adapter.callWebRemoteMethod( "x", "doSomething", new Class[] {String.class}, new String[] {"yeah"} );
        assertEquals(startSize + 1, StubbedAdapter.METHOD_CACHE.size());        
        
        assertEquals(void.class, obj.returnType);
        adapter.callWebRemoteMethod( "x", "doSomething", new Class[] {String.class}, new String[] {"no"} );
        assertEquals("no", service.something);

        
        obj = adapter.callWebRemoteMethod( "x", "yeahYeah", null, null );
        assertEquals(String.class, obj.returnType);
        assertEquals("whee", obj.returnedObject);
        
        try {
            adapter.callWebRemoteMethod( "x", "notMe", null, null );
            fail("This should not be allowed");
        } catch (SecurityException e) {
            assertNotNull(e.getMessage());
        }
        
        
        try {
            adapter.callWebRemoteMethod( "x", "abc", null, null );
            fail("This should not be allowed");
        } catch (SecurityException e) {
            assertNotNull(e.getMessage());
        }        
        
    }
    

    
    public void testAnotherClass() throws Exception {
        AnotherService b = new AnotherService();
        GWTToSeamAdapter ad = new StubbedAdapter(b);
        int oldSize = StubbedAdapter.METHOD_CACHE.size();
        ad.callWebRemoteMethod( "y", "doSomething", null, null );
        assertTrue(b.called);
        assertEquals(oldSize + 1, StubbedAdapter.METHOD_CACHE.size());
    }
    
    static class StubbedAdapter extends GWTToSeamAdapter {
        private Object target;

        public String calledService;
        
        public StubbedAdapter(Object target) {
            this.target = target;
        }
        
        @Override
        protected Object getServiceComponent(String serviceIntfName) {
            this.calledService = serviceIntfName;
            return target;
        }
    }
    
    
}
