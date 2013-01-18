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

package org.kie.guvnor.guided.rule.service;

import java.util.Date;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.guvnor.commons.service.source.ViewSourceService;
import org.kie.guvnor.commons.service.validation.ValidationService;
import org.kie.guvnor.commons.service.verification.ScopedVerificationService;
import org.kie.guvnor.guided.rule.model.GuidedEditorContent;
import org.kie.guvnor.guided.rule.model.RuleModel;
import org.uberfire.backend.vfs.Path;

@Remote
public interface GuidedRuleEditorService
        extends ViewSourceService<RuleModel>,
                ValidationService<RuleModel>,
                ScopedVerificationService<RuleModel> {

    GuidedEditorContent loadContent( final Path path );

    RuleModel loadRuleModel( Path path );

    void save( final Path path,
               final RuleModel model );

    void save( final Path path,
               final RuleModel factModels,
               final String comment,
               final Date when,
               final String lastContributor );

}
