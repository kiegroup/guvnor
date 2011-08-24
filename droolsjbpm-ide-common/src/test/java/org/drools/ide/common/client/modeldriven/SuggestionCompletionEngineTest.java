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

package org.drools.ide.common.client.modeldriven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;

import org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.server.rules.SuggestionCompletionLoader;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.junit.Test;

public class SuggestionCompletionEngineTest {

    @Test
    public void testNestedImports() {
        String pkg = "package org.test\n import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngineTest.NestedClass";

        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine engine = loader.getSuggestionEngine( pkg,
                                                                        new ArrayList<JarInputStream>(),
                                                                        new ArrayList<DSLTokenizedMappingFile>() );

        assertEquals( "String",
                      engine.getFieldType( "SuggestionCompletionEngineTest$NestedClass",
                                           "name" ) );
    }

    @Test
    public void testStringNonNumeric() {
        String pkg = "package org.test\n import org.drools.ide.common.client.modeldriven.Alert";

        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine engine = loader.getSuggestionEngine( pkg,
                                                                        new ArrayList<JarInputStream>(),
                                                                        new ArrayList<DSLTokenizedMappingFile>() );

        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      engine.getFieldType( "Alert",
                                           "message" ) );

    }

    @Test
    public void testDataEnums() {
        String pkg = "package org.test\n import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngineTest.NestedClass";

        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        List<String> enums = new ArrayList<String>();

        enums.add( "'Person.age' : [42, 43] \n 'Person.sex' : ['M', 'F']" );
        enums.add( "'Driver.sex' : ['M', 'F']" );

        SuggestionCompletionEngine engine = loader.getSuggestionEngine( pkg,
                                                                        new ArrayList<JarInputStream>(),
                                                                        new ArrayList<DSLTokenizedMappingFile>(),
                                                                        enums );
        assertEquals( "String",
                      engine.getFieldType( "SuggestionCompletionEngineTest$NestedClass",
                                           "name" ) );

        assertEquals( 3,
                      engine.getDataEnumListsSize() );
        String[] items = engine.getDataEnumList( "Person.age" );
        assertEquals( 2,
                      items.length );
        assertEquals( "42",
                      items[0] );
        assertEquals( "43",
                      items[1] );

        items = engine.getEnums( new FactPattern( "Person" ),
                                 "age" ).fixedList;
        assertEquals( 2,
                      items.length );
        assertEquals( "42",
                      items[0] );
        assertEquals( "43",
                      items[1] );

        assertNull( engine.getEnums( new FactPattern( "Nothing" ),
                                     "age" ) );

        assertEquals( null,
                      engine.getEnums( new FactPattern( "Something" ),
                                       "else" ) );

    }

    @Test
    public void testDataEnums3() {
        String pkg = "package org.test\n import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngineTest.NestedClass";

        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        List<String> enums = new ArrayList<String>();

        enums.add( "'Fact.f1' : ['a1', 'a2'] \n 'Fact.f2' : ['def1', 'def2', 'def3'] \n 'Fact.f2[f1=a2]' : ['c1', 'c2']" );

        SuggestionCompletionEngine engine = loader.getSuggestionEngine( pkg,
                                                                        new ArrayList<JarInputStream>(),
                                                                        new ArrayList<DSLTokenizedMappingFile>(),
                                                                        enums );
        assertEquals( "String",
                      engine.getFieldType( "SuggestionCompletionEngineTest$NestedClass",
                                           "name" ) );

        FactPattern pat = new FactPattern( "Fact" );
        SingleFieldConstraint f1 = new SingleFieldConstraint( "f1" );
        f1.setValue( "a1" );
        pat.addConstraint( f1 );
        pat.addConstraint( new SingleFieldConstraint( "f2" ) );

        DropDownData data = engine.getEnums( pat,
                                             "f2" );

        assertNotNull( data );
        assertEquals( 3,
                      data.fixedList.length );

    }

    @Test
    public void testDataEnums2() {
        String pkg = "package org.test\n import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngineTest.Fact";

        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        List<String> enums = new ArrayList<String>();

        enums.add( "'Fact.field1' : ['val1', 'val2'], 'Fact.field2' : ['val3', 'val4'], 'Fact.field2[field1=val1]' : ['f1val1a', 'f1val1b'], 'Fact.field2[field1=val2]' : ['f1val2a', 'f1val2b']" );

        SuggestionCompletionEngine engine = loader.getSuggestionEngine( pkg,
                                                                        new ArrayList<JarInputStream>(),
                                                                        new ArrayList<DSLTokenizedMappingFile>(),
                                                                        enums );
        assertEquals( "String",
                      engine.getFieldType( "SuggestionCompletionEngineTest$Fact",
                                           "field1" ) );
        assertEquals( "String",
                      engine.getFieldType( "SuggestionCompletionEngineTest$Fact",
                                           "field2" ) );

        assertEquals( 4,
                      engine.getDataEnumListsSize() );

        String[] items = engine.getDataEnumList( "Fact.field2" );
        assertEquals( 2,
                      items.length );
        assertEquals( "val3",
                      items[0] );
        assertEquals( "val4",
                      items[1] );

        FactPattern pat = new FactPattern( "Fact" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "field2" );
        pat.addConstraint( sfc );
        items = engine.getEnums( pat,
                                 "field2" ).fixedList;
        assertEquals( 2,
                      items.length );
        assertEquals( "val3",
                      items[0] );
        assertEquals( "val4",
                      items[1] );

        items = engine.getDataEnumList( "Fact.field1" );
        assertEquals( 2,
                      items.length );
        assertEquals( "val1",
                      items[0] );
        assertEquals( "val2",
                      items[1] );

        items = engine.getEnums( new FactPattern( "Fact" ),
                                 "field1" ).fixedList;
        assertEquals( 2,
                      items.length );
        assertEquals( "val1",
                      items[0] );
        assertEquals( "val2",
                      items[1] );

    }

    @Test
    public void testCompletions() {

        final SuggestionCompletionEngine com = new SuggestionCompletionEngine();

        com.setFactTypes( new String[]{"Person", "Vehicle"} );

        com.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "Person",
                     new ModelField[]{
                             new ModelField( "age",
                                             Integer.class.getName(),
                                             FIELD_CLASS_TYPE.REGULAR_CLASS,
                                             SuggestionCompletionEngine.TYPE_NUMERIC ),
                             new ModelField( "rank",
                                             Integer.class.getName(),
                                             FIELD_CLASS_TYPE.REGULAR_CLASS,
                                             SuggestionCompletionEngine.TYPE_COMPARABLE ),
                             new ModelField( "name",
                                             String.class.getName(),
                                             FIELD_CLASS_TYPE.REGULAR_CLASS,
                                             SuggestionCompletionEngine.TYPE_STRING )
                } );

                put( "Vehicle",
                     new ModelField[]{
                             new ModelField( "make",
                                             String.class.getName(),
                                             FIELD_CLASS_TYPE.REGULAR_CLASS,
                                             SuggestionCompletionEngine.TYPE_STRING ),
                             new ModelField( "type",
                                             String.class.getName(),
                                             FIELD_CLASS_TYPE.REGULAR_CLASS,
                                             SuggestionCompletionEngine.TYPE_STRING )
                } );
            }
        } );

        com.setGlobalVariables( new HashMap<String, String>() {
            {
                put( "bar",
                     "Person" );
                put( "baz",
                     "Vehicle" );
            }
        } );

        String[] c = com.getConditionalElements();
        assertEquals( "not",
                      c[0] );
        assertEquals( "exists",
                      c[1] );
        assertEquals( "or",
                      c[2] );

        c = com.getFactTypes();
        assertEquals( 2,
                      c.length );
        assertContains( "Person",
                        c );
        assertContains( "Vehicle",
                        c );

        c = com.getFieldCompletions( "Person" );
        assertEquals( "age",
                      c[0] );
        assertEquals( "rank",
                      c[1] );
        assertEquals( "name",
                      c[2] );

        c = com.getFieldCompletions( "Vehicle" );
        assertEquals( "type",
                      c[1] );
        assertEquals( "make",
                      c[0] );

        c = com.getOperatorCompletions( "Person",
                                        "name" );
        assertEquals( 6,
                      c.length );
        assertEquals( "==",
                      c[0] );
        assertEquals( "!=",
                      c[1] );
        assertEquals( "matches",
                      c[2] );

        c = com.getOperatorCompletions( "Person",
                                        "age" );
        assertEquals( 8,
                      c.length );
        assertEquals( c[0],
                      "==" );
        assertEquals( c[1],
                      "!=" );
        assertEquals( c[2],
                      "<" );
        assertEquals( c[3],
                      ">" );

        c = com.getOperatorCompletions( "Person",
                                        "rank" );
        assertEquals( 8,
                      c.length );
        assertEquals( c[0],
                      "==" );
        assertEquals( c[1],
                      "!=" );
        assertEquals( c[2],
                      "<" );
        assertEquals( c[3],
                      ">" );

        c = com.getConnectiveOperatorCompletions( "Vehicle",
                                                  "make" );
        assertEquals( 5,
                      c.length );
        assertEquals( "|| ==",
                      c[0] );

        c = com.getGlobalVariables();
        assertEquals( 2,
                      c.length );
        assertEquals( "baz",
                      c[0] );
        assertEquals( "bar",
                      c[1] );

        c = com.getFieldCompletionsForGlobalVariable( "bar" );
        assertEquals( 3,
                      c.length );
        assertEquals( "age",
                      c[0] );
        assertEquals( "rank",
                      c[1] );
        assertEquals( "name",
                      c[2] );

        c = com.getFieldCompletionsForGlobalVariable( "baz" );
        assertEquals( 2,
                      c.length );
        assertEquals( "make",
                      c[0] );
        assertEquals( "type",
                      c[1] );

        //check that it has default operators for general objects
        c = com.getOperatorCompletions( "Person",
                                        "wankle" );
        assertEquals( 4,
                      c.length );

        assertEquals( "Numeric",
                      com.getFieldType( "Person",
                                        "age" ) );

    }

    @Test
    @SuppressWarnings("serial")
    public void testCEPCompletions() {

        final SuggestionCompletionEngine com = new SuggestionCompletionEngine();

        com.setFactTypes( new String[]{"NotAnEvent", "AnEvent"} );

        com.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "NotAnEvent",
                     new ModelField[]{
                             new ModelField( "dateField",
                                             Date.class.getName(),
                                             FIELD_CLASS_TYPE.REGULAR_CLASS,
                                             SuggestionCompletionEngine.TYPE_DATE )
                } );

                put( "AnEvent",
                     new ModelField[]{
                             new ModelField( "this",
                                             Object.class.getName(),
                                             FIELD_CLASS_TYPE.REGULAR_CLASS,
                                             SuggestionCompletionEngine.TYPE_THIS ),
                             new ModelField( "dateField",
                                             Date.class.getName(),
                                             FIELD_CLASS_TYPE.REGULAR_CLASS,
                                             SuggestionCompletionEngine.TYPE_DATE )
                } );

            }
        } );

        //Set-up annotation for Type 'AnEvent'
        Map<String, List<ModelAnnotation>> annotations = new HashMap<String, List<ModelAnnotation>>();
        List<ModelAnnotation> eventAnnotations = new ArrayList<ModelAnnotation>();
        ModelAnnotation ma = new ModelAnnotation();
        ma.setAnnotationName( "role" );
        Map<String, String> mav = new HashMap<String, String>();
        mav.put( "value",
                 "event" );
        ma.setAnnotationValues( mav );
        eventAnnotations.add( ma );
        annotations.put( "AnEvent",
                         eventAnnotations );
        com.setAnnotationsForTypes( annotations );

        //Check completions
        String[] c = com.getFactTypes();
        assertEquals( 2,
                      c.length );
        assertContains( "NotAnEvent",
                        c );
        assertContains( "AnEvent",
                        c );

        c = com.getOperatorCompletions( "NotAnEvent",
                                        "dateField" );
        assertEquals( 11,
                      c.length );
        assertEquals( c[0],
                      "==" );
        assertEquals( c[1],
                      "!=" );
        assertEquals( c[2],
                      "<" );
        assertEquals( c[3],
                      ">" );
        assertEquals( c[4],
                      "<=" );
        assertEquals( c[5],
                      ">=" );
        assertEquals( c[6],
                      "== null" );
        assertEquals( c[7],
                      "!= null" );
        assertEquals( c[8],
                      "after" );
        assertEquals( c[9],
                      "before" );
        assertEquals( c[10],
                      "coincides" );

        c = com.getOperatorCompletions( "AnEvent",
                                        "this" );
        assertEquals( 17,
                      c.length );
        assertEquals( c[0],
                      "==" );
        assertEquals( c[1],
                      "!=" );
        assertEquals( c[2],
                      "== null" );
        assertEquals( c[3],
                      "!= null" );
        assertEquals( c[4],
                      "after" );
        assertEquals( c[5],
                      "before" );
        assertEquals( c[6],
                      "coincides" );
        assertEquals( c[7],
                      "during" );
        assertEquals( c[8],
                      "finishes" );
        assertEquals( c[9],
                      "finishedby" );
        assertEquals( c[10],
                      "includes" );
        assertEquals( c[11],
                      "meets" );
        assertEquals( c[12],
                      "metby" );
        assertEquals( c[13],
                      "overlaps" );
        assertEquals( c[14],
                      "overlappedby" );
        assertEquals( c[15],
                      "starts" );
        assertEquals( c[16],
                      "startedby" );

    }

    @Test
    @SuppressWarnings("serial")
    public void testCEPParameterCompletions() {

        final SuggestionCompletionEngine com = new SuggestionCompletionEngine();

        com.setFactTypes( new String[]{"AnEvent"} );

        com.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "AnEvent",
                     new ModelField[]{
                             new ModelField( "this",
                                             Object.class.getName(),
                                             FIELD_CLASS_TYPE.REGULAR_CLASS,
                                             SuggestionCompletionEngine.TYPE_THIS )
                } );

            }
        } );

        //Set-up annotation for Type 'AnEvent'
        Map<String, List<ModelAnnotation>> annotations = new HashMap<String, List<ModelAnnotation>>();
        List<ModelAnnotation> eventAnnotations = new ArrayList<ModelAnnotation>();
        ModelAnnotation ma = new ModelAnnotation();
        ma.setAnnotationName( "role" );
        Map<String, String> mav = new HashMap<String, String>();
        mav.put( "value",
                 "event" );
        ma.setAnnotationValues( mav );
        eventAnnotations.add( ma );
        annotations.put( "AnEvent",
                         eventAnnotations );
        com.setAnnotationsForTypes( annotations );

        //Check completions
        List<Integer> c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "after" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "before" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "coincides" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "during" );
        assertEquals( 4,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );
        assertEquals( 4,
                      c.get( 3 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "finishes" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "finishedby" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "includes" );
        assertEquals( 4,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );
        assertEquals( 4,
                      c.get( 3 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "meets" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "metby" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "overlaps" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "overlappedby" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "starts" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = SuggestionCompletionEngine.getCEPOperatorParameterSets( "startedby" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

    }

    @Test
    public void testCEPOperatorValidation() {

        assertFalse( SuggestionCompletionEngine.isCEPOperator( "==" ) );
        assertFalse( SuggestionCompletionEngine.isCEPOperator( "!=" ) );
        assertFalse( SuggestionCompletionEngine.isCEPOperator( "<" ) );
        assertFalse( SuggestionCompletionEngine.isCEPOperator( ">" ) );
        assertFalse( SuggestionCompletionEngine.isCEPOperator( "<=" ) );
        assertFalse( SuggestionCompletionEngine.isCEPOperator( ">=" ) );
        assertFalse( SuggestionCompletionEngine.isCEPOperator( "== null" ) );
        assertFalse( SuggestionCompletionEngine.isCEPOperator( "!= null" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "after" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "before" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "coincides" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "during" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "finishes" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "finishedby" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "includes" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "meets" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "metby" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "overlaps" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "overlappedby" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "starts" ) );
        assertTrue( SuggestionCompletionEngine.isCEPOperator( "startedby" ) );

    }

    @Test
    public void testCEPWindowOperators() {

        List<String> operators = SuggestionCompletionEngine.getCEPWindowOperators();
        assertEquals( 2,
                      operators.size() );
        assertEquals( "over window:time",
                      operators.get( 0 ) );
        assertEquals( "over window:length",
                      operators.get( 1 ) );
    }

    @Test
    public void testAdd() {
        final SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        com.setFactTypes( new String[]{"Foo"} );
        com.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "Foo",
                     new ModelField[]{
                        new ModelField( "a",
                                        String.class.getName(),
                                        FIELD_CLASS_TYPE.REGULAR_CLASS,
                                        "String" )
                } );
            }
        } );

        assertEquals( 1,
                      com.getFactTypes().length );
        assertEquals( "Foo",
                      com.getFactTypes()[0] );

        assertEquals( 1,
                      com.getFieldCompletions( "Foo" ).length );
        assertEquals( "a",
                      com.getFieldCompletions( "Foo" )[0] );

    }

    @Test
    public void testSmartEnums() {
        final SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.setDataEnumLists( new HashMap<String, String[]>() );
        sce.putDataEnumList( "Fact.type",
                               new String[]{"sex", "colour"} );
        sce.putDataEnumList( "Fact.value[type=sex]",
                               new String[]{"M", "F"} );
        sce.putDataEnumList( "Fact.value[type=colour]",
                               new String[]{"RED", "WHITE", "BLUE"} );

        FactPattern pat = new FactPattern( "Fact" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "type" );
        sfc.setValue( "sex" );
        pat.addConstraint( sfc );
        String[] result = sce.getEnums( pat,
                                        "value" ).fixedList;
        assertEquals( 2,
                      result.length );
        assertEquals( "M",
                      result[0] );
        assertEquals( "F",
                      result[1] );

        pat = new FactPattern( "Fact" );
        sfc = new SingleFieldConstraint( "type" );
        sfc.setValue( "colour" );
        pat.addConstraint( sfc );

        result = sce.getEnums( pat,
                               "value" ).fixedList;
        assertEquals( 3,
                      result.length );
        assertEquals( "RED",
                      result[0] );
        assertEquals( "WHITE",
                      result[1] );
        assertEquals( "BLUE",
                      result[2] );

        result = sce.getEnums( pat,
                               "type" ).fixedList;
        assertEquals( 2,
                      result.length );
        assertEquals( "sex",
                      result[0] );
        assertEquals( "colour",
                      result[1] );

        ActionFieldValue[] vals = new ActionFieldValue[2];
        vals[0] = new ActionFieldValue( "type",
                                        "sex",
                                        "blah" );
        vals[1] = new ActionFieldValue( "value",
                                        null,
                                        "blah" );
        result = sce.getEnums( "Fact",
                "value", vals
        ).fixedList;
        assertNotNull( result );
        assertEquals( 2,
                      result.length );
        assertEquals( "M",
                      result[0] );
        assertEquals( "F",
                      result[1] );

        assertNull( sce.getEnums( "Nothing",
                "value", vals
        ) );

    }

    @Test
    public void testSmartEnumsDependingOfSeveralFieldsTwo() {
        final SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.setDataEnumLists( new HashMap<String, String[]>() );
        sce.putDataEnumList( "Fact.field1",
                               new String[]{"a1", "a2"} );
        sce.putDataEnumList( "Fact.field2",
                               new String[]{"b1", "b2"} );
        sce.putDataEnumList( "Fact.field3[field1=a1,field2=b1]",
                               new String[]{"c1", "c2", "c3"} );
        sce.putDataEnumList( "Fact.field4[field1=a1]",
                               new String[]{"d1", "d2"} );

        FactPattern pat = new FactPattern( "Fact" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "field1" );
        sfc.setValue( "a1" );
        pat.addConstraint( sfc );
        SingleFieldConstraint sfc2 = new SingleFieldConstraint( "field2" );
        sfc2.setValue( "b1" );
        pat.addConstraint( sfc2 );

        String[] result = sce.getEnums( pat,
                                        "field3" ).fixedList;
        assertEquals( 3,
                      result.length );
        assertEquals( "c1",
                      result[0] );
        assertEquals( "c2",
                      result[1] );
        assertEquals( "c3",
                      result[2] );

    }

    @Test
    public void testSmartEnumsDependingOfSeveralFieldsFive() {
        final SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.setDataEnumLists( new HashMap<String, String[]>() );
        sce.putDataEnumList( "Fact.field1",
                               new String[]{"a1", "a2"} );
        sce.putDataEnumList( "Fact.field2",
                               new String[]{"b1", "b2"} );
        sce.putDataEnumList( "Fact.field3",
                               new String[]{"c1", "c2", "c3"} );
        sce.putDataEnumList( "Fact.longerField4",
                               new String[]{"d1", "d2"} );
        sce.putDataEnumList( "Fact.field5",
                               new String[]{"e1", "e2"} );
        sce.putDataEnumList( "Fact.field6[field1=a1, field2=b2, field3=c3,longerField4=d1,field5=e2]",
                               new String[]{"f1", "f2"} );

        FactPattern pat = new FactPattern( "Fact" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "field1" );
        sfc.setValue( "a1" );
        pat.addConstraint( sfc );
        SingleFieldConstraint sfc2 = new SingleFieldConstraint( "field2" );
        sfc2.setValue( "b2" );
        pat.addConstraint( sfc2 );
        SingleFieldConstraint sfc3 = new SingleFieldConstraint( "field3" );
        sfc3.setValue( "c3" );
        pat.addConstraint( sfc3 );
        SingleFieldConstraint sfc4 = new SingleFieldConstraint( "longerField4" );
        sfc4.setValue( "d1" );
        pat.addConstraint( sfc4 );

        assertNull( sce.getEnums( pat,
                                  "field6" ) );

        SingleFieldConstraint sfc5 = new SingleFieldConstraint( "field5" );
        sfc5.setValue( "e2" );
        pat.addConstraint( sfc5 );

        String[] result2 = sce.getEnums( pat,
                                         "field6" ).fixedList;
        assertEquals( 2,
                      result2.length );
        assertEquals( "f1",
                      result2[0] );
        assertEquals( "f2",
                      result2[1] );
    }

    @Test
    public void testSmarterLookupEnums() {
        final SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.setDataEnumLists( new HashMap<String, String[]>() );
        sce.putDataEnumList( "Fact.type",
                               new String[]{"sex", "colour"} );
        sce.putDataEnumList( "Fact.value[f1, f2]",
                               new String[]{"select something from database where x=@{f1} and y=@{f2}"} );

        FactPattern fp = new FactPattern( "Fact" );
        String[] drops = sce.getEnums( fp,
                                       "type" ).fixedList;
        assertEquals( 2,
                      drops.length );
        assertEquals( "sex",
                      drops[0] );
        assertEquals( "colour",
                      drops[1] );

        Map<String, Object> lookupFields = sce.loadDataEnumLookupFields();
        assertEquals( 1,
                      lookupFields.size() );
        String[] flds = (String[]) lookupFields.get( "Fact.value" );
        assertEquals( 2,
                      flds.length );
        assertEquals( "f1",
                      flds[0] );
        assertEquals( "f2",
                      flds[1] );

        FactPattern pat = new FactPattern( "Fact" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "f1" );
        sfc.setValue( "f1val" );
        pat.addConstraint( sfc );
        sfc = new SingleFieldConstraint( "f2" );
        sfc.setValue( "f2val" );
        pat.addConstraint( sfc );

        DropDownData dd = sce.getEnums( pat,
                                        "value" );
        assertNull( dd.fixedList );
        assertNotNull( dd.queryExpression );
        assertNotNull( dd.valuePairs );

        assertEquals( 2,
                      dd.valuePairs.length );
        assertEquals( "select something from database where x=@{f1} and y=@{f2}",
                      dd.queryExpression );
        assertEquals( "f1=f1val",
                      dd.valuePairs[0] );
        assertEquals( "f2=f2val",
                      dd.valuePairs[1] );

        //and now for the RHS
        ActionFieldValue[] vals = new ActionFieldValue[2];
        vals[0] = new ActionFieldValue( "f1",
                                        "f1val",
                                        "blah" );
        vals[1] = new ActionFieldValue( "f2",
                                        "f2val",
                                        "blah" );
        dd = sce.getEnums( "Fact",
                "value", vals
        );
        assertNull( dd.fixedList );
        assertNotNull( dd.queryExpression );
        assertNotNull( dd.valuePairs );
        assertEquals( 2,
                      dd.valuePairs.length );
        assertEquals( "select something from database where x=@{f1} and y=@{f2}",
                      dd.queryExpression );
        assertEquals( "f1=f1val",
                      dd.valuePairs[0] );
        assertEquals( "f2=f2val",
                      dd.valuePairs[1] );

    }

    @Test
    public void testSmarterLookupEnumsDifferentOrder() {
        final SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.setDataEnumLists( new HashMap<String, String[]>() );
        sce.putDataEnumList( "Fact.type",
                               new String[]{"sex", "colour"} );
        sce.putDataEnumList( "Fact.value[e1, e2]",
                               new String[]{"select something from database where x=@{e1} and y=@{e2}"} );
        sce.putDataEnumList( "Fact.value[f1, f2]",
                               new String[]{"select something from database where x=@{f1} and y=@{f2}"} );

        FactPattern fp = new FactPattern( "Fact" );
        String[] drops = sce.getEnums( fp,
                                       "type" ).fixedList;
        assertEquals( 2,
                      drops.length );
        assertEquals( "sex",
                      drops[0] );
        assertEquals( "colour",
                      drops[1] );

        Map<String, Object> lookupFields = sce.loadDataEnumLookupFields();
        assertEquals( 1,
                      lookupFields.size() );
        String[] flds = (String[]) lookupFields.get( "Fact.value" );
        assertEquals( 2,
                      flds.length );
        assertEquals( "f1",
                      flds[0] );
        assertEquals( "f2",
                      flds[1] );

        FactPattern pat = new FactPattern( "Fact" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "f1" );
        sfc.setValue( "f1val" );
        pat.addConstraint( sfc );
        sfc = new SingleFieldConstraint( "f2" );
        sfc.setValue( "f2val" );
        pat.addConstraint( sfc );

        DropDownData dd = sce.getEnums( pat,
                                        "value" );
        assertNull( dd.fixedList );
        assertNotNull( dd.queryExpression );
        assertNotNull( dd.valuePairs );

        assertEquals( 2,
                      dd.valuePairs.length );
        assertEquals( "select something from database where x=@{f1} and y=@{f2}",
                      dd.queryExpression );
        assertEquals( "f1=f1val",
                      dd.valuePairs[0] );
        assertEquals( "f2=f2val",
                      dd.valuePairs[1] );

        //and now for the RHS
        ActionFieldValue[] vals = new ActionFieldValue[2];
        vals[0] = new ActionFieldValue( "f1",
                                        "f1val",
                                        "blah" );
        vals[1] = new ActionFieldValue( "f2",
                                        "f2val",
                                        "blah" );
        dd = sce.getEnums( "Fact",
                "value", vals
        );
        assertNull( dd.fixedList );
        assertNotNull( dd.queryExpression );
        assertNotNull( dd.valuePairs );
        assertEquals( 2,
                      dd.valuePairs.length );
        assertEquals( "select something from database where x=@{f1} and y=@{f2}",
                      dd.queryExpression );
        assertEquals( "f1=f1val",
                      dd.valuePairs[0] );
        assertEquals( "f2=f2val",
                      dd.valuePairs[1] );

    }

    @Test
    public void testSimpleEnums() {
        final SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.setDataEnumLists( new HashMap<String, String[]>() );
        sce.putDataEnumList( "Fact.type",
                               new String[]{"sex", "colour"} );
        assertEquals( 2,
                      sce.getEnumValues( "Fact",
                                         "type" ).length );
        assertEquals( "sex",
                      sce.getEnumValues( "Fact",
                                         "type" )[0] );
        assertEquals( "colour",
                      sce.getEnumValues( "Fact",
                                         "type" )[1] );

    }

    private void assertContains(final String string,
                                final String[] c) {

        for ( int i = 0; i < c.length; i++ ) {
            if ( string.equals( c[i] ) ) {
                return;
            }
        }
        fail( "String array did not contain: " + string );

    }

    @Test
    public void testGlobalAndFacts() {
        final SuggestionCompletionEngine com = new SuggestionCompletionEngine();

        com.setGlobalVariables( new HashMap<String, String>() {
            {
                put( "y",
                     "Foo" );
            }
        } );

        com.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "Foo",
                     new ModelField[]{
                        new ModelField( "a",
                                        String.class.getName(),
                                        FIELD_CLASS_TYPE.REGULAR_CLASS,
                                        "String" )
                } );
            }
        } );

        assertFalse( com.isGlobalVariable( "x" ) );
        assertTrue( com.isGlobalVariable( "y" ) );
    }

    @Test
    public void testDataDropDown() {
        assertNull( DropDownData.create( null ) );
        assertNull( DropDownData.create( null,
                                         null ) );
        assertNotNull( DropDownData.create( new String[]{"hey"} ) );
        assertNotNull( DropDownData.create( "abc",
                                            new String[]{"hey"} ) );

    }

    @Test
    public void testFilter() {

        final SuggestionCompletionEngine sce = new SuggestionCompletionEngine();

        sce.setFactTypes( new String[]{"Person", "Vehicle"} );

        sce.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "Person",
                     new ModelField[]{
                        new ModelField( "age",
                                        Integer.class.getName(),
                                        FIELD_CLASS_TYPE.REGULAR_CLASS,
                                        SuggestionCompletionEngine.TYPE_NUMERIC ),
                } );

                put( "Vehicle",
                     new ModelField[]{
                        new ModelField( "make",
                                        String.class.getName(),
                                        FIELD_CLASS_TYPE.REGULAR_CLASS,
                                        SuggestionCompletionEngine.TYPE_STRING ),
                } );
            }
        } );

        assertEquals( 2,
                      sce.getFactTypes().length );
        sce.setFactTypeFilter( new FactTypeFilter() {
            public boolean filter(String originalFact) {
                return "Person".equals( originalFact );
            }
        } );

        assertEquals( 1,
                      sce.getFactTypes().length );
        sce.setFilteringFacts( false );

        assertEquals( 2,
                      sce.getFactTypes().length );
        sce.setFilteringFacts( true );
        assertEquals( 1,
                      sce.getFactTypes().length );

        sce.setFactTypeFilter( null );
        assertEquals( 2,
                      sce.getFactTypes().length );

    }

    public static class NestedClass {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Fact {
        private String field1;
        private String field2;

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

    }
}
