package org.kie.guvnor.testscenario.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TestResultMessage {

    private String message;

    public TestResultMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
