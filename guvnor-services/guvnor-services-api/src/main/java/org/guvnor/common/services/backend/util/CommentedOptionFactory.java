package org.guvnor.common.services.backend.util;

import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;

public interface CommentedOptionFactory {

    CommentedOption makeCommentedOption( final String commitMessage );

    CommentedOption makeCommentedOption( final String commitMessage, final Identity identity, final SessionInfo sessionInfo );

}
