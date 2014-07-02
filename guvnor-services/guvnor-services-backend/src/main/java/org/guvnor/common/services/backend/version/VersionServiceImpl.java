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

package org.guvnor.common.services.backend.version;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.version.VersionService;
import org.guvnor.common.services.shared.version.model.PortableVersionRecord;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;

import static org.uberfire.java.nio.file.StandardCopyOption.*;

@Service
@ApplicationScoped
public class VersionServiceImpl implements VersionService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public List<VersionRecord> getVersion( final Path path ) {
        try {
            final List<VersionRecord> records = ioService.getFileAttributeView( Paths.convert( path ), VersionAttributeView.class ).readAttributes().history().records();

            final List<VersionRecord> result = new ArrayList<VersionRecord>( records.size() );

            for ( final VersionRecord record : records ) {
                result.add( new PortableVersionRecord( record.id(), record.author(), record.email(), record.comment(), record.date(), record.uri() ) );
            }

            return result;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path getPathToPreviousVersion(String uri) {
        URI uri1 = URI.create(uri);
        org.uberfire.java.nio.file.Path path = ioService.get(uri1);
        return Paths.convert(path);
    }

    @Override
    public Path restore( final Path _path,
                         final String comment ) {
        try {
            final org.uberfire.java.nio.file.Path path = Paths.convert( _path );

            final org.uberfire.java.nio.file.Path target = path.getFileSystem().getPath( path.toString() );

            return Paths.convert( ioService.copy( path, target, REPLACE_EXISTING, new CommentedOption( getSessionInfo().getId(), identity.getName(), null, comment ) ) );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    protected SessionInfo getSessionInfo() {
        return new SafeSessionInfo(sessionInfo);
    }
}
