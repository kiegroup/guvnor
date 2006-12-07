package org.drools.brms.client.modeldriven;

import java.util.List;

import org.drools.brms.client.modeldriven.model.ActionRetractFact;
import org.drools.brms.client.modeldriven.model.ActionSetField;
import org.drools.brms.client.modeldriven.model.FactPattern;
import org.drools.brms.client.modeldriven.model.IAction;
import org.drools.brms.client.modeldriven.model.IPattern;
import org.drools.brms.client.modeldriven.model.RuleModel;

import junit.framework.TestCase;

public class RuleModelTest extends TestCase {

    public void testBoundFactFinder() {
        RuleModel model = new RuleModel();
        
        assertNull(model.getBoundFact( "x" ));
        model.lhs = new IPattern[3];
        
        FactPattern x = new FactPattern("Car");
        model.lhs[0] = x;
        x.boundName = "x";
        
        assertNotNull(model.getBoundFact( "x" ));
        assertEquals(x, model.getBoundFact( "x" ));
        
        FactPattern y = new FactPattern("Car");
        model.lhs[1] = y;
        y.boundName = "y";
        
        FactPattern other = new FactPattern("House");
        model.lhs[2] = other;
        
        assertEquals(y, model.getBoundFact( "y" ));
        assertEquals(x, model.getBoundFact( "x" ));
        
        
        model.rhs = new IAction[1];
        ActionSetField set = new ActionSetField();
        set.variable = "x";
        model.rhs[0] = set;
        
        assertTrue(model.isBoundFactUsed( "x" ));
        assertFalse(model.isBoundFactUsed( "y" ));
        
        assertEquals(3, model.lhs.length);
        assertFalse(model.removeLhsItem( 0 ));
        assertEquals(3, model.lhs.length);
        
        ActionRetractFact fact = new ActionRetractFact("q");
        model.rhs[0] = fact;
        assertTrue(model.isBoundFactUsed( "q" ));
        assertFalse(model.isBoundFactUsed( "x" ));
    }
    
    public void testBindingList() {
        RuleModel model = new RuleModel();
        
        model.lhs = new IPattern[3];
        FactPattern x = new FactPattern("Car");
        model.lhs[0] = x;
        x.boundName = "x";
        
        FactPattern y = new FactPattern("Car");
        model.lhs[1] = y;
        y.boundName = "y";
        
        FactPattern other = new FactPattern("House");
        model.lhs[2] = other;
        
        
        List b = model.getBoundFacts();
        assertEquals(2, b.size());
        
        assertEquals("x", b.get( 0 ));
        assertEquals("y", b.get( 1 ));
        
        
        
    }
    
    public void testRemoveItemLhs() {
        RuleModel model = new RuleModel();
        
        model.lhs = new IPattern[3];
        FactPattern x = new FactPattern("Car");
        model.lhs[0] = x;
        x.boundName = "x";
        
        FactPattern y = new FactPattern("Car");
        model.lhs[1] = y;
        y.boundName = "y";
        
        FactPattern other = new FactPattern("House");
        model.lhs[2] = other;
        
        assertEquals(3, model.lhs.length);
        assertEquals(x, model.lhs[0]);
        
        model.removeLhsItem( 0 );
        
        assertEquals(2, model.lhs.length);
        assertEquals(y, model.lhs[0]);
        
    }
    
    public void testRemoveItemRhs() {
        RuleModel model = new RuleModel();
        
        model.rhs = new IAction[3];
        ActionRetractFact r0 = new ActionRetractFact("x");
        ActionRetractFact r1 = new ActionRetractFact("y");
        ActionRetractFact r2 = new ActionRetractFact("z");
        
        model.rhs[0] = r0;
        model.rhs[1] = r1;
        model.rhs[2] = r2;
        
        model.removeRhsItem(1);
        
        assertEquals(2, model.rhs.length);
        assertEquals(r0, model.rhs[0]);
        assertEquals(r2, model.rhs[1]);
        
    }
    
    
}
