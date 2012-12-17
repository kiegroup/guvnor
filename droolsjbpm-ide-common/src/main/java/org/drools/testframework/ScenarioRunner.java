/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testframework;

import static org.mvel2.MVEL.eval;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.drools.ClockType;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.ide.common.client.modeldriven.testing.ActivateRuleFlowGroup;
import org.drools.ide.common.client.modeldriven.testing.CallFieldValue;
import org.drools.ide.common.client.modeldriven.testing.CallMethod;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Expectation;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.RetractFact;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;
import org.drools.ide.common.server.util.ScenarioXMLPersistence;
import org.drools.rule.Package;
import org.drools.spi.Activation;
import org.drools.time.impl.PseudoClockScheduler;
import org.mvel2.MVEL;

/**
 * This actually runs the test scenarios.
 */
public class ScenarioRunner {

    private final Scenario                scenario;
    private final Map<String, Object>     populatedData = new HashMap<String, Object>();
    private final Map<String, Object>     globalData    = new HashMap<String, Object>();
    private final Map<String, FactHandle> factHandles   = new HashMap<String, FactHandle>();

    private final InternalWorkingMemory   workingMemory;
    private final TypeResolver            resolver;

    /**
     * This constructor is normally used by Guvnor for running tests on a users
     * request.
     * 
     * @param scenario
     *            The scenario to run.
     * @param resolver
     *            A populated type resolved to be used to resolve the types in
     *            the scenario.
     *            <p/>
     *            For info on how to invoke this, see
     *            ContentPackageAssemblerTest.testPackageWithRuleflow in
     *            guvnor-webapp This requires that the classloader for the
     *            thread context be set appropriately. The PackageBuilder can
     *            provide a suitable TypeResolver for a given package header,
     *            and the Package config can provide a classloader.
     */
    public ScenarioRunner(final Scenario scenario,
                          final TypeResolver resolver,
                          final InternalWorkingMemory wm) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.scenario = scenario;
        this.workingMemory = wm;
        this.resolver = resolver;
        runScenario();
    }

    /**
     * Use this constructor if you have a scenario in a file, for instance.
     * 
     * @throws ClassNotFoundException
     */
    public ScenarioRunner(String xml,
                          RuleBase ruleBase) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.scenario = ScenarioXMLPersistence.getInstance().unmarshal( xml );

        SessionConfiguration sessionConfiguration = new SessionConfiguration();
        sessionConfiguration.setClockType( ClockType.PSEUDO_CLOCK );

        this.workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession( sessionConfiguration,
                                                                                  null );

        ClassLoader classLoader = ((InternalRuleBase) ruleBase).getRootClassLoader();
        HashSet<String> imports = getImports( ruleBase.getPackages()[0] );

        this.resolver = new ClassTypeResolver( imports,
                                               classLoader );
        runScenario();
    }

    public HashSet<String> getImports(Package aPackage) {
        HashSet<String> imports = new HashSet<String>();
        imports.add( aPackage.getName() + ".*" );
        imports.addAll( aPackage.getImports().keySet() );

        return imports;
    }

    interface Populate {
        public void go() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;
    }

    private void runScenario() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        scenario.setLastRunResult( new Date() );
        //stub out any rules we don't want to have the consequences firing of.
        HashSet<String> ruleList = new HashSet<String>();
        ruleList.addAll( scenario.getRules() );

        TestingEventListener listener = null;

        List<Populate> toPopulate = new ArrayList<Populate>();

        for ( final FactData fact : scenario.getGlobals() ) {
            final Object factObject = eval( "new " + getTypeName( resolver,
                                                                  fact ) + "()" );
            toPopulate.add( new Populate() {
                public void go() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
                    populateFields( fact,
                                    factObject );
                }
            } );
            globalData.put( fact.getName(),
                            factObject );
            this.workingMemory.setGlobal( fact.getName(),
                                          factObject );
        }

        doPopulate( toPopulate );

        for ( Iterator<Fixture> iterator = scenario.getFixtures().iterator(); iterator.hasNext(); ) {
            Fixture fixture = iterator.next();

            if ( fixture instanceof FactData ) {
                //deal with facts and globals
                final FactData fact = (FactData) fixture;
                final Object factObject = (fact.isModify()) ? this.populatedData.get( fact.getName() ) : eval( "new " + getTypeName( resolver,
                                                                                                                                     fact ) + "()" );
                if ( fact.isModify() ) {
                    if ( !this.factHandles.containsKey( fact.getName() ) ) {
                        throw new IllegalArgumentException( "Was not a previously inserted fact. [" + fact.getName() + "]" );
                    }
                    toPopulate.add( new Populate() {
                        public void go() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
                            populateFields( fact,
                                            factObject );
                            workingMemory.update( factHandles.get( fact.getName() ),
                                                  factObject );
                        }
                    } );
                } else /* a new one */{
                    populatedData.put( fact.getName(),
                                       factObject );
                    toPopulate.add( new Populate() {
                        public void go() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
                            populateFields( fact,
                                            factObject );
                            factHandles.put( fact.getName(),
                                             workingMemory.insert( factObject ) );
                        }
                    } );
                }
            } else if ( fixture instanceof RetractFact ) {
                RetractFact retractFact = (RetractFact) fixture;
                this.workingMemory.retract( this.factHandles.get( retractFact.getName() ) );
                this.populatedData.remove( retractFact.getName() );
            } else if ( fixture instanceof CallMethod ) {
                CallMethod aCall = (CallMethod) (fixture);
                Object targetInstance = populatedData.get( aCall.getVariable() );
                executeMethodOnObject( aCall,
                                       targetInstance );
            } else if ( fixture instanceof ActivateRuleFlowGroup ) {
                String ruleFlowGroupName = ((ActivateRuleFlowGroup) fixture).getName();
                workingMemory.getAgenda().getRuleFlowGroup( ruleFlowGroupName ).setAutoDeactivate( false );
                workingMemory.getAgenda().activateRuleFlowGroup( ruleFlowGroupName );
            } else if ( fixture instanceof ExecutionTrace ) {
                doPopulate( toPopulate );
                ExecutionTrace executionTrace = (ExecutionTrace) fixture;
                //create the listener to trace rules

                if ( listener != null ) this.workingMemory.removeEventListener( listener ); //remove the old
                listener = new TestingEventListener();

                this.workingMemory.addEventListener( listener );

                //set up the time machine
                applyTimeMachine( this.workingMemory,
                                  executionTrace );

                //love you
                long time = System.currentTimeMillis();
                this.workingMemory.fireAllRules( listener.getAgendaFilter( ruleList,
                                                                           scenario.isInclusive() ),
                                                                           getMaxFireCount());
                executionTrace.setExecutionTimeResult( System.currentTimeMillis() - time );
                executionTrace.setNumberOfRulesFired( listener.totalFires );
                executionTrace.setRulesFired( listener.getRulesFiredSummary() );

            } else if ( fixture instanceof Expectation ) {
                doPopulate( toPopulate );
                Expectation assertion = (Expectation) fixture;
                if ( assertion instanceof VerifyFact ) {
                    verify( (VerifyFact) assertion );
                } else if ( assertion instanceof VerifyRuleFired ) {
                    verify( (VerifyRuleFired) assertion,
                            (listener.firingCounts != null) ? listener.firingCounts : new HashMap<String, Integer>() );
                }
            } else {
                throw new IllegalArgumentException( "Not sure what to do with " + fixture );
            }

        }

        doPopulate( toPopulate );
    }

    private int getMaxFireCount() {
        String property = System.getProperty("guvnor.testscenario.maxrulefirings");
        if (property == null) {
            return scenario.getMaxRuleFirings();
        } else {
            return Integer.parseInt(property);
        }
    }

    private void doPopulate(List<Populate> toPopulate) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for ( Populate p : toPopulate ) {
            p.go();
        }
        toPopulate.clear();
    }

    private String getTypeName(TypeResolver resolver,
                               FactData fact) throws ClassNotFoundException {

        String fullName = resolver.getFullTypeName( fact.getType() );
        if ( fullName.equals( "java.util.List" ) || fullName.equals( "java.util.Collection" ) ) {
            return "java.util.ArrayList";
        } else {
            return fullName;
        }
    }

    private void applyTimeMachine(final InternalWorkingMemory wm,
                                  ExecutionTrace executionTrace) {
        long targetTime = 0;
        if ( executionTrace.getScenarioSimulatedDate() != null ) {
            targetTime = executionTrace.getScenarioSimulatedDate().getTime();
        } else {
            targetTime = new Date().getTime();
        }

        long currentTime = wm.getSessionClock().getCurrentTime();
        ((PseudoClockScheduler) wm.getSessionClock()).advanceTime( targetTime - currentTime,
                                                                   TimeUnit.MILLISECONDS );
    }

    void verify(VerifyRuleFired assertion,
                Map<String, Integer> firingCounts) {

        assertion.setActualResult( firingCounts.containsKey( assertion.getRuleName() ) ? firingCounts.get( assertion.getRuleName() ) : 0 );
        if ( assertion.getExpectedFire() != null ) {
            if ( assertion.getExpectedFire() ) {
                if ( assertion.getActualResult() > 0 ) {
                    assertion.setSuccessResult( true );
                    assertion.setExplanation( "Rule [" + assertion.getRuleName() + "] was actived " + assertion.getActualResult() + " times." );
                } else {
                    assertion.setSuccessResult( false );
                    assertion.setExplanation( "Rule [" + assertion.getRuleName() + "] was not activated. Expected it to be activated." );
                }
            } else {
                if ( assertion.getActualResult() == 0 ) {
                    assertion.setSuccessResult( true );
                    assertion.setExplanation( "Rule [" + assertion.getRuleName() + "] was not activated." );
                } else {
                    assertion.setSuccessResult( false );
                    assertion.setExplanation( "Rule [" + assertion.getRuleName() + "] was activated " + assertion.getActualResult() + " times, but expected none." );
                }
            }
        }

        if ( assertion.getExpectedCount() != null ) {
            if ( assertion.getActualResult().equals( assertion.getExpectedCount() ) ) {
                assertion.setSuccessResult( true );
                assertion.setExplanation( "Rule [" + assertion.getRuleName() + "] activated " + assertion.getActualResult() + " times." );
            } else {
                assertion.setSuccessResult( false );
                assertion.setExplanation( "Rule [" + assertion.getRuleName() + "] activated " + assertion.getActualResult() + " times. Expected " + assertion.getExpectedCount() + " times." );
            }
        }
    }

    void verify(VerifyFact value) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        //Clear existing results
        for ( VerifyField vf : value.getFieldValues() ) {
            vf.setSuccessResult( null );
            vf.setExplanation( "Fact of type [" + value.getName() + "] was not found in the results." );
        }
        
        if ( !value.anonymous ) {
            Object factObject = this.populatedData.get( value.getName() );
            if ( factObject == null ) factObject = this.globalData.get( value.getName() );
            FactFieldValueVerifier fieldVerifier = new FactFieldValueVerifier( populatedData,
                                                                               value.getName(),
                                                                               factObject,
                                                                               resolver );
            fieldVerifier.checkFields( value.getFieldValues() );
        } else {
            Iterator obs = this.workingMemory.iterateObjects();
            while ( obs.hasNext() ) {
                Object factObject = obs.next();
                if ( factObject.getClass().getSimpleName().equals( value.getName() ) ) {
                    FactFieldValueVerifier fieldVerifier = new FactFieldValueVerifier( populatedData,
                                                                                       value.getName(),
                                                                                       factObject,
                                                                                       resolver );
                    fieldVerifier.checkFields( value.getFieldValues() );
                    if ( value.wasSuccessful() ) return;
                }
            }
            for ( VerifyField vfl : value.getFieldValues() ) {
                if ( vfl.getSuccessResult() == null ) {
                    vfl.setSuccessResult( Boolean.FALSE );
                    vfl.setActualResult( "No match" );
                }
            }
        }
    }

    Object populateFields(FactData fact,
                          Object factObject) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        for ( int i = 0; i < fact.getFieldData().size(); i++ ) {
            FieldData field = (FieldData) fact.getFieldData().get( i );
            Object val = null;

            if ( field.getValue() != null ) {

                if ( field.getValue().startsWith( "=" ) ) {
                    // eval the val into existence
                    val = eval( field.getValue().substring( 1 ),
                                populatedData );
                } else if ( field.getNature() == FieldData.TYPE_ENUM ) {
                    // The string representation of a java enum value is a
                    // format like CheeseType.CHEDDAR
                    String valueOfEnum = field.getValue();
                    String fullName = null;
                    if ( field.getValue().indexOf( "." ) != -1 ) {
                        String classNameOfEnum = field.getValue().substring( 0,
                                                                             field.getValue().lastIndexOf( "." ) );
                        valueOfEnum = field.getValue().substring( field.getValue().lastIndexOf( "." ) + 1 );
                        try {
                            //This is a Java enum type if the type can be resolved by ClassTypeResolver
                            //Revisit: Better way to determine java enum type or Guvnor enum type.
                            fullName = resolver.getFullTypeName( classNameOfEnum );
                            if ( fullName != null && !"".equals( fullName ) ) {
                                valueOfEnum = fullName + "." + valueOfEnum;
                            }
                            val = eval( valueOfEnum );
                        } catch ( ClassNotFoundException e ) {
                            // This is a Guvnor enum type
                            fullName = classNameOfEnum;
                            if ( fullName != null && !"".equals( fullName ) ) {
                                valueOfEnum = fullName + "." + valueOfEnum;
                            }
                            val = valueOfEnum;
                        }
                    } else {
                        val = valueOfEnum;
                    }
                } else if (FieldTypeResolver.isDate(field.getName(), factObject)) {
                    val = DateObjectFactory.getObject(FieldTypeResolver.getFieldType(field.getName(), factObject), field.getValue());
                }else {
                    val = field.getValue();
                }

                Map<String, Object> vars = new HashMap<String, Object>();
                vars.putAll( populatedData );
                vars.put( "__val__",
                          val );
                vars.put( "__fact__",
                          factObject );
                eval( "__fact__." + field.getName() + " = __val__",
                        vars );
            }
        }
        return factObject;
    }

    Object executeMethodOnObject(CallMethod fact,
                                 Object factObject) {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put( "__fact__",
                  factObject );
        String methodName = "__fact__." + fact.getMethodName() + "(";
        for ( int i = 0; i < fact.getCallFieldValues().length; i++ ) {
            CallFieldValue field = (CallFieldValue) fact.getCallFieldValues()[i];
            Object val;
            if ( field.value != null && !field.value.equals( "" ) ) {
                if ( field.value.startsWith( "=" ) ) {
                    // eval the val into existence
                    val = populatedData.get( field.value.substring( 1 ) );
                } else {
                    val = field.value;
                }
                vars.put( "__val" + i + "__",
                          val );
                methodName = methodName + "__val" + i + "__";
                if ( i < fact.getCallFieldValues().length - 1 ) {
                    methodName = methodName + ",";
                }

            }
        }
        methodName = methodName + ")";
        eval( methodName,
                vars );
        return factObject;
    }

    /**
     * True if the scenario was run with 100% success.
     */
    public boolean wasSuccess() {
        return this.scenario.wasSuccessful();
    }

    Scenario getScenario() {
        return scenario;
    }

    Map<String, Object> getPopulatedData() {
        return populatedData;
    }

    Map<String, Object> getGlobalData() {
        return globalData;
    }

    Map<String, FactHandle> getFactHandles() {
        return factHandles;
    }

    InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    TypeResolver getResolver() {
        return resolver;
    }

}
