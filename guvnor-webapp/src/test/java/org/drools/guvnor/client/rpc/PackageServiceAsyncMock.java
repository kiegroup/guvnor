/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

public abstract class PackageServiceAsyncMock implements PackageServiceAsync {

    public void loadSnapshotInfo(String packageName, String snapshotName, AsyncCallback<SnapshotInfo> async) {
    }

    public void listPackages(String p0, AsyncCallback<PackageConfigData[]> cb) {
    }

    public void listPackages(AsyncCallback<PackageConfigData[]> cb) {
    }

    public void listArchivedPackages(AsyncCallback<PackageConfigData[]> cb) {
    }

    public void loadGlobalPackage(AsyncCallback<PackageConfigData> cb) {
    }

    public void createPackage(String p0, String p1, String p2, AsyncCallback<String> cb) {
    }

    public void createSubPackage(String p0, String p1, String p2, AsyncCallback<String> cb) {
    }

    public void loadPackageConfig(String p0, AsyncCallback<PackageConfigData> cb) {
    }

    public void validatePackageConfiguration(PackageConfigData p0, AsyncCallback<ValidatedResponse> cb) {
    }

    public void savePackage(PackageConfigData p0, AsyncCallback cb) {
    }

    public void createPackageSnapshot(String p0, String p1, boolean p2, String p3, AsyncCallback cb) {
    }

    public void copyOrRemoveSnapshot(String p0, String p1, boolean p2, String p3, AsyncCallback cb) {
    }

    public void buildPackage(String p0, boolean p1, String p2, String p3, String p4, boolean p5, String p6, String p7, boolean p8, String p9, AsyncCallback<BuilderResult> cb) {
    }

    public void buildPackageSource(String p0, AsyncCallback<String> cb) {
    }

    public void copyPackage(String p0, String p1, AsyncCallback<String> cb) {
    }

    public void removePackage(String p0, AsyncCallback cb) {
    }

    public void renamePackage(String p0, String p1, AsyncCallback<String> cb) {
    }

    public void rebuildSnapshots(AsyncCallback cb) {
    }

    public void rebuildPackages(AsyncCallback cb) {
    }

    public void listRulesInPackage(String p0, AsyncCallback<String[]> cb) {
    }

    public void listImagesInPackage(String p0, AsyncCallback<String[]> cb) {
    }

    public void listSnapshots(String p0, AsyncCallback<SnapshotInfo[]> cb) {
    }

    public void listTypesInPackage(String p0, AsyncCallback<String[]> cb) {
    }

    public void installSampleRepository(AsyncCallback cb) {
    }

    public void compareSnapshots(String p0, String p1, String p2, AsyncCallback<SnapshotDiffs> cb) {
    }

    public void compareSnapshots(SnapshotComparisonPageRequest p0, AsyncCallback<SnapshotComparisonPageResponse> cb) {
    }

    public void runScenario(String p0, Scenario p1, AsyncCallback<SingleScenarioResult> cb) {
    }

    public void runScenariosInPackage(String p0, AsyncCallback<BulkTestRunResult> cb) {
    }

    public void updateDependency(String uuid, String dependencyPath, AsyncCallback cb) {
    }

    public void getDependencies(String uuid, AsyncCallback<String[]> cb) {
    }
}
