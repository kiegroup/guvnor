package org.drools.ide.common.client.modeldriven.brl;

/**
 * Represents a constraint, which may be part of a direct field constraint or a connective.
 * @author Michael Neale
 *
 */
public class BaseSingleFieldConstraint
    implements
    PortableObject {

    /**
     * This is used only when constraint is first created.
     * This means that there is no value yet for the constraint.
     */
    public static final int TYPE_UNDEFINED = 0;

    /**
     * This may be string, or number, anything really.
     */
    public static final int TYPE_LITERAL   = 1;

    /**
     * This is when it is set to a valid previously bound variable.
     */
    public static final int TYPE_VARIABLE  = 2;

    /**
     * This is for a "formula" that calculates a value.
     */
    public static final int TYPE_RET_VALUE = 3;

    /**
     * This is not used yet. ENUMs are not suitable for business rules
     * until we can get data driven non code enums.
     */
    public static final int TYPE_ENUM      = 4;

    /**
     * The fieldName and fieldBinding is not used in the case of a predicate.
     */
    public static final int TYPE_PREDICATE = 5;

    /**
     * This is for a "expression builder" that calculates a value.
     */
    public static final int TYPE_EXPR_BUILDER_VALUE = 6;
    
    /**
     * This is for a field to be a placeholder for a template
     */
    public static final int TYPE_TEMPLATE = 7;

    private String           value;
    private int              constraintValueType;
    
	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setConstraintValueType(int constraintValueType) {
		this.constraintValueType = constraintValueType;
	}

	public int getConstraintValueType() {
		return constraintValueType;
	}
}
