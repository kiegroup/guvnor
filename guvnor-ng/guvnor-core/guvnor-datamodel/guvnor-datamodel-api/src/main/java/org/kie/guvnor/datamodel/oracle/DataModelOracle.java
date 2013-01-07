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

package org.kie.guvnor.datamodel.oracle;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kie.guvnor.datamodel.model.DSLSentence;
import org.kie.guvnor.datamodel.model.DropDownData;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.MethodInfo;
import org.kie.guvnor.datamodel.model.ModelField;

public interface DataModelOracle {

    String[] getFactTypes();

    String[] getEnumValues( final String factType,
                            final String factField );

    boolean hasEnums( String type );

    boolean hasEnums( final String factType,
                      final String factField );

    boolean isDependentEnum( final String factType,
                             final String factField,
                             final String field );

    DropDownData getEnums( final String type,
                           final String field );

    DropDownData getEnums( String factType,
                           String factField,
                           Map<String, String> currentValueMap );

    boolean isFactTypeRecognized( String factType );

    boolean isFactTypeAnEvent( String factType );

    String[] getConnectiveOperatorCompletions( String factType,
                                               String fieldName );

    String[] getOperatorCompletions( String factType,
                                     String fieldName );

    String[] getFieldCompletions( String factType );

    String getFactNameFromType( String classType );

    List<String> getMethodNames( String factType );

    List<String> getMethodNames( String factName,
                                 int i );

    String[] getGlobalVariables();

    boolean isGlobalVariable( String variable );

    String[] getFieldCompletionsForGlobalVariable( String variable );

    String getGlobalVariable( String variable );

    String getFieldType( String variableClass,
                         String fieldName );

    Collection<? extends String> getMethodParams( String variableClass,
                                                  String methodNameWithParams );

    DSLSentence[] getDSLActions();

    String[] getGlobalCollections();

    DSLSentence[] getDSLConditions();

    String getParametricFieldType( String factType,
                                   String fieldName );

    String[] getFieldCompletions( FieldAccessorsAndMutators accessor,
                                  String factType );

    List<MethodInfo> getMethodInfosForGlobalVariable( String variable );

    MethodInfo getMethodInfo( String factName,
                              String methodName );

    String getFieldClassName( String factName,
                              String fieldName );

}
