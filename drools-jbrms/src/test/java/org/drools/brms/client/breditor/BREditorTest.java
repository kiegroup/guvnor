package org.drools.brms.client.breditor;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class BREditorTest extends TestCase {

    public void  testRearrange() {
        List l = new ArrayList();
        l.add( "foo" );
        l.add( "bar" );
        int idx = 1;
        BREditor.shuffle(l, idx, true);
        
        assertEquals("bar", l.get( 0 ));
        assertEquals("foo", l.get( 1 ));
     
        l = new ArrayList();
        
        l.add( "a" );
        l.add( "b" );
        l.add( "c" );
        
        BREditor.shuffle( l, 0, true );
        assertEquals("a", l.get( 0 ));
        assertEquals("b", l.get( 1 ));
        assertEquals("c", l.get( 2 ));
        
        
        BREditor.shuffle( l, 0, false );
        assertEquals("b", l.get( 0 ));
        assertEquals("a", l.get( 1 ));
        assertEquals("c", l.get( 2 ));
        

        BREditor.shuffle( l, 2, true );
        assertEquals("b", l.get( 0 ));
        assertEquals("c", l.get( 1 ));
        assertEquals("a", l.get( 2 ));
        
        
        BREditor.shuffle( l, 2, false );
        assertEquals("b", l.get( 0 ));
        assertEquals("c", l.get( 1 ));
        assertEquals("a", l.get( 2 ));        
        
    }
    
}
