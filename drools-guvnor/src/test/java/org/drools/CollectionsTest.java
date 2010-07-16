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

package org.drools;

import static org.drools.nvbj.List;
import static org.drools.nvbj.Map;
import static org.drools.nvbj.__;
import static org.drools.nvbj.println;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;


public class CollectionsTest extends TestCase {

	public void testList() {
		List<String> ls = List("a", "b", "c");
		assertEquals(3, ls.size());
		assertEquals("b", ls.get(1));
	}

	public void testMap() {
		println("this is less verbose");
		Map<String, String> mp = Map(
				__("name", "michael"),
				__("age", "42"));
		assertEquals(2, mp.size());
		assertEquals("michael", mp.get("name"));
		assertEquals("42", mp.get("age"));


	}

}
