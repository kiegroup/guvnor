package org.drools.ide.common.client.modeldriven.brl;


/**
 * This represents a constraint on a fact - involving a SINGLE FIELD.
 * 
 * Can also include optional "connective constraints" that extend the options for matches.
 * @author Michael Neale
 */
public class SingleFieldConstraint extends BaseSingleFieldConstraint implements FieldConstraint {

    private String                 fieldBinding;
    private String                 fieldName;
    private String                 operator;
    private String                 fieldType;
    private FieldConstraint  parent;

    /**
     * Used instead of "value" when constraintValueType = TYPE_EXPR_BUILDER.
     * Esteban Aliverti
     */
    private ExpressionFormLine expression = new ExpressionFormLine();

    public ConnectiveConstraint[] connectives;

    public SingleFieldConstraint(final String field, final String fieldType, final FieldConstraint parent) {
        this.setFieldName(field);
        this.setFieldType(fieldType);
        this.setParent(parent);
    }

    public SingleFieldConstraint(final String field) {
        this.setFieldName(field);
        this.setFieldType("");
        this.setParent(null);
    }

    public SingleFieldConstraint() {
        this.setFieldName(null);
        this.setFieldType("");
        this.setParent(null);
    }

    public void setFieldBinding(String fieldBinding) {
		this.fieldBinding = fieldBinding;
	}

	public String getFieldBinding() {
		return fieldBinding;
	}

	/**
     * This adds a new connective.
     *
     */
    public void addNewConnective() {
        if ( this.connectives == null ) {
            this.connectives = new ConnectiveConstraint[]{new ConnectiveConstraint(this.getFieldName(), this.getFieldType(), null, null)};
        } else {
            final ConnectiveConstraint[] newList = new ConnectiveConstraint[this.connectives.length + 1];
            for ( int i = 0; i < this.connectives.length; i++ ) {
                newList[i] = this.connectives[i];
            }
            newList[this.connectives.length] = new ConnectiveConstraint(this.getFieldName(), this.getFieldType(), null, null);
            this.connectives = newList;
        }
    }

    /**
     * Returns true of there is a field binding.
     */
    public boolean isBound() {
        return this.getFieldBinding() != null && this.getFieldBinding().length() > 0; 
    }

    public ExpressionFormLine getExpressionValue() {
        return expression;
    }

    public void setExpressionValue(ExpressionFormLine expression) {
        this.expression = expression;
    }

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setParent(FieldConstraint parent) {
		this.parent = parent;
	}

	public FieldConstraint getParent() {
		return parent;
	}
}
