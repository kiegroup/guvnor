package org.drools.guvnor.server;

import junit.framework.TestCase;

public class CategoryFilterTest extends TestCase {

	public void testMakePath() {
		CategoryFilter filter = new CategoryFilter();
		assertEquals("HR", filter.makePath("/", "HR"));
		assertEquals("HR", filter.makePath(null, "HR"));
		assertEquals("HR", filter.makePath("", "HR"));
		assertEquals("foo/bar", filter.makePath("foo", "bar"));
	}

}
