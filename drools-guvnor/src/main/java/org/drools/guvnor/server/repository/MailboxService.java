package org.drools.guvnor.server.repository;

import org.drools.repository.RulesRepository;
import org.drools.repository.AssetItem;
import org.drools.repository.UserInfo;
import org.drools.repository.UserInfo.InboxEntry;
import org.drools.guvnor.server.util.LoggingHelper;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.*;

/**
 * This service the "delivery" of messages to users inboxes for events.
 * Ideally only one instance of this running at a time (at least on a node) to avoid doubling up.
 *
 * @author Michael Neale
 */
public class MailboxService {

    private static final Logger log = LoggingHelper.getLogger( MailboxService.class );
    public static final String MAILMAN = "mailman";
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private static MailboxService INSTANCE = new MailboxService();


    public static MailboxService getInstance() { return INSTANCE; }

    /**
     * Should be the for the "mailman" user.
     */
    private RulesRepository repository;



    private MailboxService() {}

    MailboxService(RulesRepository systemRepo) {
        init(systemRepo);
    }

    public void init(RulesRepository systemRepo) {
        log.info("Starting mailbox service");
        this.repository = systemRepo;
    }



    public void wakeUp() {
        log.info("Waking up");
        executor.execute(new Runnable() {
            public void run() {
                processOutgoing();
            }
        });
    }

    /** Process any waiting messages */
    void processOutgoing()  {
             log.info("Processing outgoing messages");
            if (repository != null) {
                UserInbox mailman = new UserInbox(repository, MAILMAN);
                final List<UserInfo.InboxEntry> es  = mailman.loadIncoming();
                log.debug("Outgoing messages size " + es.size());
                //wipe out inbox for mailman here...
                UserInfo.eachUser(this.repository, new UserInfo.Command() {
                    public void process(final String toUser) {

                        //String toUser = userNode.getName();
                        log.debug("Processing any inbound messages for " + toUser);
                        if (toUser.equals(MAILMAN)) return;
                        UserInbox inbox = new UserInbox(repository, toUser);
                        Set<String> recentEdited = makeSetOf(inbox.loadRecentEdited());
                        for (UserInfo.InboxEntry e : es) {
                        	//the user who edited the item wont receive a message in inbox. 
                            if (!e.from.equals(toUser) && recentEdited.contains(e.assetUUID)) {
                                inbox.addToIncoming(e.assetUUID, e.note, e.from);
                            }
                        }
                    }
                });
                mailman.clearIncoming();
                repository.save();
            }

    }

    private Set<String> makeSetOf(List<InboxEntry> inboxEntries) {
        Set<String> entries = new HashSet<String>();
        for(InboxEntry e : inboxEntries) {
            entries.add(e.assetUUID);
        }
        return entries;
    }

    /**
     * Call this to note that there has been a change - will then publish to any interested parties.
     * @param item
     */
    public void recordItemUpdated(AssetItem item) {
        final String id = item.getUUID();
        final String name = item.getName();
        final String from = item.getRulesRepository().getSession().getUserID();
        executor.execute(new Runnable() {
            public void run() {
				// write the message to the admins outbox
				UserInbox inbox = new UserInbox(repository, MAILMAN);
				inbox.addToIncoming(id, name, from);
				processOutgoing();

				repository.save();
			}
        });
    }


}
