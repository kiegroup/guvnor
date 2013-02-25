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

import org.drools.guvnor.models.guided.dtable.backend.GuidedDTDRLPersistence;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.BaseSourceService;

public class GuidedDecisionTableSourceService
        extends BaseSourceService<GuidedDecisionTable52> {

    private static final String PATTERN = ".gdst";

    protected GuidedDecisionTableSourceService() {
        super( "/src/main/resources" );
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    public String getSource( final Path path,
                             final GuidedDecisionTable52 model ) {
        return new StringBuilder()
                .append( returnPackageDeclaration( path ) ).append( "\n" )
                .append( model.getImports().toString() ).append( "\n" )
                .append( GuidedDTDRLPersistence.getInstance().marshal( model ) ).toString();
    }

}
