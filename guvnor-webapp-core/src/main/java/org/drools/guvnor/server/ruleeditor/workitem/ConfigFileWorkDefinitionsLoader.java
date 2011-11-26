/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.server.ruleeditor.workitem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class to load Work Definitions from configuration file
 * 'workitem-definitions.xml'
 */
public class ConfigFileWorkDefinitionsLoader extends AbstractWorkDefinitionsLoader {

    private static final String                    WORKITEM_DEFINITIONS = "/workitem-definitions.xml";
    private static ConfigFileWorkDefinitionsLoader INSTANCE;

    private ConfigFileWorkDefinitionsLoader() {
        super();
    }

    public List<String> loadWorkDefinitions() throws IOException {

        //Load file into a String
        StringBuilder definition = new StringBuilder();
        Scanner scanner = new Scanner( this.getClass().getResourceAsStream( WORKITEM_DEFINITIONS ) );
        try {
            while ( scanner.hasNextLine() ) {
                definition.append(scanner.nextLine()).append(NEW_LINE);
            }
        } finally {
            scanner.close();
        }

        //Add definition to definitions list
        List<String> definitions = new ArrayList<String>();
        definitions.add( definition.toString() );
        return definitions;
    }

    public synchronized static ConfigFileWorkDefinitionsLoader getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new ConfigFileWorkDefinitionsLoader();
        }
        return INSTANCE;
    }

}
