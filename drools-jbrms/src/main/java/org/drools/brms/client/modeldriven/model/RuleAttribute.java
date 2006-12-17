package org.drools.brms.client.modeldriven.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This holds values for rule attributes (eg salience, agenda-group etc).
 * @author Michael Neale
 */
public class RuleAttribute
    implements
    IsSerializable {
    
    public RuleAttribute(String name,
                         String value) {
        this.attributeName = name;
        this.value = value;
    }
    public String attributeName;
    public String value;

}
