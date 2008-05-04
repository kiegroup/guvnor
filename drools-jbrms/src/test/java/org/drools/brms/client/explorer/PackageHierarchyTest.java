package org.drools.brms.client.explorer;

import org.drools.brms.client.explorer.PackageHierarchy.Folder;

import junit.framework.TestCase;

public class PackageHierarchyTest extends TestCase {

	public void testSimple() {
		PackageHierarchy h = new PackageHierarchy();
		h.addPackage("com.foo");
		Folder root = h.root;

		assertEquals(1, root.children.size());
		Folder f = (Folder) root.children.get(0);
		assertEquals("com", f.name);
		assertEquals(1, f.children.size());
		f = (Folder) f.children.get(0);
		assertEquals("foo", f.name);


		h.addPackage("com.bar");
		f = (Folder) root.children.get(0);
		assertEquals("com", f.name);
		assertEquals(2, f.children.size());
		f = (Folder) f.children.get(1);
		assertEquals("bar", f.name);

		h.addPackage("goo.bar.baz");
		assertEquals(2, root.children.size());
		f = (Folder) root.children.get(1);
		assertEquals("goo", f.name);

		assertEquals(1, f.children.size());
		f = (Folder) f.children.get(0);
		assertEquals("bar", f.name);

		assertEquals(1, f.children.size());
		f= (Folder) f.children.get(0);
		assertEquals("baz", f.name);


		h.addPackage("goo.char.baz");
		assertEquals(2, root.children.size());
		f = (Folder) root.children.get(1);
		assertEquals(2, f.children.size());
		f = (Folder) f.children.get(1);
		assertEquals("char", f.name);
		assertEquals(1, f.children.size());
		f = (Folder) f.children.get(0);
		assertEquals("baz", f.name);

		h.addPackage("Whee");
		assertEquals(3, root.children.size());


	}

	public void testComplex() {

		PackageHierarchy h = new PackageHierarchy();
		h.addPackage("com.bar");
		h.addPackage("com.bar.baz");
		assertEquals(1, h.root.children.size());
		Folder f = (Folder) h.root.children.get(0);
		assertEquals(2, f.children.size());
		f = (Folder) f.children.get(0);
		assertEquals("bar", f.name);
		assertEquals(0, f.children.size());

		f = (Folder) h.root.children.get(0);
		f = (Folder) f.children.get(1);
		assertEquals("bar", f.name);

		assertEquals(1, f.children.size());
		f = (Folder) f.children.get(0);
		assertEquals("baz", f.name);

	}

}
