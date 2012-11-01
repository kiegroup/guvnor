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
package org.drools.guvnor.client.rpc;

import java.util.List;

import org.drools.guvnor.client.rpc.Path;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

@RemoteServiceRelativePath("assetService")
public interface AssetService
    extends
    RemoteService {

    /**
     * This will quickly return a list of assets
     * 
     * @deprecated in favour of {@link quickFindAsset(QueryPageRequest)}
     */
    public TableDataResult quickFindAsset(String searchText,
                                          boolean searchArchived,
                                          int skip,
                                          int numRows) throws SerializationException;

    /**
     * Runs a full text search using JCR.
     * 
     * @param text
     * @param seekArchived
     * @param skip
     * @param numRows
     * @return
     * @throws SerializationException
     * @deprecated in favour of {@link queryFullText(QueryPageRequest)}
     */
    public TableDataResult queryFullText(String text,
                                         boolean seekArchived,
                                         int skip,
                                         int numRows) throws SerializationException;

    /**
     * Returns the lockers user name
     * 
     * @param assetPath
     * @return Lockers user name or null if there is no lock.
     */
    public String getAssetLockerUserName(Path assetPath);

    /**
     * Locks the asset, if a lock already exists this over writes it.
     * 
     * @param assetPath
     */
    public void lockAsset(Path assetPath);

    /**
     * Unlocks the asset.
     * 
     * @param assetPath
     */
    public void unLockAsset(Path assetPath);

    /**
     * This will quickly return a list of assets
     * 
     * @param queryRequest
     *            The parameters for the search
     */
    public PageResponse<QueryPageRow> quickFindAsset(QueryPageRequest queryRequest) throws SerializationException;

    public void archiveAsset(Path assetPath);

    public void unArchiveAsset(Path assetPath);

    /**
     * Archive assets based on Path
     * 
     * @param uuids
     */
    public void archiveAssets(Path[] assetPaths,
                              boolean value);

    /**
     * Remove an asset based on Path
     * 
     * @param Path path
     */
    public void removeAsset(Path assetPath);

    /**
     * Remove assets based on Path
     * 
     * @param Path paths
     */
    public void removeAssets(Path[] assetPaths);

    /**
     * This will return the effective source for an asset (in DRL). Used as an
     * aid for debugging.
     */
    public String buildAssetSource(Asset asset) throws SerializationException;

    /**
     * This will build the asset and return any build results (errors). This is
     * only to report on the results - it will generally not store any state or
     * apply any changed.
     */
    public BuilderResult validateAsset(Asset asset) throws SerializationException;

    /**
     * Rename an asset.
     */
    public Path renameAsset(Path assetPath,
                              String newName);

    /**
     * This loads up all the stuff for a rule asset based on the Path (always
     * latest and editable version).
     */
    public Asset loadRuleAsset(Path assetPath) throws SerializationException;

    public Asset[] loadRuleAssets(Path[] assetPaths) throws SerializationException;

    /**
     * This checks in a new version of an asset.
     * 
     * @return the UUID of the asset you are checking in, null if there was some
     *         problem (and an exception was not thrown).
     */
    public String checkinVersion(Asset asset) throws SerializationException;

    /**
     * This will restore the specified version in the repository, saving, and
     * creating a new version (with all the restored content).
     */
    public void restoreVersion(Path versionPath,
    		                   Path assetPath,
                               String comment);

    /**
     * This will load the history of the given asset or package, in a summary
     * format suitable for display in a table.
     */
    public TableDataResult loadItemHistory(Path uuid) throws SerializationException;

    /**
     * This will load the history of the given asset, in a summary format
     * suitable for display in a table.
     */
    public TableDataResult loadAssetHistory(String packageUUID,
                                            String assetName) throws SerializationException;

    /**
     * This will load all archived assets, in a summary format suitable for
     * display in a table.
     * 
     * @deprecated in favor of {@link loadArchivedAssets(PageRequest)}
     */
    public TableDataResult loadArchivedAssets(int skip,
                                              int numRows) throws SerializationException;

    /**
     * This will load all archived assets, in a summary format suitable for
     * display in a table.
     */

    public PageResponse<AdminArchivedPageRow> loadArchivedAssets(PageRequest request) throws SerializationException;

    /**
     * Supports filtering and pagination.
     * 
     * @param request
     *            never null, contains filter and pagination values
     * @return never null, contains the {@link List} of {@link AssetPageRow}
     * @throws SerializationException
     */
    public PageResponse<AssetPageRow> findAssetPage(AssetPageRequest request) throws SerializationException;

    /**
     * Given a format, this will return assets that match. It can also be used
     * for "pagination" by passing in start and finish row numbers.
     * 
     * @param packageUUID
     *            The package uuid to search inside.
     * @param format
     *            The format to filter on. If this is empty - it will look for
     *            all non "known" asset types (ie "misc" stuff).
     * @param numRows
     *            The number of rows to return. -1 means all.
     * @param startRow
     *            The starting row number if paging - if numRows is -1 then this
     *            is ignored.
     * @deprecated by {@link #findAssetPage(AssetPageRequest)}
     */
    public TableDataResult listAssets(String packageUUID,
                                      String formats[],
                                      int skip,
                                      int numRows,
                                      String tableConfig) throws SerializationException;

    /**
     * Given a format, this will return assets that match. It can also be used
     * for "pagination" by passing in start and finish row numbers.
     * 
     * @param packageName
     *            The name of package to search inside.
     * @param format
     *            The format to filter on. If this is empty - it will look for
     *            all non "known" asset types (ie "misc" stuff).
     * @param numRows
     *            The number of rows to return. -1 means all.
     * @param startRow
     *            The starting row number if paging - if numRows is -1 then this
     *            is ignored.
     * @deprecated by {@link #findAssetPage(AssetPageRequest)}
     */
    public TableDataResult listAssetsWithPackageName(String packageName,
                                                     String formats[],
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException;

    /**
     * Copies an asset into a new destination package.
     * 
     * @param assetPath
     *            The source asset path.
     * @param newPackage
     *            The destination package (may be the same as the current source
     *            package, but in that case the asset has to have a different
     *            name).
     * @param newName
     *            The new name of the asset.
     */
    public Path copyAsset(Path assetPath,
                            String newPackage,
                            String newName);

    /**
     * Prompt an asset into Global area.
     * 
     * @param assetPath The source asset Path.
     */
    public void promoteAssetToGlobalArea(Path assetPath);

    /**
     * This moves an asset to the given target package.
     */
    public void changeAssetPackage(Path assetPath,
                                   String newPackage,
                                   String comment);

    /**
     * Return a list of discussion items for a given asset...
     */
    public List<DiscussionRecord> loadDiscussionForAsset(Path assetPath);

    /**
     * Append a discussion item for the current user.
     */
    public List<DiscussionRecord> addToDiscussionForAsset(Path assetPath,
                                                          String comment);

    /** Only for admins, they can nuke it from orbit to clear it out */
    public void clearAllDiscussionsForAsset(Path assetPath);

    /**
     * This will change the state of an asset.
     * 
     * @param Path
     *            The Path of the asset we are tweaking.
     * @param newState
     *            The new state to set. It must be valid in the repo.
     */
    public void changeState(Path assetPath,
                            String newState);

    /**
     * This will change the state of package.
     * 
     * @param uuid
     *            The UUID of the asset we are tweaking.
     * @param newState
     *            The new state to set. It must be valid in the repo.
     */
    public void changePackageState(String uuid,
                                   String newStatee);

    /**
     * This will return the number of Assets matching the given criteria
     * 
     * @param request
     * @return
     */
    public long getAssetCount(AssetPageRequest request) throws SerializationException;

    /**
     * Convert an Asset to a target asset format
     * 
     * @param assetPath
     * @param targetFormat
     * @return
     */
    public ConversionResult convertAsset(Path assetPath,
                                         String targetFormat) throws SerializationException;

}
