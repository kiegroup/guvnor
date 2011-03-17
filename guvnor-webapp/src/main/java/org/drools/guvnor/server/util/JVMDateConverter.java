/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drools.guvnor.client.util.DateConverter;

/**
 * Convenience class to handle date conversion in a normal JVM (i.e. not running
 * under GWT). Used primarily from JUnit Tests.
 */
public class JVMDateConverter
    implements
    DateConverter {

    private static final SimpleDateFormat formatter = new SimpleDateFormat( "dd-MMM-yyyy" );

    //Singleton
    private static DateConverter          INSTANCE;

    public static synchronized DateConverter getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new JVMDateConverter();
        }
        return INSTANCE;
    }

    private JVMDateConverter() {
    }

    public String format(Date date) {
        return formatter.format( date );
    }

    public Date parse(String text) {
        try {
            return formatter.parse( text );
        } catch ( ParseException pe ) {
        }
        return null;
    }

}
