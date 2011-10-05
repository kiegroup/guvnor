/*
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

package org.drools.ide.common.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.junit.Before;
import org.junit.Test;

public class GuidedDTXMLPersistenceTest {

    private GuidedDecisionTableModelUpgradeHelper upgrader = new GuidedDecisionTableModelUpgradeHelper();
    
    @Before
    public void setUp() throws Exception {
        GuidedDTXMLPersistence.getInstance();
    }

    @Test
    public void testRoundTrip() {

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        dt.getActionCols().add( new ActionInsertFactCol52() );
        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setFactField( "foo" );
        dt.getActionCols().add( set );

        dt.getMetadataCols().add( new MetadataCol52() );

        dt.getAttributeCols().add( new AttributeCol52() );

        Pattern52 p = new Pattern52();
        ConditionCol52 c = new ConditionCol52();
        p.getConditions().add( c );
        dt.getConditionPatterns().add( p );

        dt.setData( upgrader.makeDataLists( new String[][]{new String[]{"1", "hola"}} ) );
        dt.setTableName( "blah" );

        String xml = GuidedDTXMLPersistence.getInstance().marshal( dt );
        System.out.println( xml );
        assertNotNull( xml );
        assertEquals( -1,
                      xml.indexOf( "ActionSetField" ) );
        assertEquals( -1,
                      xml.indexOf( "ConditionCol" ) );
        assertEquals( -1,
                      xml.indexOf( "GuidedDecisionTable" ) );

        GuidedDecisionTable52 dt_ = GuidedDTXMLPersistence.getInstance().unmarshal( xml );
        assertNotNull( dt_ );
        assertEquals( "blah",
                      dt_.getTableName() );
        assertEquals( 1,
                      dt_.getMetadataCols().size() );
        assertEquals( 1,
                      dt_.getAttributeCols().size() );
        assertEquals( 2,
                      dt_.getActionCols().size() );
        assertEquals( 1,
                      dt_.getConditionPatterns().size() );
        assertEquals( 1,
                      dt_.getConditionPatterns().get( 0 ).getConditions().size() );

    }

    @Test
    public void testBackwardsCompatability() throws Exception {
        String xml = BRLPersistenceTest.loadResource( "ExistingDecisionTable.xml" );
        GuidedDecisionTable52 dt_ = GuidedDTXMLPersistence.getInstance().unmarshal( xml );
        assertNotNull( dt_ );
        assertEquals( "blah",
                      dt_.getTableName() );
        assertEquals( 1,
                      dt_.getMetadataCols().size() );
        assertEquals( 1,
                      dt_.getAttributeCols().size() );
        assertEquals( 2,
                      dt_.getActionCols().size() );
        assertEquals( 1,
                      dt_.getConditionPatterns().size() );
        assertEquals( 1,
                      dt_.getConditionPatterns().get( 0 ).getConditions().size() );

        assertTrue( dt_.getActionCols().get( 1 ) instanceof ActionSetFieldCol52 );
        ActionSetFieldCol52 asf = (ActionSetFieldCol52) dt_.getActionCols().get( 1 );
        assertEquals( "foo",
                      asf.getFactField() );
        assertEquals( false,
                      asf.isUpdate() );
    }

}
