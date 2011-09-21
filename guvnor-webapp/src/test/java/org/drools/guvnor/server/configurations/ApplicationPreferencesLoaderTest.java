/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.configurations;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ApplicationPreferencesLoaderTest {

    @Test
    public void testLoad() throws Exception {
        Map<String, String> preferences = ApplicationPreferencesLoader.load();

        assertNotNull( preferences );
        assertEquals( 11,
                      preferences.size() );
        assertTrue( preferences.containsKey( "visual-ruleflow" ) );
        assertTrue( preferences.containsKey( "verifier" ) );
        assertTrue( preferences.containsKey( "flex-bpel-editor" ) );
        assertEquals( "true",
                      preferences.get( "verifier" ) );
        assertEquals( "true",
                      preferences.get( "oryx-bpmn-editor" ) );
        assertEquals( "false",
                      preferences.get( "rule-modeller-onlyShowDSLStatements" ) );
    }

}
