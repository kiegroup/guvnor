package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This contains the payload for vanilla text of a rule.
 * Basically is a mutable string.
 */
public class TextData implements IsSerializable {
    public String content;
    public boolean dirty;
}
