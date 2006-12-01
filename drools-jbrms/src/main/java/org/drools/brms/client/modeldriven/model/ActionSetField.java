package org.drools.brms.client.modeldriven.model;

/**
 * For setting a field on a bound LHS variable or a global.
 * @author Michael Neale
 *
 */
public class ActionSetField implements IAction {

    public String variable;
    public ActionFieldValue[] fieldValues;
    public boolean modify;

    
}
