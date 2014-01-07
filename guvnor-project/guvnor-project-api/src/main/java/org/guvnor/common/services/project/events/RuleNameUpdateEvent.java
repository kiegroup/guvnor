package org.guvnor.common.services.project.events;

import java.util.Collection;
import java.util.Map;

import org.guvnor.common.services.project.model.Project;

public class RuleNameUpdateEvent {

    private final Project project;
    private final Map<String, Collection<String>> ruleNames;

    public RuleNameUpdateEvent( final Project project,
                                final Map<String, Collection<String>> ruleNames ) {
        this.project = project;
        this.ruleNames = ruleNames;
    }

    public Map<String, Collection<String>> getRuleNames() {
        return ruleNames;
    }

    public Project getProject() {
        return project;
    }
}
