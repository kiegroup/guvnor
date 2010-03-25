package org.drools.factconstraint.helper;

import java.util.LinkedList;

import org.drools.guvnor.client.factconstraints.helper.ConstraintsContainer;
import org.drools.guvnor.client.factconstraints.predefined.IntegerConstraint;
import org.drools.guvnor.client.factconstraints.predefined.NotNullConstraint;
import org.drools.guvnor.client.factcontraints.Constraint;

import static org.junit.Assert.*;
import org.junit.Test;


public class ConstraintsConstrainerTest {

	@Test
	public void test() {
		LinkedList<Constraint> list = new LinkedList<Constraint>();
		Constraint cons = new IntegerConstraint();
        cons.setFactType("Person");
        cons.setFieldName("age");
        
        list.add(cons);
		ConstraintsContainer cc = new ConstraintsContainer(list);
		assertTrue(cc.hasConstraints("Person"));
		assertFalse(cc.hasConstraints("Person3"));
		
		assertEquals(1, cc.getConstraints("Person").size());
		
		cons = new NotNullConstraint();
        cons.setFactType("Person");
        cons.setFieldName("name");
        
        cc.addConstraint(cons);
        assertEquals(2, cc.getConstraints("Person").size());
        assertEquals(1, cc.getConstraints("Person", "age").size());
        assertSame(cons, cc.getConstraints("Person", "name").get(0));
        assertEquals(0, cc.getConstraints("Person", "toothCount").size());
        
        cons = new NotNullConstraint();
        cons.setFactType("Pet");
        cons.setFieldName("name");
		
        cc.addConstraint(cons);
        
        assertEquals(1, cc.getConstraints("Pet").size());
        
        assertEquals(1, cc.getConstraints("Pet", "name").size());
	}
	
}
