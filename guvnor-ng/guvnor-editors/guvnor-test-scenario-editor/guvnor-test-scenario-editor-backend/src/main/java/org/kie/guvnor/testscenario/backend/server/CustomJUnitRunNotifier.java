package org.kie.guvnor.testscenario.backend.server;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.kie.guvnor.testscenario.model.TestResultMessage;

import javax.enterprise.event.Event;
import java.util.ArrayList;
import java.util.List;

public class CustomJUnitRunNotifier
        extends RunNotifier {

    public CustomJUnitRunNotifier(final Event<TestResultMessage> testResultMessageEvent) {

        addListener(new RunListener() {

            public void testRunFinished(Result result) throws Exception {
                testResultMessageEvent.fire(new TestResultMessage(
                        result.wasSuccessful(),
                        result.getRunCount(),
                        result.getFailureCount(),
                        getFailures(result.getFailures())));
            }
        });
    }

    private List<org.kie.guvnor.testscenario.model.Failure> getFailures(List<Failure> failures) {
        ArrayList<org.kie.guvnor.testscenario.model.Failure> result = new ArrayList<org.kie.guvnor.testscenario.model.Failure>();

        for (Failure failure : failures) {
            result.add(new org.kie.guvnor.testscenario.model.Failure(
                    failure.getDescription().getDisplayName(),
                    failure.getMessage()));
        }

        return result;
    }
}
