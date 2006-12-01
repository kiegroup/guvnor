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

    public String field;
    public String value;
    
}
