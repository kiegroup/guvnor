package org.drools.brms.client.modeldriven.model;

/**
 * For setting a field on a bound LHS variable or a global.
 * @author Michael Neale
 *
 */
public class ActionSetField extends ActionFieldList {

    public ActionSetField(String var) {
        this.variable = var;
    }
    
    public ActionSetField() {}
    public String variable;
    public boolean modify;

     
 
    
}
