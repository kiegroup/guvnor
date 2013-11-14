package org.guvnor.common.services.backend.rulenames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.guvnor.common.services.shared.rulenames.RuleNameUpdateEvent;
import org.junit.Test;

import static junit.framework.Assert.*;

public class RuleNamesServiceImplTest {

    @Test
    public void testBasic() throws Exception {
        RuleNamesServiceImpl ruleNamesService = new RuleNamesServiceImpl();

        HashMap<String, Collection<String>> ruleNames = new HashMap<String, Collection<String>>();

        ArrayList<String> rules = new ArrayList<String>();
        rules.add("Rule 1");
        rules.add("Rule 2");
        ruleNames.put("testPackage", rules);

        ruleNamesService.onRuleNamesUpdated(new RuleNameUpdateEvent(ruleNames));

        assertEquals(2, ruleNamesService.getRuleNames().size());
        assertEquals("Rule 1", ruleNamesService.getRuleNames().get(0));
        assertEquals("Rule 2", ruleNamesService.getRuleNames().get(1));

        assertEquals(2, ruleNamesService.getRuleNamesForPackage("testPackage").size());
        assertEquals("Rule 1", ruleNamesService.getRuleNamesForPackage("testPackage").toArray()[0]);
        assertEquals("Rule 2", ruleNamesService.getRuleNamesForPackage("testPackage").toArray()[1]);

        assertEquals(1, ruleNamesService.getRuleNamesMap().keySet().size());
        assertTrue(ruleNamesService.getRuleNamesMap().keySet().contains("testPackage"));
        assertEquals("Rule 1", ruleNamesService.getRuleNamesMap().get("testPackage").toArray()[0]);
        assertEquals("Rule 2", ruleNamesService.getRuleNamesMap().get("testPackage").toArray()[1]);
    }
}
