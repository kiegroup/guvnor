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

package org.kie.guvnor.services.backend.metadata;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.io.attribute.DublinCoreView;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class MetadataServiceImpl implements MetadataService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Override
    public Metadata getMetadata( final Path resource ) {

        final org.kie.commons.java.nio.file.Path path = paths.convert( resource );

        final DublinCoreView dcoreView = ioService.getFileAttributeView( path, DublinCoreView.class );

        final Metadata metadata = new Metadata();
        metadata.setPath( paths.convert( path.toRealPath() ) );
        return metadata;
    }

    @Override
    public Map<String, Object> configAttrs( final Map<String, Object> attrs,
                                            final Metadata metadata ) {
        //cleanup existing attrs -> looking for key's that should be removed
        //build atts
        return attrs;
    }
}
