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

package org.drools.guvnor.client.rpc;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BulkTestRunResultTest {

    @Test
    public void testPrinting() {
        BulkTestRunResult res = new BulkTestRunResult();
        assertNotNull(res.toString());

        res.setResults( new ScenarioResultSummary[2] );
        res.getResults()[0] = new ScenarioResultSummary(0, 2, "A", "", "");
        res.getResults()[1] = new ScenarioResultSummary(0, 2, "A", "", "");
        assertNotNull(res.toString());
        //System.out.println(res.toString());
        assertTrue(res.toString().startsWith("SUCCESS"));

        res.getResults()[1] = new ScenarioResultSummary(1, 2, "A", "", "");
        System.out.println(res.toString());
        assertTrue(res.toString().indexOf("FAILURE") > -1);

    }

}
