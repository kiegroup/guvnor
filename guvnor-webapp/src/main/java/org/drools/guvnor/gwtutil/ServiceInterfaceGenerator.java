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
package org.drools.guvnor.gwtutil;

import java.lang.reflect.Method;

public class ServiceInterfaceGenerator {

    /**
     * Generated service interface from given interface. This can be used in GWT servlet end-point.
     * @param cls Class from generate
     * @param serviceMethodName service method name e.x getService()
     */
    public static void generate(final Class cls,
                                final String serviceMethodName) {
        String line = "";
        Method[] methods = cls.getMethods();
        for ( int i = 0; i < methods.length; i++ ) {
            Method meth = methods[i];
            if ( meth.getDeclaringClass() == cls ) {
                Class[] exes = meth.getExceptionTypes();

                String retType = typeName( meth.getReturnType().getName() );
                line += "public " + retType + " " + meth.getName() + "(";
                Class params[] = meth.getParameterTypes();
                String body = serviceMethodName + "." + meth.getName() + "(";
                for ( int j = 0; j < params.length; j++ ) {
                    String type = params[j].getName();
                    type = typeName( type );
                    line += type;
                    line += " p" + j;
                    body += " p" + j;
                    if ( j < params.length - 1 ) {
                        line += ", ";
                        body += ", ";
                    }
                }

                body += ");";
                line += ") ";
                if ( exes.length > 0 ) {
                    line += "throws " + exes[0].getName();
                }
                line += " {\n";
                if ( retType.equals( "void" ) ) {
                    line += "\t" + body + "\n";
                } else {
                    line += "\t return " + body + "\n";
                }
                line += "}\n";
            }
        }
        System.out.println( "/** PLACE THE FOLLOWING IN RepositoryServiceServlet.java **/\n" );

        System.out.println( line );
    }

    private static String typeName(String type) {
        if ( type.startsWith( "[L" ) ) {
            type = type.replace( "[L",
                                 "" ).replace( ";",
                                               "[]" );
        }
        return type;
    }

}
