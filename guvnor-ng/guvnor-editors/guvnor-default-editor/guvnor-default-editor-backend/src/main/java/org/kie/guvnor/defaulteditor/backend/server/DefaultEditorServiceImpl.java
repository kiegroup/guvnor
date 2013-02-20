/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.defaulteditor.backend.server;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.guvnor.dsltext.service.DSLTextEditorService;
import org.kie.guvnor.services.inbox.AssetEditedEvent;
import org.kie.guvnor.services.inbox.AssetOpenedEvent;
import org.kie.guvnor.services.metadata.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

@Service
@ApplicationScoped
public class DefaultEditorServiceImpl
        implements DefaultEditorService {

    private static final Logger log = LoggerFactory.getLogger( DefaultEditorServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private Paths paths;

    @Inject
    private Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache;

    @Inject
    private Identity identity;
    
    @Inject
    private Event<AssetEditedEvent> assetEditedEvent;
    
    @Inject
    private Event<AssetOpenedEvent> assetOpenedEvent;
    
    @Override
    public void save(Path path, String content, Metadata metadata, String comment) {

        ioService.write(
                paths.convert(path),
                content,
                metadataService.setUpAttributes(path, metadata),
                makeCommentedOption(comment));
    }

    @Override
    public void save(Path path, String content, String comment) {
        ioService.write(paths.convert(path),
                content,
                makeCommentedOption(comment));
    }

    private CommentedOption makeCommentedOption(final String commitMessage) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption(name,
                null,
                commitMessage,
                when);
        return co;
    }
}
