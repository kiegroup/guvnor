package org.guvnor.common.services.shared.test;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TestResultMessage {

    private String identifier;
    private int    runCount;
    private Long   runTime;
    private List<Failure> failures = new ArrayList<Failure>();

    public TestResultMessage() {
    }

    public TestResultMessage(String identifier,
                             int runCount,
                             long runTime,
                             List<Failure> failures) {
        this.identifier = identifier;
        this.runCount = runCount;
        this.runTime = runTime;
        this.failures = failures;
    }

    public boolean wasSuccessful() {
        return failures.isEmpty();
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getRunCount() {
        return runCount;
    }

    public long getRunTime() {
        return runTime;
    }

    public List<Failure> getFailures() {
        return failures;
    }

    public List<String> getResultStrings() {
        List<String> result = new ArrayList<String>(3 + (failures == null ? 0 : failures.size()));
        result.add("RunCount: " + this.runCount);
        if (this.failures != null) {
            for (Failure failure : this.failures) {
                result.add("Failure: " + failure.getMessage());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "TestResultMessage{" +
               "identifier=" + identifier +
               ", runCount=" + runCount +
               ", failures=" + failures +
               '}';
    }
}
