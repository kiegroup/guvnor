package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Used to transfer the payload for a simple DSL based rule.
 * 
 * @author Michael Neale
 */
public class DSLRuleData
    implements
    IsSerializable {

    public String[] lhsSuggestions;
    public String[] rhsSuggestions;    
    public RuleContentText text;
    
}
