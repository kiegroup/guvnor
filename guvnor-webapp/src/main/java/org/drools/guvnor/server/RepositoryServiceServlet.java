/*
 * Copyright 2010 JBoss Inc
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.client.rpc.AssetService;
import org.drools.guvnor.client.rpc.CategoryService;
import org.drools.guvnor.client.rpc.PackageService;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.security.AuthorizationException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * GWT RPC service endpoint for Repository service. A place to hang some exception handling mainly.
 * This passes on all requests unmolested to the underlying ServiceImplemention class.
 */
public class RepositoryServiceServlet extends RemoteServiceServlet
        implements
        RepositoryService,
        AssetService,
        PackageService,
        CategoryService {

    private static final long serialVersionUID = 495822L;

    private static final LoggingHelper log = LoggingHelper.getLogger( RepositoryServiceServlet.class );

    @Inject
    private ServiceImplementation serviceImplementation;

    @Inject
    private RepositoryAssetService assetService;

    @Inject
    private RepositoryPackageService packageService;

    @Inject
    private RepositoryCategoryService categoryService;

    @Override
    protected void doUnexpectedFailure(Throwable e) {
        if ( e.getCause() instanceof AuthorizationException ) {
            HttpServletResponse response = getThreadLocalResponse();
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                log.error( e.getMessage(),
                        e.getCause() );
                e.printStackTrace();
                response.setContentType( "text/plain" );
                response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
                writer.write( "Sorry, insufficient permissions to perform this action." );
            } catch (IOException ex) {
                getServletContext().log( "respondWithUnexpectedFailure failed while sending the previous failure to the client",
                        ex );
            } finally {
                close( writer );
            }
        } else if ( e.getCause() instanceof RulesRepositoryException ) {
            log.error( e.getMessage(),
                    e.getCause() );
            sendErrorMessage( e.getCause().getMessage() );
        } else {
            if ( e.getCause() != null ) {
                log.error( e.getMessage(),
                        e.getCause() );
            } else {
                log.error( e.getMessage(),
                        e );
            }
            sendErrorMessage( "Sorry, a technical error occurred. Please contact a system administrator." );
        }
    }

    private void close(PrintWriter writer) {
        if ( writer != null ) {
            writer.flush();
            writer.close();
        }
    }

    private void sendErrorMessage(String msg) {
        HttpServletResponse response = getThreadLocalResponse();
        response.setContentType( "text/plain" );
        response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write( msg );
        } catch (IOException ex) {
            getServletContext().log( "respondWithUnexpectedFailure failed while sending the previous failure to the client",
                    ex );
        } finally {
            close( writer );
        }
    }

    /**
     * PLACE THE FOLLOWING IN RepositoryServiceServlet.java *
     */

    public java.lang.String[] loadChildCategories(java.lang.String p0) {
        return categoryService.loadChildCategories( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForCategories(java.lang.String p0, int p1, int p2, java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return categoryService.loadRuleListForCategories( p0, p1, p2, p3 );
    }

    public org.drools.guvnor.client.rpc.PageResponse loadRuleListForCategories(org.drools.guvnor.client.rpc.CategoryPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return categoryService.loadRuleListForCategories( p0 );
    }

    public java.lang.Boolean createCategory(java.lang.String p0, java.lang.String p1, java.lang.String p2) {
        return categoryService.createCategory( p0, p1, p2 );
    }

    public void removeCategory(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        categoryService.removeCategory( p0 );
    }

    public void renameCategory(java.lang.String p0, java.lang.String p1) {
        categoryService.renameCategory( p0, p1 );
    }


    /**
     * PLACE THE FOLLOWING IN RepositoryServiceServlet.java *
     */

    public org.drools.guvnor.client.rpc.PackageConfigData[] listPackages(java.lang.String p0) {
        return packageService.listPackages( p0 );
    }

    public org.drools.guvnor.client.rpc.PackageConfigData[] listPackages() {
        return packageService.listPackages();
    }

    public org.drools.guvnor.client.rpc.PackageConfigData[] listArchivedPackages() {
        return packageService.listArchivedPackages();
    }

    public org.drools.guvnor.client.rpc.PackageConfigData loadGlobalPackage() {
        return packageService.loadGlobalPackage();
    }

    public SnapshotInfo loadSnapshotInfo(String packageName, String snapshotName) {
        return packageService.loadSnapshotInfo( packageName, snapshotName );
    }

    public java.lang.String createPackage(java.lang.String p0,
                                          java.lang.String p1,
                                          java.lang.String p2) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.createPackage( p0,
                p1, p2);
    }

    public java.lang.String createSubPackage(java.lang.String p0,
                                             java.lang.String p1,
                                             java.lang.String p2) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.createSubPackage( p0,
                p1,
                p2 );
    }

    public org.drools.guvnor.client.rpc.PackageConfigData loadPackageConfig(java.lang.String p0) {
        return packageService.loadPackageConfig( p0 );
    }

    public org.drools.guvnor.client.rpc.ValidatedResponse validatePackageConfiguration(org.drools.guvnor.client.rpc.PackageConfigData p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.validatePackageConfiguration( p0 );
    }

    public void savePackage(org.drools.guvnor.client.rpc.PackageConfigData p0) throws com.google.gwt.user.client.rpc.SerializationException {
        packageService.savePackage( p0 );
    }

    public void createPackageSnapshot(java.lang.String p0,
                                      java.lang.String p1,
                                      boolean p2,
                                      java.lang.String p3) {
        packageService.createPackageSnapshot( p0,
                p1,
                p2,
                p3 );
    }

    public void copyOrRemoveSnapshot(java.lang.String p0,
                                     java.lang.String p1,
                                     boolean p2,
                                     java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializationException {
        packageService.copyOrRemoveSnapshot( p0,
                p1,
                p2,
                p3 );
    }

    public org.drools.guvnor.client.rpc.BuilderResult buildPackage(java.lang.String p0,
                                                                   boolean p1,
                                                                   java.lang.String p2,
                                                                   java.lang.String p3,
                                                                   java.lang.String p4,
                                                                   boolean p5,
                                                                   java.lang.String p6,
                                                                   java.lang.String p7,
                                                                   boolean p8,
                                                                   java.lang.String p9) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.buildPackage( p0,
                p1,
                p2,
                p3,
                p4,
                p5,
                p6,
                p7,
                p8,
                p9 );
    }

    public java.lang.String buildPackageSource(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.buildPackageSource( p0 );
    }

    public String copyPackage(java.lang.String p0,
                              java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.copyPackage( p0,
                p1 );
    }

    public void removePackage(java.lang.String p0) {
        packageService.removePackage( p0 );
    }

    public java.lang.String renamePackage(java.lang.String p0,
                                          java.lang.String p1) {
        return packageService.renamePackage( p0,
                p1 );
    }

    public void rebuildSnapshots() throws com.google.gwt.user.client.rpc.SerializationException {
        packageService.rebuildSnapshots();
    }

    public void rebuildPackages() throws com.google.gwt.user.client.rpc.SerializationException {
        packageService.rebuildPackages();
    }

    public java.lang.String[] listRulesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.listRulesInPackage( p0 );
    }

    public java.lang.String[] listImagesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.listImagesInPackage( p0 );
    }

    public org.drools.guvnor.client.rpc.SnapshotInfo[] listSnapshots(java.lang.String p0) {
        return packageService.listSnapshots( p0 );
    }

    public java.lang.String[] listTypesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.listTypesInPackage( p0 );
    }

    public void installSampleRepository() throws com.google.gwt.user.client.rpc.SerializationException {
        packageService.installSampleRepository();
    }

    public org.drools.guvnor.client.rpc.SnapshotDiffs compareSnapshots(java.lang.String p0,
                                                                       java.lang.String p1,
                                                                       java.lang.String p2) {
        return packageService.compareSnapshots( p0,
                p1,
                p2 );
    }

    public org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse compareSnapshots(org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest p0) {
        return packageService.compareSnapshots( p0 );
    }

    public org.drools.guvnor.client.rpc.SingleScenarioResult runScenario(java.lang.String p0,
                                                                         org.drools.ide.common.client.modeldriven.testing.Scenario p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.runScenario( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.BulkTestRunResult runScenariosInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return packageService.runScenariosInPackage( p0 );
    }


    /**
     * PLACE THE FOLLOWING IN RepositoryServiceServlet.java *
     */

    public org.drools.guvnor.client.rpc.PageResponse quickFindAsset(org.drools.guvnor.client.rpc.QueryPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.quickFindAsset( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult quickFindAsset(java.lang.String p0,
                                                                       boolean p1,
                                                                       int p2,
                                                                       int p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.quickFindAsset( p0,
                p1,
                p2,
                p3 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult queryFullText(java.lang.String p0,
                                                                      boolean p1,
                                                                      int p2,
                                                                      int p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.queryFullText( p0,
                p1,
                p2,
                p3 );
    }

    public java.lang.String getAssetLockerUserName(java.lang.String p0) {
        return assetService.getAssetLockerUserName( p0 );
    }

    public void lockAsset(java.lang.String p0) {
        assetService.lockAsset( p0 );
    }

    public void unLockAsset(java.lang.String p0) {
        assetService.unLockAsset( p0 );
    }

    public void archiveAsset(java.lang.String p0) {
        assetService.archiveAsset( p0 );
    }

    public void unArchiveAsset(java.lang.String p0) {
        assetService.unArchiveAsset( p0 );
    }

    public void archiveAssets(java.lang.String[] p0,
                              boolean p1) {
        assetService.archiveAssets( p0,
                p1 );
    }

    public void removeAsset(java.lang.String p0) {
        assetService.removeAsset( p0 );
    }

    public void removeAssets(java.lang.String[] p0) {
        assetService.removeAssets( p0 );
    }

    public java.lang.String buildAssetSource(org.drools.guvnor.client.rpc.RuleAsset p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.buildAssetSource( p0 );
    }

    public org.drools.guvnor.client.rpc.BuilderResult validateAsset(org.drools.guvnor.client.rpc.RuleAsset p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.validateAsset( p0 );
    }

    public java.lang.String renameAsset(java.lang.String p0,
                                        java.lang.String p1) {
        return assetService.renameAsset( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.RuleAsset loadRuleAsset(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadRuleAsset( p0 );
    }

    public org.drools.guvnor.client.rpc.RuleAsset[] loadRuleAssets(java.lang.String[] p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadRuleAssets( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadAssetHistory(java.lang.String p0,
                                                                         java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadAssetHistory( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadItemHistory(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadItemHistory( p0 );
    }

    public org.drools.guvnor.client.rpc.PageResponse loadArchivedAssets(org.drools.guvnor.client.rpc.PageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadArchivedAssets( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadArchivedAssets(int p0,
                                                                           int p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.loadArchivedAssets( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.PageResponse findAssetPage(org.drools.guvnor.client.rpc.AssetPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.findAssetPage( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult listAssets(java.lang.String p0,
                                                                   java.lang.String[] p1,
                                                                   int p2,
                                                                   int p3,
                                                                   java.lang.String p4) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.listAssets( p0,
                p1,
                p2,
                p3,
                p4 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult listAssetsWithPackageName(java.lang.String p0,
                                                                                  java.lang.String[] p1,
                                                                                  int p2,
                                                                                  int p3,
                                                                                  java.lang.String p4) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.listAssetsWithPackageName( p0,
                p1,
                p2,
                p3,
                p4 );
    }

    public java.lang.String copyAsset(java.lang.String p0,
                                      java.lang.String p1,
                                      java.lang.String p2) {
        return assetService.copyAsset( p0,
                p1,
                p2 );
    }

    public void promoteAssetToGlobalArea(java.lang.String p0) {
        assetService.promoteAssetToGlobalArea( p0 );
    }

    public void changeAssetPackage(java.lang.String p0,
                                   java.lang.String p1,
                                   java.lang.String p2) {
        assetService.changeAssetPackage( p0,
                p1,
                p2 );
    }

    public void changeState(java.lang.String p0,
                            java.lang.String p1) {
        assetService.changeState( p0,
                p1 );
    }

    public void changePackageState(java.lang.String p0,
                                   java.lang.String p1) {
        assetService.changePackageState( p0,
                p1 );
    }

    public java.util.List loadDiscussionForAsset(java.lang.String p0) {
        return assetService.loadDiscussionForAsset( p0 );
    }

    public java.util.List addToDiscussionForAsset(java.lang.String p0,
                                                  java.lang.String p1) {
        return assetService.addToDiscussionForAsset( p0,
                p1 );
    }

    public void clearAllDiscussionsForAsset(java.lang.String p0) {
        assetService.clearAllDiscussionsForAsset( p0 );
    }
    
    public long getAssetCount(org.drools.guvnor.client.rpc.AssetPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.findAssetPage( p0 ).getTotalRowSize();
    }

    /**
     * PLACE THE FOLLOWING IN RepositoryServiceServlet.java *
     */


    public org.drools.guvnor.client.rpc.PageResponse loadRuleListForState(org.drools.guvnor.client.rpc.StatePageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.loadRuleListForState( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForState(java.lang.String p0,
                                                                             int p1,
                                                                             int p2,
                                                                             java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.loadRuleListForState( p0,
                p1,
                p2,
                p3 );
    }

    public org.drools.guvnor.client.rpc.TableConfig loadTableConfig(java.lang.String p0) {
        return serviceImplementation.loadTableConfig( p0 );
    }

    public java.lang.String createNewRule(java.lang.String p0,
                                          java.lang.String p1,
                                          java.lang.String p2,
                                          java.lang.String p3,
                                          java.lang.String p4) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createNewRule( p0,
                p1,
                p2,
                p3,
                p4 );
    }
    
    public String createNewRule(org.drools.guvnor.client.rpc.NewAssetConfiguration p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createNewRule( p0 );
    }

    public String createNewRule(org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createNewRule( p0 );
    }

    public java.lang.String createNewImportedRule(java.lang.String p0,
                                                  java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createNewImportedRule( p0,
                p1 );
    }

    public void deleteUncheckedRule(java.lang.String p0) {
        serviceImplementation.deleteUncheckedRule( p0 );
    }

    public void clearRulesRepository() {
        serviceImplementation.clearRulesRepository();
    }

    public java.lang.String[] listWorkspaces() {
        return serviceImplementation.listWorkspaces();
    }

    public void createWorkspace(java.lang.String p0) {
        serviceImplementation.createWorkspace( p0 );
    }

    public void removeWorkspace(java.lang.String p0) {
        serviceImplementation.removeWorkspace( p0 );
    }

    public void updateWorkspace(java.lang.String p0,
                                java.lang.String[] p1,
                                java.lang.String[] p2) {
        serviceImplementation.updateWorkspace( p0,
                p1,
                p2 );
    }

    public void updateDependency(java.lang.String p0,
                                 java.lang.String p1) {
        packageService.updateDependency( p0,
                p1 );
    }

    public java.lang.String[] getDependencies(java.lang.String p0) {
        return packageService.getDependencies( p0 );
    }

    public java.lang.String checkinVersion(org.drools.guvnor.client.rpc.RuleAsset p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return assetService.checkinVersion( p0 );
    }

    public void restoreVersion(java.lang.String p0,
                               java.lang.String p1,
                               java.lang.String p2) {
        assetService.restoreVersion( p0,
                p1,
                p2 );
    }

    public java.lang.String[] listStates() throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.listStates();
    }

    public java.lang.String createState(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createState( p0 );
    }

    public void renameState(java.lang.String p0,
                            java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        serviceImplementation.renameState( p0,
                p1 );
    }

    public void removeState(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        serviceImplementation.removeState( p0 );
    }

    public org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine loadSuggestionCompletionEngine(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.loadSuggestionCompletionEngine( p0 );
    }

    public java.lang.String[] getCustomSelectors() throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.getCustomSelectors();
    }

    public org.drools.guvnor.client.rpc.PageResponse showLog(org.drools.guvnor.client.rpc.PageRequest p0) {
        return serviceImplementation.showLog( p0 );
    }

    public org.drools.guvnor.client.rpc.LogEntry[] showLog() {
        return serviceImplementation.showLog();
    }

    public void cleanLog() {
        serviceImplementation.cleanLog();
    }

    public java.lang.String[] loadDropDownExpression(java.lang.String[] p0,
                                                     java.lang.String p1) {
        return serviceImplementation.loadDropDownExpression( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.PageResponse queryFullText(org.drools.guvnor.client.rpc.QueryPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.queryFullText( p0 );
    }

    public org.drools.guvnor.client.rpc.PageResponse queryMetaData(org.drools.guvnor.client.rpc.QueryMetadataPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.queryMetaData( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult queryMetaData(org.drools.guvnor.client.rpc.MetaDataQuery[] p0,
                                                                      java.util.Date p1,
                                                                      java.util.Date p2,
                                                                      java.util.Date p3,
                                                                      java.util.Date p4,
                                                                      boolean p5,
                                                                      int p6,
                                                                      int p7) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.queryMetaData( p0,
                p1,
                p2,
                p3,
                p4,
                p5,
                p6,
                p7 );
    }

    public org.drools.guvnor.client.rpc.PageResponse listUserPermissions(org.drools.guvnor.client.rpc.PageRequest p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.listUserPermissions( p0 );
    }

    public java.util.Map listUserPermissions() throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.listUserPermissions();
    }

    public java.util.Map retrieveUserPermissions(java.lang.String p0) {
        return serviceImplementation.retrieveUserPermissions( p0 );
    }

    public void updateUserPermissions(java.lang.String p0,
                                      java.util.Map p1) {
        serviceImplementation.updateUserPermissions( p0,
                p1 );
    }

    public java.lang.String[] listAvailablePermissionTypes() {
        return serviceImplementation.listAvailablePermissionTypes();
    }

    public List<String> listAvailablePermissionRoleTypes() {
        return serviceImplementation.listAvailablePermissionRoleTypes();
    }

    public void deleteUser(java.lang.String p0) {
        serviceImplementation.deleteUser( p0 );
    }

    public void createUser(java.lang.String p0) {
        serviceImplementation.createUser( p0 );
    }

    public java.util.List subscribe() {
        return serviceImplementation.subscribe();
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadInbox(java.lang.String p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.loadInbox( p0 );
    }

    public org.drools.guvnor.client.rpc.PageResponse loadInbox(org.drools.guvnor.client.rpc.InboxPageRequest p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.loadInbox( p0 );
    }

    public java.lang.String processTemplate(java.lang.String p0,
                                            java.util.Map p1) {
        return serviceImplementation.processTemplate( p0,
                p1 );
    }

    public java.util.Map loadSpringContextElementData() throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.loadSpringContextElementData();
    }

    public java.util.Map loadWorkitemDefinitionElementData() throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.loadWorkitemDefinitionElementData();
    }
    
    public Set<PortableWorkDefinition> loadWorkItemDefinitions(String p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.loadWorkItemDefinitions( p0 );
    }
    
    public boolean doesAssetExistInPackage(java.lang.String p0,
                                           java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.doesAssetExistInPackage( p0,
                                                     p1 );
    }

}
