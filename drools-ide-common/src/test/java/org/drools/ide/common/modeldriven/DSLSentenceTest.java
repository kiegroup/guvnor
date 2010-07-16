/**
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

import junit.framework.TestCase;

import org.drools.ide.common.client.modeldriven.brl.DSLSentence;

public class DSLSentenceTest extends TestCase {

    public void testSentence() {

        final DSLSentence sen = new DSLSentence();
        sen.sentence = "this is {something} here and {here}";
        assertEquals( "this is something here and here",
                      sen.toString() );

        sen.sentence = "foo bar";
        assertEquals( "foo bar",
                      sen.toString() );

        final DSLSentence newOne = sen.copy();
        assertFalse( newOne == sen );
        assertEquals( newOne.sentence,
                      sen.sentence );
    }

    public void testEnumSentence(){
        final DSLSentence sen = new DSLSentence();
        sen.sentence = "this is {variable:ENUM:Value.test} here and {here}";
        assertEquals( "this is variable here and here",sen.toString() );
    }

    public void testLogColonSentence(){
        final DSLSentence sen = new DSLSentence();
        sen.sentence = "Log : \"{message}\"";
        assertEquals( "Log : \"message\"",sen.toString() );
    }

    public void testWithNewLines() {
    	final DSLSentence sen = new DSLSentence();
        sen.sentence = "this is {variable}\\n here and {here}";
        assertEquals( "this is variable\n here and here",sen.toString() );

    }
}