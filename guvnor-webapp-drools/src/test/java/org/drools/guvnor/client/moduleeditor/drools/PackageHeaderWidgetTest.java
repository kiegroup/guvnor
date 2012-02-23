/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.moduleeditor.drools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.drools.guvnor.shared.modules.ModuleHeader;
import org.drools.guvnor.shared.modules.ModuleHeader.Global;
import org.drools.guvnor.shared.modules.ModuleHeader.Import;
import org.drools.guvnor.shared.modules.ModuleHeaderHelper;
import org.junit.Test;

public class PackageHeaderWidgetTest {

    @Test
    public void testEmpty() {

        ModuleHeader mh = ModuleHeaderHelper.parseHeader( null );
        assertNotNull( mh );
        assertNotNull( mh.getGlobals() );
        assertNotNull( mh.getImports() );

        mh = ModuleHeaderHelper.parseHeader( "" );
        assertNotNull( mh );
        assertNotNull( mh.getGlobals() );
        assertNotNull( mh.getImports() );

    }

    @Test
    public void testImports() {
        String s = "import goo.bar.Whee;\n\nimport wee.waah.Foo\nimport nee.Nah";
        ModuleHeader mh = ModuleHeaderHelper.parseHeader( s );
        assertNotNull( mh );
        assertNotNull( mh.getGlobals() );
        assertNotNull( mh.getImports() );

        assertEquals( 0,
                      mh.getGlobals().size() );
        assertEquals( 3,
                      mh.getImports().size() );
        Import i = mh.getImports().get( 0 );
        assertEquals( "goo.bar.Whee",
                      i.getType() );

        i = mh.getImports().get( 1 );
        assertEquals( "wee.waah.Foo",
                      i.getType() );

        i = mh.getImports().get( 2 );
        assertEquals( "nee.Nah",
                      i.getType() );

    }

    @Test
    public void testGlobals() {
        String s = "global goo.bar.Whee x;\n\nglobal wee.waah.Foo asd\nglobal nee.Nah d";
        ModuleHeader mh = ModuleHeaderHelper.parseHeader( s );
        assertNotNull( mh );
        assertNotNull( mh.getGlobals() );
        assertNotNull( mh.getImports() );

        assertEquals( 3,
                      mh.getGlobals().size() );
        assertEquals( 0,
                      mh.getImports().size() );

        Global i = mh.getGlobals().get( 0 );
        assertEquals( "goo.bar.Whee",
                      i.getType() );
        assertEquals( "x",
                      i.getName() );

        i = mh.getGlobals().get( 1 );
        assertEquals( "wee.waah.Foo",
                      i.getType() );
        assertEquals( "asd",
                      i.getName() );

        i = mh.getGlobals().get( 2 );
        assertEquals( "nee.Nah",
                      i.getType() );
        assertEquals( "d",
                      i.getName() );

    }

    @Test
    public void testGlobalsImports() {
        String s = "import goo.bar.Whee;\n\nglobal wee.waah.Foo asd";
        ModuleHeader mh = ModuleHeaderHelper.parseHeader( s );
        assertNotNull( mh );
        assertEquals( 1,
                      mh.getImports().size() );
        assertEquals( 1,
                      mh.getGlobals().size() );

        Import i = mh.getImports().get( 0 );
        assertEquals( "goo.bar.Whee",
                      i.getType() );

        Global g = mh.getGlobals().get( 0 );
        assertEquals( "wee.waah.Foo",
                      g.getType() );
        assertEquals( "asd",
                      g.getName() );

    }

    @Test
    public void testAdvanced() {
        String s = "import goo.bar.Whee;\nglobal Wee waa;\n \nsomething else maybe dialect !";
        assertEquals( null,
                      ModuleHeaderHelper.parseHeader( s ) );
    }

    @Test
    public void testRenderTypes() {
        ModuleHeader mh = new ModuleHeader();
        mh.getImports().add( new Import( "foo.bar.Baz" ) );
        String h = ModuleHeaderHelper.renderModuleHeader( mh );
        assertNotNull( h );
        assertEquals( "import foo.bar.Baz",
                      h.trim() );
        mh = ModuleHeaderHelper.parseHeader( h );
        assertEquals( 1,
                      mh.getImports().size() );
        Import i = mh.getImports().get( 0 );
        assertEquals( "foo.bar.Baz",
                      i.getType() );

        mh.getGlobals().add( new Global( "foo.Bar",
                                         "xs" ) );
        mh.getGlobals().add( new Global( "whee.wah",
                                         "tt" ) );
        h = ModuleHeaderHelper.renderModuleHeader( mh );
        assertEquals( "import foo.bar.Baz\nglobal foo.Bar xs\nglobal whee.wah tt",
                      h.trim() );

    }

}
