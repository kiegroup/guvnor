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

package org.drools.guvnor.client.explorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.drools.guvnor.client.explorer.PackageHierarchy.Folder;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.junit.Test;

public class PackageHierarchyTest {

    @Test
    public void testSimple() {
		PackageHierarchy h = new PackageHierarchy();
		h.addPackage(new PackageConfigData("com.foo"));
		Folder root = h.getRoot();

		assertEquals(1, root.getChildren().size());
		Folder f = (Folder) root.getChildren().get(0);
		assertEquals(null, f.getConfig());
		assertEquals("com", f.getName());
		assertEquals(1, f.getChildren().size());
		f = (Folder) f.getChildren().get(0);
		assertEquals("foo", f.getName());
		assertNotNull(f.getConfig());


		h.addPackage(new PackageConfigData("com.bar"));
		f = (Folder) root.getChildren().get(0);
		assertEquals("com", f.getName());
		assertNull(f.getConfig());
		assertEquals(2, f.getChildren().size());
		f = (Folder) f.getChildren().get(1);
		assertEquals("bar", f.getName());
		assertNotNull(f.getConfig());

		h.addPackage(new PackageConfigData("goo.bar.baz"));
		assertEquals(2, root.getChildren().size());
		f = (Folder) root.getChildren().get(1);
		assertEquals("goo", f.getName());

		assertEquals(1, f.getChildren().size());
		f = (Folder) f.getChildren().get(0);
		assertEquals("bar", f.getName());

		assertEquals(1, f.getChildren().size());
		f= (Folder) f.getChildren().get(0);
		assertEquals("baz", f.getName());


		h.addPackage(new PackageConfigData("goo.char.baz"));
		assertEquals(2, root.getChildren().size());
		f = (Folder) root.getChildren().get(1);
		assertEquals(2, f.getChildren().size());
		f = (Folder) f.getChildren().get(1);
		assertEquals("char", f.getName());
		assertEquals(1, f.getChildren().size());
		f = (Folder) f.getChildren().get(0);
		assertEquals("baz", f.getName());

		h.addPackage(new PackageConfigData("Whee"));
		assertEquals(3, root.getChildren().size());


	}

    @Test
    public void testComplex() {

		PackageHierarchy h = new PackageHierarchy();
		h.addPackage(new PackageConfigData("com.bar"));
		h.addPackage(new PackageConfigData("com.bar.baz"));
		assertEquals(1, h.getRoot().getChildren().size());
		Folder f = (Folder) h.getRoot().getChildren().get(0);
		assertEquals(2, f.getChildren().size());
		f = (Folder) f.getChildren().get(0);
		assertEquals("bar", f.getName());
		assertEquals(0, f.getChildren().size());

		f = (Folder) h.getRoot().getChildren().get(0);
		f = (Folder) f.getChildren().get(1);
		assertEquals("bar", f.getName());

		assertEquals(1, f.getChildren().size());
		f = (Folder) f.getChildren().get(0);
		assertEquals("baz", f.getName());

	}

}
