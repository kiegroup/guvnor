package org.guvnor.common.services.backend.util;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

public interface CommentedOptionFactory {

    CommentedOption makeCommentedOption( final String commitMessage );

    CommentedOption makeCommentedOption( final String commitMessage, final User identity, final SessionInfo sessionInfo );

}
