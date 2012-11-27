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

package org.kie.guvnor.guided.service;

import java.util.Set;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.guvnor.guided.client.util.WorkingSetConfigData;
import org.kie.guvnor.guided.model.RuleModel;
import org.kie.guvnor.guided.model.analysis.AnalysisReport;
import org.kie.guvnor.guided.model.templates.TemplateModel;
import org.uberfire.backend.vfs.Path;

@Remote
public interface GuidedEditorService {

    RuleModel loadModel( final Path path );

    TemplateModel loadTemplateModel( final Path path );

    void save( final Path path,
               final RuleModel model );

    void save( final Path path,
               final TemplateModel model );

    AnalysisReport verifyAssetWithoutVerifiersRules( final RuleModel model,
                                                     final Set<WorkingSetConfigData> activeWorkingSets );

    /**
     * @param valuePairs key=value pairs to be interpolated into the expression.
     * @param expression The expression, which will then be eval'ed to generate a
     * String[]
     */
    String[] loadDropDownExpression( final String[] valuePairs,
                                     final String expression );
}
