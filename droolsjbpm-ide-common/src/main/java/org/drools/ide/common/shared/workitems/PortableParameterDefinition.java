/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.ide.common.shared.workitems;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

/**
 * A ParameterDefinition used in Guvnor.
 * 
 * @see org.drools.process.core.ParameterDefinition
 */
public abstract class PortableParameterDefinition
    implements
    PortableObject {

    private static final long serialVersionUID = 540L;

    private String            name;

    public PortableParameterDefinition() {
    }

    public PortableParameterDefinition(String name) {
        setName( name );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if ( name == null ) {
            throw new IllegalArgumentException( "Name cannot be null" );
        }
        this.name = name;
    }

    public abstract String asString();

    public abstract String getClassName();

    public String getSimpleClassName() {
        String className = getClassName();
        if ( className == null ) {
            return null;
        }
        int index = className.lastIndexOf( "." );
        if ( index >= 0 ) {
            className = className.substring( index + 1 );
        }
        return className;
    }

}
