package org.drools.guvnor.client.explorer.places;

import com.google.gwt.place.shared.Place;

public class RuntimePlace extends Place {

    public enum Location {

        PERSONAL_TASKS("PERSONAL_TASKS"),
        GROUP_TASKS("GROUP_TASKS"),
        REPORT_TEMPLATES("REPORT_TEMPLATES"),
        PREFERENCES("PREFERENCES"),
        SYSTEM("SYSTEM"),
        EXECUTION_HISTORY("EXECUTION_HISTORY"),
        PROCESS_OVERVIEW("PROCESS_OVERVIEW");

        private final String locationName;

        Location(String locationName) {
            this.locationName = locationName;
        }

        public String getLocationName() {
            return locationName;
        }

    }

    private final Location location;

    public RuntimePlace(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
