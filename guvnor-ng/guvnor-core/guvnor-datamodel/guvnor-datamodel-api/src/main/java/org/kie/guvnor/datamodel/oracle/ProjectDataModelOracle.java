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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.guvnor.datamodel.model.Annotation;
import org.kie.guvnor.datamodel.model.DropDownData;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.MethodInfo;
import org.kie.guvnor.datamodel.model.ModelField;

public interface ProjectDataModelOracle {

    //Fact and Field related methods
    String[] getFactTypes();

    String getFactNameFromType( final String classType );

    boolean isFactTypeRecognized( final String factType );

    boolean isFactTypeAnEvent( final String factType );

    boolean isDeclaredType( final String factType );

    String getSuperType( final String factType );

    Set<Annotation> getTypeAnnotation( final String factType );

    Map<String, ModelField[]> getModelFields();

    String[] getConnectiveOperatorCompletions( final String factType,
                                               final String fieldName );

    String[] getOperatorCompletions( final String factType,
                                     final String fieldName );

    String[] getFieldCompletions( final String factType );

    List<String> getMethodNames( final String factType );

    List<String> getMethodNames( final String factName,
                                 final int i );

    String getFieldType( final String variableClass,
                         final String fieldName );

    List<String> getMethodParams( final String factType,
                                  final String methodNameWithParams );

    String getParametricFieldType( final String factType,
                                   final String fieldName );

    String[] getFieldCompletions( final FieldAccessorsAndMutators accessor,
                                  final String factType );

    MethodInfo getMethodInfo( final String factName,
                              final String methodName );

    String getFieldClassName( final String factName,
                              final String fieldName );

    // Enumeration related methods
    String[] getEnumValues( final String factType,
                            final String factField );

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

}
