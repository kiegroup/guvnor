package org.drools.brms.server.contenthandler;

import junit.framework.TestCase;

public class DRLFileContentHandlerTest extends TestCase {

    public void testSniffDRLType() throws Exception {
        //in this case we have package, and N rules
        String classic = "package foobar \n rule boo \n when \n then\n end \n rule boo2 \n when \n then\n end";
        
        //in this case we just have rules
        String moreRuleClassic = "\nrule bar \n when \n then \n end\nrule x \n when \n then \n end ";
        
        //in this case we just have a single rule
        String newRule = "agenda-group 'x' \n when \n then \n";
        
        String moreSingle = "rule foo when then end";
        
        String moreNewRule = "agenda-group 'x' \n when end.bar \n then rule.end.bar";
        
        DRLFileContentHandler h = new DRLFileContentHandler();
        
        assertTrue(h.isStandAloneRule( newRule ));
        assertFalse(h.isStandAloneRule( moreRuleClassic ));
        assertFalse(h.isStandAloneRule( classic ));
        assertFalse(h.isStandAloneRule( moreSingle ));
        assertTrue(h.isStandAloneRule( moreNewRule ));
    }    
    
}
