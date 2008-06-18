    package org.jboss.seam.remoting.gwt;
/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.drools.brms.client.rpc.DetailedSerializableException;
import org.drools.brms.client.rpc.SessionExpiredException;
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

    public void testNotLoggedIn() throws Exception {
        MyServiceThingie t = new SubServiceThingie();
        GWTToSeamAdapter ad = new StubbedAdapter(t);
        try {
        ad.callWebRemoteMethod( "y", "notLoggedIn", null, null );
        } catch (InvocationTargetException e) {
            System.err.println(e.getCause());
            assertTrue(e.getCause() instanceof SessionExpiredException);
        }

    }

    public void testOtherError() throws Exception {
        MyServiceThingie t = new SubServiceThingie();
        GWTToSeamAdapter ad = new StubbedAdapter(t);
        try {
        ad.callWebRemoteMethod( "y", "goNuts", null, null );
        } catch (InvocationTargetException e) {
            System.err.println(e.getCause());
            assertTrue(e.getCause() instanceof DetailedSerializableException);

            DetailedSerializableException det = (DetailedSerializableException) e.getCause();
            assertNotNull(det.getMessage());
            assertNotNull(det.getLongDescription());
            System.err.println(det.getLongDescription());
        }

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