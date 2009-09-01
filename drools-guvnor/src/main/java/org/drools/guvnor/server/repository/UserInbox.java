package org.drools.guvnor.server.repository;

import org.drools.repository.RulesRepository;
import org.drools.repository.UserInfo;

import javax.jcr.RepositoryException;
import java.util.List;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;

/**
 * This manages the users "inbox".
 * @author Michael Neale
 */
public class UserInbox {

    static final int MAX_RECENT_EDITED = 100;

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
        List<InboxEntry> entries =  readEntries(userInfo.getProperty(INBOX, boxName));
        if (entries.size() >= MAX_RECENT_EDITED) {
            entries.remove(0);
            entries.add(new InboxEntry(assetId, note));
        } else {
            entries.add(new InboxEntry(assetId, note));
        }
        userInfo.setProperty(INBOX, boxName, new UserInfo.Val(writeEntries(entries)));

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

    public void clearAll() throws RepositoryException {
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


}

