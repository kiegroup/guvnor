package org.drools.ide.common.client.modeldriven.brl;


public class ExpressionUnboundFact extends ExpressionPart {
	private FactPattern fact;
	
	@SuppressWarnings("unused")
	private ExpressionUnboundFact() {}

	public ExpressionUnboundFact(FactPattern fact) {
		super(fact.factType, fact.factType, fact.factType);
		this.fact = fact;
	}

	public FactPattern getFact() {
		return fact;
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
