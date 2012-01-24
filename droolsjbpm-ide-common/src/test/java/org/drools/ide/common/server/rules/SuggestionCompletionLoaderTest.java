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

package org.drools.ide.common.server.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarInputStream;

import org.drools.ide.common.client.modeldriven.FactTypeFilter;
import org.drools.ide.common.client.modeldriven.ModelAnnotation;
import org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.dsl.DSLMappingEntry;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.junit.Test;

public class SuggestionCompletionLoaderTest {

    @Test
    public void testSuggestionCompLoader() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

    }

    @Test
    public void testSuggestionCompLoaderWithExtraImportProviders() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        loader.addExternalImportDescrProvider( new SuggestionCompletionLoader.ExternalImportDescrProvider() {

            public Set<ImportDescr> getImportDescrs() {
                return new HashSet<ImportDescr>() {
                    {
                        add( new ImportDescr( "java.util.List" ) );
                        add( new ImportDescr( "java.util.Set" ) );
                    }
                };
            }
        } );
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        assertEquals( 3,
                      eng.getFactTypes().length );
        List<String> factTypes = Arrays.asList( eng.getFactTypes() );
        assertTrue( factTypes.contains( "List" ) );
        assertTrue( factTypes.contains( "Set" ) );
        assertTrue( factTypes.contains( "Person" ) );

        eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person \n declare GenBean \n   id: int \n name : String \n end \n declare GenBean2 \n list: java.util.List \n gb: GenBean \n end",
                                          new ArrayList(),
                                          new ArrayList() );
        assertEquals( 5,
                      eng.getFactTypes().length );
        factTypes = Arrays.asList( eng.getFactTypes() );
        assertTrue( factTypes.contains( "List" ) );
        assertTrue( factTypes.contains( "Set" ) );
        assertTrue( factTypes.contains( "Person" ) );
        assertTrue( factTypes.contains( "GenBean" ) );
        assertTrue( factTypes.contains( "GenBean2" ) );

    }

    @Test
    public void testSuggestionCompLoaderWithExtraImportProvidersAndFilters() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        loader.addExternalImportDescrProvider( new SuggestionCompletionLoader.ExternalImportDescrProvider() {

            public Set<ImportDescr> getImportDescrs() {
                return new HashSet<ImportDescr>() {
                    {
                        add( new ImportDescr( "java.util.List" ) );
                        add( new ImportDescr( "java.util.Set" ) );
                    }
                };
            }
        } );
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person \n declare GenBean \n   id: int \n name : String \n end \n declare GenBean2 \n list: java.util.List \n gb: GenBean \n end",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        eng.setFactTypeFilter( new FactTypeFilter() {

            public boolean filter(String originalFact) {
                return originalFact.equals( "List" ) || originalFact.equals( "GenBean2" );
            }
        } );
        eng.setFilteringFacts( true );

        assertNotNull( eng );

        assertEquals( 3,
                      eng.getFactTypes().length );
        List<String> factTypes = Arrays.asList( eng.getFactTypes() );
        assertTrue( factTypes.contains( "Set" ) );
        assertTrue( factTypes.contains( "Person" ) );
        assertTrue( factTypes.contains( "GenBean" ) );

    }

    @Test
    public void testSuggestionCompLoaderWildCards() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        loader.getSuggestionEngine( "package foo \n import org.drools.*",
                                    Collections.<JarInputStream> emptyList(),
                                    Collections.<DSLTokenizedMappingFile> emptyList() );
        assertEquals( 1,
                      loader.getErrors().size() );
        String err = loader.getErrors().get( 0 );
        assertTrue( err.startsWith( "Unable" ) );
    }

    @Test
    public void testTestAnyEnum() throws Exception {
        SuggestionCompletionLoader suggestionCompletionLoader = new SuggestionCompletionLoader();
        ArrayList<DSLTokenizedMappingFile> dsls = new ArrayList<DSLTokenizedMappingFile>();

        DSLTokenizedMappingFile dslTokenizedMappingFile = new DSLTokenizedMappingFile();

        DSLMappingEntry dslMappingEntry = mock( DSLMappingEntry.class );
        when( dslMappingEntry.getSection() ).thenReturn( DSLMappingEntry.ANY );

        dslTokenizedMappingFile.getMapping().addEntry( dslMappingEntry );
        dsls.add( dslTokenizedMappingFile );

        SuggestionCompletionEngine suggestionEngine = suggestionCompletionLoader.getSuggestionEngine( "",
                                                                                                      Collections.<JarInputStream> emptyList(),
                                                                                                      dsls );

        assertEquals( 1,
                      suggestionEngine.actionDSLSentences.length );
        assertEquals( 1,
                      suggestionEngine.conditionDSLSentences.length );
        assertEquals( 0,
                      suggestionEngine.keywordDSLItems.length );

    }

    @Test
    public void testLoadDifferentFieldTypes() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.ide.common.server.rules.SomeFact",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      eng.getFieldType( "SomeFact",
                                        "age" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "SomeFact",
                                        "likes" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "SomeFact",
                                        "name" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      eng.getFieldType( "SomeFact",
                                        "bigDecimal" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      eng.getFieldType( "SomeFact",
                                        "alive" ) );
        //        assertEquals(SuggestionCompletionEngine.TYPE_COMPARABLE, eng.getFieldType( "SomeFact", "date"));
        assertEquals( SuggestionCompletionEngine.TYPE_DATE,
                      eng.getFieldType( "SomeFact",
                                        "date" ) );
        assertEquals( "Cheese",
                      eng.getFieldType( "SomeFact",
                                        "cheese" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      eng.getFieldType( "SomeFact",
                                        "dead" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      eng.getFieldType( "SomeFact",
                                        "alive" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_COLLECTION,
                      eng.getFieldType( "SomeFact",
                                        "factList" ) );
        assertEquals( "SomeFact",
                      eng.getParametricFieldType( "SomeFact",
                                                  "factList" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_COLLECTION,
                      eng.getFieldType( "SomeFact",
                                        "factListString" ) );
        assertEquals( "String",
                      eng.getParametricFieldType( "SomeFact",
                                                  "factListString" ) );
    }

    @Test
    public void testLoadDifferentMethodTypes() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.ide.common.server.rules.SomeFact",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        assertEquals( List.class.getName(),
                      eng.getMethodClassType( "SomeFact",
                                              "aMethod(int)" ) );
        assertEquals( "SomeFact",
                      eng.getParametricFieldType( "SomeFact",
                                                  "aMethod(int)" ) );
    }

    @Test
    public void testGeneratedBeans() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n declare GenBean \n   id: int \n name : String \n end \n declare GenBean2 \n list: java.util.List \n gb: GenBean \n end",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertFalse( loader.hasErrors() );
        assertNotNull( eng );

        assertEquals( 2,
                      eng.getFactTypes().length );
        assertEquals( "GenBean",
                      eng.getFactTypes()[0] );
        assertEquals( "GenBean2",
                      eng.getFactTypes()[1] );

        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      eng.getFieldType( "GenBean",
                                        "id" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "GenBean",
                                        "name" ) );

        assertEquals( "GenBean",
                      eng.getFieldType( "GenBean2",
                                        "gb" ) );
    }

    @Test
    public void testGeneratedBeansExtendsSimple() throws Exception {
        String packageDrl = "package foo \n"
                            + "declare Bean1 \n"
                            + "age: int \n"
                            + "name : String \n"
                            + "end \n"
                            + "declare Bean2 extends Bean1\n"
                            + "cheese : String \n"
                            + "end";
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( packageDrl,
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertFalse( loader.hasErrors() );
        assertNotNull( eng );

        assertEquals( 2,
                      eng.getFactTypes().length );
        assertEquals( "Bean1",
                      eng.getFactTypes()[0] );
        assertEquals( "Bean2",
                      eng.getFactTypes()[1] );

        assertEquals( 3,
                      eng.getFieldCompletions( "Bean1" ).length );
        assertEquals( "Bean1",
                      eng.getFieldType( "Bean1",
                                        "this" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      eng.getFieldType( "Bean1",
                                        "age" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Bean1",
                                        "name" ) );

        assertEquals( 4,
                      eng.getFieldCompletions( "Bean2" ).length );
        assertEquals( "Bean2",
                      eng.getFieldType( "Bean2",
                                        "this" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      eng.getFieldType( "Bean2",
                                        "age" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Bean2",
                                        "name" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Bean2",
                                        "cheese" ) );
    }

    @Test
    public void testGeneratedBeansExtendsPOJOSimple() throws Exception {
        String packageDrl = "package foo \n"
                            + "import org.drools.Address\n"
                            + "declare Address \n"
                            + "end";
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( packageDrl,
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertFalse( loader.hasErrors() );
        assertNotNull( eng );

        assertEquals( 1,
                      eng.getFactTypes().length );
        assertEquals( "Address",
                      eng.getFactTypes()[0] );

        assertEquals( 4,
                      eng.getFieldCompletions( "Address" ).length );
        assertEquals( "Address",
                      eng.getFieldType( "Address",
                                        "this" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Address",
                                        "street" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Address",
                                        "suburb" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Address",
                                        "zipCode" ) );
    }

    @Test
    public void testGeneratedBeansExtendsPOJOComplex() throws Exception {
        String packageDrl = "package foo \n"
                            + "import org.drools.Address\n"
                            + "declare Address \n"
                            + "end\n"
                            + "declare Address2 extends Address\n"
                            + "isNicePlace : Boolean \n"
                            + "end";
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( packageDrl,
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertFalse( loader.hasErrors() );
        assertNotNull( eng );

        assertEquals( 2,
                      eng.getFactTypes().length );
        assertEquals( "Address",
                      eng.getFactTypes()[0] );
        assertEquals( "Address2",
                      eng.getFactTypes()[1] );

        assertEquals( 4,
                      eng.getFieldCompletions( "Address" ).length );
        assertEquals( "Address",
                      eng.getFieldType( "Address",
                                        "this" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Address",
                                        "street" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Address",
                                        "suburb" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Address",
                                        "zipCode" ) );

        assertEquals( 5,
                      eng.getFieldCompletions( "Address2" ).length );
        assertEquals( "Address2",
                      eng.getFieldType( "Address2",
                                        "this" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Address2",
                                        "street" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Address2",
                                        "suburb" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Address2",
                                        "zipCode" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      eng.getFieldType( "Address2",
                                        "isNicePlace" ) );
    }

    @Test
    public void testGlobal() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n global org.drools.Person p",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );
        assertFalse( loader.hasErrors() );

        assertEquals( 1,
                      eng.getGlobalVariables().length );
        assertEquals( "p",
                      eng.getGlobalVariables()[0] );
        assertEquals( "Person",
                      eng.getGlobalVariable( "p" ) );
        String[] flds = (String[]) eng.getModelFields( "Person" );
        assertNotNull( flds );

        assertEquals( 0,
                      eng.getGlobalCollections().length );
    }

    @Test
    public void testGlobalCollections() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n global java.util.List ls",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );
        assertFalse( loader.hasErrors() );

        assertEquals( 1,
                      eng.getGlobalVariables().length );
        assertEquals( "ls",
                      eng.getGlobalVariables()[0] );
        assertEquals( "List",
                      eng.getGlobalVariable( "ls" ) );

        assertNotNull( eng.getGlobalCollections() );
        assertEquals( 1,
                      eng.getGlobalCollections().length );
        assertEquals( "ls",
                      eng.getGlobalCollections()[0] );
    }

    @Test
    public void testSortOrderOfFields() throws Exception {

        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.ide.common.server.rules.SomeFact",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        String[] fields = eng.getFieldCompletions( "SomeFact" );

        assertEquals( "this",
                      fields[0] );
        assertEquals( "age",
                      fields[1] );
        assertEquals( "alive",
                      fields[2] );
        assertEquals( "anEnum",
                      fields[3] );
        assertEquals( "bigDecimal",
                      fields[4] );
    }

    @Test
    public void testEnumFields() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.ide.common.server.rules.SomeFact",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );
        assertTrue( eng.hasDataEnumLists() );
        assertEquals( eng.getDataEnumList( "SomeFact.anEnum" ).length,
                      3 );
        String a[] = eng.getDataEnumList( "SomeFact.anEnum" );
        assertEquals( a[0],
                      "EnumClass.v1=EnumClass.v1" );
        assertEquals( a[1],
                      "EnumClass.v2=EnumClass.v2" );
        assertEquals( a[2],
                      "EnumClass.v3=EnumClass.v3" );
    }

    @Test
    public void testSortOrderOfFacts() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.ide.common.server.rules.SomeFact\n import org.drools.Person",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );
        String[] facts = eng.getFactTypes();
        assertEquals( 2,
                      facts.length );

        assertEquals( "Person",
                      facts[0] );
        assertEquals( "SomeFact",
                      facts[1] );
    }

    @Test
    public void testTypeDeclarations() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        String header = "";
        header += "package foo\n";

        header += "declare Applicant\n";
        header += "     creditRating: String\n";
        header += "     approved: Boolean\n";
        header += "     applicationDate: java.util.Date\n";
        header += "     age: Integer\n";
        header += "     name: String\n";
        header += "end\n";

        header += "declare LoanApplication\n";
        header += "     amount: Integer\n";
        header += "     approved: Boolean\n";
        header += "     deposit: Integer\n";
        header += "     approvedRate: Integer\n";
        header += "     lengthYears: Integer\n";
        header += "     explanation: String\n";
        header += "     insuranceCost: Integer\n";
        header += "     applicant: Applicant\n";
        header += "end\n";

        SuggestionCompletionEngine eng = loader.getSuggestionEngine( header,
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "Applicant",
                                        "creditRating" ) );
        assertEquals( "java.lang.String",
                      eng.getFieldClassName( "Applicant",
                                             "creditRating" ) );
        assertEquals( FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                      eng.getFieldClassType( "Applicant",
                                             "creditRating" ) );

        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      eng.getFieldType( "LoanApplication",
                                        "deposit" ) );
        assertEquals( "java.lang.Integer",
                      eng.getFieldClassName( "LoanApplication",
                                             "deposit" ) );
        assertEquals( FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                      eng.getFieldClassType( "LoanApplication",
                                             "deposit" ) );

        assertEquals( "Applicant",
                      eng.getFieldType( "LoanApplication",
                                        "applicant" ) );
        assertEquals( "Applicant",
                      eng.getFieldClassName( "LoanApplication",
                                                          "applicant" ) );
        assertEquals( FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                      eng.getFieldClassType( "LoanApplication",
                                             "applicant" ) );
    }

    @Test
    public void testLoaderWithExistingClassloader() throws Exception {
        MockClassLoader mcl = new MockClassLoader();
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader( mcl );
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.foo.Bar",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );
        //assertNotNull(eng.dataEnumLists);
        assertTrue( mcl.called );
    }

    static class MockClassLoader extends ClassLoader {

        public boolean called = false;

        public Class< ? > loadClass(String name) {
            called = true;
            return Object.class;
        }

    }

    @Test
    public void testTypeDeclarationsAnnotations() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        String header = "";
        header += "package foo\n";

        header += "declare Applicant\n";
        header += "@role( event )\n";
        header += "end\n";

        SuggestionCompletionEngine eng = loader.getSuggestionEngine( header,
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        assertNotNull( eng.getAnnotations() );
        assertEquals( 1,
                      eng.getAnnotations().size() );

        assertNotNull( eng.getAnnotations().get( "Applicant" ) );
        assertEquals( 1,
                      eng.getAnnotations().get( "Applicant" ).size() );

        assertNotNull( eng.getAnnotations().get( "Applicant" ).get( 0 ) );
        assertEquals( "role",
                      eng.getAnnotations().get( "Applicant" ).get( 0 ).getAnnotationName() );
        assertEquals( "event",
                      eng.getAnnotations().get( "Applicant" ).get( 0 ).getAnnotationValues().get( "value" ) );

        assertNotNull( eng.getAnnotationsForFactType( "Applicant" ) );
        assertNotNull( eng.getAnnotationsForFactType( "Applicant" ).get( 0 ) );
        assertEquals( "role",
                      eng.getAnnotationsForFactType( "Applicant" ).get( 0 ).getAnnotationName() );
        assertEquals( "event",
                      eng.getAnnotationsForFactType( "Applicant" ).get( 0 ).getAnnotationValues().get( "value" ) );

    }

    @Test
    public void testTypeDeclarationsMultipleAnnotations() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        String header = "";
        header += "package foo\n";

        header += "declare Applicant\n";
        header += "@role( event )\n";
        header += "@smurf( name = Pupa)\n";
        header += "end\n";

        SuggestionCompletionEngine eng = loader.getSuggestionEngine( header,
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        assertNotNull( eng.getAnnotations() );
        assertEquals( 1,
                      eng.getAnnotations().size() );

        assertNotNull( eng.getAnnotations().get( "Applicant" ) );
        assertEquals( 2,
                      eng.getAnnotations().get( "Applicant" ).size() );

        int idx0 = getIndexOfAnnotation( eng.getAnnotationsForFactType( "Applicant" ),
                                         "role" );
        int idx1 = getIndexOfAnnotation( eng.getAnnotationsForFactType( "Applicant" ),
                                         "smurf" );

        assertNotNull( eng.getAnnotations().get( "Applicant" ).get( idx0 ) );
        assertEquals( "role",
                      eng.getAnnotations().get( "Applicant" ).get( idx0 ).getAnnotationName() );
        assertEquals( "event",
                      eng.getAnnotations().get( "Applicant" ).get( idx0 ).getAnnotationValues().get( "value" ) );

        assertNotNull( eng.getAnnotations().get( "Applicant" ).get( idx1 ) );
        assertEquals( "smurf",
                      eng.getAnnotations().get( "Applicant" ).get( idx1 ).getAnnotationName() );
        assertEquals( "Pupa",
                      eng.getAnnotations().get( "Applicant" ).get( idx1 ).getAnnotationValues().get( "name" ) );

    }

    private int getIndexOfAnnotation(List<ModelAnnotation> annotations,
                                     String annotationName) {
        for ( int i = 0; i < annotations.size(); i++ ) {
            ModelAnnotation annotation = annotations.get( i );
            if ( annotationName.equals( annotation.getAnnotationName() ) ) {
                return i;
            }
        }
        return -1;
    }

    @Test
    public void testReadOnlyFieldWithAnnotation() throws Exception {
        // GUVNOR-1792
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        String header = "";
        header += "package foo \n import org.drools.ide.common.server.rules.ReadOnlyFact\n";

        header += "declare ReadOnlyFact\n";
        header += "@role( event )\n";
        header += "end\n";

        SuggestionCompletionEngine eng = loader.getSuggestionEngine( header,
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "ReadOnlyFact",
                                        "name" ) );
    }

    @Test
    public void testReadOnlyFieldWithAnnotationAndField() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        String header = "";
        header += "package foo \n import org.drools.ide.common.server.rules.ReadOnlyFact\n";

        header += "declare ReadOnlyFact\n";
        header += "@role( event )\n";
        header += "age: Integer\n";
        header += "end\n";

        SuggestionCompletionEngine eng = loader.getSuggestionEngine( header,
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "ReadOnlyFact",
                                        "name" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      eng.getFieldType( "ReadOnlyFact",
                                        "age" ) );
    }

    @Test
    public void testLoadDelegatedProperties() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n" +
                                                                             "import org.drools.ide.common.server.rules.MotherClass\n" +
                                                                             "import org.drools.ide.common.server.rules.DelegationClass\n" +
                                                                             "import org.drools.ide.common.server.rules.SubClass\n",
                                                                     new ArrayList(),
                                                                     new ArrayList() );
        assertNotNull( eng );

        assertEquals( SuggestionCompletionEngine.TYPE_COMPARABLE,
                      eng.getFieldType( "MotherClass",
                                        "status" ) );

        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      eng.getFieldType( "SubClass",
                                        "message" ) );
        assertEquals( SuggestionCompletionEngine.TYPE_COMPARABLE,
                      eng.getFieldType( "SubClass",
                                        "status" ) );

        assertEquals( SuggestionCompletionEngine.TYPE_COMPARABLE,
                      eng.getFieldType( "DelegationClass",
                                        "status" ) );
    }

}
