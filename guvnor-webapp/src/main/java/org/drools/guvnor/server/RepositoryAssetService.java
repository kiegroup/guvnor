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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.drools.guvnor.client.rpc.AdminArchivedPageRow;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.AssetService;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.ICanHasAttachment;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.guvnor.server.util.Discussion;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.RuleAssetPopulator;
import org.drools.repository.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.annotations.LoggedIn;
import org.jboss.seam.security.Identity;

import com.google.gwt.user.client.rpc.SerializationException;

@Named("org.drools.guvnor.client.rpc.AssetService")
public class RepositoryAssetService
        implements
        AssetService {

    private static final long serialVersionUID = 90111;

    private static final LoggingHelper log = LoggingHelper.getLogger(RepositoryAssetService.class);

    @Inject
    protected RulesRepository rulesRepository;

    @Inject
    protected RepositoryAssetOperations repositoryAssetOperations;

    @Inject
    protected ServiceSecurity serviceSecurity;

    @Inject
    protected Backchannel backchannel;

    @Inject
    protected Identity identity;

    public RulesRepository getRulesRepository() {
        return rulesRepository;
    }

    /**
     * This actually does the hard work of loading up an asset based on its
     * format.
     * <p/>
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions: 1. The user has a ANALYST_READ role or higher
     * (i.e., ANALYST) and this role has permission to access the category which
     * the asset belongs to. Or. 2. The user has a package.readonly role or
     * higher (i.e., package.admin, package.developer) and this role has
     * permission to access the package which the asset belongs to.
     */
    @WebRemote
    @LoggedIn
    public RuleAsset loadRuleAsset(String uuid) throws SerializationException {

        long time = System.currentTimeMillis();

        AssetItem item = rulesRepository.loadAssetByUUID(uuid);
        RuleAsset asset = new RuleAssetPopulator().populateFrom(item);

        asset.setMetaData(repositoryAssetOperations.populateMetaData(item));

        serviceSecurity.checkIsPackageReadOnlyOrAnalystReadOnly(asset);
        PackageItem pkgItem = handlePackageItem(item,
                asset);

        log.debug("Package: " + pkgItem.getName() + ", asset: " + item.getName() + ". Load time taken for asset: " + (System.currentTimeMillis() - time));
        UserInbox.recordOpeningEvent(item);
        return asset;

    }

    private PackageItem handlePackageItem(AssetItem item,
                                          RuleAsset asset) throws SerializationException {
        PackageItem packageItem = item.getPackage();

        ContentHandler handler = ContentManager.getHandler(asset.getFormat());
        handler.retrieveAssetContent(asset,
                item);

        asset.setReadonly(asset.getMetaData().isHasSucceedingVersion());

        if (packageItem.isSnapshot()) {
            asset.setReadonly(true);
        }
        return packageItem;
    }

    @WebRemote
    @LoggedIn
    public RuleAsset[] loadRuleAssets(String[] uuids) throws SerializationException {
        return loadRuleAssets(Arrays.asList(uuids));
    }

    @WebRemote
    @LoggedIn
    public String checkinVersion(RuleAsset asset) throws SerializationException {
        serviceSecurity.checkIsPackageDeveloperOrAnalyst(asset);

        log.info("USER:" + getCurrentUserName() + " CHECKING IN asset: [" + asset.getName() + "] UUID: [" + asset.getUuid() + "] ");
        return repositoryAssetOperations.checkinVersion(asset);
    }

    @WebRemote
    @LoggedIn
    public void restoreVersion(String versionUUID,
                               String assetUUID,
                               String comment) {
        repositoryAssetOperations.restoreVersion(versionUUID,
                assetUUID,
                comment);
    }

    @WebRemote
    @LoggedIn
    public TableDataResult loadItemHistory(String uuid) throws SerializationException {
        //VersionableItem assetItem = rulesRepository.loadAssetByUUID( uuid );
        VersionableItem assetItem = rulesRepository.loadItemByUUID(uuid);

        //serviceSecurity.checkSecurityAssetPackagePackageReadOnly( assetItem );
        return repositoryAssetOperations.loadItemHistory(assetItem);
    }

    /**
     * @deprecated in favour of {@link #loadArchivedAssets(PageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult loadAssetHistory(String packageUUID,
                                            String assetName) throws SerializationException {
        PackageItem pi = rulesRepository.loadPackageByUUID(packageUUID);
        AssetItem assetItem = pi.loadAsset(assetName);
        serviceSecurity.checkSecurityPackageReadOnlyWithPackageUuid(assetItem.getPackage().getUUID());

        return repositoryAssetOperations.loadItemHistory(assetItem);
    }

    @WebRemote
    @LoggedIn
    @Deprecated
    public TableDataResult loadArchivedAssets(int skip,
                                              int numRows) throws SerializationException {
        return repositoryAssetOperations.loadArchivedAssets(skip,
                numRows);
    }

    @WebRemote
    @LoggedIn
    public PageResponse<AdminArchivedPageRow> loadArchivedAssets(PageRequest request) throws SerializationException {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        if (request.getPageSize() != null && request.getPageSize() < 0) {
            throw new IllegalArgumentException("pageSize cannot be less than zero.");
        }

        return repositoryAssetOperations.loadArchivedAssets(request);
    }

    /**
     * @deprecated in favour of {@link #findAssetPage(AssetPageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult listAssetsWithPackageName(String packageName,
                                                     String formats[],
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException {
        PackageItem pkg = rulesRepository.loadPackage(packageName);
        return listAssets(pkg.getUUID(),
                formats,
                skip,
                numRows,
                tableConfig);
    }

    /**
     * @deprecated in favour of {@link #findAssetPage(AssetPageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult listAssets(String packageUuid,
                                      String formats[],
                                      int skip,
                                      int numRows,
                                      String tableConfig) throws SerializationException {
        log.debug("Loading asset list for [" + packageUuid + "]");
        if (numRows == 0) {
            throw new DetailedSerializationException("Unable to return zero results (bug)",
                    "probably have the parameters around the wrong way, sigh...");
        }
        return repositoryAssetOperations.listAssets(packageUuid,
                formats,
                skip,
                numRows,
                tableConfig);
    }

    @WebRemote
    @LoggedIn
    public String copyAsset(String assetUUID,
                            String newPackage,
                            String newName) {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName(newPackage);

        log.info("USER:" + getCurrentUserName() + " COPYING asset: [" + assetUUID + "] to [" + newName + "] in PACKAGE [" + newPackage + "]");
        return rulesRepository.copyAsset(assetUUID,
                newPackage,
                newName);
    }

    @WebRemote
    @LoggedIn
    public void changeAssetPackage(String uuid,
                                   String newPackage,
                                   String comment) {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName(newPackage);

        log.info("USER:" + getCurrentUserName() + " CHANGING PACKAGE OF asset: [" + uuid + "] to [" + newPackage + "]");
        rulesRepository.moveRuleItemPackage(newPackage,
                uuid,
                comment);
    }

    @WebRemote
    @LoggedIn
    public void promoteAssetToGlobalArea(String uuid) {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( RulesRepository.RULE_GLOBAL_AREA );
        AssetItem item = rulesRepository.loadAssetByUUID( uuid );
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName(item.getPackageName());

        log.info("USER:" + getCurrentUserName() + " CHANGING PACKAGE OF asset: [" + uuid + "] to [ globalArea ]");
        rulesRepository.moveRuleItemPackage(RulesRepository.RULE_GLOBAL_AREA,
                uuid,
                "promote asset to globalArea");
    }

    @WebRemote
    @LoggedIn
    public String buildAssetSource(RuleAsset asset) throws SerializationException {
        serviceSecurity.checkIsPackageDeveloperOrAnalyst(asset);
        return repositoryAssetOperations.buildAssetSource(asset);
    }

    @WebRemote
    @LoggedIn
    public String renameAsset(String uuid,
                              String newName) {
        AssetItem item = rulesRepository.loadAssetByUUID(uuid);
        serviceSecurity.checkIsPackageDeveloperOrAnalyst(item);

        return repositoryAssetOperations.renameAsset(uuid,
                newName);
    }

    @WebRemote
    @LoggedIn
    public void archiveAsset(String uuid) {
        archiveOrUnarchiveAsset(uuid,
                true);
    }

    @WebRemote
    @LoggedIn
    public BuilderResult validateAsset(RuleAsset asset) throws SerializationException {
        serviceSecurity.checkIsPackageDeveloperOrAnalyst(asset);
        return repositoryAssetOperations.validateAsset(asset);
    }

    public void unArchiveAsset(String uuid) {
        archiveOrUnarchiveAsset(uuid,
                false);
    }

    @WebRemote
    @LoggedIn
    public void archiveAssets(String[] uuids,
                              boolean value) {
        for (String uuid : uuids) {
            archiveOrUnarchiveAsset(uuid,
                    value);
        }
    }

    @WebRemote
    @LoggedIn
    public void removeAsset(String uuid) {
        try {
            AssetItem item = rulesRepository.loadAssetByUUID(uuid);
            serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid(item.getPackage().getUUID());

            item.remove();
            rulesRepository.save();
        } catch (RulesRepositoryException e) {
            log.error("Unable to remove asset.",
                    e);
            throw e;
        }
    }

    @WebRemote
    @LoggedIn
    public void removeAssets(String[] uuids) {
        for (String uuid : uuids) {
            removeAsset(uuid);
        }
    }

    @WebRemote
    @LoggedIn
    public PageResponse<AssetPageRow> findAssetPage(AssetPageRequest request) throws SerializationException {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        if (request.getPageSize() != null && request.getPageSize() < 0) {
            throw new IllegalArgumentException("pageSize cannot be less than zero.");
        }

        return repositoryAssetOperations.findAssetPage(request);
    }

    @WebRemote
    @LoggedIn
    public PageResponse<QueryPageRow> quickFindAsset(QueryPageRequest request) throws SerializationException {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        if (request.getPageSize() != null && request.getPageSize() < 0) {
            throw new IllegalArgumentException("pageSize cannot be less than zero.");
        }

        return repositoryAssetOperations.quickFindAsset(request);
    }

    /**
     * @deprecated in favour of {@link #quickFindAsset(QueryPageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult quickFindAsset(String searchText,
                                          boolean searchArchived,
                                          int skip,
                                          int numRows) throws SerializationException {
        return repositoryAssetOperations.quickFindAsset(searchText,
                searchArchived,
                skip,
                numRows);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.client.rpc.RepositoryService#lockAsset(java.lang.String
     * )
     */
    @LoggedIn
    public void lockAsset(String uuid) {
        repositoryAssetOperations.lockAsset(uuid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.client.rpc.RepositoryService#unLockAsset(java.lang.
     * String)
     */
    @LoggedIn
    public void unLockAsset(String uuid) {
        repositoryAssetOperations.unLockAsset(uuid);
    }

    /**
     * @deprecated in favour of {@link ServiceImplementation#queryFullText(QueryPageRequest)}
     */
    @WebRemote
    @LoggedIn
    public TableDataResult queryFullText(String text,
                                         boolean seekArchived,
                                         int skip,
                                         int numRows) throws SerializationException {
        if (numRows == 0) {
            throw new DetailedSerializationException("Unable to return zero results (bug)",
                    "probably have the parameters around the wrong way, sigh...");
        }
        return repositoryAssetOperations.queryFullText(text,
                seekArchived,
                skip,
                numRows);
    }

    RuleAsset[] loadRuleAssets(Collection<String> uuids) throws SerializationException {
        if (uuids == null) {
            return null;
        }
        Collection<RuleAsset> assets = new HashSet<RuleAsset>();

        for (String uuid : uuids) {
            assets.add(loadRuleAsset(uuid));
        }

        return assets.toArray(new RuleAsset[assets.size()]);
    }

    private void archiveOrUnarchiveAsset(String uuid,
                                         boolean archive) {
        try {
            AssetItem item = rulesRepository.loadAssetByUUID(uuid);
            serviceSecurity.checkIsPackageDeveloperOrAnalyst(item);

            if (item.getPackage().isArchived()) {
                throw new RulesRepositoryException("The package [" + item.getPackageName() + "] that asset [" + item.getName() + "] belongs to is archived. You need to unarchive it first.");
            }

            log.info("USER:" + getCurrentUserName() + " ARCHIVING asset: [" + item.getName() + "] UUID: [" + item.getUUID() + "] ");

            try {
                ContentHandler handler = getContentHandler(item);
                if (handler instanceof ICanHasAttachment) {
                    ((ICanHasAttachment) handler).onAttachmentRemoved(item);
                }
            } catch (IOException e) {
                log.error("Unable to remove asset attachment",
                        e);
            }

            item.archiveItem(archive);
            PackageItem pkg = item.getPackage();
            pkg.updateBinaryUpToDate(false);
            RuleBaseCache.getInstance().remove(pkg.getUUID());
            if (archive) {
                item.checkin("archived");
            } else {
                item.checkin("unarchived");
            }

            push("packageChange",
                    pkg.getName());

        } catch (RulesRepositoryException e) {
            throw e;
        }
    }

    @LoggedIn
    public List<DiscussionRecord> addToDiscussionForAsset(String assetId,
                                                          String comment) {
        return repositoryAssetOperations.addToDiscussionForAsset(assetId,
                comment);
    }

    @LoggedIn
    public void clearAllDiscussionsForAsset(final String assetId) {
        serviceSecurity.checkSecurityIsAdmin();
        repositoryAssetOperations.clearAllDiscussionsForAsset(assetId);
    }

    @LoggedIn
    public List<DiscussionRecord> loadDiscussionForAsset(String assetId) {
        return new Discussion().fromString(rulesRepository.loadAssetByUUID(assetId).getStringProperty(Discussion.DISCUSSION_PROPERTY_KEY));
    }

    /**
     * Role-based Authorization check: This method can be accessed if user has
     * following permissions: 1. The user has a Analyst role and this role has
     * permission to access the category which the asset belongs to. Or. 2. The
     * user has a package.developer role or higher (i.e., package.admin) and
     * this role has permission to access the package which the asset belongs
     * to.
     */
    @WebRemote
    @LoggedIn
    public void changeState(String uuid,
                            String newState) {
        AssetItem asset = rulesRepository.loadAssetByUUID(uuid);
        serviceSecurity.checkIsPackageDeveloperOrAnalyst(asset);

        log.info("USER:" + getCurrentUserName() + " CHANGING ASSET STATUS. Asset name, uuid: " + "[" + asset.getName() + ", " + asset.getUUID() + "]" + " to [" + newState + "]");
        String oldState = asset.getStateDescription();
        asset.updateState(newState);

        push("statusChange",
                oldState);
        push("statusChange",
                newState);

        addToDiscussionForAsset(asset.getUUID(),
                oldState + " -> " + newState);

        rulesRepository.save();
    }

    @WebRemote
    @LoggedIn
    public void changePackageState(String uuid,
                                   String newState) {

        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid(uuid);

        PackageItem pkg = rulesRepository.loadPackageByUUID(uuid);
        log.info("USER:" + getCurrentUserName() + " CHANGING Package STATUS. Asset name, uuid: " + "[" + pkg.getName() + ", " + pkg.getUUID() + "]" + " to [" + newState + "]");
        pkg.changeStatus(newState);

        rulesRepository.save();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.client.rpc.RepositoryService#getAssetLockerUserName
     * (java.lang.String)
     */
    @LoggedIn
    public String getAssetLockerUserName(String uuid) {
        return repositoryAssetOperations.getAssetLockerUserName(uuid);
    }
    
    @LoggedIn
    public long getAssetCount(AssetPageRequest request) throws SerializationException {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        return repositoryAssetOperations.getAssetCount(request);
    }

    private void push(String messageType,
                      String message) {
        backchannel.publish(new PushResponse(messageType,
                message));
    }

    private ContentHandler getContentHandler(AssetItem repoAsset) {
        return ContentManager.getHandler(repoAsset.getFormat());
    }

    private String getCurrentUserName() {
        return rulesRepository.getSession().getUserID();
    }

}
