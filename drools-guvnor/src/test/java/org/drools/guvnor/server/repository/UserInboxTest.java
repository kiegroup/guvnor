package org.drools.guvnor.server.repository;

import org.drools.repository.RulesRepository;
import org.drools.repository.AssetItem;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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

    public void testDupes() throws Exception {
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        UserInbox inb = new UserInbox(repo);
        inb.clearAll();

        for (int i =0; i < 120; i++) {
            inb.addToRecentOpened("A" + i, "NOTE");
        }

        List<UserInbox.InboxEntry> res = inb.loadRecentOpened();
        assertEquals(120, res.size());
        inb.addToRecentOpened("XX", "hey");

        assertEquals(res.size() + 1, inb.loadRecentOpened().size());
        UserInbox.InboxEntry firstOld = inb.loadRecentOpened().get(0);
        assertEquals("A0", firstOld.assetUUID);


        Thread.sleep(30);
        //shouldn't add another one... 
        inb.addToRecentOpened("A0", "hey22");

        List<UserInbox.InboxEntry> finalList = inb.loadRecentOpened();
        assertEquals(res.size() + 1, finalList.size());
        assertEquals("A1", finalList.get(0).assetUUID);

        UserInbox.InboxEntry lastEntry = finalList.get(finalList.size() - 1);
        assertEquals("A0", lastEntry.assetUUID);

        assertTrue(lastEntry.timestamp > firstOld.timestamp);
        

    }

    public void testHelper() throws Exception {
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        UserInbox ib = new UserInbox(repo);
        ib.clearAll();
        AssetItem asset = repo.loadDefaultPackage().addAsset("InBoxTestHelper", "hey");
        UserInbox.recordOpeningEvent(asset);

        List<UserInbox.InboxEntry> es = ib.loadRecentOpened();
        assertEquals(1, es.size());
        assertEquals(asset.getUUID(), es.get(0).assetUUID);
        assertEquals("InBoxTestHelper", es.get(0).note);
    }


    public void testIncoming() throws Exception {
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        UserInbox ib = new UserInbox(repo);
        ib.clearAll();
        ib.addToIncoming("XXX", "hey");
        ib.addToIncoming("YYY", "hey2");

        List<UserInbox.InboxEntry> es = ib.loadIncoming();
        assertEquals(2, es.size());
        assertEquals("XXX", es.get(0).assetUUID);
        assertEquals("YYY", es.get(1).assetUUID);

    }








}
