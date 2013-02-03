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

package org.kie.guvnor.globals.backend.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.globals.model.Global;
import org.kie.guvnor.globals.service.GlobalsEditorService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class GlobalsEditorServiceImpl
        implements GlobalsEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Override
    public BuilderResult validate( final Path path,
                                   final String content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final String content ) {
        return !validate( path, content ).hasLines();
    }

    @Override
    public AnalysisReport verify( Path path,
                                  String content ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }

    @Override
    public List<Global> load( final Path path ) {
        final String drl = ioService.readAllString( paths.convert( path ) );
        return fromDRL( drl );
    }

    @Override
    public void save( final Path path,
                      final List<Global> content,
                      final String comment ) {
        ioService.write( paths.convert( path ),
                         toDRL( content ),
                         makeCommentedOption( comment ) );
    }

    @Override
    public void save( final Path resource,
                      final List<Global> content,
                      final Metadata metadata,
                      final String comment ) {

        ioService.write( paths.convert( resource ),
                         toDRL( content ),
                         metadataService.setUpAttributes( resource, metadata ),
                         makeCommentedOption( comment ) );
    }

    private List<Global> fromDRL( final String drl ) {
        final List<Global> globals = new ArrayList<Global>();
        return globals;
    }

    private String toDRL( final List<Global> globals ) {
        return "";
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }
}
