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

package org.kie.guvnor.shared;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.guvnor.datamodel.DSLSentence;
import org.kie.guvnor.datamodel.DataModel;
import org.kie.guvnor.datamodel.DropDownData;
import org.kie.guvnor.datamodel.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.MethodInfo;

@Portable
public class MockDataModel implements DataModel {

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
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasEnums( String factType,
                             String factField ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public String getFieldType( String variableClass,
                                String fieldName ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

}
