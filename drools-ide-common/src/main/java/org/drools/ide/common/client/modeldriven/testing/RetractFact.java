package org.drools.ide.common.client.modeldriven.testing;


/**
 * Retract a named fact.
 * @author Michael Neale
 *
 */
public class RetractFact implements Fixture {

    public RetractFact() {}
    public RetractFact(String name) {
        this.name = name;
    }

    public String name;


}
