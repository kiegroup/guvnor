package org.guvnor.common.services.shared.rulenames;

import java.util.Collection;
import java.util.Map;

public class RuleNameUpdateEvent {

    private final Map<String, Collection<String>> ruleNames;

    public RuleNameUpdateEvent(Map<String, Collection<String>> ruleNames) {
        this.ruleNames = ruleNames;
    }

    public Map<String, Collection<String>> getRuleNames() {
        return ruleNames;
    }

}
