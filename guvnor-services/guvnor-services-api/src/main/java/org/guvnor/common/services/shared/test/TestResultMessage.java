package org.guvnor.common.services.shared.test;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TestResultMessage {

    private boolean wasSuccessful;
    private int runCount;
    private int failureCount;
    private List<Failure> failures;

    public TestResultMessage() {
    }

    public TestResultMessage(boolean wasSuccessful, int runCount, int failureCount, List<Failure> failures) {
        this.wasSuccessful = wasSuccessful;
        this.runCount = runCount;
        this.failureCount = failureCount;
        this.failures = failures;
    }

    public boolean wasSuccessful() {
        return wasSuccessful;
    }

    public int getRunCount() {
        return runCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public List<Failure> getFailures() {
        return failures;
    }

    public List<String> getResultStrings() { 
        List<String> result = new ArrayList<String>(3 + (failures == null ? 0 : failures.size()));
        result.add( "wasSuccessful: " + this.wasSuccessful);
        result.add( "RunCount: " + this.runCount);
        result.add( "FailureCount: " + this.failureCount );
        if( this.failures != null ) { 
            for ( Failure failure : this.failures ) { 
                result.add( "Failure: " + failure.getMessage() );
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "TestResultMessage{" +
                "wasSuccessful=" + wasSuccessful +
                ", runCount=" + runCount +
                ", failureCount=" + failureCount +
                ", failures=" + failures +
                '}';
    }
}
