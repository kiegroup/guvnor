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

package org.drools.ide.common.modeldriven.brl;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine;
import org.drools.ide.common.client.modeldriven.brl.ExpressionMethod;
import org.drools.ide.common.client.modeldriven.brl.ExpressionVariable;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionFormLineTest {

	@Test
	public void toStringText() {
		ExpressionFormLine efl = new ExpressionFormLine();
		FactPattern fact = new FactPattern("String");
		fact.boundName = "$v";
		efl.appendPart(new ExpressionVariable(fact));
		efl.appendPart(new ExpressionMethod("size", "int", SuggestionCompletionEngine.TYPE_NUMERIC));
	    assertEquals("$v.size()", efl.getText());
		
		efl.setBinding("$s");
		
	    assertEquals("$s: $v.size()", efl.getText(true));
		
	}
}
