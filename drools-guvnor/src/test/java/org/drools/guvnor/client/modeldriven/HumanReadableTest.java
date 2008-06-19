package org.drools.guvnor.client.modeldriven;
/*
 * Copyright 2005 JBoss Inc
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



import org.drools.guvnor.client.modeldriven.HumanReadable;

import junit.framework.TestCase;

public class HumanReadableTest extends TestCase {

    public void testOperatorMapping() {

        assertEquals("is not equal to", HumanReadable.getOperatorDisplayName("!="));
        assertEquals("is equal to", HumanReadable.getOperatorDisplayName("=="));
        assertEquals("xxx", HumanReadable.getOperatorDisplayName("xxx"));
        assertEquals("sounds like", HumanReadable.getOperatorDisplayName("soundslike" ));
    }

    public void testCEMapping() {

        assertEquals("There is no", HumanReadable.getCEDisplayName( "not" ));
        assertEquals("There exists", HumanReadable.getCEDisplayName( "exists" ));
        assertEquals("Any of", HumanReadable.getCEDisplayName( "or" ));
        assertEquals("xxx", HumanReadable.getCEDisplayName( "xxx" ));

    }

    public void testActionMapping() {

        assertEquals("Insert", HumanReadable.getActionDisplayName( "assert" ));
        assertEquals("foo", HumanReadable.getActionDisplayName( "foo" ));
    }






}