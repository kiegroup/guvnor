package org.guvnor.common.services.backend.rulenames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.events.RuleNameUpdateEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.rulenames.RuleNamesService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class RuleNamesServiceImpl
        implements RuleNamesService {

    private ProjectService projectService;

    // List of available rule names per project and package
    private final Map<Project, Map<String, Collection<String>>> ruleNames = new HashMap<Project, Map<String, Collection<String>>>();

    public RuleNamesServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    @Inject
    public RuleNamesServiceImpl( final ProjectService projectService ) {
        this.projectService = projectService;
    }

    @Override
    public Map<String, Collection<String>> getRuleNamesMap( final Path path ) {
        final Project project = projectService.resolveProject( path );
        if ( project == null ) {
            return Collections.emptyMap();
        }
        return ruleNames.get( project );
    }

    @Override
    public List<String> getRuleNames( final Path path ) {
        final Project project = projectService.resolveProject( path );
        if ( project == null ) {
            return Collections.emptyList();
        }

        final List<String> allTheRuleNames = new ArrayList<String>();
        for ( final String packageName : ruleNames.get( project ).keySet() ) {
            allTheRuleNames.addAll( ruleNames.get( project ).get( packageName ) );
        }
        return allTheRuleNames;
    }

    @Override
    public Collection<String> getRuleNamesForPackage( final Path path,
                                                      final String packageName ) {
        final Project project = projectService.resolveProject( path );
        if ( project == null ) {
            return Collections.emptyList();
        }

        if ( !ruleNames.get( project ).containsKey( packageName ) ) {
            return Collections.emptyList();
        }

        return ruleNames.get( project ).get( packageName );
    }

    void onRuleNamesUpdated( @Observes final RuleNameUpdateEvent ruleNameUpdateEvent ) {
        ruleNames.put( ruleNameUpdateEvent.getProject(), new HashMap<String, Collection<String>>( ruleNameUpdateEvent.getRuleNames() ) );
    }
}
