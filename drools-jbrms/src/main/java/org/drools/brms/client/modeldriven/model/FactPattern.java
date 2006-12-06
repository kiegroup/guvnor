package org.drools.brms.client.modeldriven.model;

public class FactPattern implements IPattern {

    public FactPattern() {
        this.constraints = new Constraint[0];
    }
    
    public FactPattern(String factType) {
        this.factType = factType;
        this.constraints = new Constraint[0];
    }
    
    public Constraint[] constraints;
    public String factType;
    public String boundName;
    
    
    public void addConstraint(Constraint constraint) {
        if (constraints == null) {
            constraints = new Constraint[1];            
            constraints[0] = constraint;            
        } else {
            Constraint[] newList = new Constraint[constraints.length + 1];
            for ( int i = 0; i < constraints.length; i++ ) {            
                newList[i] = constraints[i];
            }
            newList[constraints.length] = constraint;
            constraints = newList;
        }
    }
    
    
    
    
}
