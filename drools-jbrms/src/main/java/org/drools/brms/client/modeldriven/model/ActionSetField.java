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

    
    public void removeField(int idx) {
        //Unfortunately, this is kinda duplicate code with other methods, 
        //but with typed arrays, and GWT, its not really possible to do anything "better" 
        //at this point in time. 
        ActionFieldValue[] newList = new ActionFieldValue[fieldValues.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < fieldValues.length; i++ ) {
            
            if (i != idx) {
                newList[newIdx] = fieldValues[i];
                newIdx++;
            }
            
        }
        this.fieldValues = newList;        
    }
    
    public void addFieldValue(ActionFieldValue val) {
        if (fieldValues == null) {
            fieldValues = new ActionFieldValue[1];            
            fieldValues[0] = val;            
        } else {
            ActionFieldValue[] newList = new ActionFieldValue[fieldValues.length + 1];
            for ( int i = 0; i < fieldValues.length; i++ ) {            
                newList[i] = fieldValues[i];
            }
            newList[fieldValues.length] = val;
            fieldValues = newList;
        }        
    }
    
}
