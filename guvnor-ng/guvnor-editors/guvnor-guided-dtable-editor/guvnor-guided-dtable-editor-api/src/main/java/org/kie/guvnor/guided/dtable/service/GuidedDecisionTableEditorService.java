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

package org.kie.guvnor.guided.dtable.service;

import org.drools.guvnor.models.commons.workitems.PortableWorkDefinition;
import org.drools.guvnor.models.guided.dtable.model.GuidedDecisionTable52;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.guvnor.commons.service.file.FileService;
import org.kie.guvnor.commons.service.source.ViewSourceService;
import org.kie.guvnor.commons.service.validation.ValidationService;
import org.kie.guvnor.commons.service.verification.ScopedVerificationService;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.uberfire.backend.vfs.Path;

import java.util.Set;

@Remote
public interface GuidedDecisionTableEditorService
        extends FileService<GuidedDecisionTable52>,
                ViewSourceService<GuidedDecisionTable52>,
                ValidationService<GuidedDecisionTable52>,
                ScopedVerificationService<GuidedDecisionTable52> {

    GuidedDecisionTableEditorContent loadContent( final Path path );

    GuidedDecisionTable52 loadRuleModel( final Path path );

    Set<PortableWorkDefinition> loadWorkItemDefinitions( final Path path );
}
