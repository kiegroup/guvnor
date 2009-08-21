package org.drools.guvnor.server.repository;

import org.drools.repository.RulesRepository;
import org.drools.repository.UserInfo;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 *
 * This manages the users "inbox".
 * @author Michael Neale
 */
public class UserInbox {
    private RulesRepository repository;


    public UserInbox(RulesRepository repo) {
        this.repository = repo;
    }

    /**
     * This should be called when the user edits or comments on an asset.
     * @throws RepositoryException
     */
    public void addToRecentEdited(String assetId, String note) throws RepositoryException {
        UserInfo ui = new UserInfo(repository);
        //List<InboxEntry> entries =  readEntries(ui.getProperty("inbox", "recentEdited"));
        
    }
    
    public void addToRecentOpened(String assetId, String note) {

    }


    /**
     * This is called when someone else updated something that was recently edited by a given user.
     * @param assetId
     * @param note
     */
    public void addToUpdated(String assetId, String note) {}

    public InboxEntry[] loadRecentEdited() {
        return null;
    }

    public InboxEntry[] loadRecentOpened() {
        return null;
    }

    public InboxEntry[] loadUpdated() {
        return null;
    }




    public static class InboxEntry {
        public String assetUUID;
        public String note;
        public long timestamp;
    }


}

