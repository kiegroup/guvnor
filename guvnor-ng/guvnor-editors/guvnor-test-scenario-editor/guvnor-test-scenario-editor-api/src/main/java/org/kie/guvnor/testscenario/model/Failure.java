package org.kie.guvnor.testscenario.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Failure {

    private String message;

    public Failure() {

    }

    public Failure(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
