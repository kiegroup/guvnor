package org.drools.brms.client.rpc.brxml;

/**
 * This is used when asserting a new fact.
 * @author Michael Neale
 *
 */
public class ActionAssertFact
    extends
    ActionFieldList {

    public String factType;
    
    public ActionAssertFact(String type) {
        this.factType = type;
    }
    
    public ActionAssertFact() {}
    

}
