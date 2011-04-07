/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.drools.guvnor.client.rpc.AdminArchivedPageRow;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
import org.drools.guvnor.server.builder.pagerow.ArchivedAssetPageRowBuilder;
import org.drools.guvnor.server.contenthandler.BPMN2ProcessHandler;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.contenthandler.IValidating;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.AssetFormatHelper;
import org.drools.guvnor.server.util.AssetLockManager;
import org.drools.guvnor.server.util.AssetPageRowPopulator;
import org.drools.guvnor.server.util.BuilderResultHelper;
import org.drools.guvnor.server.util.Discussion;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.MetaDataMapper;
import org.drools.guvnor.server.util.QueryPageRowCreator;
import org.drools.guvnor.server.util.ServiceRowSizeHelper;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.VersionableItem;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Handles operations for Assets
 */
@Name("org.drools.guvnor.server.RepositoryAssetOperations")
@AutoCreate
public class RepositoryAssetOperations {

    private RulesRepository            repository;

    private static final LoggingHelper log = LoggingHelper
                                                   .getLogger( RepositoryAssetOperations.class );

    public void setRulesRepository(RulesRepository repository) {
        this.repository = repository;
    }

    public RulesRepository getRulesRepository() {
        return repository;
    }

    public String renameAsset(String uuid,
                              String newName) {
        return getRulesRepository().renameAsset( uuid,
                                                 newName );
    }

    protected BuilderResult buildAsset(RuleAsset asset)
                                                       throws SerializationException {
        BuilderResult result = new BuilderResult();

        try {

            ContentHandler handler = ContentManager
                    .getHandler( asset.metaData.format );
            BuilderResultHelper builderResultHelper = new BuilderResultHelper();
            if ( asset.metaData.isBinary() ) {
                AssetItem item = getRulesRepository().loadAssetByUUID(
                                                                       asset.uuid );

                handler.storeAssetContent( asset,
                                           item );

                if ( handler instanceof IValidating ) {
                    return ((IValidating) handler).validateAsset( item );
                }

                ContentPackageAssembler asm = new ContentPackageAssembler( item );
                if ( !asm.hasErrors() ) {
                    return null;
                }
                result.setLines( builderResultHelper.generateBuilderResults( asm ) );

            } else {
                if ( handler instanceof IValidating ) {
                    return ((IValidating) handler).validateAsset( asset );
                }

                PackageItem packageItem = getRulesRepository()
                        .loadPackageByUUID( asset.metaData.packageUUID );

                ContentPackageAssembler asm = new ContentPackageAssembler(
                                                                           asset,
                                                                           packageItem );
                if ( !asm.hasErrors() ) {
                    return null;
                }
                result.setLines( builderResultHelper.generateBuilderResults( asm ) );
            }
        } catch ( Exception e ) {
            log.error( "Unable to build asset.",
                       e );
            result = new BuilderResult();

            BuilderResultLine res = new BuilderResultLine();
            res.setAssetName( asset.name );
            res.setAssetFormat( asset.metaData.format );
            res.setMessage( "Unable to validate this asset. (Check log for detailed messages)." );
            res.setUuid( asset.uuid );
            result.getLines().add( res );

            return result;

        }
        return result;
    }

    protected TableDataResult loadItemHistory(final VersionableItem item)
                                                                         throws SerializationException {
        Iterator<VersionableItem> it = item.getHistory();
        //AssetHistoryIterator it = assetItem.getHistory();

        // MN Note: this uses the lazy iterator, but then loads the whole lot
        // up, and returns it.
        // The reason for this is that the GUI needs to show things in numeric
        // order by the version number.
        // When a version is restored, its previous version is NOT what you
        // thought it was - due to how JCR works
        // (its more like CVS then SVN). So to get a linear progression of
        // versions, we use the incrementing version number,
        // and load it all up and sort it. This is not ideal.
        // In future, we may do a "restore" instead just by copying content into
        // a new version, not restoring a node,
        // in which case the iterator will be in order (or you can just walk all
        // the way back).
        // So if there are performance problems with looking at lots of
        // historical versions, look at this nasty bit of code.
        List<TableDataRow> result = new ArrayList<TableDataRow>();
        while ( it.hasNext() ) {
            VersionableItem historical = (VersionableItem) it.next();
            long versionNumber = historical.getVersionNumber();
            if ( isHistory( item,
                            versionNumber ) ) {
                result.add( createHistoricalRow( result,
                                                 historical ) );
            }
        }

        TableDataResult table = new TableDataResult();
        table.data = result.toArray( new TableDataRow[result.size()] );

        return table;
    }

    private boolean isHistory(VersionableItem item,
                              long versionNumber) {
        //return versionNumber != 0 && versionNumber != item.getVersionNumber();
        //we do return the LATEST version as part of the history. 
        return versionNumber != 0;
    }

    private TableDataRow createHistoricalRow(List<TableDataRow> result,
                                             VersionableItem historical) {
        final DateFormat dateFormatter = DateFormat.getInstance();
        TableDataRow tableDataRow = new TableDataRow();
        tableDataRow.id = historical.getVersionSnapshotUUID();
        tableDataRow.values = new String[4];
        tableDataRow.values[0] = Long.toString( historical.getVersionNumber() );
        tableDataRow.values[1] = historical.getCheckinComment();
        tableDataRow.values[2] = dateFormatter.format( historical
                .getLastModified().getTime() );
        tableDataRow.values[3] = historical.getStateDescription();
        return tableDataRow;
    }

    /**
     * @param skip
     * @param numRows
     * @return
     * @throws SerializationException
     * @deprecated in favour of {@link loadArchivedAssets(PageRequest)}
     */
    protected TableDataResult loadArchivedAssets(int skip,
                                                 int numRows)
                                                             throws SerializationException {
        List<TableDataRow> result = new ArrayList<TableDataRow>();
        RepositoryFilter filter = new AssetItemFilter();

        AssetItemIterator it = getRulesRepository().findArchivedAssets();
        it.skip( skip );
        int count = 0;
        while ( it.hasNext() ) {

            AssetItem archived = (AssetItem) it.next();

            if ( filter.accept( archived,
                                "read" ) ) {
                result.add( createArchivedRow( archived ) );
                count++;
            }
            if ( count == numRows ) {
                break;
            }
        }

        return createArchivedTable( result,
                                    it );
    }

    private TableDataRow createArchivedRow(AssetItem archived) {
        TableDataRow row = new TableDataRow();
        row.id = archived.getUUID();
        row.values = new String[5];
        row.values[0] = archived.getName();
        row.values[1] = archived.getFormat();
        row.values[2] = archived.getPackageName();
        row.values[3] = archived.getLastContributor();
        row.values[4] = Long.toString( archived.getLastModified().getTime()
                .getTime() );
        return row;
    }

    private TableDataResult createArchivedTable(List<TableDataRow> result,
                                                AssetItemIterator it) {
        TableDataResult table = new TableDataResult();
        table.data = result.toArray( new TableDataRow[result.size()] );
        table.currentPosition = it.getPosition();
        table.total = it.getSize();
        table.hasNext = it.hasNext();
        return table;
    }

    protected PageResponse<AdminArchivedPageRow> loadArchivedAssets(PageRequest request) throws SerializationException {
        // Do query
        long start = System.currentTimeMillis();
        AssetItemIterator iterator = getRulesRepository().findArchivedAssets();
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        long totalRowsCount = iterator.getSize();
        PageResponse<AdminArchivedPageRow> response = new PageResponse<AdminArchivedPageRow>();
        ArchivedAssetPageRowBuilder archivedAssetPageRowBuilder = new ArchivedAssetPageRowBuilder();
        List<AdminArchivedPageRow> rowList = archivedAssetPageRowBuilder.createRows( request,
                                                                                     iterator );
        boolean bHasMoreRows = iterator.hasNext();
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setPageRowList( rowList );
        response.setLastPage( !bHasMoreRows );
        //response.setTotalRowSize( (int)iterator.getSize() );
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( request,
                                              response,
                                              totalRowsCount,
                                              rowList.size(),
                                              bHasMoreRows );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Searched for Archived Assests in " + methodDuration + " ms." );
        return response;
    }

    /**
     * @param packageUuid
     * @param formats
     * @param skip
     * @param numRows
     * @param tableConfig
     * @return
     * @throws SerializationException
     * @deprecated in favour of {@link findAssetPage(AssetPageRequest)}
     */
    protected TableDataResult listAssets(String packageUuid,
                                         String formats[],
                                         int skip,
                                         int numRows,
                                         String tableConfig)
                                                            throws SerializationException {
        long start = System.currentTimeMillis();
        PackageItem pkg = getRulesRepository().loadPackageByUUID( packageUuid );
        AssetItemIterator it;
        if ( formats.length > 0 ) {
            it = pkg.listAssetsByFormat( formats );
        } else {
            it = pkg.listAssetsNotOfFormat( AssetFormatHelper
                    .listRegisteredTypes() );
        }
        TableDisplayHandler handler = new TableDisplayHandler( tableConfig );
        log.debug( "time for asset list load: "
                   + (System.currentTimeMillis() - start) );
        return handler.loadRuleListTable( it,
                                          skip,
                                          numRows );
    }

    /**
     * @param searchText
     * @param searchArchived
     * @param skip
     * @param numRows
     * @return
     * @throws SerializationException
     * @deprecated in favour of {@link quickFindAsset(QueryPageRequest)}
     */
    protected TableDataResult quickFindAsset(String searchText,
                                             boolean searchArchived,
                                             int skip,
                                             int numRows)
                                                         throws SerializationException {
        String search = searchText.replace( '*',
                                            '%' );

        if ( !search.endsWith( "%" ) ) {
            search += "%";
        }

        List<AssetItem> resultList = new ArrayList<AssetItem>();

        long start = System.currentTimeMillis();
        AssetItemIterator it = getRulesRepository().findAssetsByName( search,
                                                                      searchArchived );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        RepositoryFilter filter = new AssetItemFilter();

        while ( it.hasNext() ) {
            AssetItem ai = it.next();
            if ( filter.accept( ai,
                                RoleTypes.PACKAGE_READONLY ) ) {
                resultList.add( ai );
            }
        }

        TableDisplayHandler handler = new TableDisplayHandler( "searchresults" );
        return handler.loadRuleListTable( resultList,
                                          skip,
                                          numRows );
    }

    /**
     * @param text
     * @param seekArchived
     * @param skip
     * @param numRows
     * @return
     * @throws SerializationException
     * @deprecated in favour of {@link queryFullText(QueryPageRequest)}
     */
    protected TableDataResult queryFullText(String text,
                                            boolean seekArchived,
                                            int skip,
                                            int numRows) throws SerializationException {
        AssetItemIterator it = getRulesRepository().queryFullText( text,
                                                                   seekArchived );

        // Add filter for READONLY permission
        List<AssetItem> resultList = new ArrayList<AssetItem>();
        RepositoryFilter filter = new PackageFilter();

        while ( it.hasNext() ) {
            AssetItem ai = it.next();
            PackageConfigData data = new PackageConfigData();
            data.uuid = ai.getPackage().getUUID();
            if ( filter.accept( data,
                                RoleTypes.PACKAGE_READONLY ) ) {
                resultList.add( ai );
            }
        }

        TableDisplayHandler handler = new TableDisplayHandler( "searchresults" );
        return handler.loadRuleListTable( resultList,
                                          skip,
                                          numRows );
    }

    // TODO: Very hard to unit test -> needs refactoring
    protected String buildAssetSource(RuleAsset asset)
                                                      throws SerializationException {
        ContentHandler handler = ContentManager
                .getHandler( asset.metaData.format );

        StringBuilder stringBuilder = new StringBuilder();
        if ( handler.isRuleAsset() ) {
            BRMSPackageBuilder builder = new BRMSPackageBuilder();
            // now we load up the DSL files
            PackageItem packageItem = getRulesRepository().loadPackage(
                                                                        asset.metaData.packageName );
            builder.setDSLFiles( BRMSPackageBuilder.getDSLMappingFiles(
                                                                        packageItem,
                                                                        new BRMSPackageBuilder.DSLErrorEvent() {
                                                                            public void recordError(AssetItem asset,
                                                                                                    String message) {
                                                                                // ignore
                                                                                // at
                                                                                // this
                                                                                // point...
                                                                            }
                                                                        } ) );
            if ( asset.metaData.isBinary() ) {
                AssetItem item = getRulesRepository().loadAssetByUUID(
                                                                       asset.uuid );

                handler.storeAssetContent( asset,
                                           item );
                ((IRuleAsset) handler).assembleDRL( builder,
                                                    item,
                                                    stringBuilder );
            } else {
                ((IRuleAsset) handler).assembleDRL( builder,
                                                    asset,
                                                    stringBuilder );
            }
        } else {
            if ( handler
                    .getClass()
                    .getName()
                    .equals( "org.drools.guvnor.server.contenthandler.BPMN2ProcessHandler" ) ) {
                BPMN2ProcessHandler bpmn2handler = ((BPMN2ProcessHandler) handler);
                bpmn2handler.assembleProcessSource( asset.content,
                                                    stringBuilder );
            }
        }
        return stringBuilder.toString();
    }

    protected PageResponse<AssetPageRow> findAssetPage(AssetPageRequest request)
                                                                                throws SerializationException {
        log.debug( "Finding asset page of packageUuid ("
                   + request.getPackageUuid() + ")" );
        long start = System.currentTimeMillis();

        PackageItem packageItem = getRulesRepository().loadPackageByUUID(
                                                                          request.getPackageUuid() );

        AssetItemIterator it;
        if ( request.getFormatInList() != null ) {
            if ( request.getFormatIsRegistered() != null ) {
                throw new IllegalArgumentException(
                                                    "Combining formatInList and formatIsRegistered is not yet supported." );
            } else {
                it = packageItem.listAssetsByFormat( request.getFormatInList() );
            }
        } else {
            if ( request.getFormatIsRegistered() != null ) {
                it = packageItem.listAssetsNotOfFormat( AssetFormatHelper
                        .listRegisteredTypes() );
            } else {
                it = packageItem.queryAssets( "" );
            }
        }

        // Populate response
        long totalRowsCount = it.getSize();
        PageResponse<AssetPageRow> response = new PageResponse<AssetPageRow>();
        List<AssetPageRow> rowList = fillAssetPageRowsForFindAssetPage( request,
                                                                        it );
        boolean bHasMoreRows = it.hasNext();
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setPageRowList( rowList );
        response.setLastPage( !bHasMoreRows );

        // Fix Total Row Size
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( request,
                                              response,
                                              totalRowsCount,
                                              rowList.size(),
                                              bHasMoreRows );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Found asset page of packageUuid ("
                   + request.getPackageUuid() + ") in " + methodDuration + " ms." );
        return response;
    }

    protected PageResponse<QueryPageRow> quickFindAsset(QueryPageRequest request) throws SerializationException {
        // Setup parameters
        String search = request.getSearchText().replace( '*',
                                                         '%' );
        if ( !search.startsWith( "%" ) ) {
            search = "%" + search;
        }
        if ( !search.endsWith( "%" ) ) {
            search += "%";
        }

        // Do query
        long start = System.currentTimeMillis();
        AssetItemIterator it = getRulesRepository().findAssetsByName( search,
                                                                      request.isSearchArchived() );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        long totalRowsCount = it.getSize();
        PageResponse<QueryPageRow> response = new PageResponse<QueryPageRow>();
        List<QueryPageRow> rowList = fillQueryPageRows( request,
                                                        it );
        boolean bHasMoreRows = it.hasNext();
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setPageRowList( rowList );
        response.setLastPage( !bHasMoreRows );

        // Fix Total Row Size
        ServiceRowSizeHelper serviceRowSizeHelper = new ServiceRowSizeHelper();
        serviceRowSizeHelper.fixTotalRowSize( request,
                                              response,
                                              totalRowsCount,
                                              rowList.size(),
                                              bHasMoreRows );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Queried repository (Quick Find) for (" + search + ") in " + methodDuration + " ms." );
        return response;
    }

    private List<QueryPageRow> fillQueryPageRows(QueryPageRequest request,
                                                 AssetItemIterator it) {
        int skipped = 0;
        Integer pageSize = request.getPageSize();
        int startRowIndex = request.getStartRowIndex();
        RepositoryFilter filter = new AssetItemFilter();
        List<QueryPageRow> rowList = new ArrayList<QueryPageRow>();

        while ( it.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            AssetItem assetItem = (AssetItem) it.next();

            // Filter surplus assets
            if ( filter.accept( assetItem,
                                RoleTypes.PACKAGE_READONLY ) ) {

                // Cannot use AssetItemIterator.skip() as it skips non-filtered
                // assets whereas startRowIndex is the index of the
                // first displayed asset (i.e. filtered)
                if ( skipped >= startRowIndex ) {
                    rowList.add( QueryPageRowCreator.makeQueryPageRow( assetItem ) );
                }
                skipped++;
            }
        }
        return rowList;
    }

    private List<AssetPageRow> fillAssetPageRowsForFindAssetPage(
                                                                 AssetPageRequest request,
                                                                 AssetItemIterator it) {
        Integer pageSize = request.getPageSize();
        it.skip( request.getStartRowIndex() );
        List<AssetPageRow> rowList = new ArrayList<AssetPageRow>();

        while ( it.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            AssetItem assetItem = (AssetItem) it.next();
            AssetPageRowPopulator assetPageRowPopulator = new AssetPageRowPopulator();
            rowList.add( assetPageRowPopulator.makeAssetPageRow( assetItem ) );
        }
        return rowList;
    }

    protected void lockAsset(String uuid) {
        AssetLockManager alm = AssetLockManager.instance();

        String userName;
        if ( Contexts.isApplicationContextActive() ) {
            userName = Identity.instance().getCredentials().getUsername();
        } else {
            userName = "anonymous";
        }

        log.info( "Locking asset uuid=" + uuid + " for user [" + userName + "]" );

        alm.lockAsset( uuid,
                       userName );
    }

    protected void unLockAsset(String uuid) {
        AssetLockManager alm = AssetLockManager.instance();
        log.info( "Unlocking asset [" + uuid + "]" );
        alm.unLockAsset( uuid );
    }

    protected String getAssetLockerUserName(String uuid) {
        AssetLockManager alm = AssetLockManager.instance();

        String userName = alm.getAssetLockerUserName( uuid );

        log.info( "Asset locked by [" + userName + "]" );

        return userName;
    }

    protected RuleAsset loadAsset(AssetItem item) throws SerializationException {

        RuleAsset asset = new RuleAsset();
        asset.uuid = item.getUUID();
        asset.name = item.getName();
        asset.description = item.getDescription();
        asset.lastModified = item.getLastModified().getTime();
        asset.lastContributor = item.getLastContributor();
        asset.state = (item.getState() != null) ? item.getState().getName() : "";
        asset.dateCreated = item.getCreatedDate().getTime();
        asset.checkinComment = item.getCheckinComment();
        asset.versionNumber = item.getVersionNumber();

        asset.metaData = populateMetaData( item );
        ContentHandler handler = ContentManager.getHandler( asset.metaData.format );
        handler.retrieveAssetContent( asset,
                                      item.getPackage(),
                                      item );

        return asset;
    }

    /**
     * Populate meta data with asset specific info.
     */
    MetaData populateMetaData(AssetItem item) {
        MetaData meta = populateMetaData( (VersionableItem) item );

        meta.packageName = item.getPackageName();
        meta.packageUUID = item.getPackage().getUUID();
        meta.setBinary( item.isBinary() );

        List<CategoryItem> categories = item.getCategories();
        fillMetaCategories( meta,
                            categories );
        meta.dateEffective = calendarToDate( item.getDateEffective() );
        meta.dateExpired = calendarToDate( item.getDateExpired() );
        return meta;

    }

    /**
     * read in the meta data, populating all dublin core and versioning stuff.
     */
    MetaData populateMetaData(VersionableItem item) {
        MetaData meta = new MetaData();

        MetaDataMapper metaDataMapper = MetaDataMapper.getInstance();
        metaDataMapper.copyToMetaData( meta,
                                       item );

        //problematic implementation of getPrecedingVersion and getPrecedingVersion().
        //commented out temporarily as this is used by the front end. 
        //meta.hasPreceedingVersion = item.getPrecedingVersion() != null;
        //meta.hasSucceedingVersion = item.getPrecedingVersion() != null;
        return meta;
    }

    private void fillMetaCategories(MetaData meta,
                                    List<CategoryItem> categories) {
        meta.categories = new String[categories.size()];
        for ( int i = 0; i < meta.categories.length; i++ ) {
            CategoryItem cat = (CategoryItem) categories.get( i );
            meta.categories[i] = cat.getFullPath();
        }
    }

    private Date calendarToDate(Calendar createdDate) {
        if ( createdDate == null ) {
            return null;
        }
        return createdDate.getTime();
    }

    protected void clearAllDiscussionsForAsset(final String assetId) {
        RulesRepository repo = getRulesRepository();
        AssetItem asset = repo.loadAssetByUUID( assetId );
        asset.updateStringProperty( "",
                                    "discussion" );
        repo.save();

        push( "discussion",
              assetId );
    }

    protected List<DiscussionRecord> addToDiscussionForAsset(String assetId,
                                                             String comment) {
        RulesRepository repo = getRulesRepository();
        AssetItem asset = repo.loadAssetByUUID( assetId );
        Discussion dp = new Discussion();
        List<DiscussionRecord> discussion = dp.fromString( asset.getStringProperty( Discussion.DISCUSSION_PROPERTY_KEY ) );
        discussion.add( new DiscussionRecord( repo.getSession().getUserID(),
                                              StringEscapeUtils.escapeXml( comment ) ) );
        asset.updateStringProperty( dp.toString( discussion ),
                                    Discussion.DISCUSSION_PROPERTY_KEY,
                                    false );
        repo.save();

        push( "discussion",
              assetId );

        MailboxService.getInstance().recordItemUpdated( asset );

        return discussion;
    }

    private void push(String messageType,
                      String message) {
        Backchannel.getInstance().publish( new PushResponse( messageType,
                                                             message ) );
    }

}
