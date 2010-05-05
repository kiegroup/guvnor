package org.drools.ide.common.client.modeldriven.brl;

public interface ExpressionVisitable {
	void accept(ExpressionVisitor visitor);
}
