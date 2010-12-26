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

package org.drools.ide.common.assistant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.ide.common.assistant.refactor.drl.VariableBinding;

public class VariableBindingTest {

	private String line;
	private String response;

    @Before
    public void setUp() throws Exception {
		line = "\tEmployee($company : company, $age : age > 80, salary > 400)";
	}

    @Test
    public void testFieldWithVariableAssignedTest1() {
		response = VariableBinding.execute(line, 24);
	    assertEquals(true, response.equals(line));
	}

    @Test
    public void testClassNameWithoutVariableAssigned() {
		response = VariableBinding.execute(line, 4);
	    assertEquals(false, response.equals(line));
	}

    @Test
    public void testFieldWithVariableAssignedTest2() {
		response = VariableBinding.execute(line, 39);
	    assertEquals(true, response.equals(line));
	}

    @Test
    public void testAssignVariableInsideTheComparator() {
		response = VariableBinding.execute(line, 50);
	    assertEquals(false, response.equals(line));
	}

    @Test
    public void testInsideFieldComparator() {
		response = VariableBinding.execute(line, 58);
	    assertEquals(true, response.equals(line));
	}

    @Test
    public void testComplexLineTestMustAssign() {
		line = "$ma20 : Double() from accumulate( $r2:ClosePrice(close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
		response = VariableBinding.execute(line, 53);
	    assertEquals(false, response.equals(line));
	}

    @Test
    public void testComplexLineTestDontMustAssign() {
		line = "$ma20 : Double() from accumulate( $r2:ClosePrice($close : close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
		response = VariableBinding.execute(line, 61);
	    assertEquals(true, response.equals(line));
	}

    @Test
    public void testComplexLineClosePriceMustAssign() {
		line = "$ma20 : Double() from accumulate( ClosePrice($close : close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
		response = VariableBinding.execute(line, 43);
	    assertEquals(false, response.equals(line));
	}

    @Test
    public void testComplexLineClosePriceDontMustAssign() {
		line = "$ma20 : Double() from accumulate( $cp : ClosePrice($close : close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
		response = VariableBinding.execute(line, 36);
	    assertEquals(true, response.equals(line));
	}

//	public void testThisDontWorks() {
//		line = "$ma20 : Double() from accumulate( $r2:ClosePrice($close : close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
//		response = VariableBinding.execute(line, 121);
//		System.out.println(response);
//	    assertEquals(true, response.equals(line));
//	}

    @Test
    public void testSampleDRL() {
		line = "\t\tMessage( status == Message.HELLO, myMessage : message )\n";
		response = VariableBinding.execute(line, 3);
	    assertEquals(false, response.equals(line));
	}

    @Test
    public void testWithoutSpacesOrTab() {
		line = "Message( status == Message.HELLO, myMessage : message )\n";
		response = VariableBinding.execute(line, 0);
	    assertEquals(false, response.equals(line));
	}

    @Test
    public void testWithoutSpacesOrTabButWithVariableAssigned() {
		line = "m : Message( status == Message.HELLO, myMessage : message )\n";
		response = VariableBinding.execute(line, 1);
	    assertEquals(true, response.equals(line));
	}

    @Test
    public void testWithoutSpaceOnLeftOfField() {
		line = "m : Message( status == Message.HELLO,message )\n";
		response = VariableBinding.execute(line, 37);
	    assertEquals(false, response.equals(line));
	}

}
