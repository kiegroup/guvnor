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

import org.drools.guvnor.models.commons.rule.DSLSentence;
import org.drools.guvnor.models.commons.imports.Imports;
import org.kie.guvnor.datamodel.model.DropDownData;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.MethodInfo;
import org.kie.guvnor.datamodel.model.ModelField;

import java.util.List;
import java.util.Map;

public interface DataModelOracle {

    String[] getFactTypes();

    String[] getAllFactTypes();

    String[] getExternalFactTypes();

    String[] getEnumValues( final String factType,
                            final String factField );

    Map<String, ModelField[]> getModelFields();

    boolean hasEnums( final String qualifiedFactField );

    boolean hasEnums( final String factType,
                      final String factField );

    boolean isDependentEnum( final String factType,
                             final String factField,
                             final String field );

    DropDownData getEnums( final String type,
                           final String field );

    DropDownData getEnums( final String factType,
                           final String factField,
                           final Map<String, String> currentValueMap );

    boolean isFactTypeRecognized( final String factType );

    boolean isFactTypeAnEvent( final String factType );

    String[] getConnectiveOperatorCompletions( final String factType,
                                               final String fieldName );

    String[] getOperatorCompletions( final String factType,
                                     final String fieldName );

    String[] getFieldCompletions( final String factType );

    String getFactNameFromType( final String classType );

    List<String> getMethodNames( final String factType );

    List<String> getMethodNames( final String factName,
                                 final int i );

    String[] getGlobalVariables();

    boolean isGlobalVariable( final String variable );

    String[] getFieldCompletionsForGlobalVariable( final String variable );

    String getGlobalVariable( final String variable );

    String getFieldType( final String variableClass,
                         final String fieldName );

    List<String> getMethodParams( final String factType,
                                  final String methodNameWithParams );

    List<DSLSentence> getDSLActions();

    String[] getGlobalCollections();

    List<DSLSentence> getDSLConditions();

    String getParametricFieldType( final String factType,
                                   final String fieldName );

    String[] getFieldCompletions( final FieldAccessorsAndMutators accessor,
                                  final String factType );

    List<MethodInfo> getMethodInfosForGlobalVariable( final String variable );

    MethodInfo getMethodInfo( final String factName,
                              final String methodName );

    String getFieldClassName( final String factName,
                              final String fieldName );

    void filter( final Imports imports );

    void filter();

}
