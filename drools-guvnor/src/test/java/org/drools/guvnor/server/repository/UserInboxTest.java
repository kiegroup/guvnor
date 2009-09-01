package org.drools.guvnor.server.repository;

import org.drools.repository.RulesRepository;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;

import java.util.List;

import junit.framework.TestCase;

/**
 * @author Michael Neale
 */
public class UserInboxTest extends TestCase {

    public void testInboxen() throws Exception {
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        UserInbox inb = new UserInbox(repo);
        inb.clearAll();
        inb.addToRecentEdited("ABC", "This is a note");
        Thread.sleep(100);
        inb.addToRecentEdited("QED", "Here we go...");
        repo.save();

        List<UserInbox.InboxEntry> entries = inb.loadRecentEdited();
        assertEquals(2, entries.size());

        UserInbox.InboxEntry e1 = entries.get(0);
        assertEquals("ABC", e1.assetUUID);
        assertEquals("This is a note", e1.note);

        UserInbox.InboxEntry e2 = entries.get(1);
        assertEquals("QED", e2.assetUUID);
        assertTrue(e2.timestamp > e1.timestamp);

        inb.clearAll();

        for (int i = 0; i < UserInbox.MAX_RECENT_EDITED; i++) {
            inb.addToRecentEdited("X" + i, "NOTE" + i);
        }

        assertEquals("X0", inb.loadRecentEdited().get(0).assetUUID);

        inb.addToRecentEdited("Y1", "NOTE");

        List<UserInbox.InboxEntry> res = inb.loadRecentEdited();
        assertEquals("X1", inb.loadRecentEdited().get(0).assetUUID);
        assertEquals("Y1", res.get(res.size() - 1 ).assetUUID);

        assertTrue(res.get(res.size() - 2 ).assetUUID.startsWith("X"));

        inb.addToRecentEdited("Y2", "NOTE");

        res = inb.loadRecentEdited();
        assertEquals("X2", inb.loadRecentEdited().get(0).assetUUID);
        assertEquals("Y2", res.get(res.size() - 1 ).assetUUID);
        assertEquals("Y1", res.get(res.size() - 2 ).assetUUID);



    }

    public void testRead() throws Exception {
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        UserInbox inb = new UserInbox(repo);
        inb.clearAll();
        inb.addToRecentOpened("QED", "hey");
        inb.addToRecentEdited("ABC", "This is a note");

        List<UserInbox.InboxEntry> es =inb.loadRecentOpened();
        assertEquals(1, es.size());
        assertEquals("QED", es.get(0).assetUUID);


    }

}
