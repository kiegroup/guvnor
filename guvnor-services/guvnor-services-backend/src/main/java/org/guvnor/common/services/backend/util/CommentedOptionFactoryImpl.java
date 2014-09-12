package org.guvnor.common.services.backend.util;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;

import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;

@ApplicationScoped
public class CommentedOptionFactoryImpl implements CommentedOptionFactory {

    @Inject
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = getIdentityName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( getSessionId(),
                name,
                null,
                commitMessage,
                when );
        return co;
    }

    protected String getIdentityName() {
        try {
            return identity.getName();
        } catch ( ContextNotActiveException e ) {
            return "unknown";
        }
    }

    protected String getSessionId() {
        try {
            return sessionInfo.getId();
        } catch ( ContextNotActiveException e ) {
            return "--";
        }
    }

}
