/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.models.testscenarios.backend.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.guvnor.models.testscenarios.shared.FactData;
import org.drools.guvnor.models.testscenarios.shared.FieldData;
import org.junit.Test;
import org.drools.guvnor.models.testscenarios.shared.Field;
import org.kie.runtime.KieSession;
import org.kie.runtime.rule.FactHandle;

public class GlobalFactPopulatorTest {

    @Test
    public void testWithGlobals() throws Exception {

        FactData global = new FactData( "Cheese",
                                        "c",
                                        Arrays.<Field> asList( new FieldData( "type",
                                                                              "cheddar" ) ),
                                        false );

        TypeResolver resolver = new ClassTypeResolver( new HashSet<String>(),
                                                       Thread.currentThread().getContextClassLoader() );
        resolver.addImport( "org.drools.guvnor.models.testscenarios.backend.Cheese" );

        KieSession ksession = mock( KieSession.class );
        Map<String, Object> populatedData = new HashMap<String, Object>();
        Map<String, Object> globalData = new HashMap<String, Object>();
        GlobalFactPopulator globalFactPopulator = new GlobalFactPopulator( populatedData,
                                                                           resolver,
                                                                           Thread.currentThread().getContextClassLoader(),
                                                                           global,
                                                                           globalData );

        globalFactPopulator.populate( ksession, 
                                      new HashMap<String, FactHandle>() );
        
        verify( ksession ).setGlobal( eq( global.getName() ), 
                                      any( Object.class )  );
        assertEquals( 1,
                      globalData.size() );
        assertEquals( 0,
                      populatedData.size() );

    }

}
