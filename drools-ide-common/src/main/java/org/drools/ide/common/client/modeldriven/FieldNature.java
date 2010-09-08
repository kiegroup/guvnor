package org.drools.ide.common.client.modeldriven;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

public interface FieldNature{

	/**
	 * This is used only when action is first created. This means that there is
	 * no value yet for the constraint.
	 */
	public static final int TYPE_UNDEFINED = 0;
	/**
	 * This may be string, or number, anything really.
	 */
	public static final int TYPE_LITERAL = 1;
	/**
	 * This is when it is set to a valid previously bound variable.
	 */
	public static final int TYPE_VARIABLE = 2;
	/**
	 * This is for a "formula" that calculates a value.
	 */
	public static final int TYPE_FORMULA = 3;
	/**
	 * This is not used yet. ENUMs are not suitable for business rules until we
	 * can get data driven non code enums.
	 */
	public static final int TYPE_ENUM = 4;
	/**
	 * The fieldName and fieldBinding is not used in the case of a predicate.
	 */
	public static final int TYPE_PREDICATE = 5;
	/**
	 * This is for a field to be a placeholder for a template
	 */
	public static final int TYPE_TEMPLATE = 7;

	/**
	 * This will return true if the value is really a "formula" - in the sense
	 * of like an excel spreadsheet.
	 * 
	 * If it IS a formula, then the value should never be turned into a string,
	 * always left as-is.
	 * 
	 */
	public abstract boolean isFormula();

	public abstract String getField();

	public abstract void setField(String field);

	public abstract String getValue();

	public abstract void setValue(String value);

	public abstract long getNature();

	public abstract void setNature(long nature);

	public abstract String getType();

	public abstract void setType(String type);

}