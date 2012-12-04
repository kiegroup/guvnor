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

package org.guvnor.jcr2vfsmigration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.rpc.Asset;
import org.kie.commons.io.IOService;
import org.kie.commons.io.options.CommentedOption;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.OpenOption;
import org.kie.commons.java.nio.file.Path;

import static org.guvnor.jcr2vfsmigration.vfs.IOServiceFactory.Migration.*;

@ApplicationScoped
public class RulesRepositoryVFS {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    private Path root;

    @PostConstruct
    protected void init() {
        setupGitRepos();
    }

    private void setupGitRepos() {
        final Iterator<FileSystem> fsIterator = ioService.getFileSystems( MIGRATION_INSTANCE ).iterator();
        if ( fsIterator.hasNext() ) {
            final FileSystem bootstrap = fsIterator.next();
            final Iterator<Path> rootIterator = bootstrap.getRootDirectories().iterator();
            if ( rootIterator.hasNext() ) {
                this.root = rootIterator.next();
            }
        }
    }

    public String checkinVersion( final Asset asset ) {
        final Path assetPath = convertUUIDToPath( asset );

        //commits the change, if many changes at once, use other options
        ioService.setAttribute( assetPath, "checkinComment", asset.getCheckinComment() );

        Map<String, Object> attrs = new HashMap<String, Object>() {{
            put( "checkinComment", asset.getCheckinComment() );
            put( "description", asset.getDescription() );
            put( "state", asset.getState() );
        }};
        //AND MORE

        //In old Guvnor, we convert domain object to binary by using content handler:
/*        ContentHandler handler = ContentManager.getHandler(asset.getFormat());
        handler.storeAssetContent(asset,  repoAsset);*/
        //Domain object to binary
        String assetContent = null;

        //TODO: vfsService needs a write(Path path, byte[] content) method.

        final OpenOption commentedOption = new CommentedOption( "user", asset.getCheckinComment() );

        //single commit for metadata and content
        ioService.write( assetPath, assetContent, attrs, commentedOption );

        //commits only metadata
        ioService.setAttributes( assetPath, attrs );

        //commits only content
        ioService.write( assetPath, assetContent );

        //commits only content and uses `commentedOption` to customize commit message, user and related
        ioService.write( assetPath, assetContent, commentedOption );

        return "";//old Guvnor returns uuid
    }

    //TODO
    public Path convertUUIDToPath( Asset asset ) {
        String packageName = asset.getMetaData().getModuleName();
        String assetName = asset.getName();
        return root.resolve( packageName + "/" + assetName );
    }
}
