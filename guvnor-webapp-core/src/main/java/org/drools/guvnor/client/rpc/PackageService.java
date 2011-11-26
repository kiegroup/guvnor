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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

public interface PackageService
        extends
        RemoteService {

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
     * This returns a list of archived packages.
     */
    public PackageConfigData[] listArchivedPackages();

    /**
     * This returns the global packages.
     */
    public PackageConfigData loadGlobalPackage();

    public SnapshotInfo loadSnapshotInfo(String packageName, String snapshotName);

    /**
     * This creates a package of the given name, and checks it in.
     *
     * @return UUID of the created item.
     */
    public String createPackage(String name,
                                String description,
                                String format) throws SerializationException;

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
     * Validate package configuration
     *
     * @return A ValidatedReponse, with any errors to be reported. No payload is
     *         in the response. If there are any errors, the user should be
     *         given the option to review them, and correct them if needed (but
     *         a save will not be prevented this way - as its not an exception).
     */
    public ValidatedResponse validatePackageConfiguration(PackageConfigData data) throws SerializationException;

    /**
     * Saves the package config data in place (does not create a new version of
     * anything).
     *
     * @return A ValidatedReponse, with any errors to be reported. No payload is
     *         in the response. If there are any errors, the user should be
     *         given the option to review them, and correct them if needed (but
     *         a save will not be prevented this way - as its not an exception).
     */
    public void savePackage(PackageConfigData data) throws SerializationException;

    /**
     * Create a package snapshot for deployment.
     *
     * @param packageName     THe name of the package to copy.
     * @param snapshotName    The name of the snapshot. Has to be unique unless existing one
     *                        is to be replaced.
     * @param replaceExisting Replace the existing one (must be true to replace an existing
     *                        snapshot of the same name).
     * @param comment         A comment to be added to the copied one.
     */
    public void createPackageSnapshot(String packageName,
                                      String snapshotName,
                                      boolean replaceExisting,
                                      String comment);

    /**
     * This alters an existing snapshot, it can be used to copy or delete it.
     *
     * @param packageName     The package name that we are dealing with.
     * @param snapshotName    The snapshot name (this must exist)
     * @param delete          true if the snapshotName is to be removed.
     * @param newSnapshotName The name of the target snapshot that the contents will be
     *                        copied to.
     */
    public void copyOrRemoveSnapshot(String packageName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException;

    /**
     * Build the package (may be a snapshot) and return the result.
     * <p/>
     * This will then store the result in the package as an attachment.
     * <p/>
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
     * This will return the effective DRL for a package. This would be the
     * equivalent if all the rules were written by hand in the one file. It may
     * not actually be compiled this way in the implementation, so this is for
     * display and debugging assistance only.
     * <p/>
     * It should still generate
     *
     * @throws SerializationException
     */
    public String buildPackageSource(String packageUUID) throws SerializationException;

    /**
     * Copy the package (everything).
     *
     * @param sourcePackageName
     * @param destPackageName
     */
    public String copyPackage(String sourcePackageName,
                              String destPackageName) throws SerializationException;

    /**
     * Permanently remove a package (delete it).
     *
     * @param uuid of the package.
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
     * This will list the images available in a package. This has an upper limit
     * of what it will return (it just doesn't make sense to show a list of 20K
     * items !).
     */
    public String[] listImagesInPackage(String packageName) throws SerializationException;

    /**
     * This will load a list of snapshots for the given package. Snapshots are
     * created by taking a labelled copy of a package, at a point in time, for
     * instance for deployment.
     */
    public SnapshotInfo[] listSnapshots(String packageName);

    /**
     * List the fact types (class names) in the scope of a given package. This
     * may not include things on the "system" classpath, but only things
     * specifically scoped to the package (eg in jars that have been uploaded to
     * it as an asset).
     */
    public String[] listTypesInPackage(String packageUUID) throws SerializationException;

    /**
     * Installs the sample repository, wiping out what was already there.
     * Generally shouldn't call this unless it is new !
     */
    public void installSampleRepository() throws SerializationException;

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
     * @param packageName The package name the scenario is to be run in.
     * @param scenario    The scenario to run.
     * @return The scenario, with the results fields populated.
     * @throws SerializationException
     */
    public SingleScenarioResult runScenario(String packageName,
                                            Scenario scenario) throws SerializationException;

    /**
     * This should be pretty obvious what it does !
     */
    public BulkTestRunResult runScenariosInPackage(String packageUUID) throws SerializationException;


    public void updateDependency(String uuid, String dependencyPath);

    public String[] getDependencies(String uuid);


}
