package org.drools.brms.client.modeldriven.brxml;


/**
 * Holds field and value for "action" parts of the rule.
 * 
 * @author Michael Neale
 */
public class ActionFieldValue
    implements
    PortableObject {

    public ActionFieldValue(String field, String value) {
        this.field = field;
        this.value = value;
    }
    
    public ActionFieldValue() {}
    
    public String field;
    public String value;
    
}
