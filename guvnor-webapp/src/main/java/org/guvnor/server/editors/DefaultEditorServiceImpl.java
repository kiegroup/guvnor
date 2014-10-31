/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.server.editors;

import org.guvnor.shared.editors.DefaultEditorService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

@Service
@ApplicationScoped
public class DefaultEditorServiceImpl
        implements DefaultEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    protected User identity;

    @Inject
    protected SessionInfo sessionInfo;

    @Override
    public Path save(Path path, String content, String commitMessage) {

        ioService.write(Paths.convert(path),
                content,
                makeCommentedOption(commitMessage));

        return path;
    }

    protected CommentedOption makeCommentedOption(final String commitMessage) {
        return new CommentedOption(
                sessionInfo.getId(),
                identity.getIdentifier(),
                null,
                commitMessage,
                new Date());
    }
}
