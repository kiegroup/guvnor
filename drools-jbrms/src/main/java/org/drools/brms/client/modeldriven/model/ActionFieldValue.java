package org.drools.brms.client.modeldriven.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Holds field/values for "action" parts of the rule.
 * 
 * @author Michael Neale
 */
public class ActionFieldValue
    implements
    IsSerializable {

    public ActionFieldValue(String field, String value) {
        this.field = field;
        this.value = value;
    }
    
    public ActionFieldValue() {}
    
    public String field;
    public String value;
    
}
