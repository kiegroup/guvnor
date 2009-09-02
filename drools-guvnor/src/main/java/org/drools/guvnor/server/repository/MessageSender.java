package org.drools.guvnor.server.repository;

import org.drools.repository.RulesRepository;
import org.drools.repository.AssetItem;
import org.drools.repository.UserInfo;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.ScopeType;

import javax.jcr.RepositoryException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Manages the "delivery" of messages to users inboxes for events.
 * @author Michael Neale
 */
@Scope(ScopeType.APPLICATION)
@Startup
@Name("MessageProcessor")
public class MessageSender {


    @In public BRMSRepositoryConfiguration configurator;

    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    public RulesRepository repository;


    public void recordItemUpdated(AssetItem item) {
        final String id = item.getUUID();
        final String name = item.getName();

        executor.execute(new Runnable() {
            public void run() {

                try {
                    //write the message to the admins outbox
                    UserInfo queue = new UserInfo(repository);
                    

                    //at the end, schedule a mailbox sweep... (to send)
                } catch (RepositoryException e) {

                }



            }
        });
    }



    @Create public void create() {
        this.repository = new RulesRepository(configurator.newSession("mailman"));
        //kick off a task to deliver things here??

    }


    @Destroy public void destroy() { this.repository.logout(); }





    
}
