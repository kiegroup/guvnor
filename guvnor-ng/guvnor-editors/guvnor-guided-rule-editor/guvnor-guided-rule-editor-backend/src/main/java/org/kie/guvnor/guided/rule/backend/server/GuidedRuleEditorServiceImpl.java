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

package org.kie.guvnor.guided.rule.backend.server;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.guvnor.commons.data.workingset.WorkingSetConfigData;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.guided.rule.backend.server.util.BRDRLPersistence;
import org.kie.guvnor.guided.rule.backend.server.util.BRLPersistence;
import org.kie.guvnor.guided.rule.backend.server.util.BRXMLPersistence;
import org.kie.guvnor.guided.rule.model.GuidedEditorContent;
import org.kie.guvnor.guided.rule.model.RuleModel;
import org.kie.guvnor.guided.rule.service.GuidedRuleEditorService;
import org.kie.guvnor.project.service.ProjectService;
import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class GuidedRuleEditorServiceImpl
        implements GuidedRuleEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private ProjectService projectService;

    @Inject
    private DataModelService dataModelService;

    @Override
    public GuidedEditorContent loadContent( final Path path ) {
        final RuleModel model = loadRuleModel( path );
        final DataModelOracle dataModel = dataModelService.getDataModel( path );
        return new GuidedEditorContent( dataModel, model );
    }

    @Override
    public RuleModel loadRuleModel( Path path ) {
        return BRXMLPersistence.getInstance().unmarshal( ioService.readAllString( paths.convert( path ) ) );
    }

    @Override
    public void save( final Path path,
                      final RuleModel model ) {
        final BRLPersistence p = BRXMLPersistence.getInstance();
        final String xml = p.marshal( model );
        ioService.write( paths.convert( path ),
                         xml );
    }

    @Override
    public void save( final Path path,
                      final RuleModel factModels,
                      final String comment,
                      final Date when,
                      final String lastContributor ) {
        //TODO:

    }

    @Override
    public String[] loadDropDownExpression( final String[] valuePairs,
                                            String expression ) {
        final Map<String, String> context = new HashMap<String, String>();

        for ( final String valuePair : valuePairs ) {
            if ( valuePair == null ) {
                return new String[ 0 ];
            }
            final String[] pair = valuePair.split( "=" );
            context.put( pair[ 0 ],
                         pair[ 1 ] );
        }
        // first interpolate the pairs
        expression = (String) TemplateRuntime.eval( expression,
                                                    context );

        // now we can eval it for real...
        Object result = MVEL.eval( expression );
        if ( result instanceof String[] ) {
            return (String[]) result;
        } else if ( result instanceof List ) {
            List l = (List) result;
            String[] xs = new String[ l.size() ];
            for ( int i = 0; i < xs.length; i++ ) {
                Object el = l.get( i );
                xs[ i ] = el.toString();
            }
            return xs;
        } else {
            return null;
        }
    }

    @Override
    public String toSource( final RuleModel model ) {
        return BRDRLPersistence.getInstance().marshal( model );
    }

    @Override
    public AnalysisReport verify( final Path path,
                                  final RuleModel content,
                                  final Collection<WorkingSetConfigData> activeWorkingSets ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final RuleModel content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final RuleModel content ) {
        return !validate( path, content ).hasLines();
    }
}
