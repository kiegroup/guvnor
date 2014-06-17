package org.guvnor.inbox.backend.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.DescriptiveThreadFactory;

import static javax.ejb.TransactionAttributeType.*;

@Singleton
@Startup
@TransactionAttribute(NOT_SUPPORTED)
public class MailboxProcessOutgoingExecutorManager {

    @Inject
    private InboxBackend inboxBackend;

    private AtomicBoolean useExecService = new AtomicBoolean( false );
    private ExecutorService executorService = null;

    @Asynchronous
    public void execute( final AsyncMailboxProcessOutgoing mailboxProcessOutgoing ) {
        if ( useExecService.get() ) {
            getExecutorService().execute( new DescriptiveRunnable() {
                @Override
                public void run() {
                    mailboxProcessOutgoing.execute( inboxBackend );
                }

                @Override
                public String getDescription() {
                    return mailboxProcessOutgoing.getDescription();
                }
            } );
        } else {
            mailboxProcessOutgoing.execute( inboxBackend );
        }
    }

    private synchronized ExecutorService getExecutorService() {
        if ( executorService == null ) {
            executorService = Executors.newCachedThreadPool( new DescriptiveThreadFactory() );
        }
        return executorService;
    }

    public void setInboxBackend( final InboxBackend inboxBackend ) {
        this.inboxBackend = inboxBackend;
        this.useExecService.set( true );
    }

    public void shutdown() {
        if ( useExecService.get() ) {
            executorService.shutdownNow();
        }
    }
}
