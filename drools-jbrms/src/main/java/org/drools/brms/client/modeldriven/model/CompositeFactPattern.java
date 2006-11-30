package org.drools.brms.client.modeldriven.model;


/**
 * Represents first order logic like Or, Not, Exists.
 * 
 * @author Michael Neale
 */
public class CompositeFactPattern implements IPattern {

    /**
     * this will one of: [Not, Exist, Or]
     */
    public String type;
    public FactPattern[] patterns;
    
    
}
