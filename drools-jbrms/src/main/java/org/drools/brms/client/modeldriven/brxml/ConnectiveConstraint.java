package org.drools.brms.client.modeldriven.brxml;


/**
 * This is for a connective constraint that adds more options to a field constraint. 
 * @author Michael Neale
 */
public class ConnectiveConstraint
    implements
    PortableObject {

    public ConnectiveConstraint() {}
    
    public ConnectiveConstraint(String opr,
                                String val) {
        this.operator = opr;
        this.value = val;
    }
    public String operator;
    public String value;
    
}
