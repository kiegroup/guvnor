package org.drools.brms.client.breditor;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class ChoiceListTest extends TestCase {

    public void testFilter() {
        
        List source = new ArrayList();
        source.add("foo bar");
        source.add( "baz" );
        source.add( "barry" );
        
        List result = ListUtil.filter(source, null);
        
        assertEquals(3, result.size());
        
        result = ListUtil.filter(source, "ba");
        assertEquals(2, result.size());
        
        assertEquals(0, ListUtil.filter(source, "xx").size());
        
        assertEquals(1, ListUtil.filter(source, "barry").size());
        
    }
    
}
