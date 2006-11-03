package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the guts of a plain text (advanced) rule.
 * @author Michael Neale
 */
public class RuleContentText
    implements
    IsSerializable {
    
    public String content;
}
