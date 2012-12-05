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

package org.kie.guvnor.datamodel.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;

@Portable
public class MockDataModel implements DataModelOracle {

    private Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>();

    private Map<String, String[]> dataEnumLists = new HashMap<String, String[]>();

    private transient Map<String, Object> dataEnumLookupFields;

    public MockDataModel() {
    }

    @Override
    public String[] getFactTypes() {
        return new String[]{ "Applicant", "IncomeSource" };
    }

    @Override
    public String[] getFields( final String typeName ) {
        if ( typeName.equalsIgnoreCase( "Applicant" ) ) {
            return new String[]{ "this", "age", "applicationDate", "creditRating", "name", "approved" };
        }
        return new String[]{ "this", "amount", "type" };
    }

    @Override
    public String[] getEnumValues( String factType,
                                   String factField ) {
        return this.dataEnumLists.get( factType + "." + factField );
    }

    @Override
    public boolean hasEnums( String factType,
                             String factField ) {
        boolean hasEnums = false;
        final String qualifiedFactField = factType + "." + factField;
        final String dependentType = qualifiedFactField + "[";
        for ( String e : this.dataEnumLists.keySet() ) {
            //e.g. Fact.field1
            if ( e.equals( qualifiedFactField ) ) {
                return true;
            }
            //e.g. Fact.field2[field1=val2]
            if ( e.startsWith( dependentType ) ) {
                return true;
            }
        }
        return hasEnums;
    }

    @Override
    public boolean isDependentEnum( String factType,
                                    String factField,
                                    String field ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DropDownData getEnums( String factType,
                                  String factField,
                                  Map<String, String> currentValueMap ) {
        Map<String, Object> dataEnumLookupFields = loadDataEnumLookupFields();

        if ( !currentValueMap.isEmpty() ) {
            // we may need to check for data dependent enums
            Object _typeFields = dataEnumLookupFields.get( factType + "." + factField );

            if ( _typeFields instanceof String ) {
                String typeFields = (String) _typeFields;

                StringBuilder dataEnumListsKeyBuilder = new StringBuilder( factType );
                dataEnumListsKeyBuilder.append( "." ).append( factField );

                boolean addOpeninColumn = true;
                String[] splitTypeFields = typeFields.split( "," );
                for ( int j = 0; j < splitTypeFields.length; j++ ) {
                    String typeField = splitTypeFields[ j ];

                    for ( Map.Entry<String, String> currentValueEntry : currentValueMap.entrySet() ) {
                        String fieldName = currentValueEntry.getKey();
                        String fieldValue = currentValueEntry.getValue();
                        if ( fieldName.trim().equals( typeField.trim() ) ) {
                            if ( addOpeninColumn ) {
                                dataEnumListsKeyBuilder.append( "[" );
                                addOpeninColumn = false;
                            }
                            dataEnumListsKeyBuilder.append( typeField ).append( "=" ).append( fieldValue );

                            if ( j != ( splitTypeFields.length - 1 ) ) {
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

                String queryString = getQueryString( factType,
                                                     factField,
                                                     fieldsNeeded,
                                                     this.dataEnumLists );

                String[] valuePairs = new String[ fieldsNeeded.length ];

                // collect all the values of the fields needed, then return it
                // as a string...
                for ( int i = 0; i < fieldsNeeded.length; i++ ) {
                    for ( Map.Entry<String, String> currentValueEntry : currentValueMap.entrySet() ) {
                        String fieldName = currentValueEntry.getKey();
                        String fieldValue = currentValueEntry.getValue();
                        if ( fieldName.equals( fieldsNeeded[ i ] ) ) {
                            valuePairs[ i ] = fieldsNeeded[ i ] + "=" + fieldValue;
                        }
                    }
                }

                if ( valuePairs.length > 0 && valuePairs[ 0 ] != null ) {
                    return DropDownData.create( queryString,
                                                valuePairs );
                }
            }
        }
        return DropDownData.create( getEnumValues( factType,
                                                   factField ) );
    }

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
                            typeFieldBuilder.append( bits[ i ].substring( 0,
                                                                          bits[ i ].indexOf( '=' ) ) );
                            if ( i != ( bits.length - 1 ) ) {
                                typeFieldBuilder.append( "," );
                            }
                        }

                        dataEnumLookupFields.put( factField,
                                                  typeFieldBuilder.toString() );
                    } else {
                        String[] fields = predicate.split( "," );
                        for ( int i = 0; i < fields.length; i++ ) {
                            fields[ i ] = fields[ i ].trim();
                        }
                        dataEnumLookupFields.put( factField,
                                                  fields );
                    }
                }
            }
        }

        return dataEnumLookupFields;
    }

    String getQueryString( String factType,
                           String field,
                           String[] fieldsNeeded,
                           Map<String, String[]> dataEnumLists ) {
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
                    String a = values[ i ].trim();
                    String b = fieldsNeeded[ i ].trim();
                    if ( !a.equals( b ) ) {
                        fail = true;
                        break;
                    }
                }
                if ( fail ) {
                    continue;
                }

                String[] qry = this.dataEnumLists.get( key );
                return qry[ 0 ];
            } else if ( key.startsWith( factType + "." + field ) && ( fieldsNeeded == null || fieldsNeeded.length == 0 ) ) {
                String[] qry = this.dataEnumLists.get( key );
                return qry[ 0 ];
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean containsFactType( String lhsBindingType ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isFactTypeAnEvent( String factType ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getConnectiveOperatorCompletions( String factType,
                                                      String fieldName ) {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getOperatorCompletions( String factType,
                                            String fieldName ) {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getFieldCompletions( String factType ) {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getFactNameFromType( String classType ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getMethodFullNames( String factName,
                                            int i ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getGlobalVariables() {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isGlobalVariable( String variable ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getFieldCompletionsForGlobalVariable( String variable ) {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getGlobalVariable( String variable ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getFieldType( String factType,
                                String factField ) {
        ModelField field = null;
        ModelField[] fields = this.modelFields.get( factType );
        if ( fields == null ) {
            return null;
        }
        for ( ModelField modelField : fields ) {
            if ( modelField.getName().equals( factField ) ) {
                field = modelField;
            }
        }
        return field == null ? null : field.getType();
    }

    @Override
    public List<String> getMethodNames( String factType ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<? extends String> getMethodParams( String variableClass,
                                                         String methodNameWithParams ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DSLSentence[] getDSLActions() {
        return new DSLSentence[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getGlobalCollections() {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DSLSentence[] getDSLConditions() {
        return new DSLSentence[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getParametricFieldType( String factType,
                                          String fieldName ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getFieldCompletions( FieldAccessorsAndMutators accessor,
                                         String factType ) {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<MethodInfo> getMethodInfosForGlobalVariable( String variable ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MethodInfo getMethodinfo( String factName,
                                     String methodName ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getFieldClassName( String factName,
                                     String fieldName ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setFieldsForTypes( Map<String, ModelField[]> fieldsForType ) {
        this.modelFields.clear();
        this.modelFields.putAll( fieldsForType );
    }

    public void putDataEnumList( String name,
                                 String[] value ) {
        this.dataEnumLists.put( name,
                                value );
    }

}
