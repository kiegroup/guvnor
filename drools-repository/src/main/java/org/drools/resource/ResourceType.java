/**
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

package org.drools.resource;

public class ResourceType {
    public static final ResourceType DRL_FILE = new ResourceType( 1 );
    public static final ResourceType RULE     = new ResourceType( 2 );
    public static final ResourceType FUNCTION = new ResourceType( 3 );
    public static final ResourceType DSL_FILE = new ResourceType( 4 );
    public static final ResourceType XLS_FILE = new ResourceType( 5 );
    
    
    private int type;
    
    private ResourceType(int type) {
        this.type = type;
    }
    
    public int getType()  {
        return this.type;
    }
    
    private Object readResolve() throws java.io.ObjectStreamException {
        switch ( this.type ) {
            case 1:
                return DRL_FILE;
            case 2:
                return RULE;
            case 3:
                return FUNCTION;
            case 4:
                return DSL_FILE;
            case 5:
                return XLS_FILE;
                default:
        }
        throw new RuntimeException( "unable to determine ResourceType for value [" + this.type + "]" );
    }    
}
