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

package org.kie.guvnor.guided.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import javax.inject.Inject;

import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.commons.service.source.SourceContext;
import org.kie.guvnor.guided.template.model.TemplateModel;
import org.kie.guvnor.guided.template.server.util.BRDRTPersistence;
import org.kie.guvnor.guided.template.service.GuidedRuleTemplateEditorService;
import org.uberfire.backend.server.util.Paths;

public class GuidedRuleTemplateSourceService
        extends BaseSourceService<TemplateModel> {

    private static final String PATTERN = ".template";

    @Inject
    private Paths paths;

    @Inject
    private GuidedRuleTemplateEditorService guidedRuleTemplateEditorService;

    protected GuidedRuleTemplateSourceService() {
        super( "/src/main/resources" );
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    public SourceContext getSource( final Path path ) {
        //Load model and convert to DRL
        final TemplateModel model = guidedRuleTemplateEditorService.loadTemplateModel( paths.convert( path ) );
        final String drl = getSource(path,model);
        final boolean hasDSL = model.hasDSLSentences();

        //Construct Source context. If the resource has DSL Sentences it needs to be a .dslr file
        String destinationPath = stripProjectPrefix( path );
        destinationPath = correctFileName( destinationPath,
                                           ( hasDSL ? ".dslr" : ".drl" ) );
        final ByteArrayInputStream is = new ByteArrayInputStream( drl.getBytes() );
        final BufferedInputStream bis = new BufferedInputStream( is );
        final SourceContext context = new SourceContext( bis,
                                                         destinationPath );
        return context;
    }

    @Override
    public String getSource(Path path, TemplateModel model) {
        return new StringBuilder()
                .append(returnPackageDeclaration(path)).append("\n")
                .append(model.getImports().toString()).append("\n")
                .append(BRDRTPersistence.getInstance().marshal(model)).toString();
    }

}
