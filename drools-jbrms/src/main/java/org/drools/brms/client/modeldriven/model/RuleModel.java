package org.drools.brms.client.modeldriven.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RuleModel implements IsSerializable {

    public IPattern[] lhs;
    public IAction[] rhs;
    
    /**
     * This will return the fact pattern that a variable is bound to. 
     * 
     * @param var The bound fact variable (NOT bound field).
     * @return null or the FactPattern found. 
     */
    public FactPattern getBoundFact(String var) {
        if (lhs == null ) return null;
        for ( int i = 0; i < lhs.length; i++ ) {
            
            if (lhs[i] instanceof FactPattern) {
                FactPattern p = (FactPattern) lhs[i];
                if (p.boundName != null && var.equals( p.boundName)) {
                    return p;
                }
            }
        }
        return null;
    }
    
}
