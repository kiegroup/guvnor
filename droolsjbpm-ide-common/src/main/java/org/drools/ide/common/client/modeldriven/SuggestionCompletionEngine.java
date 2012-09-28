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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.shared.api.PortableObject;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;

/**
 * An suggestion completion processor. This should be usable in both GWT/Web and
 * the IDE. The data for this can be loaded into this from simple string lists.
 */
public class SuggestionCompletionEngine
    implements
        PortableObject {

    /** These are the explicit types supported */
    public static final String                      TYPE_COLLECTION          = "Collection";
    public static final String                      TYPE_COMPARABLE          = "Comparable";
    public static final String                      TYPE_STRING              = "String";
    public static final String                      TYPE_NUMERIC             = "Numeric";
    public static final String                      TYPE_NUMERIC_BIGDECIMAL  = "BigDecimal";
    public static final String                      TYPE_NUMERIC_BIGINTEGER  = "BigInteger";
    public static final String                      TYPE_NUMERIC_BYTE        = "Byte";
    public static final String                      TYPE_NUMERIC_DOUBLE      = "Double";
    public static final String                      TYPE_NUMERIC_FLOAT       = "Float";
    public static final String                      TYPE_NUMERIC_INTEGER     = "Integer";
    public static final String                      TYPE_NUMERIC_LONG        = "Long";
    public static final String                      TYPE_NUMERIC_SHORT       = "Short";
    public static final String                      TYPE_BOOLEAN             = "Boolean";
    public static final String                      TYPE_DATE                = "Date";
    public static final String                      TYPE_OBJECT              = "Object";                                                                                                                                                      // for all other unknown
    public static final String                      TYPE_FINAL_OBJECT        = "FinalObject";                                                                                                                                                 // for all other unknown
    public static final String                      TYPE_THIS                = "this";

    //Standard annotations
    public static final String                      ANNOTATION_ROLE          = "role";
    public static final String                      ANNOTATION_ROLE_EVENT    = "event";

    /**
     * The operators that are used at different times (based on type).
     */

    private static final String[]                   STANDARD_OPERATORS       = new String[]{"==", "!=", "== null", "!= null"};

    private static final String[]                   COMPARABLE_OPERATORS     = new String[]{"==", "!=", "<", ">", "<=", ">=", "== null", "!= null"};

    private static final String[]                   STRING_OPERATORS         = new String[]{"==", "!=", "<", ">", "<=", ">=", "matches", "soundslike", "== null", "!= null"};

    private static final String[]                   EXPLICIT_LIST_OPERATORS  = new String[]{"in", "not in"};

    private static final String[]                   COLLECTION_OPERATORS     = new String[]{"contains", "excludes", "==", "!=", "== null", "!= null"};

    private static final String[]                   SIMPLE_CEP_OPERATORS     = new String[]{"after", "before", "coincides"};

    private static final String[]                   COMPLEX_CEP_OPERATORS    = new String[]{"during", "finishes", "finishedby", "includes", "meets", "metby", "overlaps", "overlappedby", "starts", "startedby"};

    private static final String[]                   WINDOW_CEP_OPERATORS     = new String[]{"over window:time", "over window:length"};

    private static final String[]                   STANDARD_CONNECTIVES     = new String[]{"|| ==", "|| !=", "&& !="};

    private static final String[]                   STRING_CONNECTIVES       = new String[]{"|| ==", "|| !=", "&& !=", "&& >", "&& <", "|| >", "|| <", "&& >=", "&& <=", "|| <=", "|| >=", "&& matches", "|| matches"};

    private static final String[]                   COMPARABLE_CONNECTIVES   = new String[]{"|| ==", "|| !=", "&& !=", "&& >", "&& <", "|| >", "|| <", "&& >=", "&& <=", "|| <=", "|| >="};

    private static final String[]                   COLLECTION_CONNECTIVES   = new String[]{"|| ==", "|| !=", "&& !=", "|| contains", "&& contains", "|| excludes", "&& excludes"};

    private static final String[]                   SIMPLE_CEP_CONNECTIVES   = new String[]{"|| after", "|| before", "|| coincides", "&& after", "&& before", "&& coincides"};

    private static final String[]                   COMPLEX_CEP_CONNECTIVES  = new String[]{"|| during", "|| finishes", "|| finishedby", "|| includes", "|| meets", "|| metby", "|| overlaps", "|| overlappedby", "|| starts", "|| startedby",
                                                                                           "&& during", "&& finishes", "&& finishedby", "&& includes", "&& meets", "&& metby", "&& overlaps", "&& overlappedby", "&& starts", "&& startedby"};

    private static final Map<String, List<Integer>> CEP_OPERATORS_PARAMETERS = new HashMap<String, List<Integer>>();
    {
        CEP_OPERATORS_PARAMETERS.put( "after",
                                      Arrays.asList( new Integer[]{0, 1, 2} ) );
        CEP_OPERATORS_PARAMETERS.put( "before",
                                      Arrays.asList( new Integer[]{0, 1, 2} ) );
        CEP_OPERATORS_PARAMETERS.put( "coincides",
                                      Arrays.asList( new Integer[]{0, 1, 2} ) );
        CEP_OPERATORS_PARAMETERS.put( "during",
                                      Arrays.asList( new Integer[]{0, 1, 2, 4} ) );
        CEP_OPERATORS_PARAMETERS.put( "finishes",
                                      Arrays.asList( new Integer[]{0, 1} ) );
        CEP_OPERATORS_PARAMETERS.put( "finishedby",
                                      Arrays.asList( new Integer[]{0, 1} ) );
        CEP_OPERATORS_PARAMETERS.put( "includes",
                                      Arrays.asList( new Integer[]{0, 1, 2, 4} ) );
        CEP_OPERATORS_PARAMETERS.put( "meets",
                                      Arrays.asList( new Integer[]{0, 1} ) );
        CEP_OPERATORS_PARAMETERS.put( "metby",
                                      Arrays.asList( new Integer[]{0, 1} ) );
        CEP_OPERATORS_PARAMETERS.put( "overlaps",
                                      Arrays.asList( new Integer[]{0, 1, 2} ) );
        CEP_OPERATORS_PARAMETERS.put( "overlappedby",
                                      Arrays.asList( new Integer[]{0, 1, 2} ) );
        CEP_OPERATORS_PARAMETERS.put( "starts",
                                      Arrays.asList( new Integer[]{0, 1} ) );
        CEP_OPERATORS_PARAMETERS.put( "startedby",
                                      Arrays.asList( new Integer[]{0, 1} ) );
    }

    /** The top level conditional elements (first order logic) */
    private static final String[]                   CONDITIONAL_ELEMENTS     = new String[]{"not", "exists", "or"};

    /**
     * A map of the field that contains the parametrized type of a collection
     * List<String> name key = "name" value = "String"
     */
    private Map<String, String>                     fieldParametersType      = new HashMap<String, String>();

    /**
     * Contains a map of globals (name is key) and their type (value).
     */
    private Map<String, String>                     globalTypes              = new HashMap<String, String>();

    /**
     * Contains a map of { TypeName.field : String[] } - where a list is valid
     * values to display in a drop down for a given Type.field combination.
     */
    private Map<String, String[]>                   dataEnumLists            = new HashMap<String, String[]>();                                                                                                                               // TODO this is
    // a PROBLEM as its not always String[]

    /**
     * A map of Annotations for FactTypes. Key is FactType, value is list of
     * annotations
     */
    private Map<String, List<ModelAnnotation>>      annotationsForTypes      = new HashMap<String, List<ModelAnnotation>>();

    /**
     * This will show the names of globals that are a collection type.
     */
    private String[]                                globalCollections;

    /**
     * DSL language extensions, if needed, if provided by the package.
     */
    public DSLSentence[]                            conditionDSLSentences    = new DSLSentence[0];
    public DSLSentence[]                            actionDSLSentences       = new DSLSentence[0];
    public DSLSentence[]                            keywordDSLItems          = new DSLSentence[0];
    public DSLSentence[]                            anyScopeDSLItems         = new DSLSentence[0];

    /**
     * This is used to calculate what fields an enum list may depend on.
     * Optional.
     */
    private transient Map<String, Object>           dataEnumLookupFields;

    // /**
    // * For bulk loading up the data (from a previous rule save)
    // *
    // * @param factToFields A map of "FactType" (key - String) to String[]
    // (value)
    // * @param factFieldToOperator A map of "FactType.field" (key - String) to
    // String[] operators
    // * @param factFieldToConnectiveOperator A map of "FactType.field" (key
    // -String) to String[] operators
    // * that are valid CONNECTIVE operators.
    // *
    // * @param globals A map of global variable name to its fields (String[]).
    // * @param boundFacts A map of bound facts to types.
    // * @param conditionDSLs a list of DSLSentence suggestions for the LHS
    // * @param actionDSLs a list of DSLSentence suggestions for the RHS
    // *
    // */
    // public void load(
    // Map factToFields,
    // Map factFieldToOperator,
    // Map factFieldToConnectiveOperator,
    // Map globals,
    // List conditionDSLs,
    // List actionDSLs
    // ) {
    // this.factToFields = factToFields;
    // this.factFieldToOperator = factFieldToOperator;
    // this.factFieldToConnectiveOperator = factFieldToConnectiveOperator;
    // this.actionDSLSentences = actionDSLs;
    // this.conditionDSLSentences = conditionDSLs;
    // this.globals = globals;
    //
    // }

    private Map<String, List<MethodInfo>>           methodInfos              = new HashMap<String, List<MethodInfo>>();

    private Map<String, ModelField[]>               modelFields              = new HashMap<String, ModelField[]>();
    private Map<String, ModelField[]>               filterModelFields        = null;

    private Map<String, FieldAccessorsAndMutators>  accessorsAndMutators     = new HashMap<String, FieldAccessorsAndMutators>();
    private FactTypeFilter                          factFilter               = null;
    private boolean                                 filteringFacts           = true;

    public SuggestionCompletionEngine() {

    }

    public String[] getConditionalElements() {
        return CONDITIONAL_ELEMENTS;
    }

    public DSLSentence[] getDSLConditions() {
        return this.conditionDSLSentences;
    }

    public DSLSentence[] getDSLActions() {
        return this.actionDSLSentences;
    }

    public String[] getConnectiveOperatorCompletions(final String factType,
                                                     final String fieldName) {
        final String fieldType = this.getFieldType( factType + "." + fieldName );

        if ( fieldType == null ) {
            return STANDARD_CONNECTIVES;
        } else if ( fieldName.equals( TYPE_THIS ) ) {
            if ( this.isFactTypeAnEvent( factType ) ) {
                return joinArrays( STANDARD_CONNECTIVES,
                                   SIMPLE_CEP_CONNECTIVES,
                                   COMPLEX_CEP_CONNECTIVES );
            } else {
                return STANDARD_CONNECTIVES;
            }
        } else if ( fieldType.equals( TYPE_STRING ) ) {
            return STRING_CONNECTIVES;
        } else if ( isNumeric( fieldType ) ) {
            return COMPARABLE_CONNECTIVES;
        } else if ( fieldType.equals( TYPE_DATE ) ) {
            return joinArrays( COMPARABLE_CONNECTIVES,
                               SIMPLE_CEP_CONNECTIVES );
        } else if ( fieldType.equals( TYPE_COMPARABLE ) ) {
            return COMPARABLE_CONNECTIVES;
        } else if ( fieldType.equals( TYPE_COLLECTION ) ) {
            return COLLECTION_CONNECTIVES;
        } else {
            return STANDARD_CONNECTIVES;
        }

    }

    public String[] getFieldCompletions(final String factType) {
        return this.getModelFields( factType );
    }

    public String[] getFieldCompletions(FieldAccessorsAndMutators accessorOrMutator,
                                        String factType) {
        return this.getModelFields( accessorOrMutator,
                                    factType );
    }

    public String[] getOperatorCompletions(final String factType,
                                           final String fieldName) {

        String fieldType = getFieldType( factType,
                                         fieldName );

        if ( fieldType == null ) {
            return STANDARD_OPERATORS;
        } else if ( fieldName.equals( TYPE_THIS ) ) {
            if ( this.isFactTypeAnEvent( factType ) ) {
                return joinArrays( STANDARD_OPERATORS,
                                   SIMPLE_CEP_OPERATORS,
                                   COMPLEX_CEP_OPERATORS );
            } else {
                return STANDARD_OPERATORS;
            }
        } else if ( fieldType.equals( TYPE_STRING ) ) {
            return joinArrays( STRING_OPERATORS,
                               EXPLICIT_LIST_OPERATORS );
        } else if ( SuggestionCompletionEngine.isNumeric( fieldType ) ) {
            return joinArrays( COMPARABLE_OPERATORS,
                               EXPLICIT_LIST_OPERATORS );
        } else if ( fieldType.equals( TYPE_DATE ) ) {
            return joinArrays( COMPARABLE_OPERATORS,
                               EXPLICIT_LIST_OPERATORS,
                               SIMPLE_CEP_OPERATORS );
        } else if ( fieldType.equals( TYPE_COMPARABLE ) ) {
            return COMPARABLE_OPERATORS;
        } else if ( fieldType.equals( TYPE_COLLECTION ) ) {
            return COLLECTION_OPERATORS;
        } else {
            return STANDARD_OPERATORS;
        }
    }

    public static boolean isNumeric(String type) {
        if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BIGDECIMAL ) ) {
            return true;
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BIGINTEGER ) ) {
            return true;
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BYTE ) ) {
            return true;
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_DOUBLE ) ) {
            return true;
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT ) ) {
            return true;
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER ) ) {
            return true;
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_LONG ) ) {
            return true;
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_SHORT ) ) {
            return true;
        }
        return false;
    }

    public String[] getFieldCompletionsForGlobalVariable(final String varName) {
        final String type = this.getGlobalVariable( varName );
        return this.getModelFields( type );
    }

    public List<MethodInfo> getMethodInfosForGlobalVariable(final String varName) {
        final String type = this.getGlobalVariable( varName );
        return this.methodInfos.get( type );
    }

    private String[] toStringArray(final Set< ? > set) {
        final String[] f = new String[set.size()];
        int i = 0;
        for ( final Iterator< ? > iter = set.iterator(); iter.hasNext(); i++ ) {
            f[i] = iter.next().toString();
        }
        return f;
    }

    /**
     * This returns a list of enums options (values) that can be used for the
     * given field of the given FactPattern. This also takes into account enums
     * that depend on other fields.
     */
    public DropDownData getEnums(String factType,
                                 CompositeFieldConstraint constraintList,
                                 String field) {

        if ( field == null ) {
            return null;
        }
        Map<String, String> currentValueMap = new HashMap<String, String>();

        if ( constraintList != null && constraintList.constraints != null ) {
            for ( FieldConstraint con : constraintList.constraints ) {
                if ( con instanceof SingleFieldConstraint ) {
                    SingleFieldConstraint sfc = (SingleFieldConstraint) con;
                    String fieldName = sfc.getFieldName();
                    currentValueMap.put( fieldName,
                                         sfc.getValue() );
                }
            }
        }
        return getEnums( factType,
                         field,
                         currentValueMap );
    }

    /**
     * Similar to the one above - but this one is for RHS.
     */
    public DropDownData getEnums(String type,
                                 String field,
                                 FieldNature[] currentFieldNatures) {
        Map<String, String> currentValueMap = new HashMap<String, String>();

        if ( currentFieldNatures != null ) {
            for ( FieldNature currentFieldNature : currentFieldNatures ) {
                currentValueMap.put( currentFieldNature.getField(),
                                     currentFieldNature.getValue() );
            }
        }
        return getEnums( type,
                         field,
                         currentValueMap );
    }

    /**
     * This returns a list of enums options (values) that can be used for the
     * given field of the given FactPattern. This also takes into account enums
     * that depend on other fields.
     */
    public DropDownData getEnums(String type,
                                 String field,
                                 Map<String, String> currentValueMap) {

        Map<String, Object> dataEnumLookupFields = loadDataEnumLookupFields();

        if ( !currentValueMap.isEmpty() ) {
            // we may need to check for data dependent enums
            Object _typeFields = dataEnumLookupFields.get( type + "." + field );

            if ( _typeFields instanceof String ) {
                String typeFields = (String) _typeFields;

                StringBuilder dataEnumListsKeyBuilder = new StringBuilder( type );
                dataEnumListsKeyBuilder.append( "." ).append( field );

                boolean addOpeninColumn = true;
                String[] splitTypeFields = typeFields.split( "," );
                for ( int j = 0; j < splitTypeFields.length; j++ ) {
                    String typeField = splitTypeFields[j];

                    for ( Map.Entry<String, String> currentValueEntry : currentValueMap.entrySet() ) {
                        String fieldName = currentValueEntry.getKey();
                        String fieldValue = currentValueEntry.getValue();
                        if ( fieldName.trim().equals( typeField.trim() ) ) {
                            if ( addOpeninColumn ) {
                                dataEnumListsKeyBuilder.append( "[" );
                                addOpeninColumn = false;
                            }
                            dataEnumListsKeyBuilder.append( typeField ).append( "=" ).append( fieldValue );

                            if ( j != (splitTypeFields.length - 1) ) {
                                dataEnumListsKeyBuilder.append( "," );
                            }
                        }
                    }
                }

                if ( !addOpeninColumn ) {
                    dataEnumListsKeyBuilder.append( "]" );
                }

                DropDownData data = DropDownData.create( this.dataEnumLists.get( dataEnumListsKeyBuilder.toString() ) );
                if ( data != null ) {
                    return data;
                }
            } else if ( _typeFields != null ) {
                // these enums are calculated on demand, server side...
                String[] fieldsNeeded = (String[]) _typeFields;

                String queryString = getQueryString( type,
                                                     field,
                                                     fieldsNeeded,
                                                     this.dataEnumLists );

                String[] valuePairs = new String[fieldsNeeded.length];

                // collect all the values of the fields needed, then return it
                // as a string...
                for ( int i = 0; i < fieldsNeeded.length; i++ ) {
                    for ( Map.Entry<String, String> currentValueEntry : currentValueMap.entrySet() ) {
                        String fieldName = currentValueEntry.getKey();
                        String fieldValue = currentValueEntry.getValue();
                        if ( fieldName.equals( fieldsNeeded[i] ) ) {
                            valuePairs[i] = fieldsNeeded[i] + "=" + fieldValue;
                        }
                    }
                }

                if ( valuePairs.length > 0 && valuePairs[0] != null ) {
                    return DropDownData.create( queryString,
                                                valuePairs );
                }
            }
        }
        return DropDownData.create( getEnumValues( type,
                                                   field ) );
    }

    /**
     * Get the query string for a fact.field It will ignore any specified field,
     * and just look for the string - as there should only be one Fact.field of
     * this type (it is all determined server side).
     * 
     * @param fieldsNeeded
     */
    String getQueryString(String factType,
                          String field,
                          String[] fieldsNeeded,
                          Map<String, String[]> dataEnumLists) {
        for ( Iterator<String> iterator = dataEnumLists.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            if ( key.startsWith( factType + "." + field ) && fieldsNeeded != null && key.contains( "[" ) ) {

                String[] values = key.substring( key.indexOf( '[' ) + 1,
                                                 key.lastIndexOf( ']' ) ).split( "," );

                if ( values.length != fieldsNeeded.length ) {
                    continue;
                }

                boolean fail = false;
                for ( int i = 0; i < values.length; i++ ) {
                    String a = values[i].trim();
                    String b = fieldsNeeded[i].trim();
                    if ( !a.equals( b ) ) {
                        fail = true;
                        break;
                    }
                }
                if ( fail ) {
                    continue;
                }

                String[] qry = getDataEnumList( key );
                return qry[0];
            } else if ( key.startsWith( factType + "." + field ) && (fieldsNeeded == null || fieldsNeeded.length == 0) ) {
                String[] qry = getDataEnumList( key );
                return qry[0];
            }
        }
        throw new IllegalStateException();
    }

    /**
     * For simple cases - where a list of values are known based on a field.
     */
    public String[] getEnumValues(String factType,
                                  String field) {
        return this.getDataEnumList( factType + "." + field );
    }

    public boolean hasEnums(String factType,
                            String field) {
        return this.hasEnums( factType + "." + field );
    }

    public boolean hasEnums(String type) {
        boolean hasEnums = false;
        final String dependentType = type + "[";
        for ( String e : this.dataEnumLists.keySet() ) {
            //e.g. Fact.field1
            if ( e.equals( type ) ) {
                return true;
            }
            //e.g. Fact.field2[field1=val2]
            if ( e.startsWith( dependentType ) ) {
                return true;
            }
        }
        return hasEnums;
    }

    /**
     * Check whether the childField is related to the parentField through a
     * chain of enumeration dependencies. Both fields belong to the same Fact
     * Type. Furthermore code consuming this function should ensure both
     * parentField and childField relate to the same Fact Pattern
     * 
     * @param factType
     * @param baseField
     * @param childField
     * @return
     */
    public boolean isDependentEnum(String factType,
                                   String parentField,
                                   String childField) {
        Map<String, Object> enums = loadDataEnumLookupFields();
        if ( enums.isEmpty() ) {
            return false;
        }
        //Check if the childField is a direct descendant of the parentField
        final String key = factType + "." + childField;
        if ( !enums.containsKey( key ) ) {
            return false;
        }

        //Otherwise follow the dependency chain...
        final Object _parent = enums.get( key );
        if ( _parent instanceof String ) {
            final String _parentField = (String) _parent;
            if ( _parentField.equals( parentField ) ) {
                return true;
            } else {
                return isDependentEnum( factType,
                                        parentField,
                                        _parentField );
            }
        }
        return false;
    }

    /**
     * This is only used by enums that are like Fact.field[something=X] and so
     * on.
     */
    Map<String, Object> loadDataEnumLookupFields() {
        if ( this.dataEnumLookupFields == null ) {
            this.dataEnumLookupFields = new HashMap<String, Object>();
            Set<String> keys = this.dataEnumLists.keySet();
            for ( String key : keys ) {
                if ( key.indexOf( '[' ) != -1 ) {
                    int ix = key.indexOf( '[' );
                    String factField = key.substring( 0,
                                                      ix );
                    String predicate = key.substring( ix + 1,
                                                      key.indexOf( ']' ) );
                    if ( predicate.indexOf( '=' ) > -1 ) {

                        String[] bits = predicate.split( "," );
                        StringBuilder typeFieldBuilder = new StringBuilder();

                        for ( int i = 0; i < bits.length; i++ ) {
                            typeFieldBuilder.append( bits[i].substring( 0,
                                                                        bits[i].indexOf( '=' ) ) );
                            if ( i != (bits.length - 1) ) {
                                typeFieldBuilder.append( "," );
                            }
                        }

                        dataEnumLookupFields.put( factField,
                                                  typeFieldBuilder.toString() );
                    } else {
                        String[] fields = predicate.split( "," );
                        for ( int i = 0; i < fields.length; i++ ) {
                            fields[i] = fields[i].trim();
                        }
                        dataEnumLookupFields.put( factField,
                                                  fields );
                    }
                }
            }
        }

        return dataEnumLookupFields;
    }

    public void addMethodInfo(String factName,
                              List<MethodInfo> methodInfos) {
        this.methodInfos.put( factName,
                              methodInfos );
    }

    public List<String> getMethodParams(String factName,
                                        String methodNameWithParams) {
        if ( methodInfos.get( factName ) != null ) {
            List<MethodInfo> infos = methodInfos.get( factName );

            for ( MethodInfo info : infos ) {
                if ( info.getNameWithParameters().startsWith( methodNameWithParams ) ) {
                    return info.getParams();
                }
            }
        }

        return null;
    }

    public List<String> getMethodNames(String factName) {
        List<MethodInfo> infos = methodInfos.get( factName );
        List<String> methodList = new ArrayList<String>();

        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                methodList.add( info.getName() );
            }
        }

        return methodList;
    }

    public MethodInfo getMethodinfo(String factName,
                                    String methodFullName) {
        List<MethodInfo> infos = methodInfos.get( factName );

        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( info.getNameWithParameters().equals( methodFullName ) ) {
                    return info;
                }
            }
        }

        return null;
    }

    public String getMethodClassType(String factName,
                                     String methodFullName) {
        List<MethodInfo> infos = methodInfos.get( factName );

        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( info.getNameWithParameters().equals( methodFullName ) ) {
                    return info.getReturnClassType();
                }
            }
        }

        return null;
    }

    public List<String> getMethodFullNames(String factName) {
        return getMethodFullNames( factName,
                                   -1 );
    }

    public List<String> getMethodFullNames(String factName,
                                           int paramCount) {
        List<MethodInfo> infos = methodInfos.get( factName );
        List<String> methodList = new ArrayList<String>();

        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( paramCount == -1 || info.getParams().size() <= paramCount ) {
                    methodList.add( info.getNameWithParameters() );
                }
            }
        }

        return methodList;
    }

    /**
     * Returns fact's name from class type
     * 
     * @param type
     * @return
     */
    public String getFactNameFromType(String type) {
        if ( type == null ) {
            return null;
        }
        if ( getModelFields().containsKey( type ) ) {
            return type;
        }
        for ( Map.Entry<String, ModelField[]> entry : getModelFields().entrySet() ) {
            for ( ModelField mf : entry.getValue() ) {
                if ( TYPE_THIS.equals( mf.getName() ) && type.equals( mf.getClassName() ) ) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * returns the type of parametric class List<String> a in a class called
     * Toto key = "Toto.a" value = "String"
     */
    public String getParametricFieldType(final String factType,
                                         final String fieldName) {
        return this.getParametricFieldType( factType + "." + fieldName );
    }

    public String getParametricFieldType(String fieldName) {
        return this.fieldParametersType.get( fieldName );
    }

    public void putParametricFieldType(String fieldName,
                                       String type) {
        this.fieldParametersType.put( fieldName,
                                      type );
    }

    public String getGlobalVariable(String name) {
        return this.globalTypes.get( name );
    }

    public boolean isGlobalVariable(String name) {
        return this.globalTypes.containsKey( name );
    }

    public void setGlobalVariables(Map<String, String> globalTypes) {
        this.globalTypes = globalTypes;
    }

    public String[] getGlobalVariables() {
        return toStringArray( this.globalTypes.keySet() );
    }

    public void setGlobalCollections(String[] globalCollections) {
        this.globalCollections = globalCollections;
    }

    public String[] getGlobalCollections() {
        return this.globalCollections;
    }

    public String[] getDataEnumList(String type) {
        return this.dataEnumLists.get( type );
    }

    public void setDataEnumLists(Map<String, String[]> data) {
        this.dataEnumLists = data;
    }

    public void putDataEnumList(String name,
                                String[] value) {
        this.dataEnumLists.put( name,
                                value );
    }

    public void putAllDataEnumLists(Map<String, String[]> value) {
        this.dataEnumLists.putAll( value );
    }

    public int getDataEnumListsSize() {
        return this.dataEnumLists.size();
    }

    public boolean hasDataEnumLists() {
        return this.dataEnumLists != null && this.dataEnumLists.size() > 0;
    }

    public void setAnnotationsForTypes(final Map<String, List<ModelAnnotation>> annotationsForTypes) {
        this.annotationsForTypes = annotationsForTypes;
    }

    public void setFactTypes(String[] factTypes) {
        for ( String factType : factTypes ) {
            //adds the fact type with no fields.
            this.getModelFields().put( factType,
                                       new ModelField[0] );
        }
    }

    public void setFactTypeFilter(FactTypeFilter filter) {
        this.factFilter = filter;
        filterModelFields();
    }

    public void setFieldsForTypes(Map<String, ModelField[]> fieldsForType) {
        this.getModelFields().clear();
        this.getModelFields().putAll( fieldsForType );
    }

    /**
     * Returns all the fact types.
     * 
     * @return
     */
    public String[] getFactTypes() {
        String[] types = this.getModelFields().keySet().toArray( new String[this.getModelFields().size()] );
        Arrays.sort( types );
        return types;
    }

    /**
     * Return a list of annotations for a FactType
     * 
     * @param factType
     * @return
     */
    public List<ModelAnnotation> getAnnotationsForFactType(String factType) {
        return this.annotationsForTypes.get( factType );
    }

    /**
     * Return a Map of FactTypes (key) and a List (value) of their corresponding
     * annotations.
     * 
     * @return
     */
    public Map<String, List<ModelAnnotation>> getAnnotations() {
        return this.annotationsForTypes;
    }

    /**
     * Check whether a given FactType has been annotated as an Event
     * 
     * @param factType
     * @return
     */
    public boolean isFactTypeAnEvent(String factType) {
        List<ModelAnnotation> annotations = this.annotationsForTypes.get( factType );
        if ( annotations == null || annotations.size() == 0 ) {
            return false;
        }
        for ( ModelAnnotation ma : annotations ) {
            if ( ma.getAnnotationName().equals( ANNOTATION_ROLE ) ) {
                for ( String v : ma.getAnnotationValues().values() ) {
                    if ( v.equals( ANNOTATION_ROLE_EVENT ) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean containsFactType(String modelClassName) {
        if ( modelClassName == null ) {
            return false;
        }
        if ( modelClassName.contains( "." ) ) {
            modelClassName = modelClassName.substring( modelClassName.lastIndexOf( "." ) + 1 );
        }
        return this.getModelFields().containsKey( modelClassName );
    }

    public ModelField getField(String modelClassName,
                                String fieldName) {

        String shortName = this.getFactNameFromType( modelClassName );

        ModelField[] fields = this.getModelFields().get( shortName );

        if ( fields == null ) {
            return null;
        }

        for ( ModelField modelField : fields ) {
            if ( modelField.getName().equals( fieldName ) ) {
                return modelField;
            }
        }

        return null;
    }

    public String[] getModelFields(FieldAccessorsAndMutators accessorOrMutator,
                                   String modelClassName) {

        String shortName = this.getFactNameFromType( modelClassName );

        if ( !this.getModelFields().containsKey( shortName ) ) {
            return new String[0];
        }

        ModelField[] fields = this.getModelFields().get( shortName );

        List<String> fieldNames = new ArrayList<String>();

        for ( int i = 0; i < fields.length; i++ ) {
            String fieldName = fields[i].getName();
            if ( FieldAccessorsAndMutators.compare( accessorOrMutator,
                                                    this.accessorsAndMutators.get( shortName + "." + fieldName ) ) ) {
                fieldNames.add( fieldName );
            }
        }

        return fieldNames.toArray( new String[fieldNames.size()] );
    }

    public String[] getModelFields(String modelClassName) {

        String shortName = this.getFactNameFromType( modelClassName );

        if ( !this.getModelFields().containsKey( shortName ) ) {
            return new String[0];
        }

        ModelField[] fields = this.getModelFields().get( shortName );

        String[] fieldNames = new String[fields.length];

        for ( int i = 0; i < fields.length; i++ ) {
            fieldNames[i] = fields[i].getName();
        }

        return fieldNames;
    }

    /**
     * @param propertyName
     *            of the type class.field
     * @return
     */
    public String getFieldClassName(String propertyName) {
        String[] split = propertyName.split( "\\." );
        if ( split.length != 2 ) {
            throw new IllegalArgumentException( "Invalid format '" + propertyName + "'. It must be of type className.propertyName" );
        }
        return this.getFieldClassName( split[0],
                                       split[1] );
    }

    public String getFieldClassName(String modelClassName,
                                    String fieldName) {
        ModelField field = this.getField( modelClassName,
                                          fieldName );
        return field == null ? null : field.getClassName();
    }

    public ModelField.FIELD_CLASS_TYPE getFieldClassType(String modelClassName,
                                                         String fieldName) {
        ModelField field = this.getField( modelClassName,
                                          fieldName );
        return field == null ? null : field.getClassType();
    }

    public String getFieldType(String propertyName) {
        String[] split = propertyName.split( "\\.",
                                             3 );
        if ( split.length != 2 ) {
            throw new IllegalArgumentException( "Invalid format '" + propertyName + "'. It must be of type className.propertyName" );
        }
        return this.getFieldType( split[0],
                                  split[1] );
    }

    public String getFieldType(String modelClassName,
                               String fieldName) {
        ModelField field = this.getField( modelClassName,
                                          fieldName );
        return field == null ? null : field.getType();
    }

    public void setAccessorsAndMutators(Map<String, FieldAccessorsAndMutators> accessorsAndMutators) {
        this.accessorsAndMutators = accessorsAndMutators;
    }

    public void setModelFields(Map<String, ModelField[]> modelFields) {
        this.modelFields = modelFields;
        filterModelFields();
    }

    private void filterModelFields() {
        if ( factFilter != null ) {
            filterModelFields = new HashMap<String, ModelField[]>();
            for ( Map.Entry<String, ModelField[]> entry : modelFields.entrySet() ) {
                if ( !factFilter.filter( entry.getKey() ) ) {
                    filterModelFields.put( entry.getKey(),
                                           entry.getValue() );
                }
            }
        }
    }

    public Map<String, ModelField[]> getModelFields() {
        if ( factFilter != null && isFilteringFacts() ) {
            return filterModelFields;
        }
        return modelFields;
    }

    public boolean isFilteringFacts() {
        return filteringFacts;
    }

    public void setFilteringFacts(boolean filterFacts) {
        this.filteringFacts = filterFacts;
    }

    /**
     * Check whether an operator is a CEP operator
     * 
     * @param operator
     * @return True if the operator is a CEP operator
     */
    public static boolean isCEPOperator(String operator) {
        if ( operator == null ) {
            return false;
        }

        String[] operators = joinArrays( SIMPLE_CEP_OPERATORS,
                                         COMPLEX_CEP_OPERATORS,
                                         SIMPLE_CEP_CONNECTIVES,
                                         COMPLEX_CEP_CONNECTIVES );

        for ( int i = 0; i < operators.length; i++ ) {
            if ( operator.equals( operators[i] ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the parameter sets for the given CEP Operator (simple, or connective)
     * e.g. CEP operator "during" requires 0, 1, 2 or 4 parameters so the
     * returned list contains 0, 1, 2 and 4.
     * 
     * @param operator
     * @return
     */
    public static List<Integer> getCEPOperatorParameterSets(String operator) {
        List<Integer> sets = new ArrayList<Integer>();
        if ( operator == null ) {
            return sets;
        }
        if ( operator.startsWith( "|| " ) || operator.startsWith( "&& " ) ) {
            operator = operator.substring( 3 );
        }
        if ( !CEP_OPERATORS_PARAMETERS.containsKey( operator ) ) {
            return sets;
        }

        return CEP_OPERATORS_PARAMETERS.get( operator );
    }

    /**
     * Return a list of operators applicable to CEP windows
     * 
     * @return
     */
    public static List<String> getCEPWindowOperators() {
        return Arrays.asList( WINDOW_CEP_OPERATORS );
    }

    /**
     * Check whether an operator is a CEP 'window' operator
     * 
     * @param operator
     * @return True if the operator is a CEP 'window' operator
     */
    public static boolean isCEPWindowOperator(String operator) {
        if ( operator == null ) {
            return false;
        }

        for ( String cepWindowOperator : WINDOW_CEP_OPERATORS ) {
            if ( operator.equals( cepWindowOperator ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the operator is 'window over:time'
     * 
     * @param operator
     * @return if
     */
    public static boolean isCEPWindowOperatorTime(String operator) {
        if ( operator == null ) {
            return false;
        }
        return WINDOW_CEP_OPERATORS[0].equals( operator );
    }

    /**
     * Check if the operator is 'window over:length'
     * 
     * @param operator
     * @return if
     */
    public static boolean isCEPWindowOperatorLength(String operator) {
        if ( operator == null ) {
            return false;
        }
        return WINDOW_CEP_OPERATORS[1].equals( operator );
    }

    //Join an arbitrary number of arrays together
    private static String[] joinArrays(String[] first,
                                       String[]... others) {
        int totalLength = first.length;
        for ( String[] other : others ) {
            totalLength = totalLength + other.length;
        }
        String[] result = new String[totalLength];

        System.arraycopy( first,
                          0,
                          result,
                          0,
                          first.length );
        int offset = first.length;
        for ( String[] other : others ) {
            System.arraycopy( other,
                              0,
                              result,
                              offset,
                              other.length );
            offset = offset + other.length;
        }
        return result;
    }

    /**
     * Check whether an operator requires a list of values (i.e. the operator is
     * either "in" or "not in"). Operators requiring a list of values can only
     * be compared to literal values.
     * 
     * @param operator
     * @return True if the operator requires a list values
     */
    public static boolean operatorRequiresList(String operator) {
        if ( operator == null || operator.equals( "" ) ) {
            return false;
        }
        for ( String explicitListOperator : EXPLICIT_LIST_OPERATORS ) {
            if ( operator.equals( explicitListOperator ) ) {
                return true;
            }
        }
        return false;
    }

}
