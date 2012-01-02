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

package org.drools.ide.common.modeldriven;

import org.drools.ide.common.client.modeldriven.brl.DSLComplexVariableValue;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.DSLVariableValue;

public class DSLSentenceTest {

    @Test
    public void testSentence() {

        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "this is {something} here and {here}" );
        assertEquals( "this is {something} here and {here}",
                      sen.toString() );

        sen.setDefinition( "foo bar" );
        assertEquals( "foo bar",
                      sen.toString() );

        final DSLSentence newOne = sen.copy();
        assertFalse( newOne == sen );
        assertEquals( newOne.getDefinition(),
                      sen.getDefinition() );
        assertEquals( newOne.getValues(),
                      sen.getValues() );
    }

    @Test
    public void testEnumSentence() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "this is {variable:ENUM:Value.test} here and {here}" );
        assertEquals( "this is {variable} here and {here}",
                      sen.toString() );
    }

    @Test
    public void testLogColonSentence() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "Log : \"{message}\"" );
        assertEquals( "Log : \"{message}\"",
                      sen.toString() );
    }

    @Test
    public void testWithNewLines() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "this is {variable}\\n here and {here}" );
        assertEquals( "this is {variable}\n here and {here}",
                      sen.toString() );

    }

    @Test
    public void testInterpolate1() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "{something} here" );
        sen.getValues().set( 0, new DSLVariableValue("word"));
        assertEquals( "word here",
                      sen.interpolate() );
    }

    @Test
    public void testInterpolate2() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "a {here}" );
        sen.getValues().set( 0, new DSLVariableValue("word"));
        assertEquals( "a word",
                      sen.interpolate() );
    }

    @Test
    public void testInterpolate3() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "a {here} and {here}" );
        sen.getValues().set( 0, new DSLVariableValue("word"));
        sen.getValues().set( 1, new DSLVariableValue("word") );
        assertEquals( "a word and word",
                      sen.interpolate() );
    }

    @Test
    public void testEnumSentenceContainingRegEx() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "When a person exists with social security number \"{chooseSSN:\\d{3}-\\d{2}-\\d{4}}\"" );
        sen.getValues().set( 0, new DSLVariableValue("333-22-4444"));
        assertEquals( "When a person exists with social security number \"333-22-4444\"",
                      sen.interpolate() );
    }

    @Test
    public void testEnumSentenceWithBoolean() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "When a person is alive {alive:BOOLEAN:checked}" );
        sen.getValues().set( 0, new DSLVariableValue("true" ));
        assertEquals( "When a person is alive true",
                      sen.interpolate() );
    }

    @Test
    public void testEnumSentenceWithEnumeration() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "When a person is \"{rating:ENUM:Person.gender}\"" );
        sen.getValues().set( 0, new DSLVariableValue("Male"));
        assertEquals( "When a person is \"Male\"",
                      sen.interpolate() );
    }

    @Test
    public void testEnumSentenceWithDate() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "When a person was born on \"{dob:DATE:default}\"" );
        sen.getValues().set( 0, new DSLVariableValue("31-Dec-1999"));
        assertEquals( "When a person was born on \"31-Dec-1999\"",
                      sen.interpolate() );
    }
    
    @Test
    public void testDSLComplexVariableValueInterpolation() {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "a {here} and {here}" );
        sen.getValues().set( 0, new DSLComplexVariableValue("123","word"));
        sen.getValues().set( 1, new DSLComplexVariableValue("some-other-value","word") );
        assertEquals( "a word and word",
                      sen.interpolate() );
    }

}
