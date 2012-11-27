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

package org.kie.guvnor.editors.guided.server;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.editors.guided.client.util.WorkingSetConfigData;
import org.kie.guvnor.editors.guided.model.RuleModel;
import org.kie.guvnor.editors.guided.model.analysis.AnalysisReport;
import org.kie.guvnor.editors.guided.model.templates.TemplateModel;
import org.kie.guvnor.editors.guided.server.util.BRLPersistence;
import org.kie.guvnor.editors.guided.server.util.BRXMLPersistence;
import org.kie.guvnor.editors.guided.service.GuidedEditorService;
import org.uberfire.backend.server.VFSServicesServerImpl;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class GuidedEditorServiceMockImpl
        implements GuidedEditorService {

    @Inject
    VFSServicesServerImpl vfs;

    @Override
    public RuleModel loadModel( final Path path ) {
        final BRLPersistence p = BRXMLPersistence.getInstance();

        return p.unmarshal( vfs.readAllString( path ) );
    }

    @Override
    public TemplateModel loadTemplateModel( Path path ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save( Path path,
                      RuleModel model ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save( Path path,
                      TemplateModel model ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AnalysisReport verifyAssetWithoutVerifiersRules( RuleModel model,
                                                            Set<WorkingSetConfigData> activeWorkingSets ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] loadDropDownExpression( String[] valuePairs,
                                            String expression ) {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
