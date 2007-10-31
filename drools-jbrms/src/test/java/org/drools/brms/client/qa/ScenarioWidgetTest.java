package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.testing.FactData;

public class ScenarioWidgetTest extends TestCase {

    public void testMapSeparate() {
        List l = new ArrayList();
        FactData fd1 = new FactData("Driver", "d1", null, false);
        FactData fd2 = new FactData("Driver", "d2", null, false);
        FactData fd3 = new FactData("Person", "d3", null, false);
        l.add(fd1); l.add(fd2); l.add(fd3);




        Map facts = ScenarioWidget.breakUpFactData(l);

        assertEquals(2, facts.size());


        List fl = (List) facts.get("Driver");
        assertEquals(2, fl.size());
        assertEquals(fd1, fl.get(0));
        assertEquals(fd2, fl.get(1));

        List gl = (List) facts.get("Person");
        assertEquals(1, gl.size());
        assertEquals(fd3, gl.get(0));
    }

}
