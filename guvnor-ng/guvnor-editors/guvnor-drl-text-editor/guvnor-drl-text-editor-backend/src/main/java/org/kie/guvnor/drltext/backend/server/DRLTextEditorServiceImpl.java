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

package org.kie.guvnor.drltext.backend.server;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.service.verification.model.AnalysisReport;
import org.kie.guvnor.drltext.service.DRLTextEditorService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class DRLTextEditorServiceImpl implements DRLTextEditorService {
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    
    @Inject
    private Paths paths;
    
    @Override
    public BuilderResult validate( final Path path,
                                   final String content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final String content ) {
        return !validate( path, content ).hasLines();
    }

    @Override
    public AnalysisReport verify( Path path,
                                  String content ) {
        //TODO {porcelli} verify
        return new AnalysisReport();
    }
    
    @Override
    public void save( final Path resource,
                      final String content) {
        final org.kie.commons.java.nio.file.Path path = paths.convert( resource );
        
        Map<String, Object> attrs;

        try {
            attrs = ioService.readAttributes( path );
        } catch ( final NoSuchFileException ex ) {
            attrs = new HashMap<String, Object>();
        }

/*        if ( config != null ) {
            attrs = resourceConfigService.configAttrs( attrs, config );
        }
        if ( metadata != null ) {
            attrs = metadataService.configAttrs( attrs, metadata );
        }*/

        ioService.write( path, content, attrs );
    }
}
