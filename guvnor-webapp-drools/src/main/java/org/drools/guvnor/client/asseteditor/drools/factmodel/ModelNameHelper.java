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
package org.drools.guvnor.client.asseteditor.drools.factmodel;

import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;

public class ModelNameHelper {

    private static Constants           constants         = ((Constants) GWT.create( Constants.class ));

    private static Map<String, String> TYPE_DESCRIPTIONS = new HashMap<String, String>() {
                                                             private static final long serialVersionUID = 510l;
                                                             {
                                                                 put( "Integer",
                                                                      constants.WholeNumberInteger() );
                                                                 put( "Boolean",
                                                                      constants.TrueOrFalse() );
                                                                 put( "String",
                                                                      constants.Text() );
                                                                 put( "java.util.Date",
                                                                      constants.Date() );
                                                                 put( "java.math.BigDecimal",
                                                                      constants.DecimalNumber() );

                                                             }
                                                         };

    public String getDesc(FieldMetaModel fieldMetaModel) {
        if ( TYPE_DESCRIPTIONS.containsKey( fieldMetaModel.type ) ) {
            return TYPE_DESCRIPTIONS.get( fieldMetaModel.type );
        }
        return fieldMetaModel.type;
    }

    public Map<String, String> getTypeDescriptions() {
        return TYPE_DESCRIPTIONS;
    }

    public boolean isUniqueName(String type) {
        if ( getTypeDescriptions().containsKey( type ) ) {
            return false;
        }
        return true;
    }

    public void changeNameInModelNameHelper(String oldName,
                                            String newName) {
        getTypeDescriptions().remove( oldName );
        getTypeDescriptions().put( newName,
                                   newName );
    }

    public String getUserFriendlyTypeName(String systemTypeName) {
        if(systemTypeName.contains( "." )) {
            systemTypeName = systemTypeName.substring( systemTypeName.lastIndexOf( "." ) + 1 );
        }
        String userFriendlyName = getTypeDescriptions().get( systemTypeName );
        if ( userFriendlyName == null ) {
            return systemTypeName;
        } else {
            return userFriendlyName;
        }
    }

}
