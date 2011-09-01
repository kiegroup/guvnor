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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.repository.RepositoryStartupService;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthorizationException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
    private static boolean testListenerInit = false;

    /**
     * This is used by the pass through methods below.
     * Michael got tired of trying to read other peoples overly abstracted code, so its just generated dumb code to
     * reduce dependencies on libraries.
     */
    public static ServiceImplementation getService() {
        if ( Contexts.isApplicationContextActive() ) {
            return (ServiceImplementation) Component.getInstance( "org.drools.guvnor.client.rpc.RepositoryService" );
        }
        //this is only for out of container hosted mode in GWT
        synchronized (RepositoryServiceServlet.class) {
            ServiceImplementation serviceImplementation = new ServiceImplementation();
            serviceImplementation.setRulesRepository( new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) ) );
            handleTestListenerInit();
            return serviceImplementation;
        }

    }

    private static void handleTestListenerInit() {
        if ( !testListenerInit ) {
            MailboxService.getInstance().init( new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) ) );
            RepositoryStartupService.registerCheckinListener();
            testListenerInit = true;
        }
    }

    public static RepositoryAssetService getAssetService() {
        if ( Contexts.isApplicationContextActive() ) {
            return (RepositoryAssetService) Component.getInstance( "org.drools.guvnor.client.rpc.AssetService" );
        }
        //this is only for out of container hosted mode in GWT
        synchronized (RepositoryServiceServlet.class) {
            RepositoryAssetService repositoryAssetService = new RepositoryAssetService();
            repositoryAssetService.setRulesRepository( new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) ) );

            handleTestListenerInit();
            return repositoryAssetService;
        }

    }

    public static RepositoryPackageService getPackageService() {
        if ( Contexts.isApplicationContextActive() ) {
            return (RepositoryPackageService) Component.getInstance( "org.drools.guvnor.client.rpc.PackageService" );
        }
        //this is only for out of container hosted mode in GWT
        synchronized (RepositoryServiceServlet.class) {
            RepositoryPackageService repositoryPackageService = new RepositoryPackageService();
            repositoryPackageService.setRulesRepository( new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) ) );

            handleTestListenerInit();
            return repositoryPackageService;
        }
    }

    public static RepositoryCategoryService getCategoryService() {
        if ( Contexts.isApplicationContextActive() ) {
            return (RepositoryCategoryService) Component.getInstance( "org.drools.guvnor.client.rpc.CategoryService" );
        }
        //this is only for out of container hosted mode in GWT
        synchronized (RepositoryServiceServlet.class) {
            RepositoryCategoryService repositoryCategoryService = new RepositoryCategoryService();
            repositoryCategoryService.setRulesRepository( new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) ) );

            handleTestListenerInit();
            return repositoryCategoryService;
        }
    }

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
        return getCategoryService().loadChildCategories( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForCategories(java.lang.String p0, int p1, int p2, java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return getCategoryService().loadRuleListForCategories( p0, p1, p2, p3 );
    }

    public org.drools.guvnor.client.rpc.PageResponse loadRuleListForCategories(org.drools.guvnor.client.rpc.CategoryPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getCategoryService().loadRuleListForCategories( p0 );
    }

    public java.lang.Boolean createCategory(java.lang.String p0, java.lang.String p1, java.lang.String p2) {
        return getCategoryService().createCategory( p0, p1, p2 );
    }

    public void removeCategory(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        getCategoryService().removeCategory( p0 );
    }

    public void renameCategory(java.lang.String p0, java.lang.String p1) {
        getCategoryService().renameCategory( p0, p1 );
    }


    /**
     * PLACE THE FOLLOWING IN RepositoryServiceServlet.java *
     */

    public org.drools.guvnor.client.rpc.PackageConfigData[] listPackages(java.lang.String p0) {
        return getPackageService().listPackages( p0 );
    }

    public org.drools.guvnor.client.rpc.PackageConfigData[] listPackages() {
        return getPackageService().listPackages();
    }

    public org.drools.guvnor.client.rpc.PackageConfigData[] listArchivedPackages() {
        return getPackageService().listArchivedPackages();
    }

    public org.drools.guvnor.client.rpc.PackageConfigData loadGlobalPackage() {
        return getPackageService().loadGlobalPackage();
    }

    public SnapshotInfo loadSnapshotInfo(String packageName, String snapshotName) {
        return getPackageService().loadSnapshotInfo( packageName, snapshotName );
    }

    public java.lang.String createPackage(java.lang.String p0,
                                          java.lang.String p1,
                                          java.lang.String p2) throws com.google.gwt.user.client.rpc.SerializationException {
        return getPackageService().createPackage( p0,
                p1, p2);
    }

    public java.lang.String createSubPackage(java.lang.String p0,
                                             java.lang.String p1,
                                             java.lang.String p2) throws com.google.gwt.user.client.rpc.SerializationException {
        return getPackageService().createSubPackage( p0,
                p1,
                p2 );
    }

    public org.drools.guvnor.client.rpc.PackageConfigData loadPackageConfig(java.lang.String p0) {
        return getPackageService().loadPackageConfig( p0 );
    }

    public org.drools.guvnor.client.rpc.ValidatedResponse validatePackageConfiguration(org.drools.guvnor.client.rpc.PackageConfigData p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getPackageService().validatePackageConfiguration( p0 );
    }

    public void savePackage(org.drools.guvnor.client.rpc.PackageConfigData p0) throws com.google.gwt.user.client.rpc.SerializationException {
        getPackageService().savePackage( p0 );
    }

    public void createPackageSnapshot(java.lang.String p0,
                                      java.lang.String p1,
                                      boolean p2,
                                      java.lang.String p3) {
        getPackageService().createPackageSnapshot( p0,
                p1,
                p2,
                p3 );
    }

    public void copyOrRemoveSnapshot(java.lang.String p0,
                                     java.lang.String p1,
                                     boolean p2,
                                     java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializationException {
        getPackageService().copyOrRemoveSnapshot( p0,
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
        return getPackageService().buildPackage( p0,
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
        return getPackageService().buildPackageSource( p0 );
    }

    public String copyPackage(java.lang.String p0,
                              java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return getPackageService().copyPackage( p0,
                p1 );
    }

    public void removePackage(java.lang.String p0) {
        getPackageService().removePackage( p0 );
    }

    public java.lang.String renamePackage(java.lang.String p0,
                                          java.lang.String p1) {
        return getPackageService().renamePackage( p0,
                p1 );
    }

    public void rebuildSnapshots() throws com.google.gwt.user.client.rpc.SerializationException {
        getPackageService().rebuildSnapshots();
    }

    public void rebuildPackages() throws com.google.gwt.user.client.rpc.SerializationException {
        getPackageService().rebuildPackages();
    }

    public java.lang.String[] listRulesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getPackageService().listRulesInPackage( p0 );
    }

    public java.lang.String[] listImagesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getPackageService().listImagesInPackage( p0 );
    }

    public org.drools.guvnor.client.rpc.SnapshotInfo[] listSnapshots(java.lang.String p0) {
        return getPackageService().listSnapshots( p0 );
    }

    public java.lang.String[] listTypesInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getPackageService().listTypesInPackage( p0 );
    }

    public void installSampleRepository() throws com.google.gwt.user.client.rpc.SerializationException {
        getPackageService().installSampleRepository();
    }

    public org.drools.guvnor.client.rpc.SnapshotDiffs compareSnapshots(java.lang.String p0,
                                                                       java.lang.String p1,
                                                                       java.lang.String p2) {
        return getPackageService().compareSnapshots( p0,
                p1,
                p2 );
    }

    public org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse compareSnapshots(org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest p0) {
        return getPackageService().compareSnapshots( p0 );
    }

    public org.drools.guvnor.client.rpc.SingleScenarioResult runScenario(java.lang.String p0,
                                                                         org.drools.ide.common.client.modeldriven.testing.Scenario p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return getPackageService().runScenario( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.BulkTestRunResult runScenariosInPackage(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getPackageService().runScenariosInPackage( p0 );
    }


    /**
     * PLACE THE FOLLOWING IN RepositoryServiceServlet.java *
     */

    public org.drools.guvnor.client.rpc.PageResponse quickFindAsset(org.drools.guvnor.client.rpc.QueryPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().quickFindAsset( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult quickFindAsset(java.lang.String p0,
                                                                       boolean p1,
                                                                       int p2,
                                                                       int p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().quickFindAsset( p0,
                p1,
                p2,
                p3 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult queryFullText(java.lang.String p0,
                                                                      boolean p1,
                                                                      int p2,
                                                                      int p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().queryFullText( p0,
                p1,
                p2,
                p3 );
    }

    public java.lang.String getAssetLockerUserName(java.lang.String p0) {
        return getAssetService().getAssetLockerUserName( p0 );
    }

    public void lockAsset(java.lang.String p0) {
        getAssetService().lockAsset( p0 );
    }

    public void unLockAsset(java.lang.String p0) {
        getAssetService().unLockAsset( p0 );
    }

    public void archiveAsset(java.lang.String p0) {
        getAssetService().archiveAsset( p0 );
    }

    public void unArchiveAsset(java.lang.String p0) {
        getAssetService().unArchiveAsset( p0 );
    }

    public void archiveAssets(java.lang.String[] p0,
                              boolean p1) {
        getAssetService().archiveAssets( p0,
                p1 );
    }

    public void removeAsset(java.lang.String p0) {
        getAssetService().removeAsset( p0 );
    }

    public void removeAssets(java.lang.String[] p0) {
        getAssetService().removeAssets( p0 );
    }

    public java.lang.String buildAssetSource(org.drools.guvnor.client.rpc.RuleAsset p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().buildAssetSource( p0 );
    }

    public org.drools.guvnor.client.rpc.BuilderResult validateAsset(org.drools.guvnor.client.rpc.RuleAsset p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().validateAsset( p0 );
    }

    public java.lang.String renameAsset(java.lang.String p0,
                                        java.lang.String p1) {
        return getAssetService().renameAsset( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.RuleAsset loadRuleAsset(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().loadRuleAsset( p0 );
    }

    public org.drools.guvnor.client.rpc.RuleAsset[] loadRuleAssets(java.lang.String[] p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().loadRuleAssets( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadAssetHistory(java.lang.String p0,
                                                                         java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().loadAssetHistory( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadItemHistory(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().loadItemHistory( p0 );
    }

    public org.drools.guvnor.client.rpc.PageResponse loadArchivedAssets(org.drools.guvnor.client.rpc.PageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().loadArchivedAssets( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadArchivedAssets(int p0,
                                                                           int p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().loadArchivedAssets( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.PageResponse findAssetPage(org.drools.guvnor.client.rpc.AssetPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().findAssetPage( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult listAssets(java.lang.String p0,
                                                                   java.lang.String[] p1,
                                                                   int p2,
                                                                   int p3,
                                                                   java.lang.String p4) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().listAssets( p0,
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
        return getAssetService().listAssetsWithPackageName( p0,
                p1,
                p2,
                p3,
                p4 );
    }

    public java.lang.String copyAsset(java.lang.String p0,
                                      java.lang.String p1,
                                      java.lang.String p2) {
        return getAssetService().copyAsset( p0,
                p1,
                p2 );
    }

    public void promoteAssetToGlobalArea(java.lang.String p0) {
        getAssetService().promoteAssetToGlobalArea( p0 );
    }

    public void changeAssetPackage(java.lang.String p0,
                                   java.lang.String p1,
                                   java.lang.String p2) {
        getAssetService().changeAssetPackage( p0,
                p1,
                p2 );
    }

    public void changeState(java.lang.String p0,
                            java.lang.String p1) {
        getAssetService().changeState( p0,
                p1 );
    }

    public void changePackageState(java.lang.String p0,
                                   java.lang.String p1) {
        getAssetService().changePackageState( p0,
                p1 );
    }

    public java.util.List loadDiscussionForAsset(java.lang.String p0) {
        return getAssetService().loadDiscussionForAsset( p0 );
    }

    public java.util.List addToDiscussionForAsset(java.lang.String p0,
                                                  java.lang.String p1) {
        return getAssetService().addToDiscussionForAsset( p0,
                p1 );
    }

    public void clearAllDiscussionsForAsset(java.lang.String p0) {
        getAssetService().clearAllDiscussionsForAsset( p0 );
    }


    /**
     * PLACE THE FOLLOWING IN RepositoryServiceServlet.java *
     */


    public org.drools.guvnor.client.rpc.PageResponse loadRuleListForState(org.drools.guvnor.client.rpc.StatePageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().loadRuleListForState( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForState(java.lang.String p0,
                                                                             int p1,
                                                                             int p2,
                                                                             java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().loadRuleListForState( p0,
                p1,
                p2,
                p3 );
    }

    public org.drools.guvnor.client.rpc.TableConfig loadTableConfig(java.lang.String p0) {
        return getService().loadTableConfig( p0 );
    }

    public java.lang.String createNewRule(java.lang.String p0,
                                          java.lang.String p1,
                                          java.lang.String p2,
                                          java.lang.String p3,
                                          java.lang.String p4) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().createNewRule( p0,
                p1,
                p2,
                p3,
                p4 );
    }
    
    public java.lang.String createNewImportedRule(java.lang.String p0,
                                                  java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().createNewImportedRule( p0,
                p1 );
    }

    public void deleteUncheckedRule(java.lang.String p0) {
        getService().deleteUncheckedRule( p0 );
    }

    public void clearRulesRepository() {
        getService().clearRulesRepository();
    }

    public java.lang.String[] listWorkspaces() {
        return getService().listWorkspaces();
    }

    public void createWorkspace(java.lang.String p0) {
        getService().createWorkspace( p0 );
    }

    public void removeWorkspace(java.lang.String p0) {
        getService().removeWorkspace( p0 );
    }

    public void updateWorkspace(java.lang.String p0,
                                java.lang.String[] p1,
                                java.lang.String[] p2) {
        getService().updateWorkspace( p0,
                p1,
                p2 );
    }

    public void updateDependency(java.lang.String p0,
                                 java.lang.String p1) {
        getPackageService().updateDependency( p0,
                p1 );
    }

    public java.lang.String[] getDependencies(java.lang.String p0) {
        return getPackageService().getDependencies( p0 );
    }

    public java.lang.String checkinVersion(org.drools.guvnor.client.rpc.RuleAsset p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getAssetService().checkinVersion( p0 );
    }

    public void restoreVersion(java.lang.String p0,
                               java.lang.String p1,
                               java.lang.String p2) {
        getAssetService().restoreVersion( p0,
                p1,
                p2 );
    }

    public java.lang.String[] listStates() throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().listStates();
    }

    public java.lang.String createState(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().createState( p0 );
    }

    public void renameState(java.lang.String p0,
                            java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        getService().renameState( p0,
                p1 );
    }

    public void removeState(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        getService().removeState( p0 );
    }

    public org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine loadSuggestionCompletionEngine(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().loadSuggestionCompletionEngine( p0 );
    }

    public java.lang.String[] getCustomSelectors() throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().getCustomSelectors();
    }

    public org.drools.guvnor.client.rpc.PageResponse showLog(org.drools.guvnor.client.rpc.PageRequest p0) {
        return getService().showLog( p0 );
    }

    public org.drools.guvnor.client.rpc.LogEntry[] showLog() {
        return getService().showLog();
    }

    public void cleanLog() {
        getService().cleanLog();
    }

    public java.lang.String[] loadDropDownExpression(java.lang.String[] p0,
                                                     java.lang.String p1) {
        return getService().loadDropDownExpression( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.PageResponse queryFullText(org.drools.guvnor.client.rpc.QueryPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().queryFullText( p0 );
    }

    public org.drools.guvnor.client.rpc.PageResponse queryMetaData(org.drools.guvnor.client.rpc.QueryMetadataPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().queryMetaData( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult queryMetaData(org.drools.guvnor.client.rpc.MetaDataQuery[] p0,
                                                                      java.util.Date p1,
                                                                      java.util.Date p2,
                                                                      java.util.Date p3,
                                                                      java.util.Date p4,
                                                                      boolean p5,
                                                                      int p6,
                                                                      int p7) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().queryMetaData( p0,
                p1,
                p2,
                p3,
                p4,
                p5,
                p6,
                p7 );
    }

    public org.drools.guvnor.client.rpc.PageResponse listUserPermissions(org.drools.guvnor.client.rpc.PageRequest p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return getService().listUserPermissions( p0 );
    }

    public java.util.Map listUserPermissions() throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return getService().listUserPermissions();
    }

    public java.util.Map retrieveUserPermissions(java.lang.String p0) {
        return getService().retrieveUserPermissions( p0 );
    }

    public void updateUserPermissions(java.lang.String p0,
                                      java.util.Map p1) {
        getService().updateUserPermissions( p0,
                p1 );
    }

    public java.lang.String[] listAvailablePermissionTypes() {
        return getService().listAvailablePermissionTypes();
    }

    public List<String> listAvailablePermissionRoleTypes() {
        return getService().listAvailablePermissionRoleTypes();
    }

    public void deleteUser(java.lang.String p0) {
        getService().deleteUser( p0 );
    }

    public void createUser(java.lang.String p0) {
        getService().createUser( p0 );
    }

    public java.util.List subscribe() {
        return getService().subscribe();
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadInbox(java.lang.String p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return getService().loadInbox( p0 );
    }

    public org.drools.guvnor.client.rpc.PageResponse loadInbox(org.drools.guvnor.client.rpc.InboxPageRequest p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return getService().loadInbox( p0 );
    }

    public java.lang.String processTemplate(java.lang.String p0,
                                            java.util.Map p1) {
        return getService().processTemplate( p0,
                p1 );
    }

    public java.lang.Boolean isHostedMode() {
        return getService().isHostedMode();
    }

    public java.util.Map loadSpringContextElementData() throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return getService().loadSpringContextElementData();
    }

    public java.util.Map loadWorkitemDefinitionElementData() throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return getService().loadWorkitemDefinitionElementData();
    }
    
    public boolean doesAssetExistInPackage(java.lang.String p0,
                                           java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return getService().doesAssetExistInPackage( p0,
                                                     p1 );
    }

}
