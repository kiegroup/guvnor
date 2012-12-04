/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.jcr2vfsmigration;

import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.PathImpl;
import org.drools.guvnor.client.rpc.Asset;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
public class RulesRepositoryVFS {
    @Inject
    @Named("fs")
    private ActiveFileSystems fileSystems;
    
    @Inject
    private VFSService vfsService;
    
    protected Root root = null;

    @PostConstruct
    protected void init() {
        setupGitRepos();
    }
    
    private void setupGitRepos() {
        final org.uberfire.backend.vfs.Path rootPath = fileSystems.getBootstrapFileSystem().getRootDirectories().get( 0 );
        root = new Root( rootPath, new DefaultPlaceRequest( "RepositoryEditor" ) );
    }
       
    public String checkinVersion(Asset asset) {
        Path assetPathInVFS = convertUUIDToPath(asset);
    	vfsService.setAttribute(assetPathInVFS, "checkinComment", asset.getCheckinComment(), null);
    	vfsService.setAttribute(assetPathInVFS, "description", asset.getDescription(), null);
    	vfsService.setAttribute(assetPathInVFS, "state", asset.getState(), null);
    	//AND MORE
    	
    	//In old Guvnor, we convert domain object to binary by using content handler:
/*        ContentHandler handler = ContentManager.getHandler(asset.getFormat());
        handler.storeAssetContent(asset,  repoAsset);*/
    	//Domain object to binary 
    	byte[] assetContent;
    	
    	//TODO: vfsService needs a write(Path path, byte[] content) method.
    	//vfsService.write(asset.getPath(), assetContent);
    	
    	return "";//old Guvnor returns uuid
    }
    
    //TODO
    public static Path convertUUIDToPath(Asset asset) {
        String packageName = asset.getMetaData().getModuleName();
        String assetName = asset.getName();
        return new PathImpl(assetName, packageName + "/" + assetName);
    }
}
