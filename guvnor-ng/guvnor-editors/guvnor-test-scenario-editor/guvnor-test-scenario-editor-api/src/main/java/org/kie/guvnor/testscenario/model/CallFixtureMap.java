package org.kie.guvnor.testscenario.model;

import java.util.HashMap;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CallFixtureMap
        extends HashMap<String, FixtureList>
        implements Fixture {

    private static final long serialVersionUID = 2610717943001697257L;

}
