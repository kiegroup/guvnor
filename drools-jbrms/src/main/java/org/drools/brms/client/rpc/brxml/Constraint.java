package org.drools.brms.client.rpc.brxml;


/**
 * This represents a contraint on a fact.
 * Can also include optional "connective constraints" that extend the options for matches.
 * @author Michael Neale
 *
 */
public class Constraint implements PortableObject {
    public String fieldBinding;
    public String fieldName;
    public String operator;
    public String value;
    
    public ConnectiveConstraint[] connectives;
    
    public Constraint(String field) {
        this.fieldName = field;
    }
    
    public Constraint() {}
    
    /**
     * This adds a new connective.
     *
     */
    public void addNewConnective() {
        if (connectives == null) {
            connectives = new ConnectiveConstraint[] {new ConnectiveConstraint()};                      
        } else {
            ConnectiveConstraint[] newList = new ConnectiveConstraint[connectives.length + 1];
            for ( int i = 0; i < connectives.length; i++ ) {            
                newList[i] = connectives[i];
            }
            newList[connectives.length] = new ConnectiveConstraint();
            connectives = newList;
        }          
    }
    
    
}
