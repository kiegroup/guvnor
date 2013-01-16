/*
 * Copyright 2013 JBoss Inc
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

package org.kie.guvnor.services.backend.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.attribute.FileTime;
import org.kie.guvnor.services.backend.config.attribute.ConfigAttributes;
import org.kie.guvnor.services.backend.config.attribute.ConfigView;
import org.kie.guvnor.services.config.ResourceConfigService;
import org.kie.guvnor.services.config.model.ResourceConfig;
import org.kie.guvnor.services.config.model.imports.ImportsConfig;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.kie.commons.validation.Preconditions.*;
import static org.kie.guvnor.services.backend.config.attribute.ConfigAttributesUtil.*;

@Service
@ApplicationScoped
public class ResourceConfigServiceImpl implements ResourceConfigService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Override
    public ResourceConfig getConfig( final Path resource ) {
        checkNotNull( "resource", resource );

        final org.kie.commons.java.nio.file.Path path = paths.convert( resource );

        final ConfigView configView = ioService.getFileAttributeView( path, ConfigView.class );

        final ImportsConfig importsConfig = new ImportsConfig();
        for ( final String i : configView.readAttributes().imports() ) {
            importsConfig.addImport( new ImportsConfig.Import( i ) );
        }

        final ResourceConfig result = new ResourceConfig();
        result.setImportsConfig( importsConfig );
        result.setImportsConfig( configView.readAttributes().content() );

        return result;
    }

    @Override
    public Map<String, Object> configAttrs( final Map<String, Object> _attrs,
                                            final ResourceConfig config ) {
        checkNotNull( "config", config );
        checkNotNull( "_attrs", _attrs );

        final Map<String, Object> attrs = cleanup( _attrs );

        final List<String> imports = new ArrayList<String>() {{
            for ( int i = 0; i < config.getImportsConfig().getImports().size(); i++ ) {
                add( i, config.getImportsConfig().getImports().get( i ).getType() );
            }
        }};

        attrs.putAll( toMap( new ConfigAttributes() {
            @Override
            public List<String> imports() {
                return imports;
            }

            @Override
            public String content() {
                return config.getStringContent();
            }

            @Override
            public FileTime lastModifiedTime() {
                return null;
            }

            @Override
            public FileTime lastAccessTime() {
                return null;
            }

            @Override
            public FileTime creationTime() {
                return null;
            }

            @Override
            public boolean isRegularFile() {
                return false;
            }

            @Override
            public boolean isDirectory() {
                return false;
            }

            @Override
            public boolean isSymbolicLink() {
                return false;
            }

            @Override
            public boolean isOther() {
                return false;
            }

            @Override
            public long size() {
                return 0;
            }

            @Override
            public Object fileKey() {
                return null;
            }
        }, "*" ) );

        return attrs;
    }
}
