package org.drools.guvnor.client.modeldriven.ui;

import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;

import junit.framework.TestCase;

public class ConstraintValueEditorTest extends TestCase {

    public void testSplit() {
        String[] res = ConstraintValueEditorHelper.splitValue( "M=Male" );
        assertEquals( "M",
                      res[0] );
        assertEquals( "Male",
                      res[1] );
    }

}
