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
import org.kie.guvnor.guided.rule.backend.server.util.BRDRTPersistence;
import org.kie.guvnor.guided.rule.backend.server.util.BRDRTXMLPersistence;
import org.kie.guvnor.guided.rule.backend.server.util.BRLPersistence;
import org.kie.guvnor.guided.rule.model.templates.GuidedTemplateEditorContent;
import org.kie.guvnor.guided.rule.model.templates.TemplateModel;
import org.kie.guvnor.guided.rule.service.GuidedRuleTemplateEditorService;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class GuidedRuleTemplateEditorServiceImpl
        implements GuidedRuleTemplateEditorService {

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
    public GuidedTemplateEditorContent loadContent( final Path path ) {
        final TemplateModel model = loadTemplateModel( path );
        final DataModelOracle dataModel = dataModelService.getDataModel( path );
        return new GuidedTemplateEditorContent( dataModel, model );
    }

    @Override
    public TemplateModel loadTemplateModel( final Path path ) {
        return (TemplateModel) BRDRTXMLPersistence.getInstance().unmarshal( ioService.readAllString( paths.convert( path ) ) );
    }

    @Override
    public void save( final Path path,
                      final TemplateModel model ) {
        final BRLPersistence p = BRDRTXMLPersistence.getInstance();
        final String xml = p.marshal( model );
        ioService.write( paths.convert( path ),
                         xml );
    }

    @Override
    public String toSource( final TemplateModel model ) {
        return BRDRTPersistence.getInstance().marshal( model );
    }

    @Override
    public AnalysisReport verify( final Path path,
                                  final TemplateModel content,
                                  final Collection<WorkingSetConfigData> activeWorkingSets ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final TemplateModel content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final TemplateModel content ) {
        return !validate( path,
                          content ).hasLines();
    }
}
