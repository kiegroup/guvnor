/**
 * Copyright 2010 JBoss Inc
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

package org.drools.factconstraints.test.client.helper;

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
	
	@Test
	public void newConfig() {
		for (String confName : new String[] {"NotNull", "IntegerConstraint", "RangeConstraint", "NotMatches", "Matches"}) {
			ConstraintConfiguration conf1 = ConstraintsContainer.getEmptyConfiguration(confName);
			ConstraintConfiguration conf2 = ConstraintsContainer.getEmptyConfiguration(confName);
			assertFalse(conf1.equals(conf2));
			
			assertEquals(conf1.getArgumentKeys(), conf2.getArgumentKeys());
		}
		
	}
	
}
