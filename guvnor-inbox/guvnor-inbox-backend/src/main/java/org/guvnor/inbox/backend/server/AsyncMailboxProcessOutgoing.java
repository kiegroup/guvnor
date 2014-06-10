package org.guvnor.inbox.backend.server;

public interface AsyncMailboxProcessOutgoing {

    void execute( final InboxBackend inboxBackend );

    String getDescription();

}
