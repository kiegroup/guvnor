package org.guvnor.common.services.backend.rulenames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.guvnor.common.services.project.events.RuleNameUpdateEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RuleNamesServiceImplTest {

    @Test
    public void testBasic() throws Exception {

        final ProjectService projectService = mock( ProjectService.class );
        final Project project = mock( Project.class );
        final Path path = mock( Path.class );
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final RuleNamesServiceImpl ruleNamesService = new RuleNamesServiceImpl( projectService );

        final HashMap<String, Collection<String>> ruleNames = new HashMap<String, Collection<String>>();

        final ArrayList<String> rules = new ArrayList<String>();
        rules.add( "Rule 1" );
        rules.add( "Rule 2" );
        ruleNames.put( "testPackage", rules );

        ruleNamesService.onRuleNamesUpdated( new RuleNameUpdateEvent( project, ruleNames ) );

        assertEquals( 2, ruleNamesService.getRuleNames( path ).size() );
        assertEquals( "Rule 1", ruleNamesService.getRuleNames( path ).get( 0 ) );
        assertEquals( "Rule 2", ruleNamesService.getRuleNames( path ).get( 1 ) );

        assertEquals( 2, ruleNamesService.getRuleNamesForPackage( path, "testPackage" ).size() );
        assertEquals( "Rule 1", ruleNamesService.getRuleNamesForPackage( path, "testPackage" ).toArray()[ 0 ] );
        assertEquals( "Rule 2", ruleNamesService.getRuleNamesForPackage( path, "testPackage" ).toArray()[ 1 ] );

        assertEquals( 1, ruleNamesService.getRuleNamesMap( path ).keySet().size() );
        assertTrue( ruleNamesService.getRuleNamesMap( path ).keySet().contains( "testPackage" ) );
        assertEquals( "Rule 1", ruleNamesService.getRuleNamesMap( path ).get( "testPackage" ).toArray()[ 0 ] );
        assertEquals( "Rule 2", ruleNamesService.getRuleNamesMap( path ).get( "testPackage" ).toArray()[ 1 ] );
    }
}
