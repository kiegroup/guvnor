package org.drools.brms.client.modeldriven.model;

/**
 * This is used to specify that the bound fact should be retracted
 * when the rule fires.
 * @author Michael Neale
 *
 */
public class ActionRetractFact
    implements
    IAction {
    
    
    public ActionRetractFact(String var) {
        this.variableName = var;
    }
    
    public String variableName;

}
