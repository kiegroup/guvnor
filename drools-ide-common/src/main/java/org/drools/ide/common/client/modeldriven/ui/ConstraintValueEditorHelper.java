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

package org.drools.ide.common.client.modeldriven.ui;

public class ConstraintValueEditorHelper {

    /**
     * 'Person.age' : ['M=Male', 'F=Female']
     *
     * This will split the drop down item into a value and a key.
     * eg key=value
     *
     */
    public static String[] splitValue(String v) {
        String[] s = new String[2];
        int pos = v.indexOf( '=' );
        s[0] = v.substring( 0, pos );
        s[1] = v.substring( pos + 1, v.length() );
        return s;
    }

}
