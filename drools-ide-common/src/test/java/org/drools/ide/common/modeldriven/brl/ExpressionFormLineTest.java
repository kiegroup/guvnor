package org.drools.ide.common.modeldriven.brl;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine;
import org.drools.ide.common.client.modeldriven.brl.ExpressionMethod;
import org.drools.ide.common.client.modeldriven.brl.ExpressionVariable;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.junit.Assert;
import org.junit.Test;

public class ExpressionFormLineTest {

	@Test
	public void toStringText() {
		ExpressionFormLine efl = new ExpressionFormLine();
		FactPattern fact = new FactPattern();
		fact.boundName = "$v";
		fact.factType = "String";
		efl.appendPart(new ExpressionVariable(fact));
		efl.appendPart(new ExpressionMethod("size", "int", SuggestionCompletionEngine.TYPE_NUMERIC));
		Assert.assertEquals("$v.size()", efl.getText());
		
		efl.setBinding("$s");
		
		Assert.assertEquals("$s: $v.size()", efl.getText(true));
		
	}
}
