package org.drools.guvnor.client.packages;

import junit.framework.TestCase;

import org.drools.guvnor.client.packages.PackageHeaderWidget.Global;
import org.drools.guvnor.client.packages.PackageHeaderWidget.Import;
import org.drools.guvnor.client.packages.PackageHeaderWidget.Types;

public class PackageHeaderWidgetTest extends TestCase {

	public void testEmpty() {

		PackageHeaderWidget.Types t = PackageHeaderHelper.parseHeader(null);
		assertNotNull(t);
		assertNotNull(t.globals);
		assertNotNull(t.imports);

		t = PackageHeaderHelper.parseHeader("");
		assertNotNull(t);
		assertNotNull(t.globals);
		assertNotNull(t.imports);

	}

	public void testImports() {
		String s = "import goo.bar.Whee;\n\nimport wee.waah.Foo\nimport nee.Nah";
		PackageHeaderWidget.Types t = PackageHeaderHelper.parseHeader(s);
		assertNotNull(t);
		assertNotNull(t.globals);
		assertNotNull(t.imports);

		assertEquals(0, t.globals.size());
		assertEquals(3, t.imports.size());
		Import i = (Import) t.imports.get(0);
		assertEquals("goo.bar.Whee", i.type);

		i = (Import) t.imports.get(1);
		assertEquals("wee.waah.Foo", i.type);

		i = (Import) t.imports.get(2);
		assertEquals("nee.Nah", i.type);

	}

	public void testGlobals() {
		String s = "global goo.bar.Whee x;\n\nglobal wee.waah.Foo asd\nglobal nee.Nah d";
		PackageHeaderWidget.Types t = PackageHeaderHelper.parseHeader(s);
		assertNotNull(t);
		assertNotNull(t.globals);
		assertNotNull(t.imports);

		assertEquals(3, t.globals.size());
		assertEquals(0, t.imports.size());

		Global i = (Global) t.globals.get(0);
		assertEquals("goo.bar.Whee", i.type);
		assertEquals("x", i.name);

		i = (Global) t.globals.get(1);
		assertEquals("wee.waah.Foo", i.type);
		assertEquals("asd", i.name);

		i = (Global) t.globals.get(2);
		assertEquals("nee.Nah", i.type);
		assertEquals("d", i.name);

	}

	public void testGlobalsImports() {
		String s = "import goo.bar.Whee;\n\nglobal wee.waah.Foo asd";
		PackageHeaderWidget.Types t = PackageHeaderHelper.parseHeader(s);
		assertNotNull(t);
		assertEquals(1, t.imports.size());
		assertEquals(1, t.globals.size());

		Import i = (Import) t.imports.get(0);
		assertEquals("goo.bar.Whee", i.type);

		Global g = (Global) t.globals.get(0);
		assertEquals("wee.waah.Foo", g.type);
		assertEquals("asd", g.name);


	}

	public void testAdvanced() {
		String s = "import goo.bar.Whee;\nglobal Wee waa;\n \nsomething else maybe dialect !";
		assertEquals(null, PackageHeaderHelper.parseHeader(s));
	}

	public void testRenderTypes() {
		Types t = new Types();
		t.imports.add(new Import("foo.bar.Baz"));
		String h = PackageHeaderHelper.renderTypes(t);
		assertNotNull(h);
		assertEquals("import foo.bar.Baz", h.trim());
		t = PackageHeaderHelper.parseHeader(h);
		assertEquals(1, t.imports.size());
		Import i = (Import) t.imports.get(0);
		assertEquals("foo.bar.Baz", i.type);

		t.globals.add(new Global("foo.Bar", "xs"));
		t.globals.add(new Global("whee.wah", "tt"));
		h = PackageHeaderHelper.renderTypes(t);
		assertEquals("import foo.bar.Baz\nglobal foo.Bar xs\nglobal whee.wah tt", h.trim());

	}

}
