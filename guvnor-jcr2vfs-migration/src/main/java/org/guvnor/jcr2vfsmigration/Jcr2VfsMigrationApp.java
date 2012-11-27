/*
 * Copyright 2012 JBoss Inc
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.AssetService;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.shared.api.PortableObject;
import org.guvnor.jcr2vfsmigration.config.MigrationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

public class Jcr2VfsMigrationApp {

    protected static final Logger logger = LoggerFactory.getLogger(Jcr2VfsMigrationApp.class);

    @Inject
    private ModuleService moduleServiceJCR;
    @Inject
    private AssetService assetServiceJCR;
    
    @Inject
    private AssetService assetServiceVFS;
    
    //TO-DO-LIST:
    //1. How to migrate the globalArea (moduleServiceJCR.listModules() wont return globalArea)
    //2. How to handle asset imported from globalArea. assetServiceJCR.findAssetPage will return assets imported from globalArea 
    //(like a symbol link). Use Asset.getMetaData().getModuleName()=="globalArea" to determine if the asset is actually from globalArea.
    //3. Do we want to migrate archived assets and archived packages? probably not...
    //4. Do we want to migrate package snapshot? probably not...As long as we migrate package history correctly, users can always build a package
    //with the specified version by themselves.    
    public void migrate() {
    	migrateStateMetaData();
    	migrateCategoryMetaData();
    	migrateRolesAndPermissionsMetaData();
    	migrateAssets();
    }
    

    public void migrateAssets() {
    	Module[] modules = moduleServiceJCR.listModules();
    	
    	for(Module module : modules) {
    		//TODO: Migrate package. How?
    		
    		//TODO: Migrate Guvnor package based permissions: admin/package.admin/package.developer/package.readonly
    		//(and dont forget to migrate category based permission, ie, analyst/analyst.readonly)
    		
    		
            Path path = new PathImpl();
            path.setUUID(module.getUuid());
            AssetPageRequest request = new AssetPageRequest( path,
                                                             null, //get assets with all formats when format=null
                                                             null,
                                                             0,
                                                             0);// return all assets in 1 page if pageSize=0
			try {
				PageResponse<AssetPageRow> response = assetServiceJCR.findAssetPage(request);
                for (AssetPageRow row : response.getPageRowList()) {
                    Path assetPath = row.getPath();
                    
                    //Migrate asset history first
                	migrateAssetHistory(assetPath);
                	

                    Asset assetJCR = assetServiceJCR.loadRuleAsset(assetPath);                    
                    Asset assetVFS = assetJCR;
                    
                    //migrate asset binary content. The binary content of assets from previous Guvnor version can not be reused by VFS directly. 
                    //Make sure we deserialize/serialize the binary content of assets 
                    PortableObject binaryContent = assetJCR.getContent();
                    assetVFS.setContent(binaryContent);
                    
                    //migrate discussions:
                    migrateAssetDiscussions(assetJCR, assetVFS, assetPath);
                    
                    //migrate state:
                    migrateAssetState(assetJCR, assetVFS, assetPath);
                    
                    //Write asset back to git repository using AssetServiceVFS
                    //TODO: We need a assetServiceVFS.checkinVersion method that takes following (extra) parameters: contributor, modified, versionNumber(?). 
                    //The checkinComment is taken from Asset
                    assetVFS.setCheckinComment(assetJCR.getCheckinComment());     
                    assetServiceVFS.checkinVersion(assetJCR);
                }	
			} catch (SerializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    //NOTE: Adding or removing asset discussions won't change asset history (in old Guvnor)
    public void migrateAssetDiscussions(Asset assetJCR, Asset assetVFS, Path assetPath) {
        List<DiscussionRecord> discussions = assetServiceJCR.loadDiscussionForAsset(assetPath);
        for(DiscussionRecord discussion : discussions) {
            //TODO: we need a addToDiscussionForAsset method that takes author as parameter so that we can write the author information back
            assetServiceVFS.addToDiscussionForAsset(assetPath, discussion.note);      
        }        
    }
    
    public void migrateAssetState(Asset assetJCR, Asset assetVFS, Path assetPath) {
    	String stateName = assetJCR.getState();
    	//If we need to convert old state to new state:
    	assetVFS.setState(stateName);
    }    
    
    public void migrateAssetHistory(Path assetPath) throws SerializationException {
    	//loadItemHistory wont return the current version
	    TableDataResult history = assetServiceJCR.loadItemHistory(assetPath);
	    TableDataRow[] rows = history.data;
        Arrays.sort( rows,
                new Comparator<TableDataRow>() {
                    public int compare( TableDataRow r1,
                                        TableDataRow r2 ) {
                        Integer v2 = Integer.valueOf( r2.values[0] );
                        Integer v1 = Integer.valueOf( r1.values[0] );

                        return v2.compareTo( v1 );
                    }
                } );

        for (TableDataRow row : rows) {
        	String versionNumber = row.values[0];
        	String checkinComment = row.values[1];
            String lastModified = row.values[2];
            String stateDescription = row.values[3];
            String lastContributor = row.values[4];
            String versionSnapshotUUID = row.id;
            
            Path historicalAssetPath = new PathImpl();
            historicalAssetPath.setUUID(versionSnapshotUUID);
            
            Asset historicalAssetJCR = assetServiceJCR.loadRuleAsset(historicalAssetPath);            
            Asset historicalSssetVFS = historicalAssetJCR;
            
            //migrate asset binary content. The binary content of assets from previous Guvnor version can not be reused by VFS directly. 
            //Make sure we deserialize/serialize the binary content of assets 
            PortableObject binaryContent = historicalAssetJCR.getContent();
            historicalSssetVFS.setContent(binaryContent);
            
            //migrate state:
            migrateAssetState(historicalAssetJCR, historicalSssetVFS, historicalAssetPath);
            
            //Write asset back to git repository using AssetServiceVFS
            //TODO: We need a assetServiceVFS.checkinVersion method that takes following (extra) parameters: contributor, modified, versionNumber(?). 
            //The checkinComment is taken from Asset
            historicalSssetVFS.setCheckinComment(checkinComment);            
            assetServiceVFS.checkinVersion(historicalSssetVFS);
        }

    }   
    
    public void migrateAssetCategories() {
    	
    }
       
    public void migrateStateMetaData() {
    	
    }
    
    public void migrateCategoryMetaData() {
    	
    }
    
    public void migratePackagePermissions() {
    	
    }
    
    public void migrateRolesAndPermissionsMetaData() {
    	
    }
    public static void main(String[] args) {
        logger.info("Migration started");
        MigrationConfig migrationConfig = new MigrationConfig();
        migrationConfig.parseArgs(args);
        // TODO
        logger.info("Migration ended");
    }

}
