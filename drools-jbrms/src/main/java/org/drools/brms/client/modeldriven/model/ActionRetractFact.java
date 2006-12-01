package org.drools.brms.client.modeldriven.model;

public class ActionRetractFact
    implements
    IAction {
    
    
    public ActionRetractFact(String var) {
        this.variableName = var;
    }
    
    public String variableName;

}
