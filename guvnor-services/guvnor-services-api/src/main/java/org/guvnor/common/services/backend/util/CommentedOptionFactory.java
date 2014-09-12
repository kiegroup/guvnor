package org.guvnor.common.services.backend.util;

import org.uberfire.java.nio.base.options.CommentedOption;

public interface CommentedOptionFactory {

    CommentedOption makeCommentedOption( final String commitMessage );

}
