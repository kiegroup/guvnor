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

package org.kie.guvnor.enums.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.validation.model.BuilderResultLine;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.datamodel.backend.server.DataEnumLoader;
import org.kie.guvnor.enums.service.EnumService;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
@Service
@ApplicationScoped
public class EnumServiceImpl implements EnumService {

    @Override
    public BuilderResult validate( final Path path,
                                   final String content ) {
        final DataEnumLoader loader = new DataEnumLoader( content );
        if ( !loader.hasErrors() ) {
            return new BuilderResult();
        } else {
            final List<BuilderResultLine> errors = new ArrayList<BuilderResultLine>();
            final List<String> errs = loader.getErrors();

            for ( final String message : errs ) {
                final BuilderResultLine result = new BuilderResultLine().setResourceName( path.getFileName() ).setResourceFormat( getFormat() ).setResourceId( path.toURI() ).setMessage( message );
                errors.add( result );
            }

            final BuilderResult result = new BuilderResult();
            result.addLines( errors );

            return result;
        }
    }

    @Override
    public boolean isValid( final Path path,
                            final String content ) {
        return !validate( path, content ).hasLines();
    }

    public String getFormat() {
        return "enumeration";
    }

    @Override
    public AnalysisReport verify( final Path path,
                                  final String content ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }
}
