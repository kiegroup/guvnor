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

package org.kie.guvnor.guided.rule.backend.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import javax.inject.Inject;

import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;
import org.kie.guvnor.commons.service.source.SourceContext;
import org.kie.guvnor.guided.rule.backend.server.util.BRDRLPersistence;
import org.kie.guvnor.guided.rule.model.RuleModel;
import org.kie.guvnor.guided.rule.service.GuidedRuleEditorService;
import org.uberfire.backend.server.util.Paths;

public class GuidedRuleSourceService
        extends BaseSourceService<RuleModel> {

    private static final String PATTERN = ".brl";

    @Inject
    private Paths paths;

    @Inject
    private GuidedRuleEditorService guidedRuleEditorService;

    protected GuidedRuleSourceService() {
        super("/src/main/resources");
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    public SourceContext getSource( final Path path ) {
        //Load model and convert to DRL
        final RuleModel model = guidedRuleEditorService.loadRuleModel( paths.convert( path ) );
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
    public String getSource(Path path, RuleModel model) {
        return new StringBuilder()
                .append(returnPackageDeclaration(path)).append("\n")
                .append(model.getImports().toString()).append("\n")
                .append(BRDRLPersistence.getInstance().marshal(model)).toString();
    }

}
