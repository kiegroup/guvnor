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

package org.drools.guvnor.server;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;
import org.drools.guvnor.shared.api.Valid;
import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.VFSTempUtil;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.user.client.rpc.SerializationException;

import java.util.*;

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
    
    public List<Module> listModules() {
        List<Module> result = new ArrayList<Module>();
        
    	DirectoryStream<org.uberfire.backend.vfs.Path> response = vfsService.newDirectoryStream(root.getPath());
        for (final org.uberfire.backend.vfs.Path child : response) {
           	System.out.println("----------child.getFileName()------" +child);

           	Map attributes = vfsService.readAttributes(child);
           	final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes(attributes);
            //TODO: how do we determine which folder is the package/module folder. by the .metadata file?
            if (attrs.isDirectory() && !".metadata".equals(child.getFileName())) {
                Module data = new Module();
                Path path = ufPathToGuvnorPath(child);
                data.setPath(path);
                //data.setUuid( packageItem.getUUID() );
                data.setName( child.getFileName() );
                data.setFormat("package");
                data.setArchived( false );
                
                //TODO: archived is deprecated?
                /*            handleIsModuleListed( archive,
                                    workspace,
                                    result,
                                    data );*/

                //TODO: no more sub modules?
                /*            data.subModules = listSubModules( packageItem,
                                    archive,
                                    null );*/
                
                result.add(data);
            } 
        }

        return result;
    }
    
    private Path ufPathToGuvnorPath(org.uberfire.backend.vfs.Path ufPath) {
    	if(ufPath instanceof org.uberfire.backend.vfs.impl.PathImpl) {
    		org.uberfire.backend.vfs.impl.PathImpl ufPathImp = (org.uberfire.backend.vfs.impl.PathImpl)ufPath;
            Path path = new PathImpl(ufPathImp.getFileName(), ufPathImp.toURI(), ufPathImp.getAttributes());
            return path;
    	}

        return null;
    }
    
    public Module loadModule(Path modulePath) {
       	Map attributes = vfsService.readAttributes(modulePath);
       	final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes(attributes);

       	
        Module data = new Module();
        data.setPath(modulePath);
        data.setName(modulePath.getFileName());
        
        //set with mock data to avoid NPE on the client 
        //data.setExternalURI(item.getExternalURI());
        //data.setCatRules(item.getCategoryRules());
        data.setDescription("mock descritpion");
        data.setArchived(false);

        data.setLastModified(new Date());
        data.setDateCreated(new Date());
        data.setCheckinComment("mock checkin comment");
        data.setLastContributor("mock LastContributor");
        data.setState("mock state");
        data.setSnapshot(false);
        data.setVersionNumber(1);
        data.setFormat("package");
        
        //data.setUuid(item.getUUID());
        //TODO: read following attributes:
/*        data.setHeader(DroolsHeader.getDroolsHeader(item));
        data.setExternalURI(item.getExternalURI());
        data.setCatRules(item.getCategoryRules());
        data.setDescription(item.getDescription());
        data.setArchived(item.isArchived());

        data.setLastModified(item.getLastModified().getTime());
        data.setDateCreated(item.getCreatedDate().getTime());
        data.setCheckinComment(item.getCheckinComment());
        data.setLastContributor(item.getLastContributor());
        data.setState(item.getStateDescription());
        data.setSnapshot(item.isSnapshot());
        data.setVersionNumber(item.getVersionNumber());
        data.setFormat(item.getFormat());*/

        return data;
    }
    
	public Path copyModule(Path sourceModulePath, String destModuleName) {
		//TODO:
		return null;
	}
	
    public void removeModule(Path modulePath) {
        //TODO:
    }
    
    public Path renameModule(Path modulePath, String newName) {
    	//TODO:
    	return null;
    }
    
    public Path createModule(String name, String description, String format) {
    	//TODO:
    	return null;    	
    }
    
    public void saveModule(Module data) throws SerializationException {
    	//TODO:    
    }
    
    //Return assets with the specified format under the specified module
    public List<Asset> listAssetsByFormat(Module module, List<String> formats, int startIndex, Integer pageSize) {
    	List<Asset> assets = new ArrayList<Asset>();
    	Asset asset = new Asset();
    	asset.setName("mock asset");
    	MetaData meta = new MetaData();
    	meta.setDisabled(false);
    	meta.setExternalSource("mocked external source");
    	meta.setValid(Valid.VALID);
    	asset.setMetaData(meta);
    	
    	assets.add(asset);
    	return assets;
    }
    
    public int listAssetsByFormatCount(Module module, List<String> formats) {
    	return 1;
    }    
    
    public List<Asset> listAssetsNotOfFormat(Module module, List<String> formats, int startIndex, Integer pageSize) {
    	List<Asset> assets = new ArrayList<Asset>();
    	Asset asset = new Asset();
    	asset.setName("mock asset");
    	MetaData meta = new MetaData();
    	meta.setDisabled(false);
    	meta.setExternalSource("mocked external source");
    	meta.setValid(Valid.VALID);
    	asset.setMetaData(meta);
    	
    	assets.add(asset);
    	return assets;
    }
    
    public int listAssetsNotOfFormatCount(Module module, List<String> formats) {
    	return 1;
    }      
    
    public Asset loadRuleAsset(Path assetPath) {
    	//Mock data
        Asset ruleAsset = new Asset();
        Path path = new PathImpl();
        ruleAsset.setPath(path);
        //ruleAsset.setUuid( assetItem.getUUID() );
        ruleAsset.setName( "mock assetName" );
        ruleAsset.setDescription( "mock Description" );
        ruleAsset.setLastModified( new Date() );
        ruleAsset.setLastContributor( "mock Contributor" );
        ruleAsset.setState( "mock state");
        ruleAsset.setDateCreated( new Date() );
        ruleAsset.setCheckinComment( "mock checkin comment" );
        ruleAsset.setVersionNumber( 2 );
        ruleAsset.setFormat("drl");
        ruleAsset.setArchived(false);
        
        //Mock metadata:
        MetaData meta = new MetaData();
        //TODO: Populuate version metadata. check  RepositoryAssetOperations.populateMetaData(VersionableItem item)
        //populateMetaData((VersionableItem) item);

        meta.setModuleName("mocked module name");
        //TODO: need module Path?
        //meta.setModuleUUID(item.getModule().getUUID());
        meta.setBinary(false);

        //TODO: category?
/*        List<CategoryItem> categories = item.getCategories();
        fillMetaCategories(meta,
                categories);*/
        
        meta.setDateEffective(new Date());
        meta.setDateExpired(new Date());
        ruleAsset.setMetaData(meta);

        return ruleAsset;
    }
}
