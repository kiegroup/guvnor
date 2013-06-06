package org.guvnor.inbox.backend.server;

import java.util.List;

import org.uberfire.paging.AbstractPageRow;
import org.uberfire.paging.PageRequest;
import org.uberfire.security.Identity;

public interface PageRowBuilder<REQUEST extends PageRequest, CONTENT> {

    public List<? extends AbstractPageRow> build();

    public void validate();

    public PageRowBuilder<REQUEST, CONTENT> withPageRequest( final REQUEST pageRequest );

    public PageRowBuilder<REQUEST, CONTENT> withIdentity( final Identity identity );

    public PageRowBuilder<REQUEST, CONTENT> withContent( final CONTENT pageRequest );

}
