/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
