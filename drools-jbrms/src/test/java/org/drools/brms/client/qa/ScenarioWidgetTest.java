package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.drools.brms.client.modeldriven.testing.FactData;

import junit.framework.TestCase;

public class ScenarioWidgetTest extends TestCase {

    public void testMapSeparate() {
        List l = new ArrayList();
        FactData fd1 = new FactData("Driver", "d1", null, false);
        FactData fd2 = new FactData("Driver", "d2", null, false);
        FactData fd3 = new FactData("Driver", "d3", null, true);
        l.add(fd1); l.add(fd2); l.add(fd3);

        HashMap facts = new HashMap();
        HashMap globals = new HashMap();

        ScenarioWidget.breakUpFactData(l, facts, globals);

        assertEquals(1, facts.size());
        assertEquals(1, globals.size());

        List fl = (List) facts.get("Driver");
        assertEquals(2, fl.size());
        assertEquals(fd1, fl.get(0));
        assertEquals(fd2, fl.get(1));

        List gl = (List) globals.get("Driver");
        assertEquals(1, gl.size());
        assertEquals(fd3, gl.get(0));
    }

}
