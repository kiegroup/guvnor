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
package org.drools.ide.common.shared.workitems;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for PortableParameterDefinitions
 */
public class PortableParameterDefinitionTests {

    @Test
    public void testAsString() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setValue( Boolean.TRUE );
        assertEquals( "Boolean.TRUE",
                      p1.asString() );

        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setValue( "PUPA" );
        p2.setClassName( "Smurf" );
        assertEquals( "Smurf.PUPA",
                      p2.asString() );

        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        p3.setValue( 1.23f );
        assertEquals( "1.23f",
                      p3.asString() );

        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        p4.setValue( 123 );
        assertEquals( "123",
                      p4.asString() );

        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setBinding( "$b" );
        assertEquals( "$b",
                      p5.asString() );

        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        p6.setValue( "hello" );
        assertEquals( "\"hello\"",
                      p6.asString() );

    }

    @Test
    public void testAsStringNullValues() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        assertEquals( "null",
                      p1.asString() );

        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setClassName( "Smurf" );
        assertEquals( "null",
                      p2.asString() );

        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        assertEquals( "null",
                      p3.asString() );

        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        assertEquals( "null",
                      p4.asString() );

        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        assertEquals( "null",
                      p5.asString() );

        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        assertEquals( "null",
                      p6.asString() );

    }

    @Test
    public void testAsStringWithBindings() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setValue( Boolean.TRUE );
        p1.setBinding( "$b" );
        assertEquals( "$b",
                      p1.asString() );

        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setValue( "PUPA" );
        p2.setBinding( "$b" );
        p2.setClassName( "Smurf" );
        assertEquals( "$b",
                      p2.asString() );

        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        p3.setValue( 1.23f );
        p3.setBinding( "$b" );
        assertEquals( "$b",
                      p3.asString() );

        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        p4.setValue( 123 );
        p4.setBinding( "$b" );
        assertEquals( "$b",
                      p4.asString() );

        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setBinding( "$b" );
        assertEquals( "$b",
                      p5.asString() );

        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        p6.setValue( "hello" );
        p6.setBinding( "$b" );
        assertEquals( "$b",
                      p6.asString() );

    }

    @Test
    public void testBindings1() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setValue( Boolean.TRUE );
        p1.setBinding( "$b" );
        assertEquals( true,
                      p1.isBound() );

        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setValue( "PUPA" );
        p2.setBinding( "$b" );
        p2.setClassName( "Smurf" );
        assertEquals( true,
                      p2.isBound() );

        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        p3.setValue( 1.23f );
        p3.setBinding( "$b" );
        assertEquals( true,
                      p3.isBound() );

        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        p4.setValue( 123 );
        p4.setBinding( "$b" );
        assertEquals( true,
                      p4.isBound() );

        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setBinding( "$b" );
        assertEquals( true,
                      p5.isBound() );

        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        p6.setValue( "hello" );
        p6.setBinding( "$b" );
        assertEquals( true,
                      p6.isBound() );

    }

    @Test
    public void testBindings2() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setValue( Boolean.TRUE );
        p1.setBinding( "" );
        assertEquals( false,
                      p1.isBound() );

        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setValue( "PUPA" );
        p2.setBinding( "" );
        p2.setClassName( "Smurf" );
        assertEquals( false,
                      p2.isBound() );

        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        p3.setValue( 1.23f );
        p3.setBinding( "" );
        assertEquals( false,
                      p3.isBound() );

        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        p4.setValue( 123 );
        p4.setBinding( "" );
        assertEquals( false,
                      p4.isBound() );

        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setBinding( "" );
        assertEquals( false,
                      p5.isBound() );

        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        p6.setValue( "hello" );
        p6.setBinding( "" );
        assertEquals( false,
                      p6.isBound() );

    }

}
