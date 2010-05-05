package org.drools.ide.common.server.util;

import org.drools.ide.common.client.modeldriven.brl.RuleModel;

public interface BRLPersistence {

    public String marshal(final RuleModel model);
    public RuleModel unmarshal(final String str);
}