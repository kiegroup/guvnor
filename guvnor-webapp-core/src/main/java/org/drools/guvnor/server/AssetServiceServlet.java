/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.drools.guvnor.client.rpc.AssetService;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.jboss.solder.core.Veto;
import org.drools.guvnor.client.rpc.Path;

import javax.inject.Inject;

@Veto
public class AssetServiceServlet
        extends RemoteServiceServlet
        implements AssetService {
    
    @Inject
    private AssetService assetService;
    
    
    public org.drools.guvnor.client.rpc.PageResponse quickFindAsset(org.drools.guvnor.client.rpc.QueryPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.quickFindAsset(p0);
    }

    public org.drools.guvnor.client.rpc.TableDataResult quickFindAsset(java.lang.String p0,
                                                                       boolean p1,
                                                                       int p2,
                                                                       int p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.quickFindAsset(p0,
                p1,
                p2,
                p3);
    }

    public org.drools.guvnor.client.rpc.TableDataResult queryFullText(java.lang.String p0,
                                                                      boolean p1,
                                                                      int p2,
                                                                      int p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.queryFullText(p0,
                p1,
                p2,
                p3);
    }

    public java.lang.String getAssetLockerUserName(Path p0) {
        return assetService.getAssetLockerUserName(p0);
    }

    public void lockAsset(Path p0) {
        assetService.lockAsset(p0);
    }

    public void unLockAsset(Path p0) {
        assetService.unLockAsset(p0);
    }

    public void archiveAsset(Path p0) {
        assetService.archiveAsset(p0);
    }

    public void unArchiveAsset(Path p0) {
        assetService.unArchiveAsset(p0);
    }

    public void archiveAssets(Path[] p0,
                              boolean p1) {
        assetService.archiveAssets(p0,
                p1);
    }

    public void removeAsset(Path p0) {
        assetService.removeAsset(p0);
    }

    public void removeAssets(Path[] p0) {
        assetService.removeAssets(p0);
    }

    public java.lang.String buildAssetSource(org.drools.guvnor.client.rpc.Asset p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.buildAssetSource(p0);
    }

    public org.drools.guvnor.client.rpc.BuilderResult validateAsset(org.drools.guvnor.client.rpc.Asset p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.validateAsset(p0);
    }

    public Path renameAsset(Path p0,
                                        java.lang.String p1) {
        return assetService.renameAsset(p0,
                p1);
    }

    public org.drools.guvnor.client.rpc.Asset loadRuleAsset(Path p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadRuleAsset(p0);
    }

    public org.drools.guvnor.client.rpc.Asset[] loadRuleAssets(Path[] p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadRuleAssets(p0);
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadAssetHistory(java.lang.String p0,
                                                                         java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadAssetHistory(p0,
                p1);
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadItemHistory(Path p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadItemHistory(p0);
    }

    public org.drools.guvnor.client.rpc.PageResponse loadArchivedAssets(org.drools.guvnor.client.rpc.PageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadArchivedAssets(p0);
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadArchivedAssets(int p0,
                                                                           int p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadArchivedAssets(p0,
                p1);
    }

    public org.drools.guvnor.client.rpc.PageResponse findAssetPage(org.drools.guvnor.client.rpc.AssetPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.findAssetPage(p0);
    }

    public org.drools.guvnor.client.rpc.TableDataResult listAssets(Path p0,
                                                                   java.lang.String[] p1,
                                                                   int p2,
                                                                   int p3,
                                                                   java.lang.String p4) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.listAssets(p0,
                p1,
                p2,
                p3,
                p4);
    }

    public Path copyAsset(Path p0,
                                      java.lang.String p1,
                                      java.lang.String p2) {
        return assetService.copyAsset(p0,
                p1,
                p2);
    }

    public void promoteAssetToGlobalArea(Path p0) {
        assetService.promoteAssetToGlobalArea(p0);
    }

    public void changeAssetPackage(Path p0,
                                   java.lang.String p1,
                                   java.lang.String p2) {
        assetService.changeAssetPackage(p0,
                p1,
                p2);
    }

    public void changeState(Path p0,
                            java.lang.String p1) {
        assetService.changeState(p0,
                p1);
    }

    public void changePackageState(java.lang.String p0,
                                   java.lang.String p1) {
        assetService.changePackageState(p0,
                p1);
    }

    public java.util.List loadDiscussionForAsset(Path p0) {
        return assetService.loadDiscussionForAsset(p0);
    }

    public java.util.List addToDiscussionForAsset(Path p0,
                                                  java.lang.String p1) {
        return assetService.addToDiscussionForAsset(p0,
                p1);
    }

    public void clearAllDiscussionsForAsset(Path p0) {
        assetService.clearAllDiscussionsForAsset(p0);
    }

    public long getAssetCount(org.drools.guvnor.client.rpc.AssetPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.findAssetPage(p0).getTotalRowSize();
    }

    public ConversionResult convertAsset(Path assetPath,
                                         String targetFormat) throws SerializationException {
        return assetService.convertAsset(assetPath,
                targetFormat);
    }

    public java.lang.String checkinVersion(org.drools.guvnor.client.rpc.Asset p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.checkinVersion(p0);
    }

    public void restoreVersion(Path p0,
    		                   Path p1,
                               java.lang.String p2) {
        assetService.restoreVersion(p0,
                p1,
                p2);
    }

}
