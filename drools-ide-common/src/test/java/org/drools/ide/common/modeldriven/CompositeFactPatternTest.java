package org.drools.ide.common.modeldriven;

import junit.framework.TestCase;

import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;

public class CompositeFactPatternTest extends TestCase {

    public void testAddPattern() {
        final CompositeFactPattern pat = new CompositeFactPattern();
        final FactPattern x = new FactPattern();
        pat.addFactPattern( x );
        assertEquals( 1,
                      pat.patterns.length );

        final FactPattern y = new FactPattern();
        pat.addFactPattern( y );
        assertEquals( 2,
                      pat.patterns.length );
        assertEquals( x,
                      pat.patterns[0] );
        assertEquals( y,
                      pat.patterns[1] );
    }

}
