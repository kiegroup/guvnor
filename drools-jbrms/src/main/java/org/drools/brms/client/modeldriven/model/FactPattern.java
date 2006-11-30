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
    
    
}
