package org.drools.factconstraint.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.config.SimpleConstraintConfigurationImpl;
import org.drools.factconstraints.client.helper.ConstraintsContainer;
import org.junit.Test;


public class ConstraintsConstrainerTest {

	@Test
	public void test() {
		LinkedList<ConstraintConfiguration> list = new LinkedList<ConstraintConfiguration>();
		ConstraintConfiguration conf = new SimpleConstraintConfigurationImpl();
        conf.setFactType("Person");
        conf.setFieldName("age");
        
        list.add(conf);
		ConstraintsContainer cc = new ConstraintsContainer(list);
		assertTrue(cc.hasConstraints("Person"));
		assertFalse(cc.hasConstraints("Person3"));
		
		assertEquals(1, cc.getConstraints("Person").size());
		
		conf = new SimpleConstraintConfigurationImpl();
        conf.setFactType("Person");
        conf.setFieldName("name");
        
        cc.addConstraint(conf);
        assertEquals(2, cc.getConstraints("Person").size());
        assertEquals(1, cc.getConstraints("Person", "age").size());
        assertSame(conf, cc.getConstraints("Person", "name").get(0));
        assertEquals(0, cc.getConstraints("Person", "toothCount").size());
        
        conf = new SimpleConstraintConfigurationImpl();
        conf.setFactType("Pet");
        conf.setFieldName("name");
		
        cc.addConstraint(conf);
        
        assertEquals(1, cc.getConstraints("Pet").size());
        
        assertEquals(1, cc.getConstraints("Pet", "name").size());
	}
	
}
