package org.drools.guvnor.server.repository;

import org.drools.repository.RulesRepository;
import org.drools.repository.UserInfo;
import org.drools.repository.AssetItem;
import org.drools.guvnor.server.util.LoggingHelper;
import org.apache.log4j.Logger;

import javax.jcr.RepositoryException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.thoughtworks.xstream.XStream;

/**
 * This manages the users "inbox".
 * @author Michael Neale
 */
public class UserInbox {

    private static final Logger log                               = LoggingHelper.getLogger( UserInbox.class );
    static final int MAX_RECENT_EDITED = 200;

    private static final String INBOX = "inbox";
    private static final String RECENT_EDITED = "recentEdited";
    private static final String RECENT_VIEWED = "recentViewed";
    
    private UserInfo userInfo;


    public UserInbox(RulesRepository repo) throws RepositoryException {
        this.userInfo = new UserInfo(repo);
    }

    /**
     * This should be called when the user edits or comments on an asset.
     * Simply adds to the list...
     */
    public void addToRecentEdited(String assetId, String note) throws RepositoryException {
        addToInbox(RECENT_EDITED, assetId, note);
    }


    public void addToRecentOpened(String assetId, String note) throws RepositoryException {
        addToInbox(RECENT_VIEWED, assetId, note);
    }


    private void addToInbox(String boxName, String assetId, String note) throws RepositoryException {
        assert boxName.equals(RECENT_EDITED) || boxName.equals(RECENT_VIEWED);
        List<InboxEntry> entries =  removeAnyExisting(assetId, readEntries(userInfo.getProperty(INBOX, boxName)));
        

        if (entries.size() >= MAX_RECENT_EDITED) {
            entries.remove(0);
            entries.add(new InboxEntry(assetId, note));
        } else {
            entries.add(new InboxEntry(assetId, note));
        }
        userInfo.setProperty(INBOX, boxName, new UserInfo.Val(writeEntries(entries)));


    }

    private List<InboxEntry> removeAnyExisting(String assetId, List<InboxEntry> inboxEntries) {
        Iterator<InboxEntry> it = inboxEntries.iterator();
        while (it.hasNext()) {
            InboxEntry e = it.next();
            if (e.assetUUID.equals(assetId)) {
                it.remove();
                return inboxEntries;
            }
        }
        return inboxEntries;
    }

    private String writeEntries(List<InboxEntry> entries) {
        return getXStream().toXML(entries);
    }

    private List<InboxEntry> readEntries(UserInfo.Val property) {
        if (!(property.value == null || property.value.equals(""))) {
            return (List<InboxEntry>) getXStream().fromXML(property.value);
        } else {
            return new ArrayList<InboxEntry>();
        }
    }


    private XStream getXStream() {
        XStream xs = new XStream();
        xs.alias("inbox-entries", List.class);
        xs.alias("entry", InboxEntry.class);
        return xs;
    }



    public List<InboxEntry> loadRecentEdited() throws RepositoryException {
        return readEntries(userInfo.getProperty(INBOX, RECENT_EDITED));
    }

    public List<InboxEntry> loadRecentOpened() throws RepositoryException {
        return readEntries(userInfo.getProperty(INBOX, RECENT_VIEWED));
    }

    /**
     * Wipe them out, all of them.
     */
    void clearAll() throws RepositoryException {
        userInfo.setProperty(INBOX, RECENT_EDITED, new UserInfo.Val(""));
        userInfo.setProperty(INBOX, RECENT_VIEWED, new UserInfo.Val(""));
    }


    /**
     * And entry in an inbox.
     */
    public static class InboxEntry {
        public InboxEntry() {}
        public InboxEntry(String assetId, String note) {
            this.assetUUID = assetId;
            this.note = note;
            this.timestamp = System.currentTimeMillis();
        }
        public String assetUUID;
        public String note;
        public long timestamp;
    }


    /** Helper method to log the opening */
    public static void recordOpeningEvent(AssetItem item) {
        try {
            UserInbox ib = new UserInbox(item.getRulesRepository());
            ib.addToRecentOpened(item.getUUID(), item.getName());
            ib.save();
        } catch (RepositoryException e) {
            log.error(e);
        }
    }

    public static void recordUserEditEvent(AssetItem item) {
        try {
            UserInbox ib = new UserInbox(item.getRulesRepository());
            ib.addToRecentEdited(item.getUUID(), item.getName());
            ib.save();
        } catch (RepositoryException e) {
            log.error(e);
        }

    }


    private void save() throws RepositoryException {
        userInfo.save();
    }


}

