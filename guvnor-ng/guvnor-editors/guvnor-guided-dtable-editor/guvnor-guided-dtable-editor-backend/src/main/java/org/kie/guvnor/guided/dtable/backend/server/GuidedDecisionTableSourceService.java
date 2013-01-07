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

import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.SourceService;
import org.kie.guvnor.guided.dtable.service.GuidedDecisionTableEditorService;
import org.uberfire.backend.server.util.Paths;

import javax.inject.Inject;

public class GuidedDecisionTableSourceService
        implements SourceService {


    private GuidedDecisionTableEditorService guidedDecisionTableEditorService;
    private Paths paths;

    public GuidedDecisionTableSourceService() {
        // For Weld
    }

    @Inject
    public GuidedDecisionTableSourceService(GuidedDecisionTableEditorService guidedDecisionTableEditorService,
                                            Paths paths) {
        this.guidedDecisionTableEditorService = guidedDecisionTableEditorService;
        this.paths = paths;
    }


    @Override
    public String getSupportedFileExtension() {
        return "gdst";
    }

    @Override
    public String toDRL(Path path) {
        return guidedDecisionTableEditorService.toSource(guidedDecisionTableEditorService.loadRuleModel(paths.convert(path)));
    }
}
