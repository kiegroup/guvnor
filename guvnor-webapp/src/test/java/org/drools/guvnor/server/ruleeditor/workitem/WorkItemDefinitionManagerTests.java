/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.ruleeditor.workitem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.drools.process.core.WorkDefinition;
import org.drools.process.core.datatype.impl.type.BooleanDataType;
import org.drools.process.core.datatype.impl.type.EnumDataType;
import org.drools.process.core.datatype.impl.type.FloatDataType;
import org.drools.process.core.datatype.impl.type.IntegerDataType;
import org.drools.process.core.datatype.impl.type.ListDataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.junit.Test;

/**
 * Tests for Work Item Definitions
 */
public class WorkItemDefinitionManagerTests {

    @Test
    public void testLoadingWorkDefinitionsFromConfigurationFile() {
        try {
            Map<String, WorkDefinition> wids = ConfigFileWorkDefinitionsLoader.getInstance().getWorkDefinitions();

            assertNotNull( wids );
            assertEquals( 1,
                          wids.size() );

            WorkDefinitionImpl wid = (WorkDefinitionImpl) wids.get( "MyTask" );

            assertEquals( "MyTask",
                          wid.getName() );
            assertEquals( "My Task",
                          wid.getDisplayName() );

            //Check parameters
            assertNotNull( wid.getParameters() );
            assertEquals( 7,
                          wid.getParameters().size() );

            assertNotNull( wid.getParameter( "StringParam" ) );
            assertNotNull( wid.getParameter( "IntegerParam" ) );
            assertNotNull( wid.getParameter( "FloatParam" ) );
            assertNotNull( wid.getParameter( "BooleanParam" ) );
            assertNotNull( wid.getParameter( "EnumParam" ) );
            assertNotNull( wid.getParameter( "ListParam" ) );
            assertNotNull( wid.getParameter( "ObjectParam" ) );

            assertTrue( wid.getParameter( "StringParam" ).getType() instanceof StringDataType );
            assertTrue( wid.getParameter( "IntegerParam" ).getType() instanceof IntegerDataType );
            assertTrue( wid.getParameter( "FloatParam" ).getType() instanceof FloatDataType );
            assertTrue( wid.getParameter( "BooleanParam" ).getType() instanceof BooleanDataType );
            assertTrue( wid.getParameter( "EnumParam" ).getType() instanceof EnumDataType );
            assertTrue( wid.getParameter( "ListParam" ).getType() instanceof ListDataType );
            assertTrue( wid.getParameter( "ObjectParam" ).getType() instanceof ObjectDataType );

            //Check results
            assertNotNull( wid.getResults() );
            assertEquals( 7,
                          wid.getResults().size() );

            assertNotNull( wid.getResult( "StringResult" ) );
            assertNotNull( wid.getResult( "IntegerResult" ) );
            assertNotNull( wid.getResult( "FloatResult" ) );
            assertNotNull( wid.getResult( "BooleanResult" ) );
            assertNotNull( wid.getResult( "EnumResult" ) );
            assertNotNull( wid.getResult( "ListResult" ) );
            assertNotNull( wid.getResult( "ObjectResult" ) );

            assertTrue( wid.getResult( "StringResult" ).getType() instanceof StringDataType );
            assertTrue( wid.getResult( "IntegerResult" ).getType() instanceof IntegerDataType );
            assertTrue( wid.getResult( "FloatResult" ).getType() instanceof FloatDataType );
            assertTrue( wid.getResult( "BooleanResult" ).getType() instanceof BooleanDataType );
            assertTrue( wid.getResult( "EnumResult" ).getType() instanceof EnumDataType );
            assertTrue( wid.getResult( "ListResult" ).getType() instanceof ListDataType );
            assertTrue( wid.getResult( "ObjectResult" ).getType() instanceof ObjectDataType );

        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
    }

}
