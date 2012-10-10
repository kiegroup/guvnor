package org.drools.guvnor.client.explorer.navigation.qa;

import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

public class VerifierPlace extends DefaultPlaceRequest {


    public VerifierPlace(String moduleUuid) {
        super("verifierPlace");
        addParameter("moduleUuid", moduleUuid);
    }

}
