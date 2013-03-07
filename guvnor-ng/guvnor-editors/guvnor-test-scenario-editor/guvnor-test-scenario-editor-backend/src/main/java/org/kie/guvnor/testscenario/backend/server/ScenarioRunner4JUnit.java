package org.kie.guvnor.testscenario.backend.server;

import org.drools.base.TypeResolver;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.kie.guvnor.testscenario.model.Scenario;
import org.kie.runtime.KieSession;

public class ScenarioRunner4JUnit extends Runner {

    // The description of the test suite
    private Description descr;
    // the actual scenario test to be executed
    private Scenario    scenario;

    public ScenarioRunner4JUnit( Scenario scenario ) throws InitializationError {
        this.scenario = scenario;
        this.descr = Description.createSuiteDescription( "Scenario test case" );
        this.descr.addChild( Description.createTestDescription( getClass(),
                                                                scenario.getName() ) );
    }

    @Override
    public Description getDescription() {
        return descr;
    }

    @Override
    public void run( RunNotifier notifier ) {
        TypeResolver typeResolver = null;
        ClassLoader classLoader = null;
        KieSession ksession = null;
        
        Description description = descr.getChildren().get( 0 );
        EachTestNotifier eachNotifier= new EachTestNotifier(notifier, description);
        try {
            eachNotifier.fireTestStarted();
            ScenarioRunner runner = new ScenarioRunner( typeResolver, 
                                                        classLoader, 
                                                        ksession );
            runner.run( scenario );
            if( ! scenario.wasSuccessful() ) {
                StringBuilder builder = new StringBuilder();
                for( String message : scenario.getFailureMessages() ) {
                    builder.append( message ).append( "\n" );
                }
                eachNotifier.addFailedAssumption( new AssumptionViolatedException( builder.toString() ) );
            }
        } catch( Throwable t ) {
            eachNotifier.addFailure( t );
        } finally {
            // has to always be called as per junit docs
            eachNotifier.fireTestFinished();
        }
    }

}
