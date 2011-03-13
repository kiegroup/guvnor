/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.gwtutil;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This utility uses reflection to generate the async interface from the Service
 * interface as per GWT standard.
 */
public class ServiceAsyncInterfaceGenerator {

    protected static String generate(Class< ? > cls) {
        String line = "";
        Method[] methods = cls.getMethods();
        for ( int i = 0; i < methods.length; i++ ) {
            Method meth = methods[i];
            if ( meth.getDeclaringClass() == cls ) {
                line += "public void "
                        + meth.getName()
                        + "(";
                Class< ? > params[] = meth.getParameterTypes();
                for ( int j = 0; j < params.length; j++ ) {
                    String type = arrayIfy( params[j].getName() );
                    if ( type.startsWith( "[L" ) ) {
                        type = type.replace( "[L",
                                             "" ).replace( ";",
                                                           "[]" );
                    }
                    line += type;
                    line += " p"
                            + j;
                    if ( j < params.length - 1 ) {
                        line += ", ";
                    }
                }
                String retType = arrayIfy( meth.getReturnType().getName() );

                // Hack for PageResponse<T> return types
                Type grt = meth.getGenericReturnType();
                if ( grt instanceof ParameterizedType ) {
                    ParameterizedType prt = (ParameterizedType) grt;
                    Type[] genericTypes = prt.getActualTypeArguments();

                    // PageResponse<T> only has one generic parameter
                    if ( genericTypes.length == 1 ) {
                        Type genericType = genericTypes[0];
                        Class< ? > rtc = (Class< ? >) genericType;
                        retType += "<"
                                   + rtc.getName()
                                   + ">";
                    }
                }

                if ( retType.equals( "void" )
                     || retType.startsWith( "java.util" ) ) {
                    if ( line.endsWith( "(" ) ) {
                        line += "AsyncCallback cb";
                    } else {
                        line += ", AsyncCallback cb";
                    }

                } else {
                    if ( line.endsWith( "(" ) ) {
                        line += "AsyncCallback<"
                                + retType
                                + "> cb";
                    } else {
                        line += ", AsyncCallback<"
                                + retType
                                + "> cb";
                    }
                }
                line += ");\n";
            }
        }
        return line;
    }

    public static String arrayIfy(String type) {
        if ( type.startsWith( "[L" ) ) {
            type = type.replace( "[L",
                                 "" ).replace( ";",
                                               "[]" );
        }
        return type;
    }

}
