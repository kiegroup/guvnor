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

/**
 * 
 * @author rikkola
 *
 */
public class Format {

    public static String format(String text,
                                String... strings) {

        StringBuilder result = new StringBuilder( text );

        for ( int i = 0; i < strings.length; i++ ) {
            String string = strings[i];
            String placeKeeper = "{" + i + "}";

            int start = result.indexOf( placeKeeper );
            int end = start + placeKeeper.length();

            result.replace( start,
                            end,
                            string );
        }

        return result.toString();
    }

    public static String format(String text,
                                int... ints) {
        return format( text,
                       toStringArray( ints ) );
    }

    private static String[] toStringArray(int[] intArray) {
        String[] result = new String[intArray.length];

        for ( int i = 0; i < intArray.length; i++ ) {
            result[i] = String.valueOf( intArray[i] );

        }

        return result;
    }
}
