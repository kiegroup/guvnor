package org.drools.ide.common.modeldriven;

import junit.framework.TestCase;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;

public class ActionFieldValueTest extends TestCase {

    public void testFormula() {
        ActionFieldValue val = new ActionFieldValue( "x",
                                                     "y",
                                                     SuggestionCompletionEngine.TYPE_NUMERIC );
        assertFalse( val.isFormula() );
        val = new ActionFieldValue( "x",
                                    "=y * 20",
                                    SuggestionCompletionEngine.TYPE_NUMERIC );
        assertTrue( val.isFormula() );
    }

}
