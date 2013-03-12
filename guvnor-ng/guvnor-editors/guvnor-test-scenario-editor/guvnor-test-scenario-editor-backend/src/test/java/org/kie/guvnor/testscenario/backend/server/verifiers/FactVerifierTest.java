/*
* Copyright 2011 JBoss Inc
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
package org.kie.guvnor.testscenario.backend.server.verifiers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.drools.base.TypeResolver;
import org.drools.guvnor.models.testscenarios.shared.VerifyFact;
import org.drools.guvnor.models.testscenarios.shared.VerifyField;
import org.junit.Before;
import org.junit.Test;
import org.kie.guvnor.testscenario.backend.server.Cheese;
import org.kie.runtime.KieSession;

public class FactVerifierTest {

    private KieSession ksession;

    @Before
    public void setUp() throws Exception {
        ksession = mock( KieSession.class );
    }

    @Test
    public void testVerifyAnonymousFacts() throws Exception {
        TypeResolver typeResolver = mock( TypeResolver.class );
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        FactVerifier factVerifier = new FactVerifier( new HashMap<String, Object>(), typeResolver, classLoader, ksession, new HashMap<String, Object>() );

        Cheese c = new Cheese();
        c.setPrice( 42 );
        c.setType( "stilton" );

        // configure the mock to return the value
        when( ksession.getObjects() ).thenReturn( Collections.singleton( (Object) c ) );

        VerifyFact vf = new VerifyFact( "Cheese",
                                        new ArrayList<VerifyField>(),
                                        true );
        vf.getFieldValues().add( new VerifyField( "price",
                                                  "42",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "stilton",
                                                  "==" ) );

        factVerifier.verify( vf );
        assertTrue( vf.wasSuccessful() );

        vf = new VerifyFact( "Person",
                             new ArrayList<VerifyField>(),
                             true );
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );

        factVerifier.verify( vf );
        assertFalse( vf.wasSuccessful() );

        vf = new VerifyFact( "Cheese",
                             new ArrayList<VerifyField>(),
                             true );
        vf.getFieldValues().add( new VerifyField( "price",
                                                  "43",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "stilton",
                                                  "==" ) );

        factVerifier.verify( vf );
        assertFalse( vf.wasSuccessful() );
        assertEquals( Boolean.FALSE,
                      vf.getFieldValues().get( 0 ).getSuccessResult() );

        vf = new VerifyFact( "Cell",
                             new ArrayList<VerifyField>(),
                             true );
        vf.getFieldValues().add( new VerifyField( "value",
                                                  "43",
                                                  "==" ) );

        factVerifier.verify( vf );
        assertFalse( vf.wasSuccessful() );
        assertEquals( Boolean.FALSE,
                      vf.getFieldValues().get( 0 ).getSuccessResult() );

    }

    @Test
    public void testVerifyFactsWithOperator() throws Exception {
        TypeResolver typeResolver = mock( TypeResolver.class );
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Cheese f1 = new Cheese( "cheddar",
                                42 );
        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put( "f1", f1 );
        
        // configure the mock to return the value
        when( ksession.getObjects() ).thenReturn( Collections.singleton( (Object) f1 ) );

        FactVerifier factVerifier = new FactVerifier( populatedData, typeResolver, classLoader, ksession, new HashMap<String, Object>() );

        // test all true
        VerifyFact vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "cheddar",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "price",
                                                  "4777",
                                                  "!=" ) );

        factVerifier.verify( vf );

        for ( int i = 0; i < vf.getFieldValues().size(); i++ ) {
            assertTrue( vf.getFieldValues().get( i ).getSuccessResult() );
        }

        vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "cheddar",
                                                  "!=" ) );
        factVerifier.verify( vf );
        assertFalse( vf.getFieldValues().get( 0 ).getSuccessResult() );

    }

    @Test
    public void testVerifyFactsWithExpression() throws Exception {
        TypeResolver typeResolver = mock( TypeResolver.class );
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Cheese f1 = new Cheese( "cheddar",
                                42 );
        f1.setPrice( 42 );

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put( "f1", f1 );
        
        // configure the mock to return the value
        when( ksession.getObjects() ).thenReturn( Collections.singleton( (Object) f1 ) );

        FactVerifier factVerifier = new FactVerifier( populatedData, typeResolver, classLoader, ksession, new HashMap<String, Object>() );

        // test all true
        VerifyFact vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "price",
                                                  "= 40 + 2",
                                                  "==" ) );
        factVerifier.verify( vf );

        assertTrue( vf.getFieldValues().get( 0 ).getSuccessResult() );
    }

    @Test
    public void testVerifyFactExplanation() throws Exception {
        Cheese f1 = new Cheese();
        f1.setType( null );

        TypeResolver typeResolver = mock( TypeResolver.class );
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put( "f1", f1 );
        
        // configure the mock to return the value
        when( ksession.getObjects() ).thenReturn( Collections.singleton( (Object) f1 ) );

        FactVerifier factVerifier = new FactVerifier( populatedData, typeResolver, classLoader, ksession, new HashMap<String, Object>() );

        VerifyFact vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "boo",
                                                  "!=" ) );

        factVerifier.verify( vf );
        VerifyField vfl = vf.getFieldValues().get( 0 );
        assertEquals( "[f1] field [type] was not [boo].",
                      vfl.getExplanation() );

    }

    @Test
    public void testVerifyFieldAndActualIsNull() throws Exception {
        Cheese f1 = new Cheese();
        f1.setType( null );

        TypeResolver typeResolver = mock( TypeResolver.class );
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put( "f1", f1 );

        // configure the mock to return the value
        when( ksession.getObjects() ).thenReturn( Collections.singleton( (Object) f1 ) );

        FactVerifier factVerifier = new FactVerifier( populatedData, typeResolver, classLoader, ksession, new HashMap<String, Object>() );

        VerifyFact vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "boo",
                                                  "==" ) );

        factVerifier.verify( vf );
        VerifyField vfl = vf.getFieldValues().get( 0 );

        assertEquals( "[f1] field [type] was [] expected [boo].",
                      vfl.getExplanation() );
        assertEquals( "boo",
                      vfl.getExpected() );
        assertEquals( "",
                      vfl.getActualResult() );

    }

}
