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
    private static final String INCOMING = "incoming";
    
    private UserInfo userInfo;


    /**
     * Create an inbox for the given user name (id)
     */
    public UserInbox(RulesRepository repo, String userName) throws RepositoryException {
        this.userInfo = new UserInfo(repo, userName);
    }

    /**
     * Create an inbox for the current sessions user id.
     */
    public UserInbox(RulesRepository repo) throws RepositoryException {
        this.userInfo = new UserInfo(repo);
    }

    /**
     * This should be called when the user edits or comments on an asset.
     * Simply adds to the list...
     */
    public void addToRecentEdited(String assetId, String note) throws RepositoryException {
        addToInbox(RECENT_EDITED, assetId, note, "self");
    }


    public void addToRecentOpened(String assetId, String note) throws RepositoryException {
        addToInbox(RECENT_VIEWED, assetId, note, "self");
    }

    public void addToIncoming(String assetId, String note, String userFrom) throws RepositoryException {
        addToInbox(INCOMING, assetId, note, userFrom);
    }


    private void addToInbox(String boxName, String assetId, String note, String userFrom) throws RepositoryException {
        assert boxName.equals(RECENT_EDITED) || boxName.equals(RECENT_VIEWED) || boxName.equals(INCOMING);
        List<InboxEntry> entries =  removeAnyExisting(assetId, readEntries(userInfo.getProperty(INBOX, boxName)));
        

        if (entries.size() >= MAX_RECENT_EDITED) {
            entries.remove(0);
            entries.add(new InboxEntry(assetId, note, userFrom));
        } else {
            entries.add(new InboxEntry(assetId, note, userFrom));
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

    public List<InboxEntry> loadIncoming() throws RepositoryException {
        return readEntries(userInfo.getProperty(INBOX, INCOMING));
    }

    /**
     * Wipe them out, all of them.
     */
    public void clearAll() throws RepositoryException {
        userInfo.setProperty(INBOX, RECENT_EDITED, new UserInfo.Val(""));
        userInfo.setProperty(INBOX, RECENT_VIEWED, new UserInfo.Val(""));
        userInfo.setProperty(INBOX, INCOMING, new UserInfo.Val(""));
    }

    public void clearIncoming() throws RepositoryException {
        userInfo.setProperty(INBOX, INCOMING, new UserInfo.Val(""));
    }


    /**
     * And entry in an inbox.
     */
    public static class InboxEntry {
        public String from;

        public InboxEntry() {}
        public InboxEntry(String assetId, String note, String userFrom) {
            this.assetUUID = assetId;
            this.note = note;
            this.timestamp = System.currentTimeMillis();
            this.from = userFrom;
        }
        public String assetUUID;
        public String note;
        public long timestamp;
    }


    /**
     * Helper method to log the opening. Will remove any inbox items that have the same id.
     */
    public static void recordOpeningEvent(AssetItem item) {
        try {
            UserInbox ib = new UserInbox(item.getRulesRepository());
            ib.addToRecentOpened(item.getUUID(), item.getName());
            List<InboxEntry> unreadIncoming = ib.removeAnyExisting(item.getUUID(), ib.loadIncoming());
            ib.userInfo.setProperty(INBOX, INCOMING, new UserInfo.Val(ib.writeEntries(unreadIncoming)));

            ib.save();
        } catch (RepositoryException e) {
            log.error(e);
        }
    }

    /** Helper method to note the event */
    public static void recordUserEditEvent(AssetItem item) {
        try {
            UserInbox ib = new UserInbox(item.getRulesRepository());
            ib.addToRecentEdited(item.getUUID(), item.getName());
            ib.save();
        } catch (RepositoryException e) {
            log.error(e);
        }

    }


    void save() throws RepositoryException {
        userInfo.save();
    }


}

