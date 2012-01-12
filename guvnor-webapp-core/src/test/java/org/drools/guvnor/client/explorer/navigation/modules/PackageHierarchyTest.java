/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.explorer.navigation.modules;

import org.drools.guvnor.client.rpc.Module;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * 
 */
public class PackageHierarchyTest {

    @Test
    public void testFlatNoSubPackages() {
        Module pc = new Module( "foo.bar" );

        //Expect the following packages:-
        // \- foo.bar

        PackageView ph = new PackageFlatView();
        ph.addPackage( pc );

        assertNotNull( ph.getRootFolder() );

        assertEquals( 1,
                      ph.getRootFolder().getChildren().size() );

        Folder f = ph.getRootFolder().getChildren().get( 0 );
        assertEquals( "foo.bar",
                      f.getFolderName() );
        assertNotNull( f.getPackageConfigData() );
        assertEquals( pc,
                      f.getPackageConfigData() );

    }

    @Test
    public void testFlatWithSubPackages() {
        Module pc = new Module( "foo.bar" );
        Module spc = new Module( "wee.yoo" );
        pc.setSubModules( new Module[]{spc} );

        //Expect the following packages:-
        // \- foo.bar
        // \- foo.bar.wee.yoo

        PackageView ph = new PackageFlatView();
        ph.addPackage( pc );

        assertNotNull( ph.getRootFolder() );

        //1st child
        assertEquals( 2,
                      ph.getRootFolder().getChildren().size() );
        Folder f1 = ph.getRootFolder().getChildren().get( 0 );
        assertEquals( "foo.bar",
                      f1.getFolderName() );
        assertNotNull( f1.getPackageConfigData() );
        assertEquals( pc,
                      f1.getPackageConfigData() );

        //2nd child
        Folder f2 = ph.getRootFolder().getChildren().get( 1 );
        assertEquals( "foo.bar.wee.yoo",
                      f2.getFolderName() );
        assertNotNull( f2.getPackageConfigData() );
        assertEquals( spc,
                      f2.getPackageConfigData() );

    }

    @Test
    public void testFlatWithSubPackages2() {
        Module pc = new Module( "foo.bar" );
        Module spc = new Module( "wee.yoo" );
        pc.setSubModules( new Module[]{spc} );
        Module spc2 = new Module( "zee.goo" );
        spc.setSubModules( new Module[]{spc2} );

        //Expect the following packages:-
        // \- foo.bar
        // \- foo.bar.wee.yoo
        // \- foo.bar.wee.yoo.zee.goo

        PackageView ph = new PackageFlatView();
        ph.addPackage( pc );

        assertNotNull( ph.getRootFolder() );

        //1st child
        assertEquals( 3,
                      ph.getRootFolder().getChildren().size() );
        Folder f1 = ph.getRootFolder().getChildren().get( 0 );
        assertEquals( "foo.bar",
                      f1.getFolderName() );
        assertNotNull( f1.getPackageConfigData() );
        assertEquals( pc,
                      f1.getPackageConfigData() );

        //2nd child
        Folder f2 = ph.getRootFolder().getChildren().get( 1 );
        assertEquals( "foo.bar.wee.yoo",
                      f2.getFolderName() );
        assertNotNull( f2.getPackageConfigData() );
        assertEquals( spc,
                      f2.getPackageConfigData() );

        //3rd child
        Folder f3 = ph.getRootFolder().getChildren().get( 2 );
        assertEquals( "foo.bar.wee.yoo.zee.goo",
                      f3.getFolderName() );
        assertNotNull( f3.getPackageConfigData() );
        assertEquals( spc2,
                      f3.getPackageConfigData() );

    }

    @Test
    public void testNestedNoSubPackages() {
        Module pc = new Module( "foo.bar" );

        //Expect the following packages:-
        // \- foo
        //    \- bar

        PackageView ph = new PackageHierarchicalView();
        ph.addPackage( pc );

        assertNotNull( ph.getRootFolder() );

        //1st child
        assertEquals( 1,
                      ph.getRootFolder().getChildren().size() );
        Folder f1 = ph.getRootFolder().getChildren().get( 0 );
        assertEquals( "foo",
                      f1.getFolderName() );
        assertNull( f1.getPackageConfigData() );

        //2nd child
        assertEquals( 1,
                      f1.getChildren().size() );
        Folder f2 = f1.getChildren().get( 0 );
        assertEquals( "bar",
                      f2.getFolderName() );
        assertEquals( pc,
                      f2.getPackageConfigData() );

    }

    @Test
    public void testNestedWithSubPackages() {
        Module pc = new Module( "foo.bar" );
        Module spc = new Module( "wee.yoo" );
        pc.setSubModules( new Module[]{spc} );

        //Expect the following packages:-
        // \- foo
        //    \- bar
        //       \- wee
        //          \- yoo

        PackageView ph = new PackageHierarchicalView();
        ph.addPackage( pc );

        assertNotNull( ph.getRootFolder() );

        //1st child
        assertEquals( 1,
                      ph.getRootFolder().getChildren().size() );
        Folder f1 = ph.getRootFolder().getChildren().get( 0 );
        assertEquals( "foo",
                      f1.getFolderName() );
        assertNull( f1.getPackageConfigData() );

        //2nd child
        assertEquals( 1,
                      f1.getChildren().size() );
        Folder f2 = f1.getChildren().get( 0 );
        assertEquals( "bar",
                      f2.getFolderName() );
        assertEquals( pc,
                      f2.getPackageConfigData() );

        //3rd child
        assertEquals( 1,
                      f2.getChildren().size() );
        Folder f3 = f2.getChildren().get( 0 );
        assertEquals( "wee",
                      f3.getFolderName() );
        assertNull( f3.getPackageConfigData() );

        //4th child
        assertEquals( 1,
                      f3.getChildren().size() );
        Folder f4 = f3.getChildren().get( 0 );
        assertEquals( "yoo",
                      f4.getFolderName() );
        assertEquals( spc,
                      f4.getPackageConfigData() );
    }

    @Test
    public void testNestedWithSubPackages2() {
        Module pc = new Module( "foo.bar" );
        Module spc = new Module( "wee.yoo" );
        pc.setSubModules( new Module[]{spc} );
        Module spc2 = new Module( "zee.goo" );
        spc.setSubModules( new Module[]{spc2} );

        //Expect the following packages:-
        // \- foo
        //    \- bar
        //       \- wee
        //          \- yoo
        //             \- zee
        //                \- goo

        PackageView ph = new PackageHierarchicalView();
        ph.addPackage( pc );

        assertNotNull( ph.getRootFolder() );

        //1st child
        assertEquals( 1,
                      ph.getRootFolder().getChildren().size() );
        Folder f1 = ph.getRootFolder().getChildren().get( 0 );
        assertEquals( "foo",
                      f1.getFolderName() );
        assertNull( f1.getPackageConfigData() );

        //2nd child
        assertEquals( 1,
                      f1.getChildren().size() );
        Folder f2 = f1.getChildren().get( 0 );
        assertEquals( "bar",
                      f2.getFolderName() );
        assertEquals( pc,
                      f2.getPackageConfigData() );

        //3rd child
        assertEquals( 1,
                      f2.getChildren().size() );
        Folder f3 = f2.getChildren().get( 0 );
        assertEquals( "wee",
                      f3.getFolderName() );
        assertNull( f3.getPackageConfigData() );

        //4th child
        assertEquals( 1,
                      f3.getChildren().size() );
        Folder f4 = f3.getChildren().get( 0 );
        assertEquals( "yoo",
                      f4.getFolderName() );
        assertEquals( spc,
                      f4.getPackageConfigData() );

        //5th child
        assertEquals( 1,
                      f4.getChildren().size() );
        Folder f5 = f4.getChildren().get( 0 );
        assertEquals( "zee",
                      f5.getFolderName() );
        assertNull( f5.getPackageConfigData() );

        //6th child
        assertEquals( 1,
                      f5.getChildren().size() );
        Folder f6 = f5.getChildren().get( 0 );
        assertEquals( "goo",
                      f6.getFolderName() );
        assertEquals( spc2,
                      f6.getPackageConfigData() );

    }

}
