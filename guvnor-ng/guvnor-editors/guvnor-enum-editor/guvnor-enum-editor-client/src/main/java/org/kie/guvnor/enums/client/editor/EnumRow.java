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

package org.kie.guvnor.enums.client.editor;

public class EnumRow {

    private String fieldName = "";
    private String factName = "";
    private String context = "";

    public EnumRow( final String line ) {

        if ( line == null || line.isEmpty() ) {
            factName = "";
            fieldName = "";
            context = "";
        } else {
            factName = line.substring( 1, line.indexOf( "." ) );
            fieldName = line.substring( line.indexOf( "." ) + 1, line.indexOf( "':" ) );
            context = line.substring( line.indexOf( ":" ) + 1 ).trim();
        }
    }

    public String getText() {
        if ( factName == "" ) {
            return "";
        } else {
            return "'" + factName + "." + fieldName + "': " + context;
        }
    }

    public String getFactName() {
        return factName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getContext() {
        return context;
    }

    public void setFactName( final String factName ) {
        this.factName = factName;

    }

    public void setFieldName( String fieldName ) {
        this.fieldName = fieldName;
    }

    public void setContext( final String context ) {
        this.context = context;
    }
}
