package org.guvnor.common.services.backend.rulenames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.guvnor.common.services.shared.rulenames.RuleNameUpdateEvent;
import org.guvnor.common.services.shared.rulenames.RuleNamesService;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class RuleNamesServiceImpl
        implements RuleNamesService {

    // List of available rule names
    private Map<String, Collection<String>> ruleNames = new HashMap<String, Collection<String>>();

    @Override
    public Map<String, Collection<String>> getRuleNamesMap() {
        return ruleNames;
    }

    @Override
    public List<String> getRuleNames() {
        List<String> allTheRuleNames = new ArrayList<String>();
        for (String packageName : ruleNames.keySet()) {
            allTheRuleNames.addAll(ruleNames.get(packageName));
        }
        return allTheRuleNames;
    }

    @Override
    public Collection<String> getRuleNamesForPackage(String packageName) {
        if (ruleNames.get(packageName) == null) {
            return Collections.EMPTY_LIST;
        } else {
            return ruleNames.get(packageName);
        }
    }

    public void onRuleNamesUpdated(@Observes final RuleNameUpdateEvent ruleNameUpdateEvent) {
        this.ruleNames = ruleNameUpdateEvent.getRuleNames();
    }

}
