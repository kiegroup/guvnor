package org.drools.ide.common.client.modeldriven.brl;

import java.util.LinkedList;
import java.util.Map;

public class ExpressionFormLine implements IAction, IPattern, Cloneable {
	
    private String binding = null;
	private LinkedList<ExpressionPart> parts = new LinkedList<ExpressionPart>() ;
    
    public ExpressionFormLine() {}

    public ExpressionFormLine(ExpressionPart part) {
    	appendPart(part);
    }
    
	public ExpressionFormLine(ExpressionFormLine other) {
		CopyExpressionVisitor copier = new CopyExpressionVisitor();
		
		for (ExpressionPart exp = copier.copy(other.getRootExpression()); exp != null; exp = exp.getNext()) {
			parts.add(exp);
		}
	}

	public String getText(boolean renderBindVariable) {
		return new ToStringVisitor().buildString(
				renderBindVariable ? getBinding() : null, 
						getRootExpression());
	}
	
	public String getText() {
		return getText(false);
	}
	
	public void appendPart(ExpressionPart part) {
		if (!parts.isEmpty()) {
			parts.getLast().setNext(part);
		}
		parts.add(part);
	}
	
	public void removeLast() {
		if (!parts.isEmpty()) {
			ExpressionPart last = parts.removeLast();
			if (last.getPrevious() != null) {
				last.getPrevious().setNext(null);
				last.setPrevious(null);
			}
		}
	}
	
	private ExpressionPart getPreviousPart() {
		return parts.getLast();
	}
	
	public String getPreviousClassType() {
		ExpressionPart last = getPreviousPart();
		return last.getPrevious() == null ? null : last.getPrevious().getClassType(); 
	}
	
	public String getClassType() {
		return parts.getLast().getClassType();
	}
	
	public String getGenericType() {
		return parts.isEmpty() ? null : parts.getLast().getGenericType();
	}
	
	public String getPreviousGenericType() {
		ExpressionPart prev = getPreviousPart().getPrevious();
		return prev == null ? null : prev.getGenericType();
	}

	public String getParametricType() {
		return parts.getLast().getParametricType();
	}
	
	public boolean isEmpty() {
		return parts.isEmpty();
	}
	
	public String getCurrentName() {
		return parts.getLast().getName();
	}
	
	public String getPreviousName() {
		ExpressionPart previousPart = getPreviousPart();
		return previousPart == null ? null : previousPart.getName(); 
	}

	public ExpressionPart getRootExpression() {
		return parts.isEmpty() ? null : parts.getFirst();
	}

	public boolean isBound() {
		return binding != null;
	}
	
	public String getBinding() {
		return binding;
	}

	public void setBinding(String binding) {
		this.binding = binding;
	}

	private static class ToStringVisitor implements ExpressionVisitor {
		private StringBuilder str;
		private boolean first;
		
		public String buildString(String bindVariable, ExpressionPart exp) {
			if (exp == null) {
				return "";
			}
			str = new StringBuilder( );
			first = true;
			exp.accept(this);
			return (bindVariable == null ? "" : bindVariable + ": ") + str.toString();
		}
		
		public void visit(ExpressionPart part) {
			throw new IllegalStateException("can't generate text for: " + part.getClass().getName());
		}

		public void visit(ExpressionField part) {
			if (!first) {
				str.append('.');
			}
			str.append(part.getName());
			moveNext(part);
		}

		public void visit(ExpressionMethod part) {
			if (!first) {
				str.append('.');
			}
			str.append(part.getName())
				.append('(')
				.append(paramsToString(part.getParams()))
				.append(')');
			moveNext(part);
		}

		public void visit(ExpressionVariable part) {
			str.append(part.getName());
			moveNext(part);			
		}

		public void visit(ExpressionUnboundFact part) {
			moveNext(part, false);
		}
		
		public void visit(ExpressionGlobalVariable part) {
			str.append(part.getName());
			moveNext(part);
		}

		public void visit(ExpressionCollection part) {
			if (!first) {
				str.append('.');
			}
			str.append(part.getName());
			moveNext(part);
		}

		public void visit(ExpressionCollectionIndex part) {
			str.append('[').append(paramsToString(part.getParams())).append(']');
			moveNext(part);
		}

		public void visit(ExpressionText part) {
			str.append(part.getName());
			moveNext(part);
		}

		private String paramsToString(Map<String, ExpressionFormLine> params) {
			if (params.isEmpty()) {
				return "";
			}
			ToStringVisitor stringVisitor = new ToStringVisitor();
			StringBuilder strParams = new StringBuilder();
			for (ExpressionFormLine param : params.values()) {
				strParams.append(", ").append(stringVisitor.buildString(param.getBinding(), param.getRootExpression()));
			}
			return strParams.substring(2);
		}
		
		private void moveNext(ExpressionPart exp) {
			moveNext(exp, true);
		}
		
		private void moveNext(ExpressionPart exp, boolean resetFirst) {
			if (exp.getNext() != null) {
				if (resetFirst) {
					first = false;
				}
				exp.getNext().accept(this);
			}
		}
	}
}
