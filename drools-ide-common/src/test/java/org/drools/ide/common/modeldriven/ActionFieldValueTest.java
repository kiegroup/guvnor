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

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;

public class ActionFieldValueTest extends TestCase {

    public void testFormula() {
        ActionFieldValue val = new ActionFieldValue( "x",
                                                     "y",
                                                     SuggestionCompletionEngine.TYPE_NUMERIC );
        assertFalse( val.isFormula() );
        val = new ActionFieldValue( "x",
                                    "=y * 20",
                                    SuggestionCompletionEngine.TYPE_NUMERIC );
        assertTrue( val.isFormula() );
    }

}
