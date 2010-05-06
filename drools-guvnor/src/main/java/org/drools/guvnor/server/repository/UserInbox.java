package org.drools.guvnor.server.repository;

import org.drools.repository.RulesRepository;
import org.drools.repository.UserInfo;
import org.drools.repository.AssetItem;
import org.drools.repository.UserInfo.InboxEntry;
import static org.drools.guvnor.client.common.Inbox.*;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;

import java.util.List;
import java.util.Iterator;

/**
 * This manages the users "inbox".
 * @author Michael Neale
 */
public class UserInbox {
    static final int MAX_RECENT_EDITED = 200;

    private static final String INBOX = "inbox";

    
    private UserInfo userInfo;


    /**
     * Create an inbox for the given user name (id)
     */
    public UserInbox(RulesRepository repo, String userName) {
        this.userInfo = new UserInfo(repo, userName);
    }

    /**
     * Create an inbox for the current sessions user id.
     */
    public UserInbox(RulesRepository repo) {
        this.userInfo = new UserInfo(repo);
    }

    /**
     * This should be called when the user edits or comments on an asset.
     * Simply adds to the list...
     */
    public void addToRecentEdited(String assetId, String note) {
        addToInbox(RECENT_EDITED, assetId, note, "self");
    }


    public void addToRecentOpened(String assetId, String note) {
        addToInbox(RECENT_VIEWED, assetId, note, "self");
    }

    public void addToIncoming(String assetId, String note, String userFrom) {
        addToInbox(INCOMING, assetId, note, userFrom);
    }


    private void addToInbox(String boxName, String assetId, String note, String userFrom) {
        assert boxName.equals(RECENT_EDITED) || boxName.equals(RECENT_VIEWED) || boxName.equals(INCOMING);
        List<InboxEntry> entries =  removeAnyExisting(assetId, userInfo.readEntries(INBOX, boxName));     

        if (entries.size() >= MAX_RECENT_EDITED) {
            entries.remove(0);
            entries.add(new InboxEntry(assetId, note, userFrom));
        } else {
            entries.add(new InboxEntry(assetId, note, userFrom));
        }
        userInfo.writeEntries(INBOX, boxName, entries);
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

    public List<InboxEntry> loadRecentEdited() {
        return userInfo.readEntries(INBOX, RECENT_EDITED);
    }

    public List<InboxEntry> loadRecentOpened() {
        return userInfo.readEntries(INBOX, RECENT_VIEWED);
    }

    public List<InboxEntry> loadIncoming() {
        return userInfo.readEntries(INBOX, INCOMING);
    }

    /**
     * Wipe them out, all of them.
     */
    public void clearAll() {
        userInfo.clear(INBOX, RECENT_EDITED);
        userInfo.clear(INBOX, RECENT_VIEWED);
        userInfo.clear(INBOX, INCOMING);
    }

    public void clearIncoming() {
        userInfo.clear(INBOX, INCOMING);
    }


    public static TableDataResult toTable(List<InboxEntry> entries, boolean showFrom) {
        TableDataResult res = new TableDataResult();
        res.currentPosition = 0;
        res.total = entries.size();
        res.hasNext = false;
        res.data = new TableDataRow[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            TableDataRow tdr = new TableDataRow();
            InboxEntry e =entries.get(i);
            tdr.id = e.assetUUID;
            if (!showFrom) {
                tdr.values = new String[2];
                tdr.values[0] = e.note;
                tdr.values[1] = Long.toString(e.timestamp);
            } else {
                tdr.values = new String[3];
                tdr.values[0] = e.note;
                tdr.values[1] = Long.toString(e.timestamp);
                tdr.values[2] = e.from;
            }
            tdr.format = AssetFormats.BUSINESS_RULE;
            res.data[i] = tdr;
        }
        return res;
    }


    /**
     * Helper method to log the opening. Will remove any inbox items that have the same id.
     */
    public static void recordOpeningEvent(AssetItem item) {
		UserInbox ib = new UserInbox(item.getRulesRepository());
		ib.addToRecentOpened(item.getUUID(), item.getName());
		List<InboxEntry> unreadIncoming = ib.removeAnyExisting(item.getUUID(),
				ib.loadIncoming());
		ib.userInfo.writeEntries(INBOX, INCOMING, unreadIncoming);

		ib.save();
	}

    /** Helper method to note the event */
    public static void recordUserEditEvent(AssetItem item) {
		UserInbox ib = new UserInbox(item.getRulesRepository());
		ib.addToRecentEdited(item.getUUID(), item.getName());
		ib.save();
	}


    void save() {
        userInfo.save();
    }


}

