package org.drools.brms.client.modeldriven.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Constraint implements IsSerializable {
    public String fieldBinding;
    public String fieldName;
    public String operator;
    public String value;
    
    public ConnectiveConstraint[] connectives;
    
    public Constraint(String field) {
        this.fieldName = field;
    }
    
    public Constraint() {}
    
    
}
