package org.drools.brms.client.rpc;

import junit.framework.TestCase;

public class MetaDataTest extends TestCase {

    public void testAddCats() {
        MetaData data = new MetaData();
        data.addCategory( "new cat" );
        assertEquals(1, data.categories.length);
        assertEquals("new cat", data.categories[0]);
        
        data.addCategory( "another" );
        assertEquals(2, data.categories.length);
        assertEquals("another", data.categories[1]);
        
        data.addCategory( "another" );
        assertEquals(2, data.categories.length);
    }
    
    public void testRemoveCats() {
        MetaData data = new MetaData();
        data.categories = new String[] {"wa", "la"};
        data.removeCategory( 0 );
        assertEquals(1, data.categories.length);
        assertEquals("la", data.categories[0]);
    }
    
}
