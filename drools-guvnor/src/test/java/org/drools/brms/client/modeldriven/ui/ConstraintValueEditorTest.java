package org.drools.brms.client.modeldriven.ui;

import junit.framework.TestCase;

public class ConstraintValueEditorTest extends TestCase {

    public void testSplit() {
        String[] res = ConstraintValueEditor.splitValue( "M=Male" );
        assertEquals("M", res[0]);
        assertEquals("Male", res[1]);
    }

}
