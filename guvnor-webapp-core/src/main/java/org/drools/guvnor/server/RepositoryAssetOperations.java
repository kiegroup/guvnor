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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang.StringEscapeUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AdminArchivedPageRow;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.server.builder.AssetItemValidator;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.DSLLoader;
import org.drools.guvnor.server.builder.PageResponseBuilder;
import org.drools.guvnor.server.builder.pagerow.ArchivedAssetPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.AssetPageRowBuilder;
import org.drools.guvnor.server.builder.pagerow.QuickFindPageRowBuilder;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.ICanRenderSource;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.AssetEditorConfiguration;
import org.drools.guvnor.server.util.AssetEditorConfigurationParser;
import org.drools.guvnor.server.util.AssetFormatHelper;
import org.drools.guvnor.server.util.AssetLockManager;
import org.drools.guvnor.server.util.Discussion;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.MetaDataMapper;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.CategoryItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.VersionableItem;
import org.drools.repository.utils.AssetValidator;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Handles operations for Assets
 */
@ApplicationScoped
public class RepositoryAssetOperations {

    private static final LoggingHelper log = LoggingHelper.getLogger(RepositoryAssetOperations.class);

    @Inject @Preferred
    private RulesRepository rulesRepository;

    @Inject
    private Backchannel backchannel;

    @Inject
    private Identity identity;

    @Inject
    private Credentials credentials;

    @Inject
    private MailboxService mailboxService;

    @Inject
    private AssetLockManager assetLockManager;

    @Inject
    private AssetValidator assetValidator;

    private final String[] registeredFormats;

    public RepositoryAssetOperations() {
        //Load recognised formats from configuration file
        AssetEditorConfigurationParser parser = new AssetEditorConfigurationParser();
        List<AssetEditorConfiguration> rfs = parser.getAssetEditors();
        this.registeredFormats = new String[rfs.size()];
        for (int i = 0; i < rfs.size(); i++) {
            AssetEditorConfiguration config = rfs.get(i);
            registeredFormats[i] = config.getFormat();
        }
    }

    @Deprecated
    public void setRulesRepositoryForTest(RulesRepository repository) {
        // TODO use GuvnorTestBase with a real RepositoryAssetOperations instead
        this.rulesRepository = repository;
    }

    public String renameAsset(String uuid,
            String newName) {
        return rulesRepository.renameAsset(uuid,
                newName);
    }

    protected BuilderResult validateAsset(Asset asset) {
        try {
            ContentHandler handler = ContentManager
                    .getHandler(asset.getFormat());
            AssetItem item = rulesRepository.loadAssetByUUID(asset.getUuid());

            handler.storeAssetContent(asset,
                    item);

            AssetItemValidator assetItemValidator = new AssetItemValidator(handler,
                    item);
            return assetItemValidator.validate();

        } catch (Exception e) {
            log.error("Unable to build asset.",
                    e);
            BuilderResult result = new BuilderResult();
            result.addLine(createBuilderResultLine(asset));
            return result;
        }
    }

    private BuilderResultLine createBuilderResultLine(Asset asset) {
        BuilderResultLine builderResultLine = new BuilderResultLine();
        builderResultLine.setAssetName(asset.getName());
        builderResultLine.setAssetFormat(asset.getFormat());
        builderResultLine.setMessage("Unable to validate this asset. (Check log for detailed messages).");
        builderResultLine.setUuid(asset.getUuid());
        return builderResultLine;
    }

    public String checkinVersion(Asset asset) throws SerializationException {
        AssetItem repoAsset = rulesRepository.loadAssetByUUID(asset.getUuid());
        if (isAssetUpdatedInRepository(asset,
                repoAsset)) {
            return "ERR: Unable to save this asset, as it has been recently updated by [" + repoAsset.getLastContributor() + "]";
        }
        MetaData meta = asset.getMetaData();
        MetaDataMapper.getInstance().copyFromMetaData(meta,
                repoAsset);

        repoAsset.updateDateEffective(dateToCalendar(meta.getDateEffective()));
        repoAsset.updateDateExpired(dateToCalendar(meta.getDateExpired()));

        repoAsset.updateCategoryList(meta.getCategories());
        repoAsset.updateDescription(asset.getDescription());

        ContentHandler handler = ContentManager.getHandler(repoAsset.getFormat());
        handler.storeAssetContent(asset,
                repoAsset);
        repoAsset.updateValid(assetValidator.validate(repoAsset));

        if (AssetFormats.affectsBinaryUpToDate(asset.getFormat())) {
            ModuleItem pkg = repoAsset.getModule();
            pkg.updateBinaryUpToDate(false);
            RuleBaseCache.getInstance().remove(pkg.getUUID());
        }
        repoAsset.checkin(asset.getCheckinComment());

        return repoAsset.getUUID();
    }

    private Calendar dateToCalendar(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private boolean isAssetUpdatedInRepository(Asset asset,
            AssetItem repoAsset) {
        return asset.getLastModified().before(repoAsset.getLastModified().getTime());
    }

    public void restoreVersion(String versionUUID,
            String assetUUID,
            String comment) {
        AssetItem old = rulesRepository.loadAssetByUUID(versionUUID);
        AssetItem head = rulesRepository.loadAssetByUUID(assetUUID);

        log.info("USER:" + getCurrentUserName() + " RESTORE of asset: [" + head.getName() + "] UUID: [" + head.getUUID() + "] with historical version number: [" + old.getVersionNumber());

        rulesRepository.restoreHistoricalAsset(old,
                head,
                comment);
    }

    protected TableDataResult loadItemHistory(final VersionableItem item) {
        Iterator<VersionableItem> it = item.getHistory();
        //AssetHistoryIterator it = assetItem.getHistory();

        // MN Note: this uses the lazy iterator, but then loads the whole lot up, and returns it. 
        // The reason for this is that the GUI needs to show things in numeric order by the version 
        // number. When a version is restored, its previous version is NOT what you thought it was 
        // - due to how JCR works (its more like CVS then SVN). So to get a linear progression of
        // versions, we use the incrementing version number, and load it all up and sort it. This 
        // is not ideal. In future, we may do a "restore" instead just by copying content into
        // a new version, not restoring a node, in which case the iterator will be in order (or 
        // you can just walk all the way back). So if there are performance problems with looking 
        // at lots of historical versions, look at this nasty bit of code.
        List<TableDataRow> result = new ArrayList<TableDataRow>();
        while (it.hasNext()) {
            VersionableItem historical = (VersionableItem) it.next();
            long versionNumber = historical.getVersionNumber();
            if (isHistory(item,
                    versionNumber)) {
                result.add(createHistoricalRow(historical));
            }
        }

        TableDataResult table = new TableDataResult();
        table.data = result.toArray(new TableDataRow[result.size()]);

        return table;
    }

    private boolean isHistory(VersionableItem item,
            long versionNumber) {
        //return versionNumber != 0 && versionNumber != item.getVersionNumber();
        //we do return the LATEST version as part of the history. 
        return versionNumber != 0;
    }

    private TableDataRow createHistoricalRow(VersionableItem historical) {
        final DateFormat dateFormatter = DateFormat.getInstance();
        TableDataRow tableDataRow = new TableDataRow();
        tableDataRow.id = historical.getVersionSnapshotUUID();
        tableDataRow.values = new String[5];
        tableDataRow.values[0] = Long.toString(historical.getVersionNumber());
        tableDataRow.values[1] = historical.getCheckinComment();
        tableDataRow.values[2] = dateFormatter.format(historical.getLastModified().getTime());
        tableDataRow.values[3] = historical.getStateDescription();
        tableDataRow.values[4] = historical.getLastContributor();
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
            int numRows) {
        List<TableDataRow> result = new ArrayList<TableDataRow>();
        RepositoryFilter filter = new AssetItemFilter(identity);

        AssetItemIterator it = rulesRepository.findArchivedAssets();
        it.skip(skip);
        int count = 0;
        while (it.hasNext()) {

            AssetItem archived = (AssetItem) it.next();

            if (filter.accept(archived,
                    "read")) {
                result.add(createArchivedRow(archived));
                count++;
            }
            if (count == numRows) {
                break;
            }
        }

        return createArchivedTable(result,
                it);
    }

    private TableDataRow createArchivedRow(AssetItem archived) {
        TableDataRow row = new TableDataRow();
        row.id = archived.getUUID();
        row.values = new String[5];
        row.values[0] = archived.getName();
        row.values[1] = archived.getFormat();
        row.values[2] = archived.getModuleName();
        row.values[3] = archived.getLastContributor();
        row.values[4] = Long.toString(archived.getLastModified().getTime()
                .getTime());
        return row;
    }

    private TableDataResult createArchivedTable(List<TableDataRow> result,
            AssetItemIterator it) {
        TableDataResult table = new TableDataResult();
        table.data = result.toArray(new TableDataRow[result.size()]);
        table.currentPosition = it.getPosition();
        table.total = it.getSize();
        table.hasNext = it.hasNext();
        return table;
    }

    protected PageResponse<AdminArchivedPageRow> loadArchivedAssets(PageRequest request) {
        // Do query
        long start = System.currentTimeMillis();
        AssetItemIterator iterator = rulesRepository.findArchivedAssets();
        log.debug("Search time: " + (System.currentTimeMillis() - start));

        // Populate response
        long totalRowsCount = iterator.getSize();

        List<AdminArchivedPageRow> rowList = new ArchivedAssetPageRowBuilder()
                .withPageRequest(request)
                .withIdentity(identity)
                .withContent(iterator)
                .build();

        PageResponse<AdminArchivedPageRow> response = new PageResponseBuilder<AdminArchivedPageRow>()
                .withStartRowIndex(request.getStartRowIndex())
                .withPageRowList(rowList)
                .withLastPage(!iterator.hasNext())
                .buildWithTotalRowCount(totalRowsCount);

        long methodDuration = System.currentTimeMillis() - start;
        log.debug("Searched for Archived Assets in " + methodDuration + " ms.");
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
            String tableConfig) {
        long start = System.currentTimeMillis();
        ModuleItem pkg = rulesRepository.loadModuleByUUID(packageUuid);
        AssetItemIterator it;
        if (formats.length > 0) {
            it = pkg.listAssetsByFormat(formats);
        } else {
            it = pkg.listAssetsNotOfFormat(AssetFormatHelper
                    .listRegisteredTypes());
        }
        TableDisplayHandler handler = new TableDisplayHandler(tableConfig);
        log.debug("time for asset list load: "
                + (System.currentTimeMillis() - start));
        return handler.loadRuleListTable(it,
                skip,
                numRows);
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
        String search = searchText.replace('*',
                '%');

        if (!search.endsWith("%")) {
            search += "%";
        }

        List<AssetItem> resultList = new ArrayList<AssetItem>();

        long start = System.currentTimeMillis();
        AssetItemIterator it = rulesRepository.findAssetsByName(search,
                searchArchived);
        log.debug("Search time: " + (System.currentTimeMillis() - start));

        RepositoryFilter filter = new AssetItemFilter(identity);

        while (it.hasNext()) {
            AssetItem ai = it.next();
            if (filter.accept(ai,
                    RoleType.PACKAGE_READONLY.getName())) {
                resultList.add(ai);
            }
        }

        TableDisplayHandler handler = new TableDisplayHandler("searchresults");
        return handler.loadRuleListTable(resultList,
                skip,
                numRows);
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

        List<AssetItem> resultList = new ArrayList<AssetItem>();
        RepositoryFilter filter = new ModuleFilter(identity);

        AssetItemIterator assetItemIterator = rulesRepository.queryFullText(text,
                seekArchived);
        while (assetItemIterator.hasNext()) {
            AssetItem assetItem = assetItemIterator.next();
            Module data = new Module();
            data.setUuid(assetItem.getModule().getUUID());
            if (filter.accept(data,
                    RoleType.PACKAGE_READONLY.getName())) {
                resultList.add(assetItem);
            }
        }

        TableDisplayHandler handler = new TableDisplayHandler("searchresults");
        return handler.loadRuleListTable(resultList,
                skip,
                numRows);
    }

    // TODO: Very hard to unit test -> needs refactoring
    protected String buildAssetSource(Asset asset) throws SerializationException {
        ContentHandler handler = ContentManager.getHandler(asset.getFormat());

        StringBuilder stringBuilder = new StringBuilder();
        if (handler.isRuleAsset()) {
            BRMSPackageBuilder builder = new BRMSPackageBuilder();
            // now we load up the DSL files
            ModuleItem moduleItem = rulesRepository.loadModule(asset.getMetaData().getModuleName());
            builder.setDSLFiles(DSLLoader.loadDSLMappingFiles(moduleItem));
            if (asset.getMetaData().isBinary()) {
                AssetItem item = rulesRepository.loadAssetByUUID(
                        asset.getUuid());

                handler.storeAssetContent(asset,
                        item);
                ((IRuleAsset) handler).assembleDRL(builder,
                        item,
                        stringBuilder);
            } else {
                ((IRuleAsset) handler).assembleDRL(builder,
                        asset,
                        stringBuilder);
            }
        } else {
            if (handler instanceof ICanRenderSource) {
                ICanRenderSource crs = (ICanRenderSource) handler;
                crs.assembleSource(asset.getContent(),
                        stringBuilder);
            }
        }
        return stringBuilder.toString();
    }

    protected PageResponse<AssetPageRow> findAssetPage(AssetPageRequest request) {
        log.debug("Finding asset page of packageUuid ("
                + request.getPackageUuid() + ")");
        long start = System.currentTimeMillis();

        AssetItemIterator iterator = getAssetIterator(request);

        // Populate response
        long totalRowsCount = iterator.getSize();

        List<AssetPageRow> rowList = new AssetPageRowBuilder()
                .withPageRequest(request)
                .withIdentity(identity)
                .withContent(iterator)
                .build();

        PageResponse<AssetPageRow> response = new PageResponseBuilder<AssetPageRow>()
                .withStartRowIndex(request.getStartRowIndex())
                .withPageRowList(rowList)
                .withLastPage(!iterator.hasNext())
                .buildWithTotalRowCount(totalRowsCount);

        long methodDuration = System.currentTimeMillis() - start;
        log.debug("Found asset page of packageUuid ("
                + request.getPackageUuid() + ") in " + methodDuration + " ms.");
        return response;
    }

    protected PageResponse<QueryPageRow> quickFindAsset(QueryPageRequest request) {
        // Setup parameters
        String search = request.getSearchText().replace('*',
                '%');
        if (!search.startsWith("%")) {
            search = "%" + search;
        }
        if (!search.endsWith("%")) {
            search += "%";
        }

        // Do query
        long start = System.currentTimeMillis();
        AssetItemIterator iterator = rulesRepository.findAssetsByName(search,
                request.isSearchArchived(),
                request.isCaseSensitive());
        log.debug("Search time: " + (System.currentTimeMillis() - start));

        // Populate response
        List<QueryPageRow> rowList = new QuickFindPageRowBuilder()
                .withPageRequest(request)
                .withIdentity(identity)
                .withContent(iterator)
                .build();

        PageResponse<QueryPageRow> response = new PageResponseBuilder<QueryPageRow>()
                .withStartRowIndex(request.getStartRowIndex())
                .withPageRowList(rowList)
                .withLastPage(!iterator.hasNext())
                .buildWithTotalRowCount(-1);//its impossible to know the exact count selected until we'v reached
        //the end of iterator

        long methodDuration = System.currentTimeMillis() - start;
        log.debug("Queried repository (Quick Find) for (" + search + ") in " + methodDuration + " ms.");
        return response;
    }

    protected void lockAsset(String uuid) {
        String userName = credentials.getUsername();

        log.info("Locking asset uuid=" + uuid + " for user [" + userName + "]");

        assetLockManager.lockAsset(uuid,
                userName);
    }

    protected void unLockAsset(String uuid) {
        log.info("Unlocking asset [" + uuid + "]");
        assetLockManager.unLockAsset(uuid);
    }

    protected String getAssetLockerUserName(String uuid) {

        String userName = assetLockManager.getAssetLockerUserName(uuid);

        log.info("Asset locked by [" + userName + "]");

        return userName;
    }

    protected Asset loadAsset(AssetItem item) throws SerializationException {

        Asset asset = new Asset();
        asset.setUuid(item.getUUID());
        asset.setName(item.getName());
        asset.setDescription(item.getDescription());
        asset.setLastModified(item.getLastModified().getTime());
        asset.setLastContributor(item.getLastContributor());
        asset.setState((item.getState() != null) ? item.getState().getName() : "");
        asset.setDateCreated(item.getCreatedDate().getTime());
        asset.setCheckinComment(item.getCheckinComment());
        asset.setVersionNumber(item.getVersionNumber());
        asset.setFormat(item.getFormat());

        asset.setMetaData(populateMetaData(item));
        ContentHandler handler = ContentManager.getHandler(asset.getFormat());
        handler.retrieveAssetContent(asset,
                item);

        return asset;
    }

    /**
     * Populate meta data with asset specific info.
     */
    MetaData populateMetaData(AssetItem item) {
        MetaData meta = populateMetaData((VersionableItem) item);

        meta.setModuleName(item.getModuleName());
        meta.setModuleUUID(item.getModule().getUUID());
        meta.setBinary(item.isBinary());

        List<CategoryItem> categories = item.getCategories();
        fillMetaCategories(meta,
                categories);
        meta.setDateEffective(calendarToDate(item.getDateEffective()));
        meta.setDateExpired(calendarToDate(item.getDateExpired()));
        return meta;

    }

    /**
     * read in the meta data, populating all dublin core and versioning stuff.
     */
    MetaData populateMetaData(VersionableItem item) {
        MetaData meta = new MetaData();
        MetaDataMapper.getInstance().copyToMetaData(meta,
                item);

        //problematic implementation of getPrecedingVersion and getPrecedingVersion().
        //commented out temporarily as this is used by the front end. 
        //meta.hasPreceedingVersion = item.getPrecedingVersion() != null;
        //meta.hasSucceedingVersion = item.getPrecedingVersion() != null;
        return meta;
    }

    private void fillMetaCategories(MetaData meta,
            List<CategoryItem> categories) {
        meta.setCategories(new String[categories.size()]);
        for (int i = 0; i < meta.getCategories().length; i++) {
            CategoryItem cat = (CategoryItem) categories.get(i);
            meta.getCategories()[i] = cat.getFullPath();
        }
    }

    private Date calendarToDate(Calendar createdDate) {
        if (createdDate == null) {
            return null;
        }
        return createdDate.getTime();
    }

    protected void clearAllDiscussionsForAsset(final String assetId) {
        RulesRepository repo = rulesRepository;
        AssetItem asset = repo.loadAssetByUUID(assetId);
        
        //Don't update the Last Modified Date as it means the Asset to which the Discussion relates
        //needs to be re-loaded to prevent an Optimistic Lock Exception in isAssetUpdatedInRepository().
        //Other Asset meta-data does not affect the Last Modified Date. Discussions are now consistent.
        asset.updateStringProperty("",
                                   Discussion.DISCUSSION_PROPERTY_KEY,
                                   false );
        repo.save();

        push("discussion",
                assetId);
    }

    protected List<DiscussionRecord> addToDiscussionForAsset(String assetId,
                                                             String comment) {
        AssetItem asset = rulesRepository.loadAssetByUUID(assetId);
        Discussion dp = new Discussion();
        List<DiscussionRecord> discussion = dp.fromString(asset.getStringProperty(Discussion.DISCUSSION_PROPERTY_KEY));
        discussion.add(new DiscussionRecord(rulesRepository.getSession().getUserID(),
                                            StringEscapeUtils.escapeXml(comment)));
        
        //Adding a new Discussion has *never* updated the Last Modified Date.
        //clearAllDiscussionsForAsset has been made consistent with this behaviour.
        asset.updateStringProperty(dp.toString(discussion),
                                   Discussion.DISCUSSION_PROPERTY_KEY,
                                   false);
        rulesRepository.save();

        push("discussion",
                assetId);

        mailboxService.recordItemUpdated(asset);

        return discussion;
    }

    protected long getAssetCount(AssetPageRequest request) {
        log.debug("Counting assets in packageUuid (" + request.getPackageUuid() + ")");
        long start = System.currentTimeMillis();

        AssetItemIterator iterator = getAssetIterator(request);

        long methodDuration = System.currentTimeMillis() - start;
        log.debug("Counted assets in packageUuid ("
                + request.getPackageUuid() + ") in " + methodDuration + " ms.");
        return iterator.getSize();
    }

    private AssetItemIterator getAssetIterator(AssetPageRequest request) {
        ModuleItem packageItem = rulesRepository.loadModuleByUUID(request.getPackageUuid());

        AssetItemIterator iterator;
        if (request.getFormatInList() != null) {
            if (request.getFormatIsRegistered() != null) {
                throw new IllegalArgumentException("Combining formatInList and formatIsRegistered is not yet supported.");
            }
            iterator = packageItem.listAssetsByFormat(request.getFormatInList());

        } else {
            if (request.getFormatIsRegistered() != null && request.getFormatIsRegistered().equals(Boolean.FALSE)) {
                iterator = packageItem.listAssetsNotOfFormat(registeredFormats);
            } else {
                iterator = packageItem.queryAssets("");
            }
        }
        return iterator;
    }

    private void push(String messageType,
            String message) {
        backchannel.publish(new PushResponse(messageType,
                message));
    }

    private String getCurrentUserName() {
        return rulesRepository.getSession().getUserID();
    }
}
