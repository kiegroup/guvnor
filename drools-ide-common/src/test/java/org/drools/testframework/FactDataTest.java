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

package org.drools.testframework;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;

public class FactDataTest {
    @Test
    public void testAdd() {
		FactData fd = new FactData("x", "y", new ArrayList(), false );
		assertEquals(0, fd.getFieldData().size());
		fd.getFieldData().add(new FieldData("x", "y"));
		assertEquals(1, fd.getFieldData().size());
		fd.getFieldData().add(new FieldData("q", "x"));
		assertEquals(2, fd.getFieldData().size());
	}
}
