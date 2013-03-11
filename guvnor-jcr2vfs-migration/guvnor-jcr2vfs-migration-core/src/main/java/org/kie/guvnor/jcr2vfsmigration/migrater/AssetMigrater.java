package org.kie.guvnor.jcr2vfsmigration.migrater;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.kie.guvnor.jcr2vfsmigration.migrater.asset.AttachementAssetMigrater;
import org.kie.guvnor.jcr2vfsmigration.migrater.asset.FactModelsMigrater;
import org.kie.guvnor.jcr2vfsmigration.migrater.asset.GuidedDecisionTableMigrater;
import org.kie.guvnor.jcr2vfsmigration.migrater.asset.GuidedEditorMigrater;
import org.kie.guvnor.jcr2vfsmigration.migrater.asset.PlainTextAssetMigrater;
import org.kie.guvnor.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;


@ApplicationScoped
public class AssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(AssetMigrater.class);

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected FactModelsMigrater factModelsMigrater;    
    @Inject
    protected GuidedEditorMigrater guidedEditorMigrater;
    @Inject
    protected PlainTextAssetMigrater plainTextAssetMigrater;        
    @Inject
    protected GuidedDecisionTableMigrater guidedDecisionTableMigrater;
    @Inject
    protected AttachementAssetMigrater attachementAssetMigrater;
    
    @Inject
    protected MetadataService metadataService;        
    @Inject
    protected MigrationPathManager migrationPathManager;
    @Inject
    private Paths paths;
    
    public void migrateAll() {
        logger.info("  Asset migration started");
        Module[] jcrModules = jcrRepositoryModuleService.listModules();
        for (Module jcrModule : jcrModules) {
            boolean hasMorePages = true;
            int startRowIndex = 0;
            final int pageSize = 100;
            while (hasMorePages) {
                AssetPageRequest request = new AssetPageRequest(jcrModule.getUuid(),
                        null, // get all formats
                        null,
                        startRowIndex,
                        pageSize);
                PageResponse<AssetPageRow> response;
                try {
                    response = jcrRepositoryAssetService.findAssetPage(request);
                    for (AssetPageRow row : response.getPageRowList()) {              
                        //TODO: weird, the VFS is always missing the third commit. 
                        //Migrate historical versions first, this includes the head version(i.e., the latest version)
                        migrateAssetHistory(jcrModule, row.getUuid());
                        
                        //Migrate asset discussions
                        migrateAssetDiscussions(jcrModule, row.getUuid());
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

    private void migrate(Module jcrModule, Asset jcrAsset, String checkinComment, Date lastModified, String lastContributor) {
        if (AssetFormats.DRL_MODEL.equals(jcrAsset.getFormat())) {
            factModelsMigrater.migrate(jcrModule, jcrAsset, checkinComment, lastModified, lastContributor );
        } else if (AssetFormats.BUSINESS_RULE.equals(jcrAsset.getFormat())) {
            guidedEditorMigrater.migrate(jcrModule, jcrAsset, checkinComment, lastModified, lastContributor );
        } else if (AssetFormats.DECISION_TABLE_GUIDED.equals(jcrAsset.getFormat())) {
            guidedDecisionTableMigrater.migrate(jcrModule, jcrAsset, checkinComment, lastModified, lastContributor );
        } else if (AssetFormats.DRL.equals(jcrAsset.getFormat()) 
                || AssetFormats.ENUMERATION.equals(jcrAsset.getFormat())
                || AssetFormats.DSL.equals(jcrAsset.getFormat())
                || AssetFormats.DSL_TEMPLATE_RULE.equals(jcrAsset.getFormat())
                || AssetFormats.FORM_DEFINITION.equals(jcrAsset.getFormat())
                || AssetFormats.FUNCTION.equals(jcrAsset.getFormat())
                || AssetFormats.SPRING_CONTEXT.equals(jcrAsset.getFormat())
                /*|| AssetFormats.SERVICE_CONFIG.equals(jcrAsset.getFormat())*/
                || AssetFormats.WORKITEM_DEFINITION.equals(jcrAsset.getFormat())
                || AssetFormats.CHANGE_SET.equals(jcrAsset.getFormat())) {
            plainTextAssetMigrater.migrate(jcrModule, jcrAsset, checkinComment, lastModified, lastContributor);
        } else if (AssetFormats.DECISION_SPREADSHEET_XLS.equals(jcrAsset.getFormat())
                 ||AssetFormats.SCORECARD_SPREADSHEET_XLS.equals(jcrAsset.getFormat())) {
            attachementAssetMigrater.migrate(jcrModule, jcrAsset, checkinComment, lastModified, lastContributor);
        } else if (AssetFormats.RULE_FLOW_RF.equals(jcrAsset.getFormat())
                || AssetFormats.BPMN_PROCESS.equals(jcrAsset.getFormat())
                || AssetFormats.BPMN2_PROCESS.equals(jcrAsset.getFormat())) {
            logger.debug("      TODO migrate asset ({}) with format({}).", jcrAsset.getName(), jcrAsset.getFormat());
        } else if (AssetFormats.MODEL.equals(jcrAsset.getFormat())) {
            // TODO return error message
            logger.info("      POJO Model jar [" + jcrAsset.getName() + "] is not supported by migration tool. Please upload your POJO model jar to Guvnor manually.");
        } else if (AssetFormats.SCORECARD_GUIDED.equals(jcrAsset.getFormat())) {
            // TODO
            logger.debug("      TODO migrate asset ({}) with format({}).", jcrAsset.getName(), jcrAsset.getFormat());
        } else {
            // TODO REPLACE ME WITH ACTUAL CODE
            logger.debug("      TODO migrate asset ({}) with format({}).", jcrAsset.getName(), jcrAsset.getFormat());
        }
        // TODO When all assetFormats types have been tried, the last else should throw an IllegalArgumentException
    }
    
    public void migrateAssetHistory(Module jcrModule, String assetUUID) throws SerializationException {
        //loadItemHistory wont return the current version
        TableDataResult history = jcrRepositoryAssetService.loadItemHistory(assetUUID);
        TableDataRow[] rows = history.data;
        Arrays.sort( rows,
                new Comparator<TableDataRow>() {
                    public int compare( TableDataRow r1,
                                        TableDataRow r2 ) {
                        Integer v2 = Integer.valueOf( r2.values[0] );
                        Integer v1 = Integer.valueOf( r1.values[0] );

                        return v1.compareTo( v2 );
                    }
                } );

        for (TableDataRow row : rows) {
            String versionSnapshotUUID = row.id;

            Asset historicalAssetJCR = jcrRepositoryAssetService.loadRuleAsset(versionSnapshotUUID);
            migrate(jcrModule, historicalAssetJCR, historicalAssetJCR.getCheckinComment(), historicalAssetJCR.getLastModified(), historicalAssetJCR.getLastContributor());
            logger.debug("    Asset ({}) with format ({}) migrated: version [{}], comment[{}], lastModified[{}]",
                    historicalAssetJCR.getName(), historicalAssetJCR.getFormat(), historicalAssetJCR.getVersionNumber(), historicalAssetJCR.getCheckinComment(), historicalAssetJCR.getLastModified());
        }
    }

    public void migrateAssetDiscussions(Module jcrModule, String assetUUID)  throws SerializationException {
        Asset assetJCR = jcrRepositoryAssetService.loadRuleAsset(assetUUID);
        List<DiscussionRecord> discussions = jcrRepositoryAssetService.loadDiscussionForAsset(assetUUID);
        
        if(discussions.size() == 0) {
            return;
        }
        
         //final org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );
        Metadata metadata = new Metadata();
        for(DiscussionRecord discussion: discussions) {
            metadata.addDiscussion( new org.kie.guvnor.services.metadata.model.DiscussionRecord( discussion.timestamp, discussion.author, discussion.note ) );
        }

        Path path = migrationPathManager.generatePathForAsset(jcrModule, assetJCR);
        metadataService.setUpAttributes(path, metadata);
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

//
//    public void migrateAssetState(Asset assetJCR, Asset assetVFS, String assetUUID) {
//        String stateName = assetJCR.getState();
//        //If we need to convert old state to new state:
//        //assetVFS.setState(stateName);
//    }
//
 
}
