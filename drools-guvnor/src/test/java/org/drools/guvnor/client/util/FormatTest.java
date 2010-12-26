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

package org.drools.guvnor.client.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * �
 * @author rikkola
 *
 */
public class FormatTest {

    @Test
    public void simpleFormatting() {
        String test = "test {0}";

        String result = Format.format( test,
                                       "hello" );

        assertEquals( "test hello",
                             result );
    }

    @Test
    public void formatTwoFormat() {
        String test = "test {0} {1}";

        String result = Format.format( test,
                                       new String[]{"hello", "toni"} );

        assertEquals( "test hello toni",
                             result );

    }

    @Test
    public void intFormat() {
        String test = "number: {0} ";

        String result = Format.format( test,
                                       123456 );

        assertEquals( "number: 123456 ",
                             result );

    }

    @Test
    public void severalInts() {
        String test = "Lotto numbers are: {0}, {1}, {2}, {3} ";

        String result = Format.format( test,
                                       4,
                                       12,
                                       42,
                                       44 );

        assertEquals( "Lotto numbers are: 4, 12, 42, 44 ",
                             result );

    }

    @Test
    public void tripleFormat() {
        String test = "{0}, this test called {1} was created by {2}";

        String result = Format.format( test,
                                       "Hello",
                                       "tripleFormat",
                                       "Toni" );

        assertEquals( "Hello, this test called tripleFormat was created by Toni",
                             result );
    }

    @Test
    public void failingFormat() {
        String test = "{0}, this test called {1} was created by {2}";

        String result = Format.format( test,
                                       new String[]{"Hello", "Toni"} );

        assertNotNull( result );

        assertEquals( "Hello, this test called Toni was created by {2}",
                             result );
    }
    @Test
    public void FormatNotIndicated() {
        String test = "number:";

        String result = Format.format( test,
                                       "tt" );

        assertEquals( "number:",
                             result );

    }
}
