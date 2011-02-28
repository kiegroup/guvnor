/*
 * Copyright 2005 JBoss Inc
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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * This is what the remote service will implement, as a servlet. (in
 * hosted/debug mode, you could also use an implementation that was in-process).
 */
public interface RepositoryService
    extends
    RemoteService {

    /**
     * @param categoryPath
     *            A "/" delimited path to a category.
     * @param callback
     */
    public String[] loadChildCategories(String categoryPath);

    /**
     * Return a a 2d array/grid of results for rules.
     * 
     * @param A
     *            "/" delimited path to a category.
     *            
     * @deprecated in favour of {@link loadRuleListForCategories(CategoryPageRequest)}
     */
    public TableDataResult loadRuleListForCategories(String categoryPath,
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException;

    /**
     * Return a list of Assets by category.
     * 
     * @param request
     *            Request specific details
     */
    public PageResponse<CategoryPageRow> loadRuleListForCategories(CategoryPageRequest request) throws SerializationException;

    /**
     * Return a a 2d array/grid of results for rules.
     * 
     * @param The
     *            name of the state.
     *            
     * @deprecated in favour of {@link loadRuleListForState(StatePageRequest)}
     */
    public TableDataResult loadRuleListForState(String state,
                                                int skip,
                                                int numRows,
                                                String tableConfig) throws SerializationException;

    /**
     * Return a list of Assets by status
     * 
     * @param request
     *            Request specific details
     */
    public PageResponse<StatePageRow> loadRuleListForState(StatePageRequest request) throws SerializationException;

    /**
     * This will return a TableConfig of header names.
     * 
     * @param listName
     *            The name of the list that we are going to render.
     * @deprecated in favour of {@link AbstractPagedTable}
     */
    public TableConfig loadTableConfig(String listName);

    /**
     * This will create a new category at the specified path.
     */
    public Boolean createCategory(String path,
                                  String name,
                                  String description);

    /**
     * Creates a brand new rule with the initial category. Return the UUID of
     * the item created. This will not check in the rule, but just leave it as
     * saved in the repo.
     */
    public String createNewRule(String ruleName,
                                String description,
                                String initialCategory,
                                String initialPackage,
                                String format) throws SerializationException;

    /**
     * Creates a new rule which is imported from global area. Return the UUID of
     * the item created. This will not check in the rule, but just leave it as
     * saved in the repo.
     */
    public String createNewImportedRule(String sharedAssetName,
                                        String initialPackage) throws SerializationException;

    /**
     * Delete un checked in Asset
     */
    public void deleteUncheckedRule(String ruleName,
                                    String initialPackage);

    /**
     * Clear the rules repositoty, Use at your own risk.
     */
    public void clearRulesRepository();

    /**
     * This returns a list of packages where rules may be added. Only the UUID
     * and the name need to be populated.
     */
    public PackageConfigData[] listPackages();

    /**
     * This returns a list of packages where rules may be added. Only the UUID
     * and the name need to be populated.
     */
    public PackageConfigData[] listPackages(String workspace);

    /**
     * This returns a list of workspaces
     */
    public String[] listWorkspaces();

    /**
     * This creates a workspace
     */
    public void createWorkspace(String workspace);

    /**
     * This removes a workspace
     */
    public void removeWorkspace(String workspace);

    /**
     * This removes a workspace
     */
    public void updateWorkspace(String workspace,
                                String[] selectedModules,
                                String[] unselectedModules);

    public void updateDependency(String uuid, String dependencyPath);

    public String[] getDependencies(String uuid);
 
    /**
     * This returns the global packages.
     */
    public PackageConfigData loadGlobalPackage();

    /**
     * This returns a list of archived packages.
     */
    public PackageConfigData[] listArchivedPackages();

    /**
     * This checks in a new version of an asset.
     * 
     * @return the UUID of the asset you are checking in, null if there was some
     *         problem (and an exception was not thrown).
     */
    public String checkinVersion(RuleAsset asset) throws SerializationException;

    /**
     * This will restore the specified version in the repository, saving, and
     * creating a new version (with all the restored content).
     */
    public void restoreVersion(String versionUUID,
                               String assetUUID,
                               String comment);

    /**
     * This creates a package of the given name, and checks it in.
     * 
     * @return UUID of the created item.
     */
    public String createPackage(String name,
                                String description) throws SerializationException;

    /**
     * This creates a package of the given name, and checks it in.
     * 
     * @return UUID of the created item.
     */
    public String createSubPackage(String name,
                                   String description,
                                   String parentPackage) throws SerializationException;

    /**
     * Loads a package by its uuid.
     * 
     * @return Well, its pretty obvious if you think about it for a minute.
     *         Really.
     */
    public PackageConfigData loadPackageConfig(String uuid);

    /**
     * Saves the package config data in place (does not create a new version of
     * anything).
     * 
     * @return A ValidatedReponse, with any errors to be reported. No payload is
     *         in the response. If there are any errors, the user should be
     *         given the option to review them, and correct them if needed (but
     *         a save will not be prevented this way - as its not an exception).
     */
    public ValidatedResponse savePackage(PackageConfigData data) throws SerializationException;

    /**
     * Returns a list of valid states.
     */
    public String[] listStates() throws SerializationException;

    /**
     * Create a state (status).
     * 
     * @return the UUID of the created StateItem.
     */
    public String createState(String name) throws SerializationException;

    /**
     * Renames a state.
     * 
     * @param oldName
     *            states old name.
     * @param newName
     *            states new name.
     * @throws SerializationException
     */
    public void renameState(String oldName,
                            String newName) throws SerializationException;

    /**
     * Removes a state.
     * 
     * @param name
     *            state name that will be removed.
     * @throws SerializationException
     */
    public void removeState(String name) throws SerializationException;

    /**
     * This will change the state of an asset or package.
     * 
     * @param uuid
     *            The UUID of the asset we are tweaking.
     * @param newState
     *            The new state to set. It must be valid in the repo.
     * @param wholePackage
     *            true if it is a package we are setting the state of. If this
     *            is true, UUID must be the status of a package, if false, it
     *            must be an asset.
     */
    public void changeState(String uuid,
                            String newState,
                            boolean wholePackage);

    /**
     * Copy the package (everything).
     * 
     * @param sourcePackageName
     * @param destPackageName
     */
    public void copyPackage(String sourcePackageName,
                            String destPackageName) throws SerializationException;

    /**
     * This will load a list of snapshots for the given package. Snapshots are
     * created by taking a labelled copy of a package, at a point in time, for
     * instance for deployment.
     */
    public SnapshotInfo[] listSnapshots(String packageName);

    /**
     * Create a package snapshot for deployment.
     * 
     * @param packageName
     *            THe name of the package to copy.
     * @param snapshotName
     *            The name of the snapshot. Has to be unique unless existing one
     *            is to be replaced.
     * @param replaceExisting
     *            Replace the existing one (must be true to replace an existing
     *            snapshot of the same name).
     * @param comment
     *            A comment to be added to the copied one.
     */
    public void createPackageSnapshot(String packageName,
                                      String snapshotName,
                                      boolean replaceExisting,
                                      String comment);

    /**
     * This alters an existing snapshot, it can be used to copy or delete it.
     * 
     * @param packageName
     *            The package name that we are dealing with.
     * @param snapshotName
     *            The snapshot name (this must exist)
     * @param delete
     *            true if the snapshotName is to be removed.
     * @param newSnapshotName
     *            The name of the target snapshot that the contents will be
     *            copied to.
     */
    public void copyOrRemoveSnapshot(String packageName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException;

    /**
     * This will remove a category. A category must have no current assets
     * linked to it, or else it will not be able to be removed.
     * 
     * @param categoryPath
     *            The full path to the category. Any sub categories will also be
     *            removed.
     * @throws SerializationException
     *             For when it all goes horribly wrong.
     */
    public void removeCategory(String categoryPath) throws SerializationException;

    /**
     * Loads up the SuggestionCompletionEngine for the given package. As this
     * doesn't change that often, its safe to cache. However, if a change is
     * made to a package, should blow away the cache.
     */
    public SuggestionCompletionEngine loadSuggestionCompletionEngine(String packageName) throws SerializationException;

    /**
     * Build the package (may be a snapshot) and return the result.
     * 
     * This will then store the result in the package as an attachment.
     * 
     * if a non null selectorName is passed in it will lookup a selector as
     * configured in the systems selectors.properties file. This will then apply
     * the filter to the package being built.
     */
    public BuilderResult buildPackage(String packageUUID,
                                      boolean force,
                                      String buildMode,
                                      String operator,
                                      String statusDescriptionValue,
                                      boolean enableStatusSelector,
                                      String categoryOperator,
                                      String category,
                                      boolean enableCategorySelector,
                                      String customSelectorName) throws SerializationException;

    /**
     * This method will ZIP the jars that compose the Model of our Domain in the
     * Package
     */

    /**
     * return custom selector names
     */
    public String[] getCustomSelectors() throws SerializationException;

    /**
     * This will return the effective DRL for a package. This would be the
     * equivalent if all the rules were written by hand in the one file. It may
     * not actually be compiled this way in the implementation, so this is for
     * display and debugging assistance only.
     * 
     * It should still generate
     * 
     * @throws SerializationException
     */
    public String buildPackageSource(String packageUUID) throws SerializationException;

    /**
     * Rename a category - taking in the full path, and just the new name.
     */
    public void renameCategory(String fullPathAndName,
                               String newName);

    /**
     * Permanently remove a package (delete it).
     * 
     * @param uuid
     *            of the package.
     */
    public void removePackage(String uuid);

    /**
     * Rename a package.
     */
    public String renamePackage(String uuid,
                                String newName);

    /**
     * This will force a rebuild of all snapshots binary data. No errors are
     * expected, as there will be no change. If there are errors, an expert will
     * need to look at them.
     */
    public void rebuildSnapshots() throws SerializationException;

    /**
     * This will force a rebuild of all packages binary data. No errors are
     * expected, as there will be no change. If there are errors, an expert will
     * need to look at them.
     */
    public void rebuildPackages() throws SerializationException;

    /**
     * This will list the rules available in a package. This has an upper limit
     * of what it will return (it just doesn't make sense to show a list of 20K
     * items !).
     */
    public String[] listRulesInPackage(String packageName) throws SerializationException;

    /**
     * 
     * @param packageName
     *            The package name the scenario is to be run in.
     * @param scenario
     *            The scenario to run.
     * @return The scenario, with the results fields populated.
     * @throws SerializationException
     */
    public SingleScenarioResult runScenario(String packageName,
                                            Scenario scenario) throws SerializationException;

    /**
     * This should be pretty obvious what it does !
     */
    public BulkTestRunResult runScenariosInPackage(String packageUUID) throws SerializationException;

    /**
     * List the fact types (class names) in the scope of a given package. This
     * may not include things on the "system" classpath, but only things
     * specifically scoped to the package (eg in jars that have been uploaded to
     * it as an asset).
     */
    public String[] listTypesInPackage(String packageUUID) throws SerializationException;

    /**
     * This will list the last N log entryies logged by the server. For
     * debugging purposes in the GUI.
     * 
     * @deprecated in favour of {@link showLogEntries()}
     */
    public LogEntry[] showLog();

    /**
     * This will list log entries logged by the server. For debugging purposes
     * in the GUI. This is an equivalent function to {@link showLog()} which has
     * been deprecated in favour of DTO centric operations.
     */
    public PageResponse<LogPageRow> showLog(PageRequest request);

    /**
     * clean up the log entry.
     */

    public void cleanLog();

    /**
     * @param valuePairs
     *            key=value pairs to be interpolated into the expression.
     * @param expression
     *            The expression, which will then be eval'ed to generate a
     *            String[]
     */
    public String[] loadDropDownExpression(String[] valuePairs,
                                           String expression);

    /**
     * Runs a full text search using JCR.
     * 
     * @param request
     * @return
     * @throws SerializationException
     */
    public PageResponse<QueryPageRow> queryFullText(QueryPageRequest request) throws SerializationException;

    /**
     * Run a meta data search. All dates are in format as configured for the
     * system. Pass in null and they will not be included in the search (that
     * applies to any field).
     * 
     * @param qr
     * @param createdAfter
     * @param createdBefore
     * @param modifiedAfter
     * @param modifiedBefore
     * @param seekArchived
     * @param skip
     * @param numRows
     * @return
     * @throws SerializationException
     * 
     * @deprecated in favour of {@link queryMetaData(QueryPageRequest)}
     */
    public TableDataResult queryMetaData(final MetaDataQuery[] qr,
                                         Date createdAfter,
                                         Date createdBefore,
                                         Date modifiedAfter,
                                         Date modifiedBefore,
                                         boolean seekArchived,
                                         int skip,
                                         int numRows) throws SerializationException;

    /**
     * Run a meta data search. All dates are in format as configured for the
     * system. Pass in null and they will not be included in the search (that
     * applies to any field).
     * 
     * @param request
     * @return
     * @throws SerializationException
     */
    public PageResponse<QueryPageRow> queryMetaData(QueryMetadataPageRequest request) throws SerializationException;

    /**
     * @return A map of username : list of permission types for display reasons.
     * 
     * @deprecated in favour of {@link listUserPermissions(PageRequest)}
     */
    public Map<String, List<String>> listUserPermissions() throws DetailedSerializationException;

    /**
     * @return A map of username : list of permission types for display reasons.
     */
    public PageResponse<PermissionsPageRow> listUserPermissions(PageRequest request) throws DetailedSerializationException;

    /**
     * Loads the user permissions.
     * 
     * @param userName
     * @return A map of permission type to the targets it applies to.
     */
    public Map<String, List<String>> retrieveUserPermissions(String userName);

    /**
     * Update the user permissions - takes the userName, and a map from
     * permission type to the list of targets it applies to.
     */
    public void updateUserPermissions(String userName,
                                      Map<String, List<String>> perms);

    /**
     * List the available permission types.
     * 
     * @return
     */
    public String[] listAvailablePermissionTypes();

    /**
     * Removes user security data.
     */
    public void deleteUser(String userName);

    /**
     * create new user.
     */
    public void createUser(String userName);

    /**
     * Installs the sample repository, wiping out what was already there.
     * Generally shouldn't call this unless it is new !
     */
    public void installSampleRepository() throws SerializationException;

    /**
     * Return a list of discussion items for a given asset...
     */
    public List<DiscussionRecord> loadDiscussionForAsset(String assetId);

    /**
     * Append a discussion item for the current user.
     */
    public List<DiscussionRecord> addToDiscussionForAsset(String assetId,
                                                          String comment);

    /** Only for admins, they can nuke it from orbit to clear it out */
    public void clearAllDiscussionsForAsset(String assetId);

    /**
     * Subscribe for a "callback" for a given request.
     */
    public List<PushResponse> subscribe();

    /**
     * Load the data for a given inbox for the currently logged in user.
     * 
     * @deprecated in favour of {@link loadInbox(InboxPageRequest)}
     */
    public TableDataResult loadInbox(String inboxName) throws DetailedSerializationException;

    /**
     * Compare two snapshots.
     * 
     * @deprecated in favour of {@link compareSnapshots(SnapshotComparisonRequest)}
     */
    public SnapshotDiffs compareSnapshots(String packageName,
                                          String firstSnapshotName,
                                          String secondSnapshotName);

    public SnapshotComparisonPageResponse compareSnapshots(SnapshotComparisonPageRequest request);

    /**
     * Load and process the repository configuration templates.
     */
    public String processTemplate(String name,
                                  Map<String, Object> data);

    public Boolean isHostedMode();

    /**
     * Load the data for a given inbox for the currently logged in user.
     */
    public PageResponse<InboxPageRow> loadInbox(InboxPageRequest request) throws DetailedSerializationException;

    /**
     * Returns the Spring context elements specified by
     * SpringContextElementsManager
     * 
     * @return a Map containing the key,value pairs of data.
     * @throws DetailedSerializationException
     */
    public Map<String, String> loadSpringContextElementData() throws DetailedSerializationException;

}
