package org.drools.guvnor.server.rest;

/**
 * Simple parameter names for Actions exposed
 * in the ActionsAPI.
 */
public enum Parameters {
    
    PackageName {
        public String toString() {
            return "package-name";
        }
    },

    SnapshotName {
        public String toString() {
            return "snapshot-name";
        }
    };

}
