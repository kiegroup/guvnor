package org.drools.brms.client.modeldriven;

import org.drools.brms.client.modeldriven.model.Constraint;
import org.drools.brms.client.modeldriven.model.FactPattern;

import junit.framework.TestCase;

public class FactPatternTest extends TestCase {

    public void testAddConstraint() {
        FactPattern p = new FactPattern();
        Constraint x = new Constraint("x");
        p.addConstraint(x);
        
        assertEquals(1, p.constraints.length);
        assertEquals(x, p.constraints[0]);
        
        Constraint y = new Constraint("y");
        
        p.addConstraint( y );
        assertEquals(2, p.constraints.length);
        assertEquals(x, p.constraints[0]);
        assertEquals(y, p.constraints[1]);
        
    }
    
    
    
}
