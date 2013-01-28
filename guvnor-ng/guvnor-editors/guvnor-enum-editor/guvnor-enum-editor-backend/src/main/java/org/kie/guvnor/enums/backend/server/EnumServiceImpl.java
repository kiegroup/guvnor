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

package org.kie.guvnor.enums.backend.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.validation.model.BuilderResultLine;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.backend.server.DataEnumLoader;
import org.kie.guvnor.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.guvnor.enums.service.EnumService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
@Service
@ApplicationScoped
public class EnumServiceImpl implements EnumService {

    private static final String FORMAT = "enumeration";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private Paths paths;

    @Inject
    private Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache;

    @Override
    public BuilderResult validate( final Path path,
                                   final String content ) {
        final DataEnumLoader loader = new DataEnumLoader( content );
        if ( !loader.hasErrors() ) {
            return new BuilderResult();
        } else {
            final List<BuilderResultLine> errors = new ArrayList<BuilderResultLine>();
            final List<String> errs = loader.getErrors();

            for ( final String message : errs ) {
                final BuilderResultLine result = new BuilderResultLine().setResourceName( path.getFileName() ).setResourceFormat( FORMAT ).setResourceId( path.toURI() ).setMessage( message );
                errors.add( result );
            }

            final BuilderResult result = new BuilderResult();
            result.addLines( errors );

            return result;
        }
    }

    @Override
    public boolean isValid( final Path path,
                            final String content ) {
        return !validate( path, content ).hasLines();
    }

    @Override
    public AnalysisReport verify( final Path path,
                                  final String content ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }

    @Override
    public void save( final Path resource,
                      final String content,
                      final Metadata metadata,
                      final String commitMessage ) {
        final org.kie.commons.java.nio.file.Path path = paths.convert( resource );

        if (metadata == null) {
            ioService.write(
                    path,
                    content,
                    metadataService.getCommentedOption(commitMessage));
        } else {
            ioService.write(
                    path,
                    content,
                    metadataService.setUpAttributes(resource, metadata),
                    metadataService.getCommentedOption(commitMessage));
        }



        invalidateDMOPackageCache.fire( new InvalidateDMOPackageCacheEvent( resource ) );
    }
}
