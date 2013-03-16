package org.kie.guvnor.testscenario.backend.server;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.kie.guvnor.testscenario.model.TestResultMessage;

import javax.enterprise.event.Event;

public class CustomJUnitRunNotifier
        extends RunNotifier {

    public CustomJUnitRunNotifier(final Event<TestResultMessage> testResultMessageEvent) {

        addListener(new RunListener() {

            public void testRunStarted(Description description) throws Exception {
                testResultMessageEvent.fire(new TestResultMessage(description.toString()));
            }
        }
        );
    }
}
