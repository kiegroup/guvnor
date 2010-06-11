package org.drools.ide.common.client.modeldriven.brl;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

/**
 * This expression represent a bound field. Right now it only acts as a Text
 * expression
 * @author esteban
 */
public class ExpressionFieldVariable extends ExpressionText {
	
	public ExpressionFieldVariable(String name) {
		super(name, "java.lang.String", SuggestionCompletionEngine.TYPE_FINAL_OBJECT);
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
