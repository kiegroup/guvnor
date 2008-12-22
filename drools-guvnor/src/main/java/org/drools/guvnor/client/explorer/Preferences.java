package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.security.Capabilities;

import java.util.Map;

/**
 * Storage for global prefs.
 * Preferences effect behaviour and display.
 * 
 * @author Michael Neale
 */
public class Preferences {

    static final Preferences INSTANCE = new Preferences();
    private Map<String, String> prefs;

    private Preferences() {
    }

    void loadPrefs(Capabilities caps) {
        this.prefs = caps.prefs;
    }

    public static boolean getBooleanPref(String name) {
        if (INSTANCE.prefs.containsKey(name)) {
            return Boolean.parseBoolean(INSTANCE.prefs.get(name));
        } else {
            return false;
        }
    }

    


}
