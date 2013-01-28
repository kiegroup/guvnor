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

package org.kie.guvnor.drltext.backend.server;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.guvnor.drltext.service.DRLTextEditorService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

@Service
@ApplicationScoped
public class DRLTextEditorServiceImpl
        implements DRLTextEditorService {
    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache;

    @Inject
    private Paths paths;

    @Override
    public BuilderResult validate(final Path path,
                                  final String content) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid(final Path path,
                           final String content) {
        return !validate(path, content).hasLines();
    }

    @Override
    public AnalysisReport verify(Path path,
                                 String content) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }

    @Override
    public String load(Path path) {
        return ioService.readAllString(paths.convert(path));
    }

    @Override
    public void save(final Path resource,
                     final String content) {
        ioService.write(paths.convert(resource), content);
    }

    @Override
    public void save(Path path, String content, Metadata metadata, String commitMessage) {


        if (metadata == null) {
            ioService.write(
                    paths.convert(path),
                    content,
                    metadataService.getCommentedOption(commitMessage));
        } else {
            ioService.write(
                    paths.convert(path),
                    content,
                    metadataService.setUpAttributes(path, metadata),
                    metadataService.getCommentedOption(commitMessage));
        }

        invalidateDMOPackageCache.fire(new InvalidateDMOPackageCacheEvent(path));
    }
}
