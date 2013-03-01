package org.kie.guvnor.testscenario.backend.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.Test;
import org.kie.definition.rule.Rule;
import org.kie.event.rule.AfterMatchFiredEvent;
import org.kie.runtime.rule.Match;


public class RuleCoverageListenerTest {

    @Test
    public void testCoverage() throws Exception {
        // configuring mock event
        AfterMatchFiredEvent amfe = mock( AfterMatchFiredEvent.class );
        Match match = mock( Match.class );
        Rule rule = mock( Rule.class );
        when( amfe.getMatch() ).thenReturn( match );
        when( match.getRule() ).thenReturn( rule );
        when( rule.getName() ).thenReturn( "rule1" ).thenReturn( "rule2" ).thenReturn( "rule3" );

        HashSet<String> rules = new HashSet<String>();
        rules.add( "rule1" );
        rules.add( "rule2" );
        rules.add( "rule3" );

        RuleCoverageListener ls = new RuleCoverageListener( rules );
        assertEquals( 3,
                      ls.rules.size() );
        assertEquals( 0,
                      ls.getPercentCovered() );

        
        ls.afterMatchFired( amfe );
        assertEquals( 2,
                      ls.rules.size() );
        assertTrue( ls.rules.contains( "rule2" ) );
        assertTrue( ls.rules.contains( "rule3" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertEquals( 33,
                      ls.getPercentCovered() );

        ls.afterMatchFired( amfe );
        assertEquals( 1,
                      ls.rules.size() );
        assertFalse( ls.rules.contains( "rule2" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertTrue( ls.rules.contains( "rule3" ) );

        assertEquals( 66,
                      ls.getPercentCovered() );

        ls.afterMatchFired( amfe );
        assertEquals( 0,
                      ls.rules.size() );
        assertFalse( ls.rules.contains( "rule2" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertFalse( ls.rules.contains( "rule3" ) );

        assertEquals( 100,
                      ls.getPercentCovered() );

    }

}

