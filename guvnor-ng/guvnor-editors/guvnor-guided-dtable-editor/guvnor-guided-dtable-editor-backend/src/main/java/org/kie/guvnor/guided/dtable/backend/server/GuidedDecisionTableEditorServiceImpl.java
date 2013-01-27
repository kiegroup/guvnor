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

package org.kie.guvnor.guided.dtable.backend.server;

import java.util.Collection;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.data.workingset.WorkingSetConfigData;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.model.workitems.PortableWorkDefinition;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.guided.dtable.backend.server.util.GuidedDTDRLPersistence;
import org.kie.guvnor.guided.dtable.backend.server.util.GuidedDTXMLPersistence;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.kie.guvnor.guided.dtable.service.GuidedDecisionTableEditorService;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class GuidedDecisionTableEditorServiceImpl
        implements GuidedDecisionTableEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private MetadataService metadataService;

    @Override
    public GuidedDecisionTableEditorContent loadContent( final Path path ) {
        final GuidedDecisionTable52 model = loadRuleModel( path );
        final DataModelOracle dataModel = dataModelService.getDataModel( path );
        return new GuidedDecisionTableEditorContent( dataModel,
                                                     model );
    }

    @Override
    public GuidedDecisionTable52 loadRuleModel( Path path ) {
        return GuidedDTXMLPersistence.getInstance().unmarshal( ioService.readAllString( paths.convert( path ) ) );
    }

    @Override
    public void save(Path path, GuidedDecisionTable52 model) {
        ioService.write(
                paths.convert(path),
                GuidedDTXMLPersistence.getInstance().marshal( model ));
    }

    @Override
    public void save(final Path path,
                     final GuidedDecisionTable52 model,
                     final Metadata metadata,
                     final String commitMessage) {

        if (metadata == null) {
            ioService.write(
                    paths.convert(path),
                    GuidedDTXMLPersistence.getInstance().marshal( model ),
                    new CommentedOption(
                            null,
                            commitMessage,
                            null,
                            null));
        } else {
            ioService.write(
                    paths.convert(path),
                    GuidedDTXMLPersistence.getInstance().marshal( model ),
                    metadataService.setUpAttributes(path, metadata),
                    new CommentedOption(
                            null,
                            commitMessage,
                            null,
                            null));
        }

    }

    @Override
    public String toSource( final GuidedDecisionTable52 model ) {
        final GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        return p.marshal( model );
    }

    @Override
    public Set<PortableWorkDefinition> loadWorkItemDefinitions( Path path ) {
        return null;
    }

    @Override
    public AnalysisReport verify( final Path path,
                                  final GuidedDecisionTable52 content,
                                  final Collection<WorkingSetConfigData> activeWorkingSets ) {
        //TODO {manstis} verify
        return new AnalysisReport();
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final GuidedDecisionTable52 content ) {
        //TODO {manstis} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final GuidedDecisionTable52 content ) {
        return !validate( path,
                          content ).hasLines();
    }
}
