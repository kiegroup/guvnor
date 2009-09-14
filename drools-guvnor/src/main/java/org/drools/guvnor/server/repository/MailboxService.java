package org.drools.guvnor.server.repository;

import org.drools.repository.RulesRepository;
import org.drools.repository.AssetItem;
import org.drools.repository.UserInfo;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.guvnor.server.util.LoggingHelper;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.apache.log4j.Logger;

import javax.jcr.RepositoryException;
import javax.jcr.Node;
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
        try {
            log.info("Processing outgoing messages");
            if (repository != null) {
                UserInbox mailman = new UserInbox(repository, MAILMAN);
                final List<UserInbox.InboxEntry> es  = mailman.loadIncoming();
                log.debug("Outgoing messages size " + es.size());
                //wipe out inbox for mailman here...
                UserInfo.eachUser(this.repository, new UserInfo.Command() {
                    public void process(final Node userNode) throws RepositoryException {

                        String toUser = userNode.getName();
                        log.debug("Processing any inbound messages for " + toUser);
                        if (toUser.equals(MAILMAN)) return;
                        UserInbox inbox = new UserInbox(repository, userNode.getName());
                        Set<String> recentEdited = makeSetOf(inbox.loadRecentEdited());
                        for (UserInbox.InboxEntry e : es) {
                            if (!e.from.equals(toUser) && recentEdited.contains(e.assetUUID)) {
                                inbox.addToIncoming(e.assetUUID, e.note, e.from);
                            }
                        }
                    }
                });
                mailman.clearIncoming();
                repository.save();
            }
        } catch (RepositoryException e) {
            log.error(e);
        }

    }

    private Set<String> makeSetOf(List<UserInbox.InboxEntry> inboxEntries) {
        Set<String> entries = new HashSet<String>();
        for(UserInbox.InboxEntry e : inboxEntries) {
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
                try {
                    //write the message to the admins outbox
                    UserInbox inbox = new UserInbox(repository, MAILMAN);
                    inbox.addToIncoming(id, name, from);
                    processOutgoing();
                } catch (RepositoryException e) {
                    log.error(e);
                }
                repository.save();
            }
        });
    }


}
