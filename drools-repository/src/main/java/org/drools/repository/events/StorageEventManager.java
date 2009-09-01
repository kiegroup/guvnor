package org.drools.repository.events;

import org.drools.repository.AssetItem;
import org.drools.repository.VersionableItem;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * This manages storage events, which may load/save from another location, or just notify on change etc.
 * @author Michael Neale
 */
public class StorageEventManager {


    static List<CheckinEvent> checkinEvents = new ArrayList<CheckinEvent>();
    static LoadEvent le = loadEvent();
    static SaveEvent se = saveEvent();

    static LoadEvent loadEvent() {
        String leClassName = System.getProperty("guvnor.loadEventListener", "");
        try {
            if (!leClassName.equals("")) {
                return (LoadEvent) Class.forName(leClassName).newInstance();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Unable to initialise the load event listener: " + leClassName);
            e.printStackTrace();
            return null;
        }
    }

    static SaveEvent saveEvent() {
        String seClassName = System.getProperty("guvnor.saveEventListener", "");
        try {
            if (!seClassName.equals("")) {
                return (SaveEvent) Class.forName(seClassName).newInstance();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Unable to initialise the save event listener: " + seClassName);
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasLoadEvent() {
        return le != null;
    }

    public static boolean hasSaveEvent() {
        return se != null;
    }

    public static LoadEvent getLoadEvent() {
        return le;
    }

    public static SaveEvent getSaveEvent() {
        return se;
    }

    public static void registerCheckinEvent(CheckinEvent ev) {
        checkinEvents.add(ev);
    }


    /**
     * Process any checkin event listeners.
     */
    public static void doCheckinEvents(VersionableItem item) {
        if (item instanceof AssetItem) {
            AssetItem asset = (AssetItem) item;
            for (CheckinEvent e : checkinEvents) {
                e.afterCheckin(asset);
            }
        }
    }
}
