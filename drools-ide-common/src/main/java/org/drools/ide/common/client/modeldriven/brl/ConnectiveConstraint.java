package org.drools.ide.common.client.modeldriven.brl;

/**
 * This is for a connective constraint that adds more options to a field constraint. 
 * @author Michael Neale
 */
public class ConnectiveConstraint extends BaseSingleFieldConstraint {

    public ConnectiveConstraint() {
    }

    public ConnectiveConstraint(final String fieldName,
    		                    final String fieldType,
    							final String opr,
                                final String val) {
    	this.fieldName = fieldName;
    	this.fieldType = fieldType;
        this.operator = opr;
        this.setValue(val);
    }

    public String operator;
    public String fieldName;
    public String fieldType;

}
