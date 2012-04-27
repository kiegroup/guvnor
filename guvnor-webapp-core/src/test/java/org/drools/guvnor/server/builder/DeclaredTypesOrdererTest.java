/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.server.builder.DeclaredTypesSorter.DeclaredTypeAssetInheritanceInformation;
import org.drools.guvnor.server.builder.DeclaredTypesSorter.DeclaredTypeInheritanceInformation;
import org.drools.repository.AssetItem;
import org.junit.Test;

public class DeclaredTypesOrdererTest {

    @Test
    public void testTypeIdentificationSingleType1() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 1,
                      types.size() );
        assertEquals( "declare Type end",
                      types.get( 0 ).getDrl() );
        assertEquals( "Type",
                      types.get( 0 ).getType() );
    }

    @Test
    public void testTypeIdentificationSingleType2() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type field1 : String end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 1,
                      types.size() );
        assertEquals( "declare Type field1 : String end",
                      types.get( 0 ).getDrl() );
        assertEquals( "Type",
                      types.get( 0 ).getType() );
    }

    @Test
    public void testTypeIdentificationSingleType3() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type\n"
                           + "end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 1,
                      types.size() );
        assertEquals( "declare Type\nend",
                      types.get( 0 ).getDrl() );
        assertEquals( "Type",
                      types.get( 0 ).getType() );
    }

    @Test
    public void testTypeIdentificationSingleType4() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type\n"
                           + "field1 : String\n"
                           + "end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 1,
                      types.size() );
        assertEquals( "declare Type\nfield1 : String\nend",
                      types.get( 0 ).getDrl() );
        assertEquals( "Type",
                      types.get( 0 ).getType() );
    }

    @Test
    public void testTypeIdentificationMultipleTypes1() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1 end declare Type2 end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "declare Type1 end",
                      types.get( 0 ).getDrl() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertEquals( "declare Type2 end",
                      types.get( 1 ).getDrl() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
    }

    @Test
    public void testTypeIdentificationMultipleTypes2() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1 field1 : String end declare Type2 field1 : String end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "declare Type1 field1 : String end",
                      types.get( 0 ).getDrl() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertEquals( "declare Type2 field1 : String end",
                      types.get( 1 ).getDrl() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
    }

    @Test
    public void testTypeIdentificationMultipleTypes3() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1\n"
                           + "end"
                           + "declare Type2\n"
                           + "end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "declare Type1\nend",
                      types.get( 0 ).getDrl() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertEquals( "declare Type2\nend",
                      types.get( 1 ).getDrl() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
    }

    @Test
    public void testTypeIdentificationMultipleTypes4() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1\n"
                           + "field1 : String\n"
                           + "end"
                           + "declare Type2\n"
                           + "field1 : String\n"
                           + "end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "declare Type1\nfield1 : String\nend",
                      types.get( 0 ).getDrl() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertEquals( "declare Type2\nfield1 : String\nend",
                      types.get( 1 ).getDrl() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
    }

    @Test
    public void testSuperTypeIdentificationMultipleTypes1() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1 end declare Type2 extends Type1 end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "declare Type1 end",
                      types.get( 0 ).getDrl() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( "declare Type2 extends Type1 end",
                      types.get( 1 ).getDrl() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
    }

    @Test
    public void testSuperTypeIdentificationMultipleTypes2() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1 field1 : String end declare Type2 extends Type1 field1 : String end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "declare Type1 field1 : String end",
                      types.get( 0 ).getDrl() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( "declare Type2 extends Type1 field1 : String end",
                      types.get( 1 ).getDrl() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
    }

    @Test
    public void testSuperTypeIdentificationMultipleTypes3() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1\n"
                           + "end\n"
                           + "declare Type2 extends Type1\n"
                           + "end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "declare Type1\nend",
                      types.get( 0 ).getDrl() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( "declare Type2 extends Type1\nend",
                      types.get( 1 ).getDrl() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
    }

    @Test
    public void testSuperTypeIdentificationMultipleTypes4() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1\n"
                           + "field1 : String\n"
                           + "end\n"
                           + "declare Type2 extends Type1\n"
                           + "field1 : String\n"
                           + "end";
        final List<DeclaredTypeInheritanceInformation> types = sorter.parseIndividualTypes( drl );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "declare Type1\nfield1 : String\nend",
                      types.get( 0 ).getDrl() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( "declare Type2 extends Type1\nfield1 : String\nend",
                      types.get( 1 ).getDrl() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
    }

    @Test
    public void testDependencyScore1() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1\n"
                           + "field1 : String\n"
                           + "end\n"
                           + "declare Type2 extends Type1\n"
                           + "field1 : String\n"
                           + "end";

        final List<AssetItem> assets = new ArrayList<AssetItem>();
        AssetItem asset1 = mock( AssetItem.class );
        when( asset1.getContent() ).thenReturn( drl );
        assets.add( asset1 );

        final List<DeclaredTypeAssetInheritanceInformation> types = sorter.sort( assets );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( 0,
                      types.get( 0 ).getDependencyScore() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
        assertEquals( 1,
                      types.get( 1 ).getDependencyScore() );
    }

    @Test
    public void testDependencyScore2() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1\n"
                           + "field1 : String\n"
                           + "end\n"
                           + "declare Type2 extends Type1\n"
                           + "field1 : String\n"
                           + "end"
                           + "declare Type3 extends Type2\n"
                           + "field1 : String\n"
                           + "end";

        final List<AssetItem> assets = new ArrayList<AssetItem>();
        AssetItem asset1 = mock( AssetItem.class );
        when( asset1.getContent() ).thenReturn( drl );
        assets.add( asset1 );

        final List<DeclaredTypeAssetInheritanceInformation> types = sorter.sort( assets );

        assertNotNull( types );
        assertEquals( 3,
                      types.size() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( 0,
                      types.get( 0 ).getDependencyScore() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
        assertEquals( 1,
                      types.get( 1 ).getDependencyScore() );
        assertEquals( "Type3",
                      types.get( 2 ).getType() );
        assertEquals( "Type2",
                      types.get( 2 ).getSuperType() );
        assertEquals( 2,
                      types.get( 2 ).getDependencyScore() );
    }

    @Test
    public void testDependencyScore3() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type1\n"
                           + "field1 : String\n"
                           + "end\n"
                           + "declare Type2 extends Type1\n"
                           + "field1 : String\n"
                           + "end"
                           + "declare Type3 extends Type1\n"
                           + "field1 : String\n"
                           + "end";

        final List<AssetItem> assets = new ArrayList<AssetItem>();
        AssetItem asset1 = mock( AssetItem.class );
        when( asset1.getContent() ).thenReturn( drl );
        assets.add( asset1 );

        final List<DeclaredTypeAssetInheritanceInformation> types = sorter.sort( assets );

        assertNotNull( types );
        assertEquals( 3,
                      types.size() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( 0,
                      types.get( 0 ).getDependencyScore() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
        assertEquals( 1,
                      types.get( 1 ).getDependencyScore() );
        assertEquals( "Type3",
                      types.get( 2 ).getType() );
        assertEquals( "Type1",
                      types.get( 2 ).getSuperType() );
        assertEquals( 1,
                      types.get( 2 ).getDependencyScore() );
    }

    @Test
    public void testDependencyScoreInvertedOrder() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl = "declare Type3 extends Type2\n"
                           + "field1 : String\n"
                           + "end\n"
                           + "declare Type2 extends Type1\n"
                           + "field1 : String\n"
                           + "end"
                           + "declare Type1\n"
                           + "field1 : String\n"
                           + "end";

        final List<AssetItem> assets = new ArrayList<AssetItem>();
        AssetItem asset1 = mock( AssetItem.class );
        when( asset1.getContent() ).thenReturn( drl );
        assets.add( asset1 );

        final List<DeclaredTypeAssetInheritanceInformation> types = sorter.sort( assets );

        assertNotNull( types );
        assertEquals( 3,
                      types.size() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( 0,
                      types.get( 0 ).getDependencyScore() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
        assertEquals( 1,
                      types.get( 1 ).getDependencyScore() );
        assertEquals( "Type3",
                      types.get( 2 ).getType() );
        assertEquals( "Type2",
                      types.get( 2 ).getSuperType() );
        assertEquals( 2,
                      types.get( 2 ).getDependencyScore() );
    }

    @Test
    public void testDependencyScoreCrossAsset1() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl1 = "declare Type1\n"
                            + "field1 : String\n"
                            + "end\n";
        final String drl2 = "declare Type2 extends Type1\n"
                            + "field1 : String\n"
                            + "end";

        final List<AssetItem> assets = new ArrayList<AssetItem>();

        AssetItem asset1 = mock( AssetItem.class );
        when( asset1.getContent() ).thenReturn( drl1 );
        assets.add( asset1 );

        AssetItem asset2 = mock( AssetItem.class );
        when( asset2.getContent() ).thenReturn( drl2 );
        assets.add( asset2 );

        final List<DeclaredTypeAssetInheritanceInformation> types = sorter.sort( assets );

        assertNotNull( types );
        assertEquals( 2,
                      types.size() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( 0,
                      types.get( 0 ).getDependencyScore() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
        assertEquals( 1,
                      types.get( 1 ).getDependencyScore() );
    }

    @Test
    public void testDependencyScoreCrossAsset2() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl1 = "declare Type1\n"
                            + "field1 : String\n"
                            + "end\n";
        final String drl2 = "declare Type2 extends Type1\n"
                            + "field1 : String\n"
                            + "end"
                            + "declare Type3 extends Type2\n"
                            + "field1 : String\n"
                            + "end";

        final List<AssetItem> assets = new ArrayList<AssetItem>();

        AssetItem asset1 = mock( AssetItem.class );
        when( asset1.getContent() ).thenReturn( drl1 );
        assets.add( asset1 );

        AssetItem asset2 = mock( AssetItem.class );
        when( asset2.getContent() ).thenReturn( drl2 );
        assets.add( asset2 );

        final List<DeclaredTypeAssetInheritanceInformation> types = sorter.sort( assets );

        assertNotNull( types );
        assertEquals( 3,
                      types.size() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( 0,
                      types.get( 0 ).getDependencyScore() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
        assertEquals( 1,
                      types.get( 1 ).getDependencyScore() );
        assertEquals( "Type3",
                      types.get( 2 ).getType() );
        assertEquals( "Type2",
                      types.get( 2 ).getSuperType() );
        assertEquals( 2,
                      types.get( 2 ).getDependencyScore() );
    }

    @Test
    public void testDependencyScoreCrossAsset3() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl1 = "declare Type1\n"
                            + "field1 : String\n"
                            + "end\n";
        final String drl2 = "declare Type2 extends Type1\n"
                            + "field1 : String\n"
                            + "end"
                            + "declare Type3 extends Type1\n"
                            + "field1 : String\n"
                            + "end";

        final List<AssetItem> assets = new ArrayList<AssetItem>();

        AssetItem asset1 = mock( AssetItem.class );
        when( asset1.getContent() ).thenReturn( drl1 );
        assets.add( asset1 );

        AssetItem asset2 = mock( AssetItem.class );
        when( asset2.getContent() ).thenReturn( drl2 );
        assets.add( asset2 );

        final List<DeclaredTypeAssetInheritanceInformation> types = sorter.sort( assets );

        assertNotNull( types );
        assertEquals( 3,
                      types.size() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( 0,
                      types.get( 0 ).getDependencyScore() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
        assertEquals( 1,
                      types.get( 1 ).getDependencyScore() );
        assertEquals( "Type3",
                      types.get( 2 ).getType() );
        assertEquals( "Type1",
                      types.get( 2 ).getSuperType() );
        assertEquals( 1,
                      types.get( 2 ).getDependencyScore() );
    }

    @Test
    public void testDependencyScoreInvertedOrderCrossAsset() {
        final DeclaredTypesSorter sorter = new DeclaredTypesSorter();
        final String drl1 = "declare Type3 extends Type2\n"
                            + "field1 : String\n"
                            + "end\n";
        final String drl2 = "declare Type2 extends Type1\n"
                            + "field1 : String\n"
                            + "end"
                            + "declare Type1\n"
                            + "field1 : String\n"
                            + "end";

        final List<AssetItem> assets = new ArrayList<AssetItem>();

        AssetItem asset1 = mock( AssetItem.class );
        when( asset1.getContent() ).thenReturn( drl1 );
        assets.add( asset1 );

        AssetItem asset2 = mock( AssetItem.class );
        when( asset2.getContent() ).thenReturn( drl2 );
        assets.add( asset2 );

        final List<DeclaredTypeAssetInheritanceInformation> types = sorter.sort( assets );

        assertNotNull( types );
        assertEquals( 3,
                      types.size() );
        assertEquals( "Type1",
                      types.get( 0 ).getType() );
        assertNull( types.get( 0 ).getSuperType() );
        assertEquals( 0,
                      types.get( 0 ).getDependencyScore() );
        assertEquals( "Type2",
                      types.get( 1 ).getType() );
        assertEquals( "Type1",
                      types.get( 1 ).getSuperType() );
        assertEquals( 1,
                      types.get( 1 ).getDependencyScore() );
        assertEquals( "Type3",
                      types.get( 2 ).getType() );
        assertEquals( "Type2",
                      types.get( 2 ).getSuperType() );
        assertEquals( 2,
                      types.get( 2 ).getDependencyScore() );
    }

}
