package org.drools.guvnor.server.ruleeditor.workitem;

import java.io.IOException;
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

    //Load file into a String
    public String loadWorkDefinitions() throws IOException {
        StringBuilder definitions = new StringBuilder();
        Scanner scanner = new Scanner( this.getClass().getResourceAsStream( WORKITEM_DEFINITIONS ) );
        try {
            while ( scanner.hasNextLine() ) {
                definitions.append( scanner.nextLine() + NEW_LINE );
            }
        } finally {
            scanner.close();
        }
        return definitions.toString();
    }

    public synchronized static ConfigFileWorkDefinitionsLoader getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new ConfigFileWorkDefinitionsLoader();
        }
        return INSTANCE;
    }

}
