package org.drools.guvnor.server.repository;

import junit.framework.TestCase;
import org.drools.repository.RulesRepository;
import org.drools.repository.AssetItem;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;

import java.util.List;

/**
 * 
 * @author Michael Neale
 */
public class MailboxServiceTest extends TestCase {

    public void testMailbox() throws Exception {
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());

        MailboxService service = new MailboxService(repo);

        AssetItem asset = repo.loadDefaultPackage().addAsset("testMailbox", "");
        
        UserInbox mailman = new UserInbox(repo, "mailman");
        assertEquals(0, mailman.loadIncoming().size());

        UserInbox ib = new UserInbox(repo, "mic");
        ib.addToRecentEdited(asset.getUUID(), "hey");
        assertEquals(0, ib.loadIncoming().size());

        UserInbox ib2 = new UserInbox(repo, "mic2");
        ib2.addToRecentEdited(asset.getUUID(), "hey");
        assertEquals(0, ib2.loadIncoming().size());


        service.recordItemUpdated(asset);

        Thread.sleep(300);
        
        List<UserInbox.InboxEntry> es = ib.loadIncoming();
        assertEquals(1, es.size());
        assertEquals(asset.getUUID(), es.get(0).assetUUID);

        es = ib2.loadIncoming();
        assertEquals(1, es.size());
        assertEquals(asset.getUUID(), es.get(0).assetUUID);


        assertEquals(0, mailman.loadIncoming().size());


        AssetItem ass2 = repo.loadDefaultPackage().addAsset("testMailbox2", "XX");


        ib2.addToRecentEdited(ass2.getUUID(), "hey");
        mailman.addToIncoming(ass2.getUUID(), "whee");
        assertEquals(1, mailman.loadIncoming().size());
        assertEquals(1, ib2.loadIncoming().size());
        service.wakeUp();
        Thread.sleep(250);
      
        assertEquals(2, ib2.loadIncoming().size());
        assertEquals(0, mailman.loadIncoming().size());
        assertEquals(1, ib.loadIncoming().size());


        MailboxService serv = MailboxService.getInstance();
        serv.init(repo);

        serv.wakeUp();
        assertEquals(2, ib2.loadIncoming().size());
        assertEquals(0, mailman.loadIncoming().size());
        assertEquals(1, ib.loadIncoming().size());

        assertSame(serv, MailboxService.getInstance());
        

    }

}
