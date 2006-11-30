package org.drools.brms.client.modeldriven.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ConnectiveConstraint
    implements
    IsSerializable {

    public ConnectiveConstraint(String opr,
                                String val) {
        this.operator = opr;
        this.value = val;
    }
    public String operator;
    public String value;
    
}
