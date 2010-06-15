package org.drools.ide.common.client.modeldriven.brl;


/**
 * @author baunax@gmail.com
 */
public class SingleFieldConstraintEBLeftSide extends SingleFieldConstraint {

    public SingleFieldConstraintEBLeftSide() {
		super();
	}

	public SingleFieldConstraintEBLeftSide(String field, String fieldType, FieldConstraint parent) {
		super(field, fieldType, parent);
	}

	public SingleFieldConstraintEBLeftSide(String field) {
		super(field);
	}

	private ExpressionFormLine expLeftSide = new ExpressionFormLine();
	
    /**
     * Returns true of there is a field binding.
     */
    public boolean isBound() {
        return expLeftSide != null && expLeftSide.isBound();
    }
    
    public ExpressionFormLine getExpressionLeftSide() {
        return expLeftSide;
    }

    public void setExpressionLeftSide(ExpressionFormLine expression) {
        this.expLeftSide = expression;
    }
    
    @Override
    public String getFieldBinding() {
    	return getExpressionLeftSide().getBinding();
    }
    
    @Override
    public void setFieldBinding(String fieldBinding) {
    	getExpressionLeftSide().setBinding(fieldBinding);
    }
    
    @Override
    public String getFieldType() {
    	return getExpressionLeftSide().getClassType();
    }

}
