package org.kie.guvnor.services.backend.inbox;

import java.util.List;

import org.kie.guvnor.commons.data.tables.AbstractPageRow;
import org.kie.guvnor.commons.data.tables.PageRequest;
import org.uberfire.security.Identity;


public interface PageRowBuilder<REQUEST extends PageRequest, CONTENT> {
    public List< ? extends AbstractPageRow> build();

    public void validate();
   
    public PageRowBuilder<REQUEST, CONTENT> withPageRequest(final REQUEST pageRequest);

    public PageRowBuilder<REQUEST, CONTENT> withIdentity(final Identity identity);

    public PageRowBuilder<REQUEST, CONTENT> withContent(final CONTENT pageRequest);

}
