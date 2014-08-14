package org.guvnor.common.services.backend.util;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

@ApplicationScoped
public class CommentedOptionFactoryImpl implements CommentedOptionFactory {

    private static final String UNKNOWN_IDENTITY = "unknown";

    private static final String UNKNOWN_SESSION = "--";

    @Inject
    private User identity;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public CommentedOption makeCommentedOption( final String commitMessage ) {
        return makeCommentedOption( commitMessage, identity, sessionInfo );
    }

    @Override
    public CommentedOption makeCommentedOption( final String commitMessage, final User identity, final SessionInfo sessionInfo ) {
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( getSessionId( sessionInfo ),
                getIdentityName( identity ),
                null,
                commitMessage,
                when );
        return co;
    }

    protected String getIdentityName( User identity ) {
        try {
            return identity != null ? identity.getIdentifier() : UNKNOWN_IDENTITY;
        } catch ( ContextNotActiveException e ) {
            return UNKNOWN_IDENTITY;
        }
    }

    protected String getSessionId( SessionInfo sessionInfo ) {
        try {
            return sessionInfo != null ? sessionInfo.getId() : UNKNOWN_SESSION;
        } catch ( ContextNotActiveException e ) {
            return UNKNOWN_SESSION;
        }
    }

}
