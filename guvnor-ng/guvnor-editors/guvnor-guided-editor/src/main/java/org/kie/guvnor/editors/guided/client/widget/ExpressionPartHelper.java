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

package org.kie.guvnor.editors.guided.client.widget;

import org.kie.guvnor.datamodel.api.client.DataModel;
import org.kie.guvnor.datamodel.api.client.MethodInfo;
import org.kie.guvnor.editors.guided.model.DataType;
import org.kie.guvnor.editors.guided.model.ExpressionCollection;
import org.kie.guvnor.editors.guided.model.ExpressionField;
import org.kie.guvnor.editors.guided.model.ExpressionGlobalVariable;
import org.kie.guvnor.editors.guided.model.ExpressionPart;
import org.kie.guvnor.editors.guided.model.ExpressionMethod;

public class ExpressionPartHelper {

    public static ExpressionPart getExpressionPartForMethod( DataModel sce,
                                                             String factName,
                                                             String methodName ) {
        MethodInfo mi = sce.getMethodinfo( factName, methodName );
        if ( DataType.TYPE_COLLECTION.equals( mi.getGenericType() ) ) {
            return new ExpressionCollection( methodName, mi.getReturnClassType(),
                                             mi.getGenericType(), mi.getParametricReturnType() );
        }
        return new ExpressionMethod( mi.getName(), mi.getReturnClassType(), mi.getGenericType() );
    }

    public static ExpressionPart getExpressionPartForField( DataModel sce,
                                                            String factName,
                                                            String fieldName ) {
        String fieldClassName = sce.getFieldClassName( factName, fieldName );
        String fieldGenericType = sce.getFieldType( factName, fieldName );
        if ( DataType.TYPE_COLLECTION.equals( fieldGenericType ) ) {
            String fieldParametricType = sce.getParametricFieldType( factName, fieldName );
            return new ExpressionCollection( fieldName, fieldClassName, fieldGenericType,
                                             fieldParametricType );
        }
        return new ExpressionField( fieldName, fieldClassName, fieldGenericType );
    }

    public static ExpressionPart getExpressionPartForGlobalVariable( DataModel sce,
                                                                     String varName ) {
        String globalVarType = sce.getGlobalVariable( varName );
        return new ExpressionGlobalVariable( varName, globalVarType, globalVarType );
    }
}
