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

package org.kie.guvnor.datamodel.backend.server;

import org.junit.Test;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.DataType;

import static org.junit.Assert.*;

public class DataModelGettersAndSettersTest {

    @Test
    public void testGettersAndSettersOnDeclaredModel() throws Exception {
        final DataModelOracle dmo = DataModelBuilder.newDataModelBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "sex",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final String[] getters = dmo.getFieldCompletions( FieldAccessorsAndMutators.ACCESSOR,
                                                          "Person" );
        assertEquals( 3,
                      getters.length );
        assertEquals( DataType.TYPE_THIS,
                      getters[ 0 ] );
        assertEquals( "age",
                      getters[ 1 ] );
        assertEquals( "sex",
                      getters[ 2 ] );

        final String[] setters = dmo.getFieldCompletions(
                FieldAccessorsAndMutators.MUTATOR,
                "Person" );
        assertEquals( 2,
                      setters.length );
        assertEquals( "age",
                      setters[ 0 ] );
        assertEquals( "sex",
                      setters[ 1 ] );
    }

    @Test
    public void testGettersAndSettersOnJavaClass() throws Exception {
        final DataModelOracle dmo = DataModelBuilder.newDataModelBuilder()
                .addClass( Person.class )
                .build();

        final String[] getters = dmo.getFieldCompletions( FieldAccessorsAndMutators.ACCESSOR,
                                                          "Person" );
        assertEquals( 2,
                      getters.length );
        assertEquals( DataType.TYPE_THIS,
                      getters[ 0 ] );
        assertEquals( "age",
                      getters[ 1 ] );

        final String[] setters = dmo.getFieldCompletions( FieldAccessorsAndMutators.MUTATOR,
                                                          "Person" );
        assertEquals( 1,
                      setters.length );
        assertEquals( "age",
                      setters[ 0 ] );

    }

    public static class Person {

        private int age;
        private String sex;

        public int getAge() {
            return age;
        }

        public void setAge( int age ) {
            this.age = age;
        }

    }
}
