package org.kie.guvnor.jcr2vfsmigration.migrater;

import java.util.Arrays;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.kie.guvnor.jcr2vfsmigration.migrater.asset.FactModelsMigrater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.guvnor.factmodel.service.FactModelService;

@ApplicationScoped
public class AssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(AssetMigrater.class);

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected FactModelsMigrater factModelsMigrater;

    public void migrateAll() {
        logger.info("  Asset migration started");
        Module[] modules = jcrRepositoryModuleService.listModules();
        for (Module module : modules) {
            boolean hasMorePages = true;
            int startRowIndex = 0;
            final int pageSize = 100;
            while (hasMorePages) {
                AssetPageRequest request = new AssetPageRequest(module.getUuid(),
                        null, // get all formats
                        null,
                        startRowIndex,
                        pageSize);
                PageResponse<AssetPageRow> response;
                try {
                    response = jcrRepositoryAssetService.findAssetPage(request);
                    for (AssetPageRow row : response.getPageRowList()) {
                        Asset jcrAsset = jcrRepositoryAssetService.loadRuleAsset(row.getUuid());
                        migrate(jcrAsset);
                        logger.debug("    Asset ({}) migrated.", jcrAsset.getName());
                    }
                } catch (SerializationException e) {
                    throw new IllegalStateException(e);
                }
                if (response.isLastPage()) {
                    hasMorePages = false;
                } else {
                    startRowIndex += pageSize;
                }
            }
        }
        logger.info("  Asset migration ended");
    }

    private void migrate(Asset jcrAsset) {
        if (AssetFormats.DRL_MODEL.equals(jcrAsset.getFormat())) {
            factModelsMigrater.migrate(jcrAsset);
        } else {
            // TODO REPLACE ME WITH ACTUAL CODE
            logger.debug("      TODO migrate asset ({}) with format({}).", jcrAsset.getName(), jcrAsset.getFormat());
            // TODO When all assetFormats types have been tried, the last else should throw an IllegalArgumentException
        }
    }

    // TODO delete code below once we have all of its functionality
//
//    @Inject
//    private RulesRepositoryVFS rulesRepositoryVFS;
//
//    public void migrateAssets() {
//        Module[] modules = jcrRepositoryModuleService.listModules();
//
//        for(Module module : modules) {
//            //TODO: Migrate package. Should already be done by ModuleMigrator
//
//            //TODO: Migrate Guvnor package based permissions: admin/package.admin/package.developer/package.readonly
//            //(and dont forget to migrate category based permission, ie, analyst/analyst.readonly)
//
//            AssetPageRequest request = new AssetPageRequest( module.getUuid(),
//                                                             null, //get assets with all formats when format=null
//                                                             null,
//                                                             0,
//                                                             0);// return all assets in 1 page if pageSize=0
//            try {
//                PageResponse<AssetPageRow> response = jcrRepositoryAssetService.findAssetPage(request);
//                for (AssetPageRow row : response.getPageRowList()) {
//
//                    //Migrate asset history first
//                    migrateAssetHistory(row.getUuid());
//
//
//                    Asset assetJCR = jcrRepositoryAssetService.loadRuleAsset(row.getUuid());
//                    Asset assetVFS = assetJCR;
//                    //migrate asset binary content. The binary content of assets from previous Guvnor version can not be reused by VFS directly.
//                    //Make sure we deserialize/serialize the binary content of assets
//                    PortableObject binaryContent = assetJCR.getContent();
//                    assetVFS.setContent(binaryContent);
//
//                    //migrate discussions:
//                    migrateAssetDiscussions(assetJCR, assetVFS, row.getUuid());
//
//                    //migrate state:
//                    migrateAssetState(assetJCR, assetVFS, row.getUuid());
//
//                    rulesRepositoryVFS.checkinVersion(assetJCR);
//                }
//            } catch (SerializationException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void migrateAssetDiscussions(Asset assetJCR, Asset assetVFS, String assetUUID) {
//        List<DiscussionRecord> discussions = jcrRepositoryAssetService.loadDiscussionForAsset(assetUUID);
//        rulesRepositoryVFS.addToDiscussionForAsset(assetVFS, discussions);
//    }
//
//    public void migrateAssetState(Asset assetJCR, Asset assetVFS, String assetUUID) {
//        String stateName = assetJCR.getState();
//        //If we need to convert old state to new state:
//        //assetVFS.setState(stateName);
//    }
//
//    public void migrateAssetHistory(String assetUUID) throws SerializationException {
//        //loadItemHistory wont return the current version
//        TableDataResult history = jcrRepositoryAssetService.loadItemHistory(assetUUID);
//        TableDataRow[] rows = history.data;
//        Arrays.sort( rows,
//                new Comparator<TableDataRow>() {
//                    public int compare( TableDataRow r1,
//                                        TableDataRow r2 ) {
//                        Integer v2 = Integer.valueOf( r2.values[0] );
//                        Integer v1 = Integer.valueOf( r1.values[0] );
//
//                        return v2.compareTo( v1 );
//                    }
//                } );
//
//        for (TableDataRow row : rows) {
//            String versionNumber = row.values[0];
//            String checkinComment = row.values[1];
//            String lastModified = row.values[2];
//            String stateDescription = row.values[3];
//            String lastContributor = row.values[4];
//            String versionSnapshotUUID = row.id;
//
//            Asset historicalAssetJCR = jcrRepositoryAssetService.loadRuleAsset(versionSnapshotUUID);
//            Asset historicalAssetVFS = historicalAssetJCR;
//
//            //TODO: migrate asset binary content. The binary content of assets from previous Guvnor version can not be reused by VFS directly.
//            //Make sure we deserialize/serialize the binary content of assets
//            PortableObject binaryContent = historicalAssetJCR.getContent();
//            historicalAssetVFS.setContent(binaryContent);
//
//            //migrate state:
//            migrateAssetState(historicalAssetJCR, historicalAssetVFS, versionSnapshotUUID);
//
//            rulesRepositoryVFS.checkinVersion(historicalAssetVFS);
//        }
//
//    }

}
