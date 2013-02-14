/*
 * Copyright 2013 JBoss Inc
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

package org.kie.guvnor.guided.scorecard.backend.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import javax.inject.Inject;

import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.commons.service.source.SourceContext;
import org.kie.guvnor.guided.scorecard.model.ScoreCardModel;
import org.kie.guvnor.guided.scorecard.service.GuidedScoreCardEditorService;
import org.uberfire.backend.server.util.Paths;

public class GuidedScoreCardSourceService
        extends BaseSourceService {

    private static final String PATTERN = ".scgd";

    @Inject
    private Paths paths;

    @Inject
    private GuidedScoreCardEditorService guidedScoreCardEditorService;

    protected GuidedScoreCardSourceService() {
        super( "/src/main/resources" );
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    public SourceContext getSource( final Path path ) {
        //Load model and convert to DRL
        final ScoreCardModel model = guidedScoreCardEditorService.loadModel( paths.convert( path ) );
        final String drl = new StringBuilder()
                .append(returnPackageDeclaration(path)).append("\n")
                .append(model.getImports().toString()).append("\n")
                .append(guidedScoreCardEditorService.toSource(model)).toString();

        //Construct Source context. If the resource has DSL Sentences it needs to be a .dslr file
        String destinationPath = stripProjectPrefix( path );
        destinationPath = correctFileName( destinationPath,
                                           ".drl" );
        final ByteArrayInputStream is = new ByteArrayInputStream( drl.getBytes() );
        final BufferedInputStream bis = new BufferedInputStream( is );
        final SourceContext context = new SourceContext( bis,
                                                         destinationPath );
        return context;
    }

}
