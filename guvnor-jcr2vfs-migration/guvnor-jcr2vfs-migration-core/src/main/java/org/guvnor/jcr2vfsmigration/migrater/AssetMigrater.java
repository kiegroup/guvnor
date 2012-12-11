package org.guvnor.jcr2vfsmigration.migrater;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.guvnor.factmodel.model.FactModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.guvnor.factmodel.service.FactModelService;
import org.uberfire.backend.vfs.PathFactory;

// TODO Maybe we should make one per asset type? Or delegate to one per asset type?
@ApplicationScoped
public class AssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(AssetMigrater.class);

//    @Inject
//    protected RepositoryModuleService jcrRepositoryModuleService;

//    @Inject
//    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected FactModelService vfsFactModelService;

    public void migrateAll() {
        logger.info("  Asset migration started");
        // TODO
        // vfsFactModelService.save(PathFactory.newPath("default://guvnor-jcr2vfs-migration/foo/bar"), new FactModels()); // TODO remove me
        logger.info("  Asset migration ended");
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
