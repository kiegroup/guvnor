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

package org.drools.ide.common.server.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.ide.common.client.modeldriven.ModelAnnotation;
import org.drools.ide.common.client.modeldriven.ModelField;
import org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.server.rules.SuggestionCompletionLoader.FieldInfo;
import org.drools.lang.dsl.DSLMappingEntry;

/**
 * A builder to incrementally populate a SuggestionCompletionEngine
 */
public class SuggestionCompletionEngineBuilder {

    private SuggestionCompletionEngine                    instance              = new SuggestionCompletionEngine();

    private Map<String, FIELD_CLASS_TYPE>                 factTypes             = new HashMap<String, FIELD_CLASS_TYPE>();
    private Map<String, Map<String, Map<String, String>>> annotationsForType    = new HashMap<String, Map<String, Map<String, String>>>();
    private Map<String, String[]>                         fieldsForType         = new HashMap<String, String[]>();
    //    private Map<String, String[]>                         modifiersForType      = new HashMap<String, String[]>();
    private Map<String, String>                           fieldTypes            = new HashMap<String, String>();
    private Map<String, Class< ? >>                       fieldClasses          = new HashMap<String, Class< ? >>();
    private Map<String, FieldInfo>                        fieldTypesField       = new HashMap<String, FieldInfo>();
    private Map<String, String>                           globalTypes           = new HashMap<String, String>();
    private List<DSLSentence>                             actionDSLSentences    = new ArrayList<DSLSentence>();
    private List<DSLSentence>                             conditionDSLSentences = new ArrayList<DSLSentence>();
    private List<DSLSentence>                             keywordDSLItems       = new ArrayList<DSLSentence>();
    private List<DSLSentence>                             anyScopeDSLItems      = new ArrayList<DSLSentence>();
    private List<String>                                  globalCollections     = new ArrayList<String>();
    private Map<String, FieldAccessorsAndMutators>        accessorsAndMutators  = new HashMap<String, FieldAccessorsAndMutators>();
    private Map<String, String[]>                         allDataEnumsList      = new HashMap<String, String[]>();

    public SuggestionCompletionEngineBuilder() {
    }

    /**
     * Start the creation of a new SuggestionCompletionEngine
     */
    public void newCompletionEngine() {
        this.instance = new SuggestionCompletionEngine();
        this.factTypes = new HashMap<String, FIELD_CLASS_TYPE>();
        this.annotationsForType = new HashMap<String, Map<String, Map<String, String>>>();
        this.fieldsForType = new HashMap<String, String[]>();
        this.fieldTypes = new HashMap<String, String>();
        this.fieldTypesField = new HashMap<String, FieldInfo>();
        this.globalTypes = new HashMap<String, String>();
        this.actionDSLSentences = new ArrayList<DSLSentence>();
        this.conditionDSLSentences = new ArrayList<DSLSentence>();
        this.keywordDSLItems = new ArrayList<DSLSentence>();
        this.anyScopeDSLItems = new ArrayList<DSLSentence>();
        this.globalCollections = new ArrayList<String>();
        this.accessorsAndMutators = new HashMap<String, FieldAccessorsAndMutators>();
        this.allDataEnumsList = new HashMap<String, String[]>();
    }

    /**
     * Adds a fact type to the engine
     * 
     * @param factType
     */
    public void addFactType(final String factType,
                            final FIELD_CLASS_TYPE type) {
        this.factTypes.put( factType,
                            type );
    }

    /**
     * Adds the annotations for a given type
     * 
     * @param factType
     * @param annotations
     */
    public void addAnnotationsForType(final String factType,
                                      final Map<String, Map<String, String>> annotations) {
        this.annotationsForType.put( factType,
                                     annotations );
    }

    /**
     * Adds the list of fields for a given type
     * 
     * @param type
     * @param fields
     */
    public void addFieldsForType(final String type,
                                 final String[] fields) {
        String[] oldFields = this.fieldsForType.get( type );
        if ( oldFields != null ) {
            List<String> mergedFields = new ArrayList<String>( Arrays.asList( oldFields ) );
            for ( String field : fields ) {
                if ( !mergedFields.contains( field ) ) {
                    mergedFields.add( field );
                }
            }
            this.fieldsForType.put( type,
                                    mergedFields.toArray( new String[mergedFields.size()] ) );
        } else {
            this.fieldsForType.put( type,
                                    fields );
        }
    }

    /**
     * @return true if this has the type already registered (field information).
     */
    public boolean hasFieldsForType(final String type) {
        return this.fieldsForType.containsKey( type );
    }

    /**
     * Adds a type declaration for a field
     * 
     * @param field
     *            format: class.field
     * @param type
     *            parametrized type of clazz
     * @param clazz
     *            the class of field
     */
    public void addFieldType(final String field,
                             final String type,
                             final Class< ? > clazz) {
        this.fieldTypes.put( field,
                             type );
        this.fieldClasses.put( field,
                               clazz );
    }

    /**
     * Adds a type declaration for a field
     * 
     * @param field
     *            format: class.field
     * @param type
     */
    public void addFieldTypeField(final String field,
                                  final FieldInfo type) {
        this.fieldTypesField.put( field,
                                  type );
    }

    /**
     * Adds a global and its corresponding type to the engine
     * 
     * @param global
     * @param type
     */
    public void addGlobalType(final String global,
                              final String type) {
        this.globalTypes.put( global,
                              type );
    }

    public void addGlobalCollection(String global) {
        this.globalCollections.add( global );
    }

    /**
     * Add a DSL sentence for an action.
     */
    public void addDSLActionSentence(final String definition) {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( definition );
        this.actionDSLSentences.add( sen );
    }

    /**
     * Add a DSL sentence for a condition.
     */
    public void addDSLConditionSentence(final String definition) {
        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( definition );
        this.conditionDSLSentences.add( sen );
    }

    static public String obtainGenericType(Type type) {
        if ( type instanceof ParameterizedType ) {
            ParameterizedType pt = (ParameterizedType) type;
            Type goodType = null;
            for ( Type t : pt.getActualTypeArguments() ) {
                goodType = t;
            }
            int index = goodType.toString().lastIndexOf( "." );
            return goodType.toString().substring( index + 1 );
        }
        return null;
    }

    public void putParametricFieldType(String fieldName,
                                       String genericType) {
        this.instance.putParametricFieldType( fieldName,
                                              genericType );
    }

    /**
     * Returns a SuggestionCompletionEngine instance populated with all the data
     * since last call to newCompletionEngine() method
     * 
     * @return
     */
    public SuggestionCompletionEngine getInstance() {
        this.instance.setFactTypes( this.factTypes.keySet().toArray( new String[this.factTypes.keySet().size()] ) );

        //FactType annotations
        Map<String, List<ModelAnnotation>> annotationMap = new HashMap<String, List<ModelAnnotation>>();
        for ( Map.Entry<String, Map<String, Map<String, String>>> factTypeEntry : this.annotationsForType.entrySet() ) {
            String factType = factTypeEntry.getKey();
            List<ModelAnnotation> annotations = new ArrayList<ModelAnnotation>();
            for ( Map.Entry<String, Map<String, String>> annotationEntry : factTypeEntry.getValue().entrySet() ) {
                ModelAnnotation ma = new ModelAnnotation();
                ma.setAnnotationName( annotationEntry.getKey() );
                ma.setAnnotationValues( annotationEntry.getValue() );
                annotations.add( ma );
            }
            annotationMap.put( factType,
                               annotations );
        }

        this.instance.setAnnotationsForTypes( annotationMap );

        //convert this.fieldsForType, this.fieldClasses and this.fieldTypes into Map<String,ModelField[]>.
        Map<String, ModelField[]> modelMap = new HashMap<String, ModelField[]>();
        for ( Map.Entry<String, String[]> typeEntry : this.fieldsForType.entrySet() ) {

            List<ModelField> fields = new ArrayList<ModelField>();
            for ( String field : typeEntry.getValue() ) {
                String fieldName = field;
                String fieldType = this.fieldTypes.get( typeEntry.getKey() + "." + field );
                Class< ? > fieldClazz = this.fieldClasses.get( typeEntry.getKey() + "." + field );

                fields.add( new ModelField(
                                            fieldName,
                                            fieldClazz == null ? fieldType : fieldClazz.getName(),
                                            this.factTypes.get( typeEntry.getKey() ),
                                            fieldType ) );
            }

            modelMap.put( typeEntry.getKey(),
                          fields.toArray( new ModelField[fields.size()] ) );
        }

        this.instance.setFieldsForTypes( modelMap );

        for ( String fieldName : this.fieldTypesField.keySet() ) {
            FieldInfo field = this.fieldTypesField.get( fieldName );
            if ( field != null ) {
                String genericType = obtainGenericType( field.getGenericType() );
                if ( genericType != null ) {
                    this.instance.putParametricFieldType( fieldName,
                                                          genericType );
                }

                Class< ? > fieldClass = field.getType();
                if ( fieldClass.isEnum() ) {
                    Field[] flds = fieldClass.getDeclaredFields();
                    List<String> listEnum = new ArrayList<String>();
                    int i = 0;
                    for ( Field f : flds ) {
                        if ( f.isEnumConstant() ) {
                            String shortName = fieldClass.getName().substring( fieldClass.getName().lastIndexOf( "." ) + 1 ) + "." + f.getName();
                            listEnum.add( shortName + "=" + shortName );
                            i++;
                        }
                    }
                    String a[] = new String[listEnum.size()];
                    i = 0;
                    for ( String value : listEnum ) {
                        a[i] = value;
                        i++;
                    }
                    this.instance.putDataEnumList( fieldName,
                                                   a );
                }
            }

        }
        this.instance.setGlobalVariables( this.globalTypes );
        this.instance.actionDSLSentences = makeArray( this.actionDSLSentences );
        this.instance.conditionDSLSentences = makeArray( this.conditionDSLSentences );
        this.instance.keywordDSLItems = makeArray( this.keywordDSLItems );
        this.instance.anyScopeDSLItems = makeArray( this.anyScopeDSLItems );
        this.instance.setGlobalCollections( this.globalCollections.toArray( new String[globalCollections.size()] ) );
        this.instance.setAccessorsAndMutators( accessorsAndMutators );
        this.instance.putAllDataEnumLists( allDataEnumsList );

        return this.instance;
    }

    private DSLSentence[] makeArray(List<DSLSentence> ls) {
        return ls.toArray( new DSLSentence[ls.size()] );
    }

    public void addDSLMapping(DSLMappingEntry entry) {
        DSLSentence sen = new DSLSentence();
        sen.setDefinition( entry.getMappingKey() );
        if ( entry.getSection() == DSLMappingEntry.CONDITION ) {
            this.conditionDSLSentences.add( sen );
        } else if ( entry.getSection() == DSLMappingEntry.CONSEQUENCE ) {
            this.actionDSLSentences.add( sen );
        } else if ( entry.getSection() == DSLMappingEntry.KEYWORD ) {
            this.keywordDSLItems.add( sen );
        } else if ( entry.getSection() == DSLMappingEntry.ANY ) {
            this.anyScopeDSLItems.add( sen );
        }

    }

    public void addFieldAccessorsAndMutatorsForField(Map<String, FieldAccessorsAndMutators> accessorsAndMutators) {
        this.accessorsAndMutators.putAll( accessorsAndMutators );
    }

    public void addAllDataEnumsList(Map<String, String[]> data) {
        this.allDataEnumsList.putAll( data );
    }
}
