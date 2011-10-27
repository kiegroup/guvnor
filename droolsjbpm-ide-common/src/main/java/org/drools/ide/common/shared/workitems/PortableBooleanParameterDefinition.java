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

/**
 * A Boolean parameter
 */
public class PortableBooleanParameterDefinition extends PortableEnumParameterDefinition {

    private static final long     serialVersionUID = 540L;

    private static final String[] VALUES           = new String[]{Boolean.toString( Boolean.TRUE ), Boolean.toString( Boolean.FALSE )};

    public PortableBooleanParameterDefinition() {
        super.setValues( VALUES );
    }

    public void setValues(String[] values) {
        throw new UnsupportedOperationException( "Cannot set values of PortableBooleanParameterDefinition" );
    }

}
