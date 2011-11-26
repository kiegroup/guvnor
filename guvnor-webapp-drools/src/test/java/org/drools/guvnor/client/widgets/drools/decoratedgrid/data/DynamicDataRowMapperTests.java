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
package org.drools.guvnor.client.widgets.drools.decoratedgrid.data;

import static org.junit.Assert.assertEquals;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for DynamicData
 */
public class DynamicDataRowMapperTests extends BaseDynamicDataTests {

    @Before
    public void setup() {
        super.setup();

        //Setup date to merge
        //[-][-][-]
        //[1][1][1]
        //[1][1][1]
        //[1][1][1]
        //[2][2][2]
        //[2][2][2]
        //[2][2][2]
        //[-][-][-]
        data.addRow( makeRow() );
        data.addRow( makeRow() );
        data.addRow( makeRow() );
        data.addRow( makeRow() );
        data.addRow( makeRow() );

        data.get( 0 ).get( 0 ).setValue( "-" );
        data.get( 0 ).get( 1 ).setValue( "-" );
        data.get( 0 ).get( 2 ).setValue( "-" );

        data.get( 1 ).get( 0 ).setValue( "1" );
        data.get( 1 ).get( 1 ).setValue( "1" );
        data.get( 1 ).get( 2 ).setValue( "1" );

        data.get( 2 ).get( 0 ).setValue( "1" );
        data.get( 2 ).get( 1 ).setValue( "1" );
        data.get( 2 ).get( 2 ).setValue( "1" );

        data.get( 3 ).get( 0 ).setValue( "1" );
        data.get( 3 ).get( 1 ).setValue( "1" );
        data.get( 3 ).get( 2 ).setValue( "1" );

        data.get( 4 ).get( 0 ).setValue( "2" );
        data.get( 4 ).get( 1 ).setValue( "2" );
        data.get( 4 ).get( 2 ).setValue( "2" );

        data.get( 5 ).get( 0 ).setValue( "2" );
        data.get( 5 ).get( 1 ).setValue( "2" );
        data.get( 5 ).get( 2 ).setValue( "2" );

        data.get( 6 ).get( 0 ).setValue( "2" );
        data.get( 6 ).get( 1 ).setValue( "2" );
        data.get( 6 ).get( 2 ).setValue( "2" );

        data.get( 7 ).get( 0 ).setValue( "-" );
        data.get( 7 ).get( 1 ).setValue( "-" );
        data.get( 7 ).get( 2 ).setValue( "-" );
    }

    @Test
    public void testMapToMergedRow() {
        //0=[-][-][-] --> 0=[-][-][-]
        //1=[1][1][1] --> 1=[1][1][1]
        //2=[1][1][1] --> 2=[2][2][2]
        //3=[1][1][1] --> 3=[2][2][2]
        //4=[2][2][2] --> 4=[2][2][2]
        //5=[2][2][2] --> 5=[-][-][-]
        //6=[2][2][2]
        //7=[-][-][-]
        RowMapper rowMapper = new RowMapper( data );
        CellValue< ? extends Comparable< ? >> cv = data.get( 1 ).get( 0 );

        data.setMerged( true );
        data.applyModelGrouping( cv );

        assertEquals( 0,
                      rowMapper.mapToMergedRow( 0 ) );
        assertEquals( 1,
                      rowMapper.mapToMergedRow( 1 ) );
        assertEquals( 1,
                      rowMapper.mapToMergedRow( 2 ) );
        assertEquals( 1,
                      rowMapper.mapToMergedRow( 3 ) );
        assertEquals( 2,
                      rowMapper.mapToMergedRow( 4 ) );
        assertEquals( 3,
                      rowMapper.mapToMergedRow( 5 ) );
        assertEquals( 4,
                      rowMapper.mapToMergedRow( 6 ) );
        assertEquals( 5,
                      rowMapper.mapToMergedRow( 7 ) );

    }

    @Test
    public void testMapToAbsoluteRow() {
        //0=[-][-][-] --> 0=[-][-][-]
        //1=[1][1][1] --> 1=[1][1][1]
        //2=[1][1][1] --> 2=[2][2][2]
        //3=[1][1][1] --> 3=[2][2][2]
        //4=[2][2][2] --> 4=[2][2][2]
        //5=[2][2][2] --> 5=[-][-][-]
        //6=[2][2][2]
        //7=[-][-][-]
        RowMapper rowMapper = new RowMapper( data );
        CellValue< ? extends Comparable< ? >> cv = data.get( 1 ).get( 0 );

        data.setMerged( true );
        data.applyModelGrouping( cv );

        assertEquals( 0,
                      rowMapper.mapToAbsoluteRow( 0 ) );
        assertEquals( 1,
                      rowMapper.mapToAbsoluteRow( 1 ) );
        assertEquals( 4,
                      rowMapper.mapToAbsoluteRow( 2 ) );
        assertEquals( 5,
                      rowMapper.mapToAbsoluteRow( 3 ) );
        assertEquals( 6,
                      rowMapper.mapToAbsoluteRow( 4 ) );
        assertEquals( 7,
                      rowMapper.mapToAbsoluteRow( 5 ) );

    }

}
